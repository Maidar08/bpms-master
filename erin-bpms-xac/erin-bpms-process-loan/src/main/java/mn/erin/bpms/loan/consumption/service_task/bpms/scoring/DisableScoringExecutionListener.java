package mn.erin.bpms.loan.consumption.service_task.bpms.scoring;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SCORING;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableScoringExecutionListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(DisableScoringExecutionListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

    ProcessEngine processEngine = caseExecution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);

    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    if (!enabledExecutions.isEmpty())
    {
      for (CaseExecution enabledExecution : enabledExecutions)
      {
        if (enabledExecution.getActivityId().equalsIgnoreCase(ACTIVITY_ID_SCORING))
        {
          try
          {
            // 1. DISABLES SCORING, BECAUSE CALCULATE INTEREST RATE STARTED
            LOG.info("############ DISABLES SCORING, BECAUSE CALCULATE INTEREST RATE STARTED WITH REQUEST ID = [{}]", requestId);
            caseService.disableCaseExecution(enabledExecution.getId(), caseVariables);
          }
          catch (Exception e)
          {
            LOG.error("############ COULD NOT DISABLE SCORING EXECUTION WITH REQUEST ID = [{}]", requestId);
          }
        }
      }
    }
  }
}
