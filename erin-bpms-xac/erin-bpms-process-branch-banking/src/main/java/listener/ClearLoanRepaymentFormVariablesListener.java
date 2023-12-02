package listener;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearLoanRepaymentFormVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable("accountId", null);
    caseService.setVariable(instanceId, "accountId", null);

    execution.setVariable("loanBalance", null);
    caseService.setVariable(instanceId, "loanBalance", null);

    execution.setVariable("currencyValue", null);
    caseService.setVariable(instanceId, "currencyValue", null);

    execution.setVariable("customerFullName", null);
    caseService.setVariable(instanceId, "customerFullName", null);

    execution.setVariable("basicPayment", null);
    caseService.setVariable(instanceId, "basicPayment", null);

    execution.setVariable("interestPayment", null);
    caseService.setVariable(instanceId, "interestPayment", null);

    execution.setVariable("penaltyAmount", null);
    caseService.setVariable(instanceId, "penaltyAmount", null);

    execution.setVariable("feePayment", null);
    caseService.setVariable(instanceId, "feePayment", null);

    execution.setVariable("totalAmount", null);
    caseService.setVariable(instanceId, "totalAmount", null);

    execution.setVariable("accountNumber", null);
    caseService.setVariable(instanceId, "accountNumber", null);

    execution.setVariable("payLoanAmount", null);
    caseService.setVariable(instanceId, "payLoanAmount", null);

    execution.setVariable("transactionDescription", null);
    caseService.setVariable(instanceId, "transactionDescription", null);

    execution.setVariable("fAccountCurrencyValue", null);
    caseService.setVariable(instanceId, "fAccountCurrencyValue", null);

    execution.setVariable("loanRepaymentType", null);
    caseService.setVariable(instanceId, "loanRepaymentType", null);

  }
}
