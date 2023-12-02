/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.cases;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.task.TaskId;

/**
 * @author Tamir
 */
public class Execution implements Entity<Execution>
{
  private ExecutionId id;

  private InstanceId instanceId;

  private ActivityId activityId;
  private TaskId taskId;

  private ExecutionType executionType;
  private String activityType;
  private String activityName;
  private String parentId;
  private String activityDescription;

  public Execution(ExecutionId id, InstanceId instanceId, ActivityId activityId, TaskId taskId)
  {
    this.id = Objects.requireNonNull(id, "Execution id is required!");
    this.instanceId = Objects.requireNonNull(instanceId, "Instance id is required!");

    this.activityId = Objects.requireNonNull(activityId, "Activity id is required!");
    this.taskId = Objects.requireNonNull(taskId, "Task id is required!");
  }

  public ExecutionId getId()
  {
    return id;
  }

  public ExecutionType getExecutionType()
  {
    return executionType;
  }

  public void setExecutionType(ExecutionType executionType)
  {
    this.executionType = executionType;
  }

  public String getActivityType()
  {
    return activityType;
  }

  public void setActivityType(String activityType)
  {
    this.activityType = activityType;
  }

  public String getActivityName()
  {
    return activityName;
  }

  public void setActivityName(String activityName)
  {
    this.activityName = activityName;
  }

  public InstanceId getInstanceId()
  {
    return instanceId;
  }

  public ActivityId getActivityId()
  {
    return activityId;
  }

  public TaskId getTaskId()
  {
    return taskId;
  }

  public void setTaskId(TaskId taskId)
  {
    this.taskId = taskId;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getActivityDescription()
  {
    return activityDescription;
  }

  public void setActivityDescription(String activityDescription)
  {
    this.activityDescription = activityDescription;
  }

  @Override
  public boolean sameIdentityAs(Execution other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
