/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.direct.online.webapp.model;

/**
 * @author Tamir
 */
public class RestTask
{
  private String taskId;
  private String executionId;
  private String instanceId;
  private String name;
  private String status;
  private String type;
  private String parentTaskId;
  private String definitionKey;

  public RestTask()
  {

  }

  public RestTask(String taskId, String executionId, String instanceId, String name, String status, String type, String parentTaskId, String definitionKey)
  {
    this.taskId = taskId;
    this.executionId = executionId;
    this.instanceId = instanceId;
    this.name = name;
    this.status = status;
    this.type = type;
    this.parentTaskId = parentTaskId;
    this.definitionKey = definitionKey;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
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

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getParentTaskId()
  {
    return parentTaskId;
  }

  public void setParentTaskId(String parentTaskId)
  {
    this.parentTaskId = parentTaskId;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }
}
