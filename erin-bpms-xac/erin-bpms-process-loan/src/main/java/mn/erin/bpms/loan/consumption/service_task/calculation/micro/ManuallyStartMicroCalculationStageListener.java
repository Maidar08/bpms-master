package mn.erin.bpms.loan.consumption.service_task.calculation.micro;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_MICRO_LOAN_CALCULATION_STAGE;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getCaseVariables;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;


/**
 * @author Lkhagvadorj.A
 **/

public class ManuallyStartMicroCalculationStageListener implements CaseExecutionListener
{
  @Override
  public void notify(DelegateCaseExecution delegateCaseExecution) throws Exception
  {
    String caseInstanceId = delegateCaseExecution.getCaseInstanceId();
    ProcessEngine processEngine = delegateCaseExecution.getProcessEngine();
    CaseService caseService = delegateCaseExecution.getProcessEngine().getCaseService();

    List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);
    Map<String, Object> executionVariables = getCaseVariables(caseInstanceId, processEngine);

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();
      String executionId = enabledExecution.getId();

      if (activityId.equalsIgnoreCase(ACTIVITY_ID_MICRO_LOAN_CALCULATION_STAGE))
      {
        caseService.manuallyStartCaseExecution(executionId, executionVariables);
      }
    }
  }
}
