package mn.erin.bpms.loan.consumption.service_task.bpms;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tamir
 */
public class DisableDownloadEnquireListener implements TaskListener
{
  private static final String DISABLE_DOWNLOAD_ENQUIRE_PROCESS = "disableDownloadEnquireProcess";
  private static final Logger LOGGER = LoggerFactory.getLogger(DisableDownloadEnquireListener.class);

  @Override
  public void notify(DelegateTask delegateTask)
  {
    LOGGER.info("############ Disable download enquire process");

    DelegateExecution execution = delegateTask.getExecution();

    execution.setVariable(DISABLE_DOWNLOAD_ENQUIRE_PROCESS, true);

  }
}
