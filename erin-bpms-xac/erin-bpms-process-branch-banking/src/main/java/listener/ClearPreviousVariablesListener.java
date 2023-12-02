package listener;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.LOAN_REPAYMENT_PROCESS_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.LOAN_REPAYMENT_VARIABLES_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearPreviousVariablesListener implements ExecutionListener
{

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();

    String processId = ((ExecutionEntity) execution).getProcessDefinition().getKey();
    String[] variablesName = {};
    if (LOAN_REPAYMENT_PROCESS_ID.equals(processId))
    {
      variablesName = LOAN_REPAYMENT_VARIABLES_NAME;
    }
    for (String variableName : variablesName)
    {
      execution.setVariable(variableName, null);
      caseService.setVariable(caseInstanceId, variableName, null);
    }
  }
}
