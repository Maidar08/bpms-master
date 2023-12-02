/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutions;
import mn.erin.domain.bpm.usecase.execution.GetEnabledExecutionsInput;
import mn.erin.domain.bpm.util.process.TaskUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;

/**
 * @author Tamir
 */
public class GetActiveTaskByInstanceId implements UseCase<String, GetActiveTaskByInstanceIdOutput>
{
  private final TaskService taskService;
  private final RuntimeService runtimeService;
  private final ExecutionService executionService;

  public GetActiveTaskByInstanceId(TaskService taskService, RuntimeService runtimeService, ExecutionService executionService)
  {
    this.taskService = Objects.requireNonNull(taskService, "Task service is required!");
    this.runtimeService = Objects.requireNonNull(runtimeService, "Runtime service is required!");
    this.executionService = Objects.requireNonNull(executionService, "Execution service is required!");
  }

  @Override
  public GetActiveTaskByInstanceIdOutput execute(String caseInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    List<Task> activeTasks = taskService.getActiveByCaseInstanceId(caseInstanceId);
    List<Task> filteredActiveTasks = TaskUtils.filterByTaskName(activeTasks);

    suspendDuplicatedTask(filteredActiveTasks, activeTasks);
    checkInEnabledTask(filteredActiveTasks, caseInstanceId);

    return new GetActiveTaskByInstanceIdOutput(filteredActiveTasks);
  }

  private void suspendDuplicatedTask(List<Task> filteredTaskList, List<Task> taskList)
  {
    for (Task filteredTask : filteredTaskList)
    {
      String definitionKey = filteredTask.getDefinitionKey();
      String processInstanceId = filteredTask.getProcessInstanceId();
      for (Task task : taskList)
      {
        if (task.getDefinitionKey().equals(definitionKey) && !task.getProcessInstanceId().equals(processInstanceId))
        {
          runtimeService.suspendProcess(task.getProcessInstanceId());
        }
      }
    }
  }

  private void checkInEnabledTask(List<Task> filteredTaskList, String caseInstanceId) throws UseCaseException
  {

    GetEnabledExecutions getEnabledExecutions = new GetEnabledExecutions(executionService);
    List<Execution> executions = getEnabledExecutions.execute(new GetEnabledExecutionsInput(caseInstanceId)).getExecutions();
    List<Task> suspendedTasks = new ArrayList<>();
    for (Execution enabled : executions)
    {
      String activityName = enabled.getActivityName();
      for (Task task : filteredTaskList)
      {
        if (task.getName().equals(activityName))
        {
          suspendedTasks.add(task);
        }
      }
    }

    for (Task task : suspendedTasks)
    {
      filteredTaskList.remove(task);
      // TODO : discuss with who implement following code.
      runtimeService.suspendProcess(task.getProcessInstanceId());
    }
  }
}
