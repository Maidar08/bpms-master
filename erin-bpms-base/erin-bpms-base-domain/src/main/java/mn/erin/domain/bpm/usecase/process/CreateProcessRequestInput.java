/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author EBazarragchaa
 */
public class CreateProcessRequestInput
{
  private final String groupNumber;
  private final String requestedUserId;
  private final String processTypeId;
  private Map<String, Serializable> parameters;
  private Map<String, Object> objectParameters;

  public CreateProcessRequestInput(String groupNumber, String requestedUserId, String processTypeId)
  {
    this.groupNumber = groupNumber;
    this.requestedUserId = requestedUserId;
    this.processTypeId = processTypeId;
  }

  public Map<String, Serializable> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  public void setParameters(Map<String, Serializable> parameters)
  {
    this.parameters = parameters;
  }

  public String getGroupNumber()
  {
    return groupNumber;
  }

  public String getRequestedUserId()
  {
    return requestedUserId;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public Map<String, Object> getObjectParameters()
  {
    return Collections.unmodifiableMap(objectParameters);
  }

  public void setObjectParameters(Map<String, Object> objectParameters)
  {
    this.objectParameters = objectParameters;
  }
}
