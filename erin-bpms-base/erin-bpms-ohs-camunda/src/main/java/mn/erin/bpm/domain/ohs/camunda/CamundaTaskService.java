/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.TaskService;

import static mn.erin.bpm.domain.ohs.camunda.util.CamundaEntityUtil.convertToTaskList;

/**
 * @author Tamir
 */
public class CamundaTaskService implements TaskService
{
  private static final String ACTIVE_TASK_STATUS = "active";
  private static final String USER_TASK = "userTask";
  private final ProcessEngineProvider processEngineProvider;

  public CamundaTaskService(ProcessEngineProvider processEngineProvider)
  {
    this.processEngineProvider = processEngineProvider;
  }

  @Override
  public List<Task> getActiveByCaseInstanceId(String caseInstanceId)
  {
    List<ProcessInstance> processInstances = getProcessInstances(caseInstanceId);
    List<Task> activeTasks = new ArrayList<>();

    // Checks process instances again.
    if (processInstances.isEmpty())
    {
      processInstances = getProcessInstances(caseInstanceId);
    }

    for (ProcessInstance processInstance : processInstances)
    {
      String processInstanceId = processInstance.getId();

      List<org.camunda.bpm.engine.task.Task> camundaTaskList = getActiveTasks(caseInstanceId, processInstanceId);

      if (camundaTaskList.isEmpty())
      {
        // Checks active tasks again.
        camundaTaskList = getActiveTasks(caseInstanceId, processInstanceId);
      }

      for (org.camunda.bpm.engine.task.Task camundaTask : camundaTaskList)
      {
        String taskId = camundaTask.getId();
        String name = camundaTask.getName();
        String executionId = camundaTask.getExecutionId();

        Task task = new Task(TaskId.valueOf(taskId), ExecutionId.valueOf(executionId), CaseInstanceId.valueOf(caseInstanceId), name);
        task.setStatus(ACTIVE_TASK_STATUS);
        task.setType(USER_TASK);
        task.setDefinitionKey(camundaTask.getTaskDefinitionKey());
        task.setProcessInstanceId(camundaTask.getProcessInstanceId());

        String description = camundaTask.getDescription();
        if (!StringUtils.isBlank(description))
        {
          task.setParentTaskId(description);
        }

        activeTasks.add(task);
      }
    }
    return activeTasks;
  }

  @Override
  public List<Task> getCompletedByCaseInstanceId(String caseInstanceId)
  {
    HistoryService historyService = processEngine().getHistoryService();

    List<HistoricTaskInstance> historicInstances = historyService.createHistoricTaskInstanceQuery()
        .caseInstanceId(caseInstanceId)
        .finished()
        .list();

    if (historicInstances.isEmpty())
    {
      return Collections.emptyList();
    }

    return convertToTaskList(historicInstances, caseInstanceId);
  }

  @Override
  public List<Task> getActiveTaskByProcessIdAndDefinitionKey(String processId, String definitionKey)
  {
    List<org.camunda.bpm.engine.task.Task> taskList = processEngine().getTaskService().createTaskQuery().processInstanceId(processId).active().list();
    List<Task> tasks = new ArrayList<>();
    for (org.camunda.bpm.engine.task.Task task : taskList)
    {
      if (task.getTaskDefinitionKey().equals(definitionKey))
      {
        Task activeTask = new Task(TaskId.valueOf(task.getId()), ExecutionId.valueOf(task.getExecutionId()), CaseInstanceId.valueOf("123"), task.getName());

        /* Put process definition id instead of definition key in order to achieve form fields from the task. */
        activeTask.setDefinitionKey(task.getProcessDefinitionId());
        tasks.add(activeTask);
      }
    }

    return tasks;
  }

  @Override
  public List<Task> getActiveTaskByProcessInstanceId(String processInstanceId)
  {
    return null;
  }

  private ProcessEngine processEngine()
  {
    return processEngineProvider.getProcessEngine();
  }

  private List<ProcessInstance> getProcessInstances(String caseInstanceId)
  {
    return processEngine().getRuntimeService().createProcessInstanceQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();
  }

  private List<org.camunda.bpm.engine.task.Task> getActiveTasks(String caseInstanceId, String processInstanceId)
  {
    return processEngine().getTaskService().createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .processInstanceId(processInstanceId)
        .active()
        .list();
  }
}
