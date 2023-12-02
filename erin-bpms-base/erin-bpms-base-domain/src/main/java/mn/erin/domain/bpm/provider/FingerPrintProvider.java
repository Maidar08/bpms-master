/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.provider;

/**
 * @author Tamir
 */
public interface FingerPrintProvider
{
  /**
   * Gets customer finger print.
   *
   * @return customer finger print string as base64.
   */
  String getCustomerFingerPrint();

  /**
   * Sets customer finger print.
   *
   * @param customerFingerPrint Given finger print string as base64.
   */
  void setCustomerFingerPrint(String customerFingerPrint);

  /**
   * Gets employee finger print.
   *
   * @return employee finger print string as base64.
   */
  String getEmployeeFingerPrint();

  /**
   * Sets employee finger print.
   *
   * @param employeeFingerPrint given employee finger print string as base64.
   */
  void setEmployeeFingerPrint(String employeeFingerPrint);
}
