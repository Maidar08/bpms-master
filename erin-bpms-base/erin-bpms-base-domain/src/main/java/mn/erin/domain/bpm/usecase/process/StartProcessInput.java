/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.HashMap;
import java.util.Map;

/**
 * @author EBazarragchaa
 */
public class StartProcessInput
{
  private final String processRequestId;
  private String processCategory;
  private String processType;
  private String createdUser;
  private Map<String, Object> parameters = new HashMap();

  public StartProcessInput(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
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

  public Map<String, Object> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters)
  {
    this.parameters = parameters;
  }
}
