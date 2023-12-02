/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.Objects;

/**
 * @author Tamir
 */
public class RestStartProcess
{
  private String processRequestId;
  private String processCategory;
  private String processType;
  private String createdUser;

  public RestStartProcess()
  {
  }

  public RestStartProcess(String processRequestId)
  {
    this.processRequestId = Objects.requireNonNull(processRequestId, "Process request id is required!");
  }

  public RestStartProcess(String processRequestId, String processCategory, String processType, String createdUser)
  {
    this.processRequestId = processRequestId;
    this.processCategory = processCategory;
    this.processType = processType;
    this.createdUser = createdUser;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getProcessCategory()
  {
    return processCategory;
  }

  public void setProcessCategory(String processCategory)
  {
    this.processCategory = processCategory;
  }

  public String getProcessType()
  {
    return processType;
  }

  public void setProcessType(String processType)
  {
    this.processType = processType;
  }

  public String getCreatedUser() { return createdUser; }

  public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }

}
