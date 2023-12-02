package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_ELEMENTARY_CRITERIA;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableElementaryCriteriaListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(DisableElementaryCriteriaListener.class);

  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

    ProcessEngine processEngine = caseExecution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    try
    {
      List<CaseExecution> enabledExecutions = CaseExecutionUtils.getEnabledExecutions(caseInstanceId, processEngine);
      Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
      if (!enabledExecutions.isEmpty())
      {
        for (CaseExecution enabledExecution : enabledExecutions)
        {
          if (ACTIVITY_ID_ELEMENTARY_CRITERIA.equals(enabledExecution.getActivityId()))
          {
            LOG.info("############## DISABLES ELEMENTARY CRITERIA WITH REQUEST ID = [{}] ##########", requestId);
            caseService.disableCaseExecution(enabledExecution.getId(), caseVariables);
          }
        }
      }
    }
    catch (Exception e)
    {
      LOG.error("########## COULD NOT DISABLE AND RE-ENABLE ELEMENTARY CRITERIA WITH REQUEST ID = [{}] ##########", requestId);
      LOG.error(e.getMessage(), e);
    }
  }
}
