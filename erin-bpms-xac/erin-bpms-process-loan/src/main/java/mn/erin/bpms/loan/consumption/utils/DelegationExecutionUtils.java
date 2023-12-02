/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.utils;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.task.Task;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Tamir
 */
public final class DelegationExecutionUtils
{
  private DelegationExecutionUtils()
  {

  }

  public static String getExecutionParameterStringValue(DelegateExecution execution, String executionParameterName)
  {
    if (execution != null && execution.hasVariable(executionParameterName))
    {
      Object value = execution.getVariable(executionParameterName);
      if (value instanceof String)
      {
        return (String) value;
      }
    }
    return null;
  }

  public static byte[] getExecutionParameterByteValue(DelegateExecution execution, String executionParameterName)
  {
    if (execution != null && execution.hasVariable(executionParameterName))
    {
      Object value = execution.getVariable(executionParameterName);
      if (value instanceof byte[])
      {
        return (byte[]) value;
      }
    }
    return null;
  }

  public static Long getExecutionParameterLongValue(DelegateExecution execution, String executionParameterName)
  {
    if (execution != null && execution.hasVariable(executionParameterName))
    {
      Object value = execution.getVariable(executionParameterName);
      if (value instanceof Long)
      {
        return (long) value;
      }
    }
    return null;
  }

  public static void setVariablesOnAllActiveTasks(DelegateExecution execution, Map<String, Object> variables)
  {
    ProcessEngine processEngine = execution.getProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    List<Task> activeTasks = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    for (Task task : activeTasks)
    {
      String processInstanceId = task.getProcessInstanceId();
      for (Map.Entry<String, Object> entry: variables.entrySet())
      {
        runtimeService.setVariable(processInstanceId, entry.getKey(), entry.getValue());
      }
    }
  }
}
