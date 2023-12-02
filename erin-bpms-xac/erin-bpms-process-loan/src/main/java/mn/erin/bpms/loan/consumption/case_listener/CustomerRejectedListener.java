package mn.erin.bpms.loan.consumption.case_listener;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerRejectedListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(CustomerRejectedListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    LOG.info("####### Customer rejected milestone occurred.");
  }
}
