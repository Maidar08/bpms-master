package mn.erin.domain.bpm.usecase.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskService;

public class GetActiveTaskByInstanceIdTest
{
  private TaskService taskService;
  private RuntimeService runtimeService;
  private ExecutionService executionService;
  private GetActiveTaskByInstanceId useCase;

  @Before
  public void setUp()
  {
    taskService = Mockito.mock(TaskService.class);
    runtimeService = Mockito.mock(RuntimeService.class);
    executionService = Mockito.mock(ExecutionService.class);
    useCase = new GetActiveTaskByInstanceId(taskService, runtimeService, executionService);
  }

  @Test(expected = NullPointerException.class)
  public void when_task_service_is_null()
  {
    new GetActiveTaskByInstanceId(null, runtimeService, executionService);
  }

  @Test(expected = NullPointerException.class)
  public void when_run_time_service_is_null()
  {
    new GetActiveTaskByInstanceId(taskService, null, executionService);
  }

  @Test(expected = NullPointerException.class)
  public void when_execution_serivce_is_null()
  {
    new GetActiveTaskByInstanceId(taskService, runtimeService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_case_instance_id_is_blank() throws UseCaseException
  {
    useCase.execute("");
  }
}