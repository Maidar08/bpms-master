/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.execution;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetEnabledExecutionsInput
{
  private String instanceId;

  public GetEnabledExecutionsInput(String instanceId)
  {
    this.instanceId = Objects.requireNonNull(instanceId, "Instance id is required!");
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }
}
