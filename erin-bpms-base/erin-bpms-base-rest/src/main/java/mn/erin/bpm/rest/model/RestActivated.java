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
public class RestActivated
{
  private final boolean isManualActivated;

  public RestActivated(boolean isManualActivated)
  {
    this.isManualActivated = isManualActivated;
  }

  public boolean isManualActivated()
  {
    return isManualActivated;
  }
}
