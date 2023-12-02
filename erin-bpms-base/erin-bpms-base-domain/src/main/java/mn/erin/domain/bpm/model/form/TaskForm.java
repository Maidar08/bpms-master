/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.form;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.task.TaskId;

import static mn.erin.domain.bpm.constants.TaskFormConstants.FORM_FIELDS_REQUIRED;
import static mn.erin.domain.bpm.constants.TaskFormConstants.FORM_ID_IS_REQUIRED;
import static mn.erin.domain.bpm.constants.TaskFormConstants.TASK_ID_IS_REQUIRED;

/**
 * Represents a BPMS task form entity.
 *
 * @author Tamir
 */
public class TaskForm implements Entity<TaskForm>
{
  private final TaskFormId taskFormId;
  private final TaskId taskId;
  private Collection<TaskFormField> taskFormFields;
  private String TaskDefinitionKey;

  public TaskForm(TaskFormId taskFormId, TaskId taskId, Collection<TaskFormField> taskFormFields)
  {
    this.taskFormId = Objects.requireNonNull(taskFormId, FORM_ID_IS_REQUIRED);
    this.taskId = Objects.requireNonNull(taskId, TASK_ID_IS_REQUIRED);
    this.taskFormFields = Objects.requireNonNull(taskFormFields, FORM_FIELDS_REQUIRED);
  }

  public TaskForm(TaskFormId taskFormId, TaskId taskId, String taskDefinitionKey, Collection<TaskFormField> taskFormFields)
  {
    this.taskFormId = Objects.requireNonNull(taskFormId, FORM_ID_IS_REQUIRED);
    this.taskId = Objects.requireNonNull(taskId, TASK_ID_IS_REQUIRED);
    this.taskFormFields = Objects.requireNonNull(taskFormFields, FORM_FIELDS_REQUIRED);
    this.TaskDefinitionKey = taskDefinitionKey;
  }

  public Collection<TaskFormField> getTaskFormFields()
  {
    return taskFormFields;
  }

  public void setTaskFormFields(Collection<TaskFormField> taskFormFields)
  {
    this.taskFormFields = taskFormFields;
  }

  public TaskId getTaskId()
  {
    return taskId;
  }

  public TaskFormId getTaskFormId()
  {
    return taskFormId;
  }

  public String getTaskDefinitionKey()
  {
    return TaskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey)
  {
    TaskDefinitionKey = taskDefinitionKey;
  }

  @Override
  public boolean sameIdentityAs(TaskForm other)
  {
    return other != null && (this.taskId.equals(other.taskId));
  }
}
