package service_task.loan_repayment;

import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_BALANCE_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_BALANCE_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.PAY_LOAN_AMOUNT_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.PAY_LOAN_AMOUNT_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_HAS_ACCESS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACC_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT_CAPITAL_A;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BLNC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_VALUE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.LOAN_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.LOAN_REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.NON_CASH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TOTAL_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TO_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Sukhbat
 */
public class ScheduledLoanPaymentTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ScheduledLoanPaymentTask.class);
  private final BranchBankingService branchBankingService;

  public ScheduledLoanPaymentTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws UseCaseException, BpmServiceException
  {
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    CaseService caseService = execution.getProcessEngine().getCaseService();

    validateLoanPaymentAmount(caseService, instanceId);

    JSONObject requestParams = new JSONObject();
    requestParams.put(TRAN_TYPE, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? "C" : "T");
    requestParams.put(FROM_ACCOUNT_ID, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? " " : caseService.getVariable(instanceId, ACCOUNT_NUMBER));
    requestParams.put(FROM_ACC_CURRENCY, getValidString(caseService.getVariable(instanceId, ACCOUNT_CURRENCY)));
    requestParams.put(AMOUNT_CAPITAL_A, caseService.getVariable(instanceId, PAY_LOAN_AMOUNT));
    requestParams.put(DESCRIPTION, getValidString(caseService.getVariable(instanceId, TRANSACTION_DESCRIPTION)));
    requestParams.put(ACC_ID, getValidString(caseService.getVariable(instanceId, ACCOUNT_ID)));
    requestParams.put(TRAN_CURRENCY, getValidString(caseService.getVariable(instanceId, CURRENCY_VALUE)));
    requestParams.put(TO_ACC_CURRENCY, getValidString(caseService.getVariable(instanceId, CURRENCY_VALUE)));

    try
    {
      Map<String, Object> toScheduledLoanPayment = branchBankingService.makeScheduledLoanPayment(instanceId, requestParams);
      String transactionId = String.valueOf(toScheduledLoanPayment.get(TRANSACTION_ID));
      String transactionDate = String.valueOf(toScheduledLoanPayment.get(TRANSACTION_DATE));
      execution.setVariable(TRANSACTION_NUMBER, transactionId);
      caseService.setVariable(instanceId, TRANSACTION_NUMBER, transactionId);
      execution.setVariable(PAID_DATE, transactionDate);
      caseService.setVariable(instanceId, PAID_DATE, transactionDate);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    LOG.info("######################## Scheduled loan repayment is successfully made with request Parameters = [{}], \n process instance id = [{}]",
        requestParams,
        instanceId);
  }

  public static void validateLoanPaymentAmount(CaseService caseService, String instanceId) throws BpmServiceException
  {
    String accountHasAccess = getValidString(caseService.getVariable(instanceId, ACCOUNT_HAS_ACCESS));
    String accountBalanceString = accountHasAccess.equals("0") ? String.valueOf(caseService.getVariable(instanceId, BLNC)) :
        String.valueOf(caseService.getVariable(instanceId, ACCOUNT_BALANCE));
    double accountBalance = Double.parseDouble(accountBalanceString);
    double loanBalance = Double.parseDouble(String.valueOf(caseService.getVariable(instanceId, LOAN_BALANCE)));
    double totalAmount = Double.parseDouble(String.valueOf(caseService.getVariable(instanceId, TOTAL_AMOUNT)));
    double payLoanAmount = Double.parseDouble(String.valueOf(caseService.getVariable(instanceId, PAY_LOAN_AMOUNT)));

    String transactionType = String.valueOf(caseService.getVariable(instanceId, TRANSACTION_TYPE));
    String paymentType = String.valueOf(caseService.getVariable(instanceId, LOAN_REPAYMENT_TYPE));

    if ((loanBalance < payLoanAmount && paymentType.equals("Хуваарийн бус")) || (totalAmount < payLoanAmount && !paymentType.equals("Хуваарийн бус")))
    {
      throw new BpmServiceException(PAY_LOAN_AMOUNT_ERROR_CODE, PAY_LOAN_AMOUNT_ERROR_MESSAGE);
    }

    if (transactionType.equals(NON_CASH) && payLoanAmount > accountBalance)
    {
      throw new BpmServiceException(ACCOUNT_BALANCE_ERROR_CODE, ACCOUNT_BALANCE_ERROR_MESSAGE);
    }
  }
}

