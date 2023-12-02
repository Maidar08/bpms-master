package mn.erin.bpms.loan.consumption.task_listener;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableElementaryCriteriaTaskListener implements TaskListener
{
  private static final Logger LOG = LoggerFactory.getLogger(DisableElementaryCriteriaTaskListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();
    String caseInstanceId = delegateTask.getCaseInstanceId();

    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    ProcessEngine processEngine = execution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    try
    {
      List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);
      Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

      if (!enabledExecutions.isEmpty())
      {
        for (CaseExecution enabledExecution : enabledExecutions)
        {
          if (ACTIVITY_ID_ELEMENTARY_CRITERIA.equals(enabledExecution.getActivityId()))
          {
            // Disables elementary criteria.
            LOG.info("########### DISABLES ELEMENTARY CRITERIA AFTER UPDATE CO-BORROWER TASK WITH REQUEST ID = [{}]", requestId);
            caseService.disableCaseExecution(enabledExecution.getId(), caseVariables);
          }
        }
      }
    }
    catch (Exception e)
    {
      LOG.error("########## COULD NOT DISABLE AND RE-ENABLE ELEMENTARY CRITERIA AFTER UPDATE CO-BORROWER TASK WITH REQUEST ID = [{}]", requestId);
      LOG.error(e.getMessage(), e);
    }
  }
}
