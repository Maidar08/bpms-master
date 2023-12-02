package listener.billing.custom;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_AMOUNT;


public class SetCustomPayAmountListener implements TaskListener
{

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();
    Map<String, Object> caseVariables = execution.getVariables();

    if(execution.getVariable("action").equals("toChildTask")){
      String paymentAmount = (String) caseVariables.get(PAYMENT_AMOUNT);
      execution.setVariable(PAY_AMOUNT, paymentAmount);
    }
  }
}
