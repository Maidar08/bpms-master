package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getEnabledExecutions;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DisableEnabledExecutionsListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(DisableEnabledExecutionsListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);

    ProcessEngine processEngine = caseExecution.getProcessEngine();
    CaseService caseService = processEngine.getCaseService();

    List<CaseExecution> enabledExecutions = getEnabledExecutions(caseInstanceId, processEngine);

    if (null != enabledExecutions)
    {
      for (CaseExecution enabledExecution : enabledExecutions)
      {
        try
        {
          String executionId = enabledExecution.getId();

          if (enabledExecution.isEnabled() && !enabledExecution.isDisabled())
          {
            LOG.info("########### DISABLES ENABLED EXECUTIONS BEFORE CREATE LOAN ACCOUNT PROCESS WITH REQUEST ID = [{}]", requestId);
            caseService.disableCaseExecution(executionId);
            LOG.info("########## SUCCESSFUL DISABLED EXECUTION WITH ACTIVITY NAME = [{}]", enabledExecution.getActivityName());
          }
        }
        catch (Exception e)
        {
          LOG.warn("############## COULD NOT DISABLE EXECUTIONS BEFORE CREATE LOAN ACCOUNT WITH REQUEST ID = [{}]", requestId);
        }
      }
    }
  }
}
