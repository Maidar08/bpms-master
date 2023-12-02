/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author EBazarragchaa
 */
public class RestProcessRequest
{
  private String processTypeId;
  private String groupNumber;
  private String tenantId;
  private String createdUserId;
  private Map<String, Serializable> parameters = new HashMap<>();

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getGroupNumber()
  {
    return groupNumber;
  }

  public void setGroupNumber(String groupNumber)
  {
    this.groupNumber = groupNumber;
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
