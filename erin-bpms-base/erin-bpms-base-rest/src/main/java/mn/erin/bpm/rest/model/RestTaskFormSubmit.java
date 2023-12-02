/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestTaskFormSubmit
{
  private String taskId;
  private String processRequestId;
  private String caseInstanceId;
  private String defKey;
  private Map<String, Object> properties = new HashMap<String, Object>();

  public RestTaskFormSubmit()
  {

  }

  public RestTaskFormSubmit(String taskId, Map<String, Object> properties)
  {
    this.taskId = taskId;
    this.properties = properties;
  }

  public RestTaskFormSubmit(String taskId, String caseInstanceId, String processRequestId, Map<String, Object> properties)
  {
    this.taskId = taskId;
    this.caseInstanceId = caseInstanceId;
    this.processRequestId = processRequestId;
    this.properties = properties;
  }

  public RestTaskFormSubmit(String taskId, String processRequestId, String caseInstanceId, String defKey,
      Map<String, Object> properties)
  {
    this.taskId = taskId;
    this.processRequestId = processRequestId;
    this.caseInstanceId = caseInstanceId;
    this.defKey = defKey;
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

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }

  public String getProcessRequestId() {
    return processRequestId;
  }

  public void setProcessRequestId(String processRequestId) {
    this.processRequestId = processRequestId;
  }

  public String getDefKey()
  {
    return defKey;
  }

  public void setDefKey(String defKey)
  {
    this.defKey = defKey;
  }
}
