package consumption.service_task_instant_loan;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static consumption.constant.CamundaVariableConstants.SUCCESS_STATUS;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACC_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT_CAPITAL_A;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TO_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_EXCG_RATE_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACC_CURRENCY_CC;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_LOAN_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class AddUnschLoanRepaymentTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AddUnschLoanRepaymentTask.class);
  private final NewCoreBankingService newCoreBankingService;

  public AddUnschLoanRepaymentTask(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "Banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String instanceId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    Map<String, Map<String, Object>> activeLoanAccountList = (Map<String, Map<String, Object>>) execution.getVariable(ACTIVE_LOAN_ACCOUNT_LIST);
    Map<String, Object> loanAccount = activeLoanAccountList.get(activeLoanAccountList.keySet().toArray()[activeLoanAccountList.size() - 1]);
    String accountId = getValidString(loanAccount.get("AccountID"));
    JSONObject requestParams = new JSONObject();
    requestParams.put(TRAN_TYPE, "T");
    requestParams.put(FROM_ACCOUNT_ID, execution.getVariable(CURRENT_ACCOUNT_NUMBER));
    requestParams.put(FROM_ACC_CURRENCY, getValidString(execution.getVariable(ACC_CURRENCY_CC)));
    requestParams.put(AMOUNT_CAPITAL_A, execution.getVariable(INTEREST_AMOUNT));
    requestParams.put(DESCRIPTION, MessageUtil.getMessageByLocale("instantLoan.xac.pay.intrest.description", "mn").getText());
    requestParams.put(ACC_ID, accountId);
    requestParams.put(TO_ACC_CURRENCY, getValidString(execution.getVariable(ACC_CURRENCY_CC)));
    requestParams.put(TRAN_CURRENCY, getValidString(execution.getVariable(ACC_CURRENCY_CC)));
    requestParams.put(TRANSACTION_EXCG_RATE_CODE, "");

    try
    {
      Map<String, Object> toLoanPayment = newCoreBankingService.addUnschLoanRepayment(instanceId, requestParams);
      String status = String.valueOf(toLoanPayment.get("status"));
      String transactionIdScheduled = String.valueOf(toLoanPayment.get("transactionIdScheduled"));
      String transactionIdUnscheduled = String.valueOf(toLoanPayment.get("transactionIdUnscheduled"));
      String appnum = String.valueOf(toLoanPayment.get("appnum"));
      String transactionDate1 = String.valueOf(toLoanPayment.get("trnDate1"));
      String transactionDate2 = String.valueOf(toLoanPayment.get("trnDate2"));
      updateTaskStatus(execution, "Add Unscheduled Interest Repayment", SUCCESS_STATUS);
      execution.setVariable("status", status);
      execution.setVariable("transactionIdScheduled", transactionIdScheduled);
      execution.setVariable("transactionIdUnscheduled", transactionIdUnscheduled);
      execution.setVariable("appnum", appnum);
      execution.setVariable("transactionDate1", transactionDate1);
      execution.setVariable("transactionDate2", transactionDate2);
    }
    catch (BpmServiceException e)
    {
      throw new BpmnError("RepaymentTransaction", getValidString(e.getMessage()).equals(EMPTY_VALUE) ? getValidString(e) : e.getMessage());
    }
    LOG.info("######################## Unscheduled loan repayment is successfully made with request Parameters = [{}], \n request id = [{}]. {}",
        requestParams, instanceId, (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
  }
}
