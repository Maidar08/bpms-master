package mn.erin.bpms.loan.consumption.service_task.bpms;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Tamir
 */
public class SetCaseInstanceIdListener implements TaskListener
{
  private static final Logger LOG = LoggerFactory.getLogger(SetCaseInstanceIdListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    CaseService caseService = delegateTask.getProcessEngine().getCaseService();
    String caseInstanceId = delegateTask.getCaseInstanceId();

    LOG.info("############# SET CASE INSTANCE ID LISTENER INVOKED AFTER CREATE TASK WITH TASK ID = [{}], TASK NAME = [{}], CASE INSTANCE ID = [{}]",
        delegateTask.getId(), delegateTask.getName(), caseInstanceId);

    DelegateExecution execution = delegateTask.getExecution();

    if (null != caseInstanceId)
    {
      LOG.info("############# Sets CASE INSTANCE ID for to execution variable.");
      execution.setVariable(CASE_INSTANCE_ID, caseInstanceId);

      LOG.info("############# Sets CASE INSTANCE ID for global case instance.");
      caseService.setVariable(caseInstanceId, CASE_INSTANCE_ID, caseInstanceId);
    }
  }
}
