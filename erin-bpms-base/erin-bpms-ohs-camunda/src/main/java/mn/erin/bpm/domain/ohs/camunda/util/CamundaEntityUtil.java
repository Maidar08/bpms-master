/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;

import mn.erin.domain.bpm.model.cases.ActivityId;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.cases.ExecutionType;
import mn.erin.domain.bpm.model.cases.InstanceId;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.model.variable.Variable;

/**
 * @author Tamir
 */
public final class CamundaEntityUtil
{
  private static final String UNKNOWN_FORM_KEY = "UNKNOWN_FORM_KEY";
  private static final String UNKNOWN_TASK_ID = "UNKNOWN_TASK_ID";

  private CamundaEntityUtil()
  {

  }

  public static List<mn.erin.domain.bpm.model.task.Task> convertToTaskList(List<HistoricTaskInstance> historicTaskInstances, String caseInstanceId)
  {
    List<mn.erin.domain.bpm.model.task.Task> completedTasks = new ArrayList<>();

    for (HistoricTaskInstance taskInstance : historicTaskInstances)
    {
      mn.erin.domain.bpm.model.task.Task task = convertToTask(taskInstance, caseInstanceId);
      completedTasks.add(task);
    }

    return completedTasks;
  }

  public static mn.erin.domain.bpm.model.task.Task convertToTask(HistoricTaskInstance taskInstance, String caseInstanceId)
  {
    String taskId = taskInstance.getId();
    String executionId = taskInstance.getExecutionId();

    String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
    String taskName = taskInstance.getName();

    mn.erin.domain.bpm.model.task.Task task = new mn.erin.domain.bpm.model.task.Task(TaskId.valueOf(taskId),
        ExecutionId.valueOf(executionId), CaseInstanceId.valueOf(caseInstanceId), taskName);
    task.setDefinitionKey(taskDefinitionKey);

    task.setEndDate(taskInstance.getEndTime());
    task.setAssignee(task.getAssignee());

    return task;
  }

  public static List<Execution> getExecutionsFrom(List<HistoricCaseActivityInstance> activityInstances)
  {
    List<Execution> executions = new ArrayList<>();

    for (HistoricCaseActivityInstance activityInstance : activityInstances)
    {
      String executionId = activityInstance.getCaseExecutionId();
      String caseInstanceId = activityInstance.getCaseInstanceId();

      String activityId = activityInstance.getCaseActivityId();
      String activityType = activityInstance.getCaseActivityType();
      String activityName = activityInstance.getCaseActivityName();
      String taskId = activityInstance.getTaskId();

      if (null == taskId)
      {
        taskId = UNKNOWN_TASK_ID;
      }
      Execution execution = new Execution(ExecutionId.valueOf(executionId), InstanceId.valueOf(caseInstanceId),
          ActivityId.valueOf(activityId), TaskId.valueOf(taskId));

      execution.setActivityType(activityType);
      execution.setActivityName(activityName);
      execution.setExecutionType(ExecutionType.COMPLETED);

      executions.add(execution);
    }
    return executions;
  }

  public static Map<String, Serializable> toVariableMap(List<Variable> variables)
  {
    Map<String, Serializable> variableMap = new HashMap<>();

    for (Variable variable : variables)
    {
      String id = variable.getId().getId();
      Serializable value = variable.getValue();

      if (null != id)
      {
        variableMap.put(id, value);
      }
    }
    return variableMap;
  }

  public static List<Execution> toExecutions(ProcessEngine processEngine, List<CaseExecution> caseExecutions)
  {
    List<Execution> executions = new ArrayList<>();

    for (CaseExecution caseExecution : caseExecutions)
    {
      Execution execution = toExecution(processEngine, caseExecution);
      executions.add(execution);
    }
    return executions;
  }

  public static Execution toExecution(ProcessEngine processEngine, CaseExecution caseExecution)
  {
    String id = caseExecution.getId();
    String instanceId = caseExecution.getCaseInstanceId();
    String activityId = caseExecution.getActivityId();

    String activityName = caseExecution.getActivityName();
    String activityType = caseExecution.getActivityType();
    ExecutionType executionType = getExecutionType(caseExecution);

    String parentId = caseExecution.getParentId();
    String activityDescription = caseExecution.getActivityDescription();

    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery()
        .caseExecutionId(id)
        .singleResult();

    String taskId = null;

    if (null != task)
    {
      taskId = task.getId();
    }

    if (null == taskId)
    {
      taskId = UNKNOWN_TASK_ID;
    }

    Execution execution = new Execution(new ExecutionId(id), new InstanceId(instanceId), new ActivityId(activityId), new TaskId(taskId));

    execution.setActivityName(activityName);
    execution.setActivityType(activityType);
    execution.setExecutionType(executionType);
    execution.setParentId(parentId);
    execution.setActivityDescription(activityDescription);

    return execution;
  }

