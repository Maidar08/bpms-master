package mn.erin.bpms.loan.consumption.service_task.bpms.contract;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tamir
 */
public class LogContractPrepCompletedListener implements ExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(LogContractPrepCompletedListener.class);

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    LOG.info("############ Completed LOAN CONTRACT PREPARATION PROCESS.");
  }
}
