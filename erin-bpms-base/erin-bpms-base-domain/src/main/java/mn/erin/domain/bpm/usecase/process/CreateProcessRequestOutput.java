/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

/**
 * @author EBazarragchaa
 */
public class CreateProcessRequestOutput
{
  private final String processRequestId;

  public CreateProcessRequestOutput(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }
}
