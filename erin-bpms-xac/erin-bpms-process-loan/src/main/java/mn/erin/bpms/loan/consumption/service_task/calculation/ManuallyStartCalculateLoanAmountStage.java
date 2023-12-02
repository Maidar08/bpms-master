package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_CALCULATE_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_REMOVE_CO_BO;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.STAGE_ACTIVITY_ID_CALCULATE_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class ManuallyStartCalculateLoanAmountStage implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(ManuallyStartCalculateLoanAmountStage.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    ProcessEngine processEngine = execution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);
    boolean isEnabledCalculateLoanAmount = isEnabledCalculateAmount(enabledExecutions);

    if (!isEnabledCalculateLoanAmount)
    {
      // manually start calculation stage.
      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        if (STAGE_ACTIVITY_ID_CALCULATE_LOAN_AMOUNT.equals(activityId))
        {
          Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
          LOG.info("########## MANUALLY STARTS CALCULATE LOAN AMOUNT STAGE with REQUEST ID = [{}]", requestId);
          caseService.manuallyStartCaseExecution(enabledExecution.getId(), caseVariables);
        }
      }
    }
  }

  private boolean isEnabledCalculateAmount(List<CaseExecution> enabledExecutions)
  {

    if (enabledExecutions.isEmpty())
    {
      return false;
    }

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();

      if (ACTIVITY_ID_CALCULATE_LOAN_AMOUNT.equals(activityId)
          || ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_REMOVE_CO_BO.equals(activityId))
      {
        return true;
      }
    }
    return false;
  }
}
