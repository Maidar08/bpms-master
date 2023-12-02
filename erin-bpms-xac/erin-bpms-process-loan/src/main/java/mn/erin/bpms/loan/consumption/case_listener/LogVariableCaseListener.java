package mn.erin.bpms.loan.consumption.case_listener;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

public class LogVariableCaseListener implements CaseExecutionListener
{
  @Override
  public void notify(DelegateCaseExecution delegateCaseExecution) throws Exception
  {
    // This Listener dedicated to debugging and check variables.
    delegateCaseExecution.getVariables();
  }
}
