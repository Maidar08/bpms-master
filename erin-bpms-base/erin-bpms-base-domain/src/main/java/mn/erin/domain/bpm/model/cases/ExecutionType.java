/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.cases;

/**
 * @author Tamir
 */
public enum ExecutionType
{
  ACTIVE("active"),
  AVAILABLE("available"),
  COMPLETED("completed"),
  ENABLED("enabled"),
  DISABLED("disabled"),
  TERMINATED("terminated");


  private String value;

  ExecutionType(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}
