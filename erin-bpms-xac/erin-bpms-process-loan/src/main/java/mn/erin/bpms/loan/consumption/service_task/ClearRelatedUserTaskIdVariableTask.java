package mn.erin.bpms.loan.consumption.service_task;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.RELATED_USER_TASK_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class ClearRelatedUserTaskIdVariableTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (execution.hasVariable(RELATED_USER_TASK_ID))
    {
      CaseService caseService = execution.getProcessEngine().getCaseService();
      String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      execution.setVariable(RELATED_USER_TASK_ID, null);
      caseService.setVariable(instanceId, RELATED_USER_TASK_ID, null);
    }
  }
}
