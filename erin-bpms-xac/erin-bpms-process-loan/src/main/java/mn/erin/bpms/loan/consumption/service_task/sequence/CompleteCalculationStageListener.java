package mn.erin.bpms.loan.consumption.service_task.sequence;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CALCULATE_INTEREST_RATE;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CALCULATE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_SCORING;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_CALCULATE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.utils.ProcessUtils.suspendActiveProcess;
import static mn.erin.bpms.loan.consumption.utils.TaskUtils.getActiveTasks;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class CompleteCalculationStageListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(CompleteCalculationStageListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    LOG.info("###### Re-activates CALCULATION_STAGE ...");

    // enables loan amount calculation.
    caseExecution.setVariable(IS_CALCULATE_LOAN_AMOUNT, true);

    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

    LOG.info("############ CASE INSTANCE ID = [{}] of REQUEST = [{}]", caseInstanceId, requestId);

    if (null != caseInstanceId)
    {
      ProcessEngine processEngine = caseExecution.getProcessEngine();
      Map<String, Object> executionVariables = CaseExecutionUtils.getCaseVariables(caseInstanceId, processEngine);

      List<CaseExecution> activeExecutions = processEngine.getCaseService().createCaseExecutionQuery()
          .active()
          .caseInstanceId(caseInstanceId)
          .list();

      for (CaseExecution activeExecution : activeExecutions)
      {
        String activityId = activeExecution.getActivityId();

        LOG.info("######### ACTIVE EXECUTION ID WITH ACTIVITY NAME = [{}]", activeExecution.getActivityName());

        if (activityId.equalsIgnoreCase(ACTIVITY_ID_STAGE_CALCULATION))
        {
          LOG.info("######## TERMINATES CALCULATION STAGE WITH REQUEST_ID = [{}]", requestId);
          processEngine.getCaseService().terminateCaseExecution(activeExecution.getId(), executionVariables);
          LOG.info("######## SUCCESSFUL TERMINATED CALCULATION STAGE  REQUEST_ID =[{}]", requestId);
        }
      }

      List<String> calcStageTaskDefKeys = Arrays.asList(TASK_DEF_KEY_CALCULATE_INTEREST_RATE,
          TASK_DEF_KEY_SCORING, TASK_DEF_KEY_CALCULATE_LOAN_AMOUNT);

      suspendCalculateStageActiveProcesses(caseExecution, caseInstanceId, requestId, calcStageTaskDefKeys);
    }
  }

  private void suspendCalculateStageActiveProcesses(DelegateCaseExecution caseExecution, String caseInstanceId, String requestId,
      List<String> taskDefKeys)
  {

    List<Task> activeTasks = getActiveTasks(caseExecution, caseInstanceId);

    if (null != activeTasks && !activeTasks.isEmpty())
    {
      LOG.info("########### SUSPENDS ACTIVE PROCESSES OF CALCULATION STAGE.");

      for (Task activeTask : activeTasks)
      {
        String taskDefinitionKey = activeTask.getTaskDefinitionKey();

        if (taskDefKeys.contains(taskDefinitionKey))
        {
          suspendActiveProcess(caseExecution, activeTask, requestId);
        }
      }
    }
  }
}
