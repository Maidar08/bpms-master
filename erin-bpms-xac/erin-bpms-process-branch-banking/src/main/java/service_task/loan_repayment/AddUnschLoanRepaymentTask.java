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

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACC_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT_CAPITAL_A;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_VALUE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TO_ACC_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_EXCG_RATE_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static service_task.loan_repayment.ScheduledLoanPaymentTask.validateLoanPaymentAmount;

public class AddUnschLoanRepaymentTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AddUnschLoanRepaymentTask.class);
  private final BranchBankingService branchBankingService;

  public AddUnschLoanRepaymentTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String instanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    CaseService caseService = execution.getProcessEngine().getCaseService();

    validateLoanPaymentAmount(caseService, instanceId);

    JSONObject requestParams = new JSONObject();
    requestParams.put(TRAN_TYPE, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? "C" : "T");
    requestParams.put(FROM_ACCOUNT_ID, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? " " : caseService.getVariable(instanceId, ACCOUNT_NUMBER));
    requestParams.put(FROM_ACC_CURRENCY, getValidString(caseService.getVariable(instanceId, ACCOUNT_CURRENCY)));
    requestParams.put(AMOUNT_CAPITAL_A, caseService.getVariable(instanceId, PAY_LOAN_AMOUNT));
    requestParams.put(DESCRIPTION, getValidString(caseService.getVariable(instanceId, TRANSACTION_DESCRIPTION)));
    requestParams.put(ACC_ID, getValidString(caseService.getVariable(instanceId, ACCOUNT_ID)));
    requestParams.put(TO_ACC_CURRENCY, getValidString(caseService.getVariable(instanceId, CURRENCY_VALUE)));
    requestParams.put(TRAN_CURRENCY, getValidString(caseService.getVariable(instanceId, CURRENCY_VALUE)));
    requestParams.put(TRANSACTION_EXCG_RATE_CODE, getValidString(caseService.getVariable(instanceId, TRANSACTION_EXCG_RATE_CODE)));

    try
    {
      Map<String, Object> toLoanPayment = branchBankingService.addUnschLoanRepayment(instanceId, requestParams);
      String status = String.valueOf(toLoanPayment.get("status"));
      String transactionIdScheduled = String.valueOf(toLoanPayment.get("transactionIdScheduled"));
      String transactionIdUnscheduled = String.valueOf(toLoanPayment.get("transactionIdUnscheduled"));
      String appnum = String.valueOf(toLoanPayment.get("appnum"));
      String transactionDate1 = String.valueOf(toLoanPayment.get("trnDate1"));
      String transactionDate2 = String.valueOf(toLoanPayment.get("trnDate2"));

      execution.setVariable("status", status);
      caseService.setVariable(instanceId, "status", status);

      execution.setVariable("transactionIdScheduled", transactionIdScheduled);
      caseService.setVariable(instanceId, "transactionIdScheduled", transactionIdScheduled);

      execution.setVariable("transactionIdUnscheduled", transactionIdUnscheduled);
      caseService.setVariable(instanceId, "transactionIdUnscheduled", transactionIdUnscheduled);

      execution.setVariable("appnum", appnum);
      caseService.setVariable(instanceId, "appnum", appnum);

      execution.setVariable("transactionDate1", transactionDate1);
      caseService.setVariable(instanceId, "transactionDate1", transactionDate1);

      execution.setVariable("transactionDate2", transactionDate2);
      caseService.setVariable(instanceId, "transactionDate2", transactionDate2);

    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    LOG.info("######################## Unscheduled loan repayment is successfully made with request Parameters = [{}], \n process instance id = [{}]",
        requestParams,
        instanceId);
  }
}
