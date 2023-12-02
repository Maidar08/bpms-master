package mn.erin.domain.bpm.usecase.execution;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

import static org.mockito.Mockito.doThrow;

/**
 * @author Bilguunbor
 */
public class CompleteExecutionByActivityIdTest
{
  private static final String INSTANCE_ID = "1";
  private static final String ACTIVITY_ID = "2";

  private CompleteExecutionByActivityId useCase;
  private CompleteExecutionByActivityIdInput input;
  private CaseService caseService;

  @Before
  public void setUp()
  {
    caseService = Mockito.mock(CaseService.class);
    useCase = new CompleteExecutionByActivityId(caseService);
    input = new CompleteExecutionByActivityIdInput(INSTANCE_ID, ACTIVITY_ID);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new CompleteExecutionByActivityId(null);
  }

  @Test
  public void when_completed_execution_by_activity_id() throws UseCaseException, BpmServiceException
  {
    useCase.execute(input);
    Mockito.verify(caseService, Mockito.atLeastOnce()).completeByActivityId(INSTANCE_ID, ACTIVITY_ID);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    doThrow(BpmServiceException.class).when(caseService).completeByActivityId(INSTANCE_ID, ACTIVITY_ID);
    useCase.execute(input);
  }
}
