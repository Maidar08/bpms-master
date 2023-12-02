package mn.erin.domain.bpm.usecase.execution;

import java.util.ArrayList;
import java.util.List;

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

public class GetEnabledExecutionsTest
{
  private static final String ACTIVITY_NAME = "calculateAmount";

  private GetEnabledExecutions useCase;
  private ExecutionService executionService;
  private GetEnabledExecutionsInput input;

  @Before
  public void setUp()
  {
    executionService = Mockito.mock(ExecutionService.class);
    useCase = new GetEnabledExecutions(executionService);
    input = new GetEnabledExecutionsInput("123");
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(executionService.getEnabledByInstanceId("123")).thenReturn(getEnabledExecutions());
    GetEnabledExecutionsOutput output = useCase.execute(input);

    Assert.assertEquals(2, output.getExecutions().size());
  }

//  Initial list size is 3. When it passes through getUniqueExecutions method it gets to 2.
  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(executionService.getEnabledByInstanceId("123")).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void verifyGetUniqueExecutions()
  {
    List<Execution> enabledExecutions = getEnabledExecutions();

    List<Execution> uniqueExecutions = useCase.getUniqueExecutions(enabledExecutions);

    Assert.assertEquals(2, uniqueExecutions.size());
  }

  private List<Execution> getEnabledExecutions()
  {
    List<Execution> enabledExecutions = new ArrayList<>();

    Execution execution = new Execution(ExecutionId.valueOf("e1"), InstanceId.valueOf("i1"), ActivityId.valueOf("a1"), TaskId.valueOf("t1"));
    execution.setActivityName(ACTIVITY_NAME);

    Execution execution1 = new Execution(ExecutionId.valueOf("e2"), InstanceId.valueOf("i2"), ActivityId.valueOf("a2"), TaskId.valueOf("t2"));
    execution1.setActivityName(ACTIVITY_NAME);

    Execution execution2 = new Execution(ExecutionId.valueOf("e3"), InstanceId.valueOf("i3"), ActivityId.valueOf("a3"), TaskId.valueOf("t3"));
    execution2.setActivityName(ACTIVITY_NAME + "Other");

    enabledExecutions.add(execution);
    enabledExecutions.add(execution1);
    enabledExecutions.add(execution2);

    return enabledExecutions;
  }
}
