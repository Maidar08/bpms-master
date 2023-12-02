package listener.account_reference;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.FEES_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearTransactionAmountListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(FEES_AMOUNT, null);
    caseService.setVariable(instanceId, FEES_AMOUNT, null);
  }
}
