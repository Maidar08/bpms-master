package mn.erin.bpms.loan.consumption.service_task;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class CleanFieldsTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    int count = Integer.parseInt(getValidString(execution.getVariable("scoringCalculationCount")));
    if(count > 1) {
      execution.setVariable("term", null);
      execution.setVariable("interestRate", null);
      execution.setVariable("loanGrantDate", null);
      execution.setVariable("firstPaymentDate", null);
      execution.setVariable("grantLoanAmountString", null);
      execution.setVariable("debtIncomeBalanceString", null);
      execution.setVariable("loanApprovalAmount", null);
      execution.setVariable("fixedAcceptedLoanAmount", null);
      execution.setVariable("salaryAmountString", null);
    }
  }
}