package mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SALARY_CALCULATION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.CO_BORROWER_FIELD_IDENTIFIER;

/**
 * @author Tamir
 */
public class ManuallyStartSalaryCalculationListener implements TaskListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManuallyStartSalaryCalculationListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    CaseService caseService = delegateTask.getProcessEngine().getCaseService();

    String caseInstanceId = delegateTask.getCaseInstanceId();
    Set<String> variableNames = delegateTask.getVariableNames();

    boolean isCriteriaWithCoBorrower = isCriteriaWithCoBorrower(variableNames);
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    if (isCriteriaWithCoBorrower)
    {
      LOGGER.info("############## ELEMENTARY CRITERIA TASK COMPLETED WITH CO-BORROWER.");
      delegateTask.setVariables(caseVariables);
    }

    else
    {

      // Activates salary calculation process task.
      LOGGER.info("############ MANUALLY ACTIVATES SALARY CALCULATION TASK, BECAUSE ELEMENTARY CRITERIA WITHOUT CO-BORROWER.");

      if (!StringUtils.isBlank(caseInstanceId))
      {
        List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
            .caseInstanceId(caseInstanceId)
            .enabled()
            .list();

        for (CaseExecution enabledExecution : enabledExecutions)
        {
          String activityId = enabledExecution.getActivityId();

          // salary calculation execution activity id.
          if (ACTIVITY_ID_SALARY_CALCULATION.equalsIgnoreCase(activityId))
          {
            caseService.manuallyStartCaseExecution(enabledExecution.getId(), caseVariables);
            LOGGER.info("############ SUCCESSFUL ACTIVATED SALARY CALCULATION TASK.");
          }
        }
      }
    }
  }

  private boolean isCriteriaWithCoBorrower(Set<String> variableNames)
  {
    for (String variableName : variableNames)
    {
      if (variableName.contains(CO_BORROWER_FIELD_IDENTIFIER))
      {
        return true;
      }
    }
    return false;
  }
}
