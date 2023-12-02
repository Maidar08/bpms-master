/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestCreatedRequest
{
  private String id;
  private String processTypeId;
  private String processTypeName;
  private String processInstanceId;

  private String createdDate;
  private String assignedUserId;

  private String createdUserId;
  private String state;
  private Map<String, Serializable> parameters;

  public RestCreatedRequest()
  {

  }

  public RestCreatedRequest(String id, String processTypeId, String processTypeName, String processInstanceId, String createdDate, String assignedUserId,
      String state, Map<String, Serializable> parameters)
  {
    this.id = id;
    this.processTypeId = processTypeId;
    this.processTypeName = processTypeName;
    this.processInstanceId = processInstanceId;
    this.createdDate = createdDate;
    this.assignedUserId = assignedUserId;
    this.state = state;
    this.parameters = parameters;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getProcessTypeName()
  {
    return processTypeName;
  }

  public void setProcessTypeName(String processTypeName)
  {
    this.processTypeName = processTypeName;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(String createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getAssignedUserId()
  {
    return assignedUserId;
  }

  public void setAssignedUserId(String assignedUserId)
  {
    this.assignedUserId = assignedUserId;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public String getCreatedUserId()
  {
    return createdUserId;
  }

  public void setCreatedUserId(String createdUserId)
  {
    this.createdUserId = createdUserId;
  }

  public Map<String, Serializable> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Serializable> parameters)
  {
    this.parameters = parameters;
  }
}
