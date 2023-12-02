package mn.erin.bpms.loan.consumption.service_task.bpms.elementary_criteria;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_ELEMENTARY_CRITERIA;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class EnableElementaryCriteriaListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(EnableElementaryCriteriaListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

    ProcessEngine processEngine = caseExecution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    try
    {

      List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);

      Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

      boolean isDisabled = false;
      String elementaryCriteriaExecId = null;

      if (!enabledExecutions.isEmpty())
      {
        for (CaseExecution enabledExecution : enabledExecutions)
        {
          if (enabledExecution.getActivityId().equalsIgnoreCase(ACTIVITY_ID_ELEMENTARY_CRITERIA))
          {
            // 1. Disables elementary criteria.
            elementaryCriteriaExecId = enabledExecution.getId();
            caseService.disableCaseExecution(elementaryCriteriaExecId, caseVariables);
            isDisabled = true;
          }
        }
      }

      // 2. Re-enables elementary criteria it prevents duplicated enabled execution.
      if (isDisabled && elementaryCriteriaExecId != null)
      {
        caseService.reenableCaseExecution(elementaryCriteriaExecId, caseVariables);
      }
    }
    catch (Exception e)
    {
      LOG.error("########## COULD NOT DISABLE AND RE-ENABLE ELEMENTARY CRITERIA WITH REQUEST ID = [{}]", requestId);
    }
  }
}
