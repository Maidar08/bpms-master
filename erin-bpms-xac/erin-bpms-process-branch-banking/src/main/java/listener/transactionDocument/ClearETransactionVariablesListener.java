package listener.transactionDocument;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_END_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_FORM_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_START_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;


public class ClearETransactionVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(ACCOUNT_ID, null);
    caseService.setVariable(instanceId, ACCOUNT_ID, null);

    execution.setVariable("transactionChannel", null);
    caseService.setVariable(instanceId, "transactionChannel", null);

    execution.setVariable(TRANSACTION_FORM_TYPE, null);
    caseService.setVariable(instanceId, TRANSACTION_FORM_TYPE, null);

    execution.setVariable(TRANSACTION_START_DATE, null);
    caseService.setVariable(instanceId, TRANSACTION_START_DATE, null);

    execution.setVariable(TRANSACTION_END_DATE, null);
    caseService.setVariable(instanceId, TRANSACTION_END_DATE, null);
  }
}
