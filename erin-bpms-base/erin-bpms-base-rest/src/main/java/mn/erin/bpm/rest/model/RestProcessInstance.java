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
public class RestProcessInstance
{
  private final String instanceId;

  public RestProcessInstance(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getInstanceId()
  {
    return instanceId;
  }
}
