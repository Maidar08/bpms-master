package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;
import mn.erin.domain.bpm.BpmModuleConstants;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST_USER_TASK_DEF_KEY;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableTasksAfterAccountCreationTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DisableTasksAfterAccountCreationTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    String accountNumber = (String) execution.getVariable(LOAN_ACCOUNT_NUMBER);

    //  disables needless enabled processes.
    if (null != accountNumber)
    {
      disableCoBorrowerExecutions(execution, requestId);
      disableCollateralExecutions(execution, caseInstanceId, requestId);

      CaseExecutionUtils.suspendActiveProcessInstancesByDefKey(caseInstanceId, execution, Arrays.asList(COLLATERAL_LIST_USER_TASK_DEF_KEY), true);
    }
  }

  private void disableCoBorrowerExecutions(DelegateExecution execution, String requestId)
  {
    ProcessEngine processEngine = execution.getProcessEngine();

    CaseService caseService = processEngine.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    if (null != caseInstanceId)
    {
      List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
          .caseInstanceId(caseInstanceId)
          .enabled()
          .list();

      for (CaseExecution enabledExecution : enabledExecutions)
      {
        String activityId = enabledExecution.getActivityId();

        // co-borrower processes.
        if (activityId.equalsIgnoreCase("PlanItem_1voieh9")
            || activityId.equalsIgnoreCase("PlanItem_0xpnjj9")
            || activityId.equalsIgnoreCase("PlanItem_1ouvsp3")
            || activityId.equalsIgnoreCase("process_task_mortgage_download_khur_coborrower")
            || activityId.equalsIgnoreCase("process_task_mortgage_mongol_bank_coborrower"))
        {
          LOGGER.info("################ DISABLES ENABLED CO-BORROWER TASK WITH ACTIVITY ID = [{}], with REQUEST ID = [{}]", activityId, requestId);
          caseService.disableCaseExecution(enabledExecution.getId());
          LOGGER.info("################ SUCCESSFUL DISABLED CO-BORROWER TASK WITH ACTIVITY ID = [{}], with REQUEST ID = [{}]", activityId, requestId);
        }
      }
    }
  }

  private void disableCollateralExecutions(DelegateExecution execution, String caseInstanceId, String requestId)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    List<CaseExecution> enabledExecutions = caseService.createCaseExecutionQuery()
        .caseInstanceId(caseInstanceId)
        .enabled()
        .list();

    for (CaseExecution enabledExecution : enabledExecutions)
    {
      String enabledActId = enabledExecution.getActivityId();

      if (enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_COLLATERAL_LIST)
          || enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_ACCOUNT_CREATION)
          || enabledActId.equals(BpmModuleConstants.ACTIVITY_ID_CREATE_COLLATERAL))
      {
        try
        {
          LOGGER.info("################ DISABLES ENABLED EXECUTION WITH ACTIVITY ID = [{}], with REQUEST ID = [{}]",
              enabledActId, requestId);
          caseService.disableCaseExecution(enabledExecution.getId());

          LOGGER.info("################ SUCCESSFUL DISABLED EXECUTION WITH ACTIVITY ID = [{}], with REQUEST ID = [{}]", enabledActId, requestId);
        }
        catch (Exception e)
        {
          LOGGER.error("##### COULD NOT DISABLE EXECUTION = [{}] with  REQUEST ID = [{}], REASON = [{}]", enabledActId, requestId, e.getMessage());
        }
      }
    }
  }
}
