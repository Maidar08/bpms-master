package consumption.service_task_instant_loan;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static consumption.constant.CamundaVariableConstants.SUCCESS_STATUS;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACC_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT_CAPITAL_A;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TO_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_EXCG_RATE_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACC_CURRENCY_CC;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_LOAN_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class ScheduledLoanPaymentTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ScheduledLoanPaymentTask.class);

  private final NewCoreBankingService newCoreBankingService;
  public ScheduledLoanPaymentTask(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException, BpmServiceException
  {
    String instanceId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
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
      Map<String, Object> toScheduledLoanPayment = newCoreBankingService.makeScheduledLoanPayment(instanceId, requestParams);
      String transactionId = String.valueOf(toScheduledLoanPayment.get(TRANSACTION_ID));
      String transactionDate = String.valueOf(toScheduledLoanPayment.get(TRANSACTION_DATE));
      updateTaskStatus(execution, "Add Scheduled Interest Repayment", SUCCESS_STATUS);
      execution.setVariable(TRANSACTION_NUMBER, transactionId);
      execution.setVariable(PAID_DATE, transactionDate);
    }
    catch (BpmServiceException e)
    {
      throw new BpmnError("RepaymentTransaction", getValidString(e.getMessage()).equals(EMPTY_VALUE) ? getValidString(e) : e.getMessage());
    }
    LOG.info("######################## Scheduled loan repayment is successfully made with request Parameters = [{}], \n request id = [{}]",
        requestParams, instanceId);
  }
}

