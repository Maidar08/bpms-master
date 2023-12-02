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
public class StartProcessOutput
{
  private final String processInstanceId;

  public StartProcessOutput(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }
}
