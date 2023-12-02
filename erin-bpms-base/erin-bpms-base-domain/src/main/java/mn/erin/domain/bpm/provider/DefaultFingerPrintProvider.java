/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.provider;

import java.io.Serializable;

/**
 * @author Tamir
 */
public class DefaultFingerPrintProvider implements FingerPrintProvider, Serializable
{
  private static final long serialVersionUID = 7613090744395613672L;

  private String customerFingerPrint;
  private String employeeFingerPrint;

  @Override
  public String getCustomerFingerPrint()
  {
    return this.customerFingerPrint;
  }

  @Override
  public void setCustomerFingerPrint(String customerFingerPrint)
  {

    if (null != customerFingerPrint)
    {
      this.customerFingerPrint = customerFingerPrint;
    }
  }

  @Override
  public String getEmployeeFingerPrint()
  {
    return this.employeeFingerPrint;
  }

  @Override
  public void setEmployeeFingerPrint(String employeeFingerPrint)
  {
    if (null != employeeFingerPrint)
    {
      this.employeeFingerPrint = employeeFingerPrint;
    }
  }
}
