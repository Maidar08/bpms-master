/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.ActivityId;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.cases.ExecutionType;
import mn.erin.domain.bpm.model.cases.InstanceId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

/**
 * @author Tamir
 */
public class GetCompletedExecutionsTest
{
  private static final String INSTANCE_ID = "1";
  private static final String ACTIVITY_NAME_VALUE = "Download info";

  private ExecutionService executionService;
  private GetCompletedExecutions useCase;

  @Before
  public void setUp()
  {
    executionService = Mockito.mock(ExecutionService.class);
    useCase = new GetCompletedExecutions(executionService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new GetCompletedExecutions(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void throw_service_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(executionService.getCompletedByInstanceId(INSTANCE_ID)).thenThrow(BpmServiceException.class);

    useCase.execute(INSTANCE_ID);
  }

  @Test
  public void when_found_completed_executions() throws UseCaseException, BpmServiceException
  {
    Execution execution = createExecution("e1", "i1", "a1", "t1", ACTIVITY_NAME_VALUE);

    Mockito.when(executionService.getCompletedByInstanceId(INSTANCE_ID)).thenReturn(Arrays.asList(execution));

    GetCompletedExecutionsOutput output = useCase.execute(INSTANCE_ID);
    Execution resultExecution = output.getExecutions().get(0);

    String executionId = resultExecution.getId().getId();
    String taskId = resultExecution.getTaskId().getId();

    Assert.assertNotNull(output.getExecutions());
    Assert.assertEquals("e1", executionId);
    Assert.assertEquals("t1", taskId);
  }

  @Test
  public void when_filtering_from_duplicated_activity() throws UseCaseException, BpmServiceException
  {
    Execution execution = createExecution("e1", "i1", "a1", "t1", ACTIVITY_NAME_VALUE);
    Execution execution2 = createExecution("e2", "i2", "a2", "t2", ACTIVITY_NAME_VALUE);

    Mockito.when(executionService.getCompletedByInstanceId(INSTANCE_ID)).thenReturn(Arrays.asList(execution, execution2));

    GetCompletedExecutionsOutput output = useCase.execute(INSTANCE_ID);
    Execution resultExecution = output.getExecutions().get(0);

    String executionId = resultExecution.getId().getId();
    String taskId = resultExecution.getTaskId().getId();

    Assert.assertNotNull(output.getExecutions());
    Assert.assertEquals(1, output.getExecutions().size());

    Assert.assertEquals("e1", executionId);
    Assert.assertEquals("t1", taskId);
    Assert.assertEquals(ACTIVITY_NAME_VALUE, resultExecution.getActivityName());
  }

  private Execution createExecution(String executionId, String instanceId, String activityId, String taskId, String activityName)
  {
    Execution execution = new Execution(ExecutionId.valueOf(executionId), InstanceId.valueOf(instanceId),
        ActivityId.valueOf(activityId), new TaskId(taskId));

    execution.setExecutionType(ExecutionType.COMPLETED);
    execution.setActivityName(activityName);
    execution.setActivityType("processTask");

    return execution;
  }
}
