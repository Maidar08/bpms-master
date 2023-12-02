package mn.erin.bpms.loan.consumption.task_listener;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.STAGE_ACTIVITY_ID_CALCULATE_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class TerminateLoanCalculationStageListener implements TaskListener
{
  private static final Logger LOG = LoggerFactory.getLogger(TerminateLoanCalculationStageListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    String caseInstanceId = (String) delegateTask.getVariable(CASE_INSTANCE_ID);
    String requestId = (String) delegateTask.getVariable(PROCESS_REQUEST_ID);

    ProcessEngine processEngine = delegateTask.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    List<CaseExecution> activeExecutions = CaseExecutionUtils.getActiveExecutions(caseInstanceId, processEngine);

    if (!activeExecutions.isEmpty())
    {
      for (CaseExecution activeExecution : activeExecutions)
      {
        String activityId = activeExecution.getActivityId();

        if (STAGE_ACTIVITY_ID_CALCULATE_LOAN_AMOUNT.equals(activityId))
        {
          LOG.info("########## TERMINATES CALCULATE LOAN AMOUNT STAGE AFTER SALARY CALCULATION WITH REQUEST ID = [{}]", requestId);

          Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
          caseService.terminateCaseExecution(activeExecution.getId(), caseVariables);

          LOG.info("########## SUCCESSFUL TERMINATED CALCULATE LOAN AMOUNT STAGE AFTER SALARY CALCULATION WITH REQUEST ID = [{}]", requestId);
        }
      }
    }
  }
}
