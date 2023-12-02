package listener;


import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearDepositContractVariables implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    String[]  clearVariables = {"contractType", "accountId", "cifNumber", "attachmentNumber"};

    for (String variableId : clearVariables){
      execution.setVariable(variableId, null);
      caseService.setVariable(instanceId, variableId, null);
    }
  }
}
