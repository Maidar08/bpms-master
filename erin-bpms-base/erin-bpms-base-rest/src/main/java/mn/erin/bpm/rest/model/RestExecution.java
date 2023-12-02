/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

/**
 * @author Tamir
 */
public class RestExecution
{
  private String executionId;
  private String instanceId;

  private String activityId;
  private String taskId;

  private String executionType;
  private String activityType;
  private String activityName;

  public RestExecution()
  {

  }

  public RestExecution(String executionId, String instanceId, String activityId, String taskId, String executionType, String activityType, String activityName)
  {
    this.executionId = executionId;
    this.instanceId = instanceId;
    this.activityId = activityId;
    this.taskId = taskId;
    this.executionType = executionType;
    this.activityType = activityType;
    this.activityName = activityName;
  }

  public String getExecutionId()
  {
    return executionId;
  }

  public void setExecutionId(String executionId)
  {
    this.executionId = executionId;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getActivityId()
  {
    return activityId;
  }

  public void setActivityId(String activityId)
  {
    this.activityId = activityId;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getExecutionType()
  {
    return executionType;
  }

  public void setExecutionType(String executionType)
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
}