  private static ExecutionType getExecutionType(CaseExecution caseExecution)
  {
    boolean isActive = caseExecution.isActive();
    boolean isEnabled = caseExecution.isEnabled();
    boolean isDisabled = caseExecution.isDisabled();
    boolean isAvailable = caseExecution.isAvailable();
    boolean isTerminated = caseExecution.isTerminated();

    if (isActive)
    {
      return ExecutionType.ACTIVE;
    }
    else if (isEnabled)
    {
      return ExecutionType.ENABLED;
    }
    else if (isDisabled)
    {
      return ExecutionType.DISABLED;
    }
    else if (isAvailable)
    {
      return ExecutionType.AVAILABLE;
    }
    else if (isTerminated)
    {
      return ExecutionType.TERMINATED;
    }
    return null;
  }

  public static TaskForm toTaskForm(TaskFormData taskFormData)
  {
    String taskId = taskFormData.getTask().getId();
    String formKey = taskFormData.getFormKey();
    String defKey = taskFormData.getTask().getTaskDefinitionKey();

    if (null == formKey)
    {
      formKey = UNKNOWN_FORM_KEY;
    }

    List<FormField> formFields = taskFormData.getFormFields();
    List<TaskFormField> taskTaskFormFields = toFormFields(formFields);

    return new TaskForm(TaskFormId.valueOf(formKey), TaskId.valueOf(taskId), defKey, taskTaskFormFields);
  }

  private static List<TaskFormField> toFormFields(List<FormField> formFields)
  {
    if (formFields.isEmpty())
    {
      return Collections.emptyList();
    }
    List<TaskFormField> taskFormFields = new ArrayList<>();

    for (FormField formField : formFields)
    {
      String formFieldId = formField.getId();
      String label = formField.getLabel();

      String type = formField.getType().getName();
      TypedValue typedValue = formField.getValue();

      Map<String, String> properties = formField.getProperties();
      List<FieldProperty> fieldProperties = toFieldProperties(properties);

      List<FormFieldValidationConstraint> validations = formField.getValidationConstraints();
      List<FieldValidation> fieldValidations = toFieldValidations(validations);

      TaskFormField taskFormField = new TaskFormField.Builder(formFieldId)
          .withValue(toValue(typedValue))
          .withLabel(label)
          .withType(type)
          .withProperties(fieldProperties)
          .withValidations(fieldValidations)
          .build();

      taskFormFields.add(taskFormField);
    }
    return taskFormFields;
  }

  private static FormFieldValue toValue(TypedValue typedValue)
  {
    if (null == typedValue)
    {
      return new FormFieldValue(null, null);
    }

    Object value = typedValue.getValue();
    ValueType type = typedValue.getType();

    Map<String, Object> valueInfo = type.getValueInfo(typedValue);
    Map<String, Object> newValueInfo = new HashMap<>();

    for (Map.Entry<String, Object> entry : valueInfo.entrySet())
    {
      String key = entry.getKey();
      Object entryValue = entry.getValue();
      if (entryValue instanceof Serializable)
      {
        newValueInfo.put(key, entryValue);
      }
      else
      {
        throw new IllegalStateException(entryValue + " for " + key + " is not Serializable!");
      }
    }

    return new FormFieldValue(value, newValueInfo);
  }

  private static List<FieldProperty> toFieldProperties(Map<String, String> properties)
  {
    if (null == properties || properties.isEmpty())
    {
      return Collections.emptyList();
    }

    List<FieldProperty> fieldProperties = new ArrayList<>();
    Set<Map.Entry<String, String>> propertyEntries = properties.entrySet();

    for (Map.Entry<String, String> property : propertyEntries)
    {
      String key = property.getKey();
      String propertyValue = property.getValue();

      fieldProperties.add(new FieldProperty(key, propertyValue));
    }
    return fieldProperties;
  }

  private static List<FieldValidation> toFieldValidations(List<FormFieldValidationConstraint> validationConstraints)
  {
    if (validationConstraints.isEmpty())
    {
      return Collections.emptyList();
    }

    List<FieldValidation> fieldValidations = new ArrayList<>();

    for (FormFieldValidationConstraint validationConstraint : validationConstraints)
    {
      String name = validationConstraint.getName();
      String configuration = (String) validationConstraint.getConfiguration();

      fieldValidations.add(new FieldValidation(name, configuration));
    }
    return fieldValidations;
  }
}
