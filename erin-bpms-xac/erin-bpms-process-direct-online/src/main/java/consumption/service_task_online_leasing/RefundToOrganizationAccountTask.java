package consumption.service_task_online_leasing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import consumption.service_task_bnpl.RefundTransactionTask;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.bnpl.AddAccLien;
import mn.erin.domain.bpm.usecase.bnpl.GetAccLien;
import mn.erin.domain.bpm.usecase.bnpl.ModifyAccLien;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static consumption.util.CamundaUtils.toTransaction;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.FAILURE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.STATUS;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.SUCCESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCOUNT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.START_DT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_RMKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LIEN_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MODULE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_LIEN_AMT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_LIEN_AMT_CURRENCY_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REASON_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ULIEN;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLECTED_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFMISC10;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_ORGANIZATION_DEFAULT_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

/**
 * @author Odgavaa
 */
public class RefundToOrganizationAccountTask implements JavaDelegate
{

  private static final Logger LOGGER = LoggerFactory.getLogger(RefundTransactionTask.class);
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final Environment environment;

  public RefundToOrganizationAccountTask(BpmsRepositoryRegistry bpmsRepositoryRegistry, BpmsServiceRegistry bpmsServiceRegistry, Environment environment)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String accountId = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String disburseAmountString = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
    String defaultAccount = String.valueOf(environment.getProperty(ONLINE_LEASING_ORGANIZATION_DEFAULT_ACCOUNT));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));

    BigDecimal disburseAmount = removeCommaAndGetBigDecimal(disburseAmountString);
    String confmisc10 = String.valueOf(execution.getVariable(CONFMISC10));
    if (!EMPTY_VALUE.equals(confmisc10) && confmisc10.equalsIgnoreCase("Y"))
    {
      BigDecimal collectedAmount = new BigDecimal(String.valueOf(execution.getVariable(COLLECTED_AMOUNT)));
      disburseAmount = disburseAmount.subtract(collectedAmount);
    }
    String logHash = getLogPrefix(processTypeId);

    try
    {
      Map<String, Object> requestBody = toTransaction(execution, defaultAccount, accountId, disburseAmount, disburseAmount, "refundTransaction");
      requestBody.put(PROCESS_REQUEST_ID, processTypeId);
      requestBody.put(PHONE_NUMBER, phoneNumber);

      final Map<String, Object> response = bpmsServiceRegistry.getDirectOnlineCoreBankingService().addTransaction(requestBody);
      if (response.get(STATUS).equals(FAILURE))
      {
        setLien(execution, accountId, disburseAmount, phoneNumber, processTypeId, logHash);
        String errorMessage = String.valueOf(response.get(ERROR_MESSAGE));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          LOGGER.info("{} FAILED TRANSACT REFUND TO DEFAULT ACCOUNT. REQUEST ID = [{}]. TRACKNUMBER = [{}]", logHash, requestId, trackNumber);
        }
        else
        {
          LOGGER.info("{} FAILED TRANSACT REFUND TO DEFAULT ACCOUNT. REQUEST ID = [{}]", logHash, requestId);
        }
        if (errorMessage == null || errorMessage.equals(NULL_STRING))
        {
          execution.setVariable(ERROR_CAUSE, "Failed to transfer refund to default account!");
        }
        throw new BpmnError("Transfer refund to default account", errorMessage);
      }
      else if (response.get(STATUS).equals(SUCCESS))
      {
        updateTaskStatus(execution, "Refund transaction task", "Success");
        LOGGER.info("{} SUCCESSFULLY TRANSACT TO DEFAULT ACCOUNT. REQUEST ID = [{}]", logHash, requestId);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        setLien(execution, accountId, disburseAmount, phoneNumber, processTypeId, logHash);
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (e.getMessage() == null)
      {
        execution.setVariable(ERROR_CAUSE, String.valueOf(e));
        updateTaskStatus(execution, "Refund transaction task", "Failed");
        throw new BpmnError("Transfer refund to default account", String.valueOf(e));
      }
      updateTaskStatus(execution, "Refund transaction task", "Failed");
      throw new BpmnError("Transfer refund to default account", e.getMessage());
    }
  }

  private void addAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, String phoneNumber, String processTypeId, String logHash) throws UseCaseException
  {
    AddAccLien addAccLien = new AddAccLien(bpmsServiceRegistry.getDirectOnlineCoreBankingService());
    Map<String, Object> requestAddLien = new HashMap<>();
    requestAddLien.put(ACCT_ID, accountId);
    requestAddLien.put(MODULE_TYPE, ULIEN);
    requestAddLien.put(NEW_LIEN_AMT, String.valueOf(disburseAmount));
    requestAddLien.put(NEW_LIEN_AMT_CURRENCY_CODE, CURRENCY_MNT);
    requestAddLien.put(START_DT, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    requestAddLien.put(REASON_CODE, "999");
    requestAddLien.put(XAC_RMKS, "");
    requestAddLien.put(PROCESS_TYPE_ID, processTypeId);
    requestAddLien.put(PHONE_NUMBER, phoneNumber);
    Map<String, Object> addAcc = addAccLien.execute(requestAddLien);

    if (addAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED, logHash);
    }
    else if (addAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, logHash);
    }
  }

  private void modifyAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, String phoneNumber, String processTypeId, String loghash) throws UseCaseException
  {
    Map<String, Object> requestModifyLien = new HashMap<>();
    requestModifyLien.put(NEW_LIEN_AMT, String.valueOf(disburseAmount));
    requestModifyLien.put(ACCT_ID, accountId);
    requestModifyLien.put(LIEN_ID, "");
    requestModifyLien.put(MODULE_TYPE, ULIEN);
    requestModifyLien.put(NEW_LIEN_AMT_CURRENCY_CODE, CURRENCY_MNT);
    requestModifyLien.put(REASON_CODE, "999");
    requestModifyLien.put(XAC_RMKS, "");
    requestModifyLien.put(PROCESS_TYPE_ID, processTypeId);
    requestModifyLien.put(PHONE_NUMBER, phoneNumber);
    ModifyAccLien modifyAccLien = new ModifyAccLien(bpmsServiceRegistry.getDirectOnlineCoreBankingService());

    Map<String, Object> modifyAcc = modifyAccLien.execute(requestModifyLien);
    if (modifyAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED, loghash);
    }
    else if (modifyAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, loghash);
    }
  }

  private void updateRequestState(DelegateExecution execution, ProcessRequestState state, String loghash) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, state);
    UpdateRequestStateOutput output = updateRequestState.execute(input);
    boolean isStateUpdated = output.isUpdated();
    if (isStateUpdated)
    {
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("{} Updated process request state to {} with request id [{}], tracknumber [{}].", loghash, fromEnumToString(state), processRequestId, trackNumber);
      }
      else
      {
        LOGGER.info("{} Updated process request state to {} with request id [{}].", loghash, fromEnumToString(state), processRequestId);
      }
    }
  }

  private void setLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, String phoneNumber, String processTypeId, String loghash)
      throws UseCaseException
  {
    try
    {
      GetAccLien getAccLien = new GetAccLien(bpmsServiceRegistry.getDirectOnlineCoreBankingService());
      Map<String, String> inputParam = new HashMap<>();
      inputParam.put(PROCESS_TYPE_ID, processTypeId);
      inputParam.put(PHONE_NUMBER, phoneNumber);
      inputParam.put(ACCOUNT_ID, accountId);
      inputParam.put(MODULE_TYPE, ULIEN);
      Map<String, Object> result = getAccLien.execute(inputParam);
      if (result.get(STATUS).equals(FAILURE))
      {
        addAccLien(execution, accountId, disburseAmount, phoneNumber, processTypeId, loghash);
      }
      else if (result.get(STATUS).equals(SUCCESS))
      {
        modifyAccLien(execution, accountId, disburseAmount, phoneNumber, processTypeId, loghash);
      }
    }
    catch (Exception e)
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, loghash);
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (e.getMessage() == null)
      {
        updateTaskStatus(execution, "Refund transaction task", "Failed");
        throw new BpmnError("Transfer refund to default account", String.valueOf(e));
      }
      updateTaskStatus(execution, "Refund transaction task", "Failed");
      throw new BpmnError("Transfer refund to default account", e.getMessage());
    }
  }
}

