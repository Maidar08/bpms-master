package consumption.service_task_bnpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import consumption.constant.CamundaVariableConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.usecase.bnpl.AddAccLien;
import mn.erin.domain.bpm.usecase.bnpl.GetAccLien;
import mn.erin.domain.bpm.usecase.bnpl.GetAccLienInput;
import mn.erin.domain.bpm.usecase.bnpl.ModifyAccLien;
import mn.erin.domain.bpm.usecase.bnpl.SetBnplInvoiceState;
import mn.erin.domain.bpm.usecase.bnpl.SetInvoiceStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static consumption.util.CamundaUtils.toTransaction;
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
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_NUM;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;

/**
 * @author Odgavaa
 */

public class RefundTransactionTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RefundTransactionTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;
  private final BnplCoreBankingService bnplCoreBankingService;

  public RefundTransactionTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, ProcessRequestRepository processRequestRepository,
      BnplCoreBankingService bnplCoreBankingService)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.processRequestRepository = processRequestRepository;
    this.bnplCoreBankingService = bnplCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String accountId = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
    String disburseAmountString = String.valueOf(execution.getVariable(INVOICE_AMOUNT_75));
    String invoiceNum = String.valueOf(execution.getVariable(INVOICE_NUM));
    BigDecimal disburseAmount = removeCommaAndGetBigDecimal(disburseAmountString);
    String defaultAccount = String.valueOf(execution.getVariable("defaultAccount"));
    try
    {
      Map<String, Object> requestBody = toTransaction(execution, defaultAccount, accountId, disburseAmount, disburseAmount, "refundTransaction");
      requestBody.put(PROCESS_REQUEST_ID, requestId);
      requestBody.put(PHONE_NUMBER, execution.getVariable(PHONE_NUMBER));
      final Map<String, Object> response = directOnlineCoreBankingService.addTransaction(requestBody);
      if (response.get(STATUS).equals(FAILURE))
      {
        setLien(execution, accountId, disburseAmount);
        String errorMessage = String.valueOf(response.get(ERROR_MESSAGE));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        LOGGER.info(BNPL_LOG + "FAILED TRANSACT REFUND TO DEFAULT ACCOUNT. REQUEST ID = [{}]", requestId);
        if (errorMessage == null || errorMessage.equals(NULL_STRING))
        {
          execution.setVariable(ERROR_CAUSE, "Failed to transfer refund to default account!");
          throw new BpmnError("Transfer refund to default account", "Failed to transfer refund to default account!");
        }
        throw new BpmnError("Transfer refund to default account", errorMessage);
      }
      else if (response.get(STATUS).equals(SUCCESS))
      {
        SetInvoiceStateInput invoiceStateInput = new SetInvoiceStateInput(invoiceNum, "CONFIRMED");
        SetBnplInvoiceState invoiceState = new SetBnplInvoiceState(bnplCoreBankingService);
        invoiceState.execute(invoiceStateInput);
        LOGGER.info(BNPL_LOG + "SUCCESSFULLY TRANSACT TO DEFAULT ACCOUNT. REQUEST ID = [{}]", requestId);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        setLien(execution, accountId, disburseAmount);
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (e.getMessage() == null)
      {
        execution.setVariable(ERROR_CAUSE, String.valueOf(e));
        throw new BpmnError("Transfer refund to default account", String.valueOf(e));
      }
      throw new BpmnError("Transfer refund to default account", e.getMessage());
    }
  }

  private void addAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount) throws UseCaseException
  {
    AddAccLien addAccLien = new AddAccLien(directOnlineCoreBankingService);
    Map<String, Object> requestAddLien = new HashMap<>();
    requestAddLien.put(ACCT_ID, accountId);
    requestAddLien.put(MODULE_TYPE, ULIEN);
    requestAddLien.put(NEW_LIEN_AMT, String.valueOf(disburseAmount));
    requestAddLien.put(NEW_LIEN_AMT_CURRENCY_CODE, CURRENCY_MNT);
    requestAddLien.put(START_DT, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    requestAddLien.put(REASON_CODE, "999");
    requestAddLien.put(XAC_RMKS, "");
    requestAddLien.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    requestAddLien.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    Map<String, Object> addAcc = addAccLien.execute(requestAddLien);

    if (addAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED);
    }
    else if (addAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED);
    }
  }

  private void modifyAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount) throws UseCaseException
  {
    Map<String, Object> requestModifyLien = new HashMap<>();
    requestModifyLien.put(NEW_LIEN_AMT, String.valueOf(disburseAmount));
    requestModifyLien.put(ACCT_ID, accountId);
    requestModifyLien.put(LIEN_ID, "");
    requestModifyLien.put(MODULE_TYPE, ULIEN);
    requestModifyLien.put(NEW_LIEN_AMT_CURRENCY_CODE, CURRENCY_MNT);
    requestModifyLien.put(REASON_CODE, "999");
    requestModifyLien.put(XAC_RMKS, "");
    requestModifyLien.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
    requestModifyLien.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
    ModifyAccLien modifyAccLien = new ModifyAccLien(directOnlineCoreBankingService);

    Map<String, Object> modifyAcc = modifyAccLien.execute(requestModifyLien);
    if (modifyAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED);
    }
    else if (modifyAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED);
    }
  }

  private void updateRequestState(DelegateExecution execution, ProcessRequestState state) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, state);
    UpdateRequestStateOutput output = updateRequestState.execute(input);
    boolean isStateUpdated = output.isUpdated();
    if (isStateUpdated)
    {
      LOGGER.info(BNPL_LOG + "Updated process request state to {} with request id [{}].", fromEnumToString(state), processRequestId);
    }
  }

  private void setLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount)
      throws UseCaseException
  {
    try
    {
      GetAccLien getAccLien = new GetAccLien(directOnlineCoreBankingService);
      Map<String, String> inputParam = new HashMap<>();
      inputParam.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      inputParam.put(CamundaVariableConstants.PHONE_NUMBER, String.valueOf(execution.getVariable(CamundaVariableConstants.PHONE_NUMBER)));
      inputParam.put(ACCOUNT_ID, accountId);
      inputParam.put(MODULE_TYPE, ULIEN);
      Map<String, Object> result = getAccLien.execute(inputParam);
      if (result.get(STATUS).equals(FAILURE))
      {
        addAccLien(execution, accountId, disburseAmount);
      }
      else if (result.get(STATUS).equals(SUCCESS))
      {
        modifyAccLien(execution, accountId, disburseAmount);
      }
    }
    catch (Exception e)
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED);
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (e.getMessage() == null)
      {
        throw new BpmnError("Transfer refund to default account", String.valueOf(e));
      }
      throw new BpmnError("Transfer refund to default account", e.getMessage());
    }
  }
}
