/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.rest.model;

import java.util.Collections;
import java.util.Map;

/**
 * @author Zorig
 */
public class RestSetExecutionVariables
{
  private String taskId;
  private String caseInstanceId;
  private Map<String, Object> properties;

  public RestSetExecutionVariables()
  {
  }

  public RestSetExecutionVariables(String taskId, String caseInstanceId, Map<String, Object> properties)
  {
    this.taskId = taskId;
    this.caseInstanceId = caseInstanceId;
    this.properties = properties;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public void setProperties(Map<String, Object> properties)
  {
    this.properties = properties;
  }
}
