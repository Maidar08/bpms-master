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
public class RestUpdated
{
  private boolean isUpdated;

  public RestUpdated(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }

  public void setUpdated(boolean isUpdated)
  {
    isUpdated = isUpdated;
  }
}
