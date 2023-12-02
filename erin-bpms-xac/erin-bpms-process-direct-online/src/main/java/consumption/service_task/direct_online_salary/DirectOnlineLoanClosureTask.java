package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.usecase.bnpl.AddAccLien;
import mn.erin.domain.bpm.usecase.bnpl.GetAccLien;
import mn.erin.domain.bpm.usecase.bnpl.GetAccLienInput;
import mn.erin.domain.bpm.usecase.bnpl.ModifyAccLien;
import mn.erin.domain.bpm.util.process.BpmUtils;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.constant.CamundaVariableConstants.VALUE_DATE;
import static consumption.util.CamundaUtils.getWsoValueDate;
import static consumption.util.CamundaUtils.updateTaskStatus;
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
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FAILED;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_ID;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_LOAN_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENCY_MNT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEFAULT_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.EXTEND;
import static mn.erin.domain.bpm.BpmModuleConstants.FAILED_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CLOSING_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_DESCRIPTION;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Lkhagvadorj.A
 **/

@SuppressWarnings("unchecked")
public class DirectOnlineLoanClosureTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineLoanClosureTask.class);
  private static final String DESCRIPTION = " тоот зээл хаав";
  private static final String TRAN_TYPE = "TranType";
  private static final String FROM_ACCOUNT_ID = "FromAccountID";
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;

  public DirectOnlineLoanClosureTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, ProcessRequestRepository processRequestRepository)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException, BpmServiceException
  {
    String logHash = ONLINE_SALARY_LOG_HASH;
    boolean isInstantLoan = false;
    if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      isInstantLoan = true;
      logHash = INSTANT_LOAN_LOG;
    }
    try
    {
      boolean hasActiveLoan = (boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT);
      List<Map<String, Object>> failedAccountList = new ArrayList<>();
      if (!hasActiveLoan)
      {
        execution.setVariable(FAILED_ACCOUNT_LIST, failedAccountList);
        Object cif = execution.getVariable(CIF_NUMBER);
        LOGGER.info("{} NO ACTIVE LOAN ACCOUNT HAS FOUND FOR CIF = [{}]. {}",logHash, cif,
            (isInstantLoan ? ". ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        return;
      }

      Map<String, Map<String, Object>> activeLoanAccountList = (Map<String, Map<String, Object>>) execution.getVariable(ACTIVE_LOAN_ACCOUNT_LIST);
      boolean isFailed = false;

      for (Map.Entry<String, Map<String, Object>> entry : activeLoanAccountList.entrySet())
      {
        Map<String, Object> account = entry.getValue();
        boolean isSuccess = false;
        if (!isFailed)
        {
          isSuccess = closeLoanAccount(account, failedAccountList, execution, isInstantLoan, logHash);
          if (!isSuccess)
          {
            isFailed = true;
          }
        }
        else
        {
          failedAccountList.add(account);
        }
      }

      if (isFailed)
      {
        execution.setVariable(FAILED_ACCOUNT_LIST, failedAccountList);
        if (!isInstantLoan)
        {
          final CaseService caseService = execution.getProcessEngine().getCaseService();
          final String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
          caseService.setVariable(instanceId, FAILED_ACCOUNT_LIST, failedAccountList);
        }
        throw new BpmnError("Loan Closure Task", "Failed to close account");
      }
      else
      {
        if (isInstantLoan)
          updateTaskStatus(execution, "Loan Closure task", "Success");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (isInstantLoan)
      {
        setLien(execution, (String) execution.getVariable("currentAccountNumber"), BpmUtils.toBigDecimal(execution.getVariable("clearBalance")), true, logHash);
        updateTaskStatus(execution, "Loan Closure task", FAILED);
      }
      throw new BpmnError("Loan Closure Task", getValidString(e.getMessage()).equals(EMPTY_VALUE) ? getValidString(e) : e.getMessage());
    }
  }

  private boolean closeLoanAccount(Map<String, Object> account, List<Map<String, Object>> failedAccountList, DelegateExecution execution, boolean isInstantLoan,
      String logHash)
  {
    String accountId = getStringValue(account, XAC_ACCOUNT_ID);
    BigDecimal closingLoanAmount = getClosingLoanAmount(account, execution.getVariable(ACTION_TYPE), isInstantLoan);
    if (closingLoanAmount.compareTo(BigDecimal.ZERO) == 0)
    {
      account.put(accountId, "closing loan amount is 0!");
      failedAccountList.add(account);
      LOGGER.info("{} FAILED TO CLOSE LOAN ACCOUNT. ACCOUNT ID = [{}] WITH AMOUNT  = [{}]. {}", logHash, accountId, closingLoanAmount,
          isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : "");
      return false;
    }

    JSONObject request = new JSONObject();
    //Эргэн төлөлт хийх дансны дугаар
    String fromAccountNumber = (String) execution.getVariable(DEFAULT_ACCOUNT);
    if (isInstantLoan) {
      fromAccountNumber = (String) execution.getVariable(BpmModuleConstants.CURRENT_ACCOUNT_NUMBER);
    }
    request.put(FROM_ACCOUNT_ID, fromAccountNumber);
    //Эргэн төлөлтийн төрөл (Бэлэн, бэлэн бус)
    request.put(TRAN_TYPE, "T");
    //Зээлийн дансны дугаар
    request.put(XAC_ACCOUNT_ID, accountId);
    request.put(VALUE_DATE, getWsoValueDate());
    //Гүйлгээний утга
    request.put(XAC_DESCRIPTION, accountId + DESCRIPTION);

    try
    {
      final Map<String, String> response = directOnlineCoreBankingService.setPayOffProc(request);
      String trnId = response.get(TRAN_ID);
      LOGGER.info("{} LOAN ACCOUNT HAS CLOSED. TRANSACTION ID = [{}], ACCOUNT ID = [{}] WITH AMOUNT  = [{}]. {}", logHash, trnId, accountId, closingLoanAmount,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      return true;
    }
    catch (Exception e)
    {
      account.put("error", e.getMessage());
      failedAccountList.add(account);
      LOGGER.info("{} FAILED TO CLOSE LOAN ACCOUNT. ACCOUNT ID = [{}] WITH AMOUNT  = [{}]. {}", logHash, accountId, closingLoanAmount,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      return false;
    }
  }

  private BigDecimal getClosingLoanAmount(Map<String, Object> accountLoanInfo, Object actionType, boolean isInstantLoan)
  {
    String closingLoanAmountString = String.valueOf(accountLoanInfo.get(XAC_CLOSING_LOAN_AMOUNT));
    if (isInstantLoan && actionType.equals(EXTEND))
    {
      closingLoanAmountString = String.valueOf(accountLoanInfo.get("ClearBalance"));
    }
    if (StringUtils.isBlank(closingLoanAmountString) || closingLoanAmountString.equals(NULL_STRING))
    {
      return BigDecimal.ZERO;
    }
    return (BigDecimal) accountLoanInfo.get(XAC_CLOSING_LOAN_AMOUNT);
  }

  private void addAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, boolean isInstantLoan, String logHash)
      throws UseCaseException
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
    requestAddLien.put(BpmModuleConstants.PHONE_NUMBER, String.valueOf(execution.getVariable(BpmModuleConstants.PHONE_NUMBER)));
    Map<String, Object> addAcc = addAccLien.execute(requestAddLien);

    if (addAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED, isInstantLoan, logHash);
      updateTaskStatus(execution, "Account block", SUCCESS);
      LOGGER.info("{} ADDED BLOCK TO LOAN ACCOUNT. ACCOUNT ID = [{}]. {}", logHash,accountId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
    else if (addAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, isInstantLoan, logHash);
      updateTaskStatus(execution, "Account block", FAILED);
      LOGGER.info("{} FAILED TO ADD BLOCK TO LOAN ACCOUNT. ACCOUNT ID = [{}]. {}", logHash, accountId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }

  private void modifyAccLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, boolean isInstantLoan, String logHash)
      throws UseCaseException
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
    requestModifyLien.put(BpmModuleConstants.PHONE_NUMBER, String.valueOf(execution.getVariable(BpmModuleConstants.PHONE_NUMBER)));
    ModifyAccLien modifyAccLien = new ModifyAccLien(directOnlineCoreBankingService);

    Map<String, Object> modifyAcc = modifyAccLien.execute(requestModifyLien);
    if (modifyAcc.get(STATUS).equals(SUCCESS))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED, isInstantLoan, logHash);
      updateTaskStatus(execution, "Account block", SUCCESS);
      LOGGER.info("{} CHANGED BLOCK OF LOAN ACCOUNT. ACCOUNT ID = [{}]. {}", logHash,accountId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
    else if (modifyAcc.get(STATUS).equals(FAILURE))
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, isInstantLoan, logHash);
      updateTaskStatus(execution, "Account block", FAILED);
      LOGGER.info("{} FAILED TO CHANGE BLOCK OF LOAN ACCOUNT. ACCOUNT ID = [{}].{}", logHash, accountId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }

  private void updateRequestState(DelegateExecution execution, ProcessRequestState state, boolean isInstantLoan, String logHash) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    boolean isStateUpdated = DigitalLoanUtils.updateRequestState(processRequestRepository, processRequestId, state);
    if (isStateUpdated)
    {
      LOGGER.info("{} Updated process request state to {} with request id [{}]. {}", logHash, fromEnumToString(state), processRequestId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }

  private void setLien(DelegateExecution execution, String accountId, BigDecimal disburseAmount, boolean isInstantLoan, String logHash)
      throws UseCaseException
  {
    try
    {
      GetAccLien getAccLien = new GetAccLien(directOnlineCoreBankingService);
      String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      Map<String, String> inputParam = new HashMap<>();
      inputParam.put(PROCESS_TYPE_ID, processTypeId);
      inputParam.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      inputParam.put(ACCOUNT_ID, accountId);
      inputParam.put(MODULE_TYPE, ULIEN);

      Map<String, Object> result = getAccLien.execute(inputParam);
      if (result.get(STATUS).equals(FAILURE))
      {
        addAccLien(execution, accountId, disburseAmount, isInstantLoan, logHash);
      }
      else if (result.get(STATUS).equals(SUCCESS))
      {
        modifyAccLien(execution, accountId, disburseAmount, isInstantLoan, logHash);
      }
    }
    catch (Exception e)
    {
      updateRequestState(execution, ProcessRequestState.AMOUNT_BLOCKED_FAILED, isInstantLoan, logHash);
      updateTaskStatus(execution, "Account block", FAILED);
      LOGGER.info("{} FAILED TO BLOCK LOAN ACCOUNT. ACCOUNT ID = [{}]. {}", logHash, accountId,
          isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : "");
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      throw new BpmnError("Failed to block loan account", getValidString(e.getMessage()).equals(EMPTY_VALUE) ? getValidString(e) : e.getMessage());
    }
  }
}
