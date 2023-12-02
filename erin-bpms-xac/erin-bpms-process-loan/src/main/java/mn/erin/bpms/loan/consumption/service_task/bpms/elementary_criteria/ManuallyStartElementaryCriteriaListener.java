package mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria;

import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_COMPLETED_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.IS_LOAN_ACCOUNT_CREATE;

/**
 * @author Tamir
 */
public class ManuallyStartElementaryCriteriaListener implements TaskListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManuallyStartElementaryCriteriaListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    String caseInstanceId = delegateTask.getCaseInstanceId();
    CaseService caseService = delegateTask.getProcessEngine().getCaseService();

    DelegateExecution execution = delegateTask.getExecution();

    List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .enabled()
        .list();

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String activityId = enabledExecution.getActivityId();

      // elementary criteria execution activity id.
      if (ACTIVITY_ID_ELEMENTARY_CRITERIA.equalsIgnoreCase(activityId))
      {
        LOGGER.info("############ MANUALLY STARTS ELEMENTARY CRITERIA EXECUTION WITH CASE INSTANCE ID = [{}]", caseInstanceId);
        caseService.manuallyStartCaseExecution(enabledExecution.getId());
        LOGGER.info("############ SUCCESSFUL MANUALLY STARTED ELEMENTARY CRITERIA EXECUTION WITH CASE INSTANCE ID = [{}]", caseInstanceId);

      }
    }
    // is completed elementary criteria variable to false value, this related to enable XYP, MONGOL BANK executions.
    execution.setVariable(IS_COMPLETED_ELEMENTARY_CRITERIA, false);
    execution.setVariable(IS_LOAN_ACCOUNT_CREATE, false);
  }
}
