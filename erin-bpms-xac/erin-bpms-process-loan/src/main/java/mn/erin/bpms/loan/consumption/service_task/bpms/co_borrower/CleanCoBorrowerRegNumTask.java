package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class CleanCoBorrowerRegNumTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanCoBorrowerRegNumTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("########### Cleaning co-borrower register number field.");

    execution.removeVariable(REGISTER_NUMBER_CO_BORROWER);

    LOGGER.info("########### Successful cleaned up co-borrower register number.");
  }
}
