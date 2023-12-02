package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class ManuallyStartRelatedTaskListener implements TaskListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManuallyStartRelatedTaskListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    CaseService caseService = delegateTask.getProcessEngine().getCaseService();

    String caseInstanceId = delegateTask.getCaseInstanceId();
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery().caseInstanceId(caseInstanceId).enabled().list();
    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();
      String[] activitiesId = new String[] { "PlanItem_0hv4otf", "process_task_loan_amount_2", "PlanItem_084motc" };
      if (Arrays.asList(activitiesId).contains(activityId))
      {
        switch (activityId)
        {
        case "PlanItem_0hv4otf":
          int salaryCalculationCount = Integer.parseInt(getValidString(caseVariables.get("salaryCalculationCount")));
          if (salaryCalculationCount > 1)
          {
            manualActivate(caseService, delegateTask, enabledExecution, caseVariables);
            LOGGER.info("############ ACTIVATED CONSUMPTION LOAN CALCULATION TASK.");
          }
          break;
        case "process_task_loan_amount_2":
          int businessCalculationCount = Integer.parseInt(getValidString(caseVariables.get("businessCalculationCount")));
          if (businessCalculationCount > 1)
          {
            manualActivate(caseService, delegateTask, enabledExecution, caseVariables);
            LOGGER.info("############  ACTIVATED MICRO LOAN CALCULATION TASK.");
          }
          break;
        case "PlanItem_084motc":
          int mortgageCalculationCount = Integer.parseInt(getValidString(caseVariables.get("mortgageCalculationCount")));
          if (mortgageCalculationCount > 1)
          {
            manualActivate(caseService, delegateTask, enabledExecution, caseVariables);
            LOGGER.info("############  ACTIVATED MORTGAGE LOAN CALCULATION TASK.");
          }
          break;
        default:
          break;
        }
      }

    if (activityId.equals("process_task_collateral_list") || activityId.equals("process_task_mortgage_collateral_list"))
      {
        String amountCalcCountString = getValidString(caseVariables.get("amountCalculationCount"));
        String microLoanCalculation = getValidString(caseVariables.get("businessCalculationCount"));
        String mortgageLoanCalculationCount = getValidString(caseVariables.get("mortgageCalculationCount"));
        if ((StringUtils.isNotBlank(amountCalcCountString) && Integer.parseInt(amountCalcCountString) > 1) ||
            (StringUtils.isNotBlank(microLoanCalculation) && Integer.parseInt(microLoanCalculation) > 1) ||
            (StringUtils.isNotBlank(mortgageLoanCalculationCount) && Integer.parseInt(mortgageLoanCalculationCount) > 1))
        {
          caseService.manuallyStartCaseExecution(enabledExecution.getId(), caseVariables);
          LOGGER.info("############  ACTIVATED  COLLATERAL LIST TASK.");
        }
      }
    }
  }

  private static void manualActivate(CaseService caseService, DelegateTask delegateTask, CaseExecution enabledExecution, Map<String, Object> caseVariables)
  {
    delegateTask.setVariable("reCalculated", true);
    caseService.manuallyStartCaseExecution(enabledExecution.getId(), caseVariables);
  }
}

