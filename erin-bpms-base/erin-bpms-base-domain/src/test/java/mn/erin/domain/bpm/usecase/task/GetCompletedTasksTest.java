package mn.erin.domain.bpm.usecase.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

public class GetCompletedTasksTest
{
  private BpmsServiceRegistry bpmsServiceRegistry;
  private GetCompletedTasks useCase;

  @Before
  public void setUp()
  {
    bpmsServiceRegistry = Mockito.mock(BpmsServiceRegistry.class);
    useCase = new GetCompletedTasks(bpmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void when_bpms_service_registry_is_null()
  {
    new GetCompletedTasks(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_case_instance_id_is_blank() throws UseCaseException
  {
    useCase.execute("");
  }
}
