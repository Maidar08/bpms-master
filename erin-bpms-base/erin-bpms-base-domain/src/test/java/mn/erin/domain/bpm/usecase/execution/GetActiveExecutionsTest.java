package mn.erin.domain.bpm.usecase.execution;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.ActivityId;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.cases.InstanceId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;


/**
 * @author Bilguunbor
 */
public class GetActiveExecutionsTest
{
  private static final String INSTANCE_ID = "1";

  private ExecutionService executionService;
  private GetActiveExecutions useCase;

  @Before
  public void setUp()
  {
    executionService = Mockito.mock(ExecutionService.class);
    useCase = new GetActiveExecutions(executionService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetActiveExecutions(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_blank() throws UseCaseException
  {
    useCase.execute(" ");
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(executionService.getActiveByInstanceId(INSTANCE_ID)).thenThrow(BpmServiceException.class);
    useCase.execute(INSTANCE_ID);
  }

  @Test
  public void when_found_active_execution() throws BpmServiceException, UseCaseException
  {
    Execution execution = new Execution(ExecutionId.valueOf("a1"), InstanceId.valueOf("b1"), ActivityId.valueOf("c1"), new TaskId("d1"));

    Mockito.when(executionService.getActiveByInstanceId(INSTANCE_ID)).thenReturn(Collections.singletonList(execution));
    GetActiveExecutionsOutput output = useCase.execute(INSTANCE_ID);

    Execution resultExecution = output.getExecutions().get(0);

    String executionId = resultExecution.getId().getId();
    String taskId = resultExecution.getTaskId().getId();

    Assert.assertNotNull(output.getExecutions());
    Assert.assertEquals("a1", executionId);
    Assert.assertEquals("d1", taskId);
  }
}
