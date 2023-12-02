package mn.erin.bpms.loan.consumption.service_task.co_borrower;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateCoBorrowerVariablesTest
{
  private static DelegateExecution execution;
  private static UpdateCoBorrowerVariablesTask updateCoBorrowerVariablesTask;

  private ProcessEngineServices processEngineServices;
  private CaseService caseService;

  @Before
  public void setUp()
  {
    execution = Mockito.mock(DelegateExecution.class);

    processEngineServices = Mockito.mock(ProcessEngineServices.class);
    caseService = Mockito.mock(CaseService.class);

    updateCoBorrowerVariablesTask = new UpdateCoBorrowerVariablesTask();
  }

  @Test(expected = NullPointerException.class)
  public void when_all_variables_null() throws Exception
  {
    Mockito.when(execution.getVariables()).thenReturn(null);
    Mockito.when(execution.getProcessEngineServices()).thenReturn(processEngineServices);
    Mockito.when(processEngineServices.getCaseService()).thenReturn(caseService);

    updateCoBorrowerVariablesTask.execute(execution);
  }
}
