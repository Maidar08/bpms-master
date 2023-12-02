package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_CREATE_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_CREATE_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.ENABLE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_STARTED_COLL_ACCOUNT_STAGE;
import static mn.erin.bpms.loan.consumption.utils.ProcessUtils.suspendActiveProcess;
import static mn.erin.bpms.loan.consumption.utils.TaskUtils.getActiveTasks;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class TerminateCreateCollateralListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(TerminateCreateCollateralListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String caseInstanceId = caseExecution.getCaseInstanceId();

    List<CaseExecution> activeExecutions = CaseExecutionUtils.getActiveExecutions(caseInstanceId, caseExecution.getProcessEngine());

    terminateActiveCreateCollateralProcess(caseInstanceId, caseExecution, activeExecutions);

    Map<String, Object> executionVariables = caseExecution.getVariables();

    Set<Map.Entry<String, Object>> variableEntries = executionVariables.entrySet();

    for (Map.Entry<String, Object> variableEntry : variableEntries)

    {
      if (variableEntry.getKey().equals(IS_STARTED_COLL_ACCOUNT_STAGE))
      {
        boolean isStartedCollAccStage = (boolean) variableEntry.getValue();
        if (isStartedCollAccStage)
        {
          caseExecution.setVariable(ENABLE_LOAN_AMOUNT, true);
        }
      }
    }
  }

  private void terminateActiveCreateCollateralProcess(String caseInstanceId, DelegateCaseExecution caseExecution,
      List<CaseExecution> activeExecutions)
  {
    CaseService caseService = caseExecution.getProcessEngineServices().getCaseService();
    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);

    for (CaseExecution activeExecution : activeExecutions)
    {
      String activityId = activeExecution.getActivityId();

      if (activityId.equals(ACTIVITY_ID_CREATE_COLLATERAL))
      {
        try
        {
          caseService.terminateCaseExecution(activeExecution.getId());

          suspendCreateCollateralProcess(caseExecution, caseInstanceId, requestId);
        }
        catch (Exception e)
        {
          LOG.error("COULD NOT TERMINATE CREATE COLLATERAL PROCESS WITH REQUEST ID = [{}],"
              + " REASON = [{}]", requestId, e.getMessage());
        }
      }
    }
  }

  private void suspendCreateCollateralProcess(DelegateCaseExecution caseExecution, String caseInstanceId, String requestId)
  {
    List<Task> activeTasks = getActiveTasks(caseExecution, caseInstanceId);

    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getTaskDefinitionKey();

      if (taskDefinitionKey.equals(TASK_DEF_CREATE_COLLATERAL))
      {
        suspendActiveProcess(caseExecution, activeTask, requestId);
      }
    }
  }
}
