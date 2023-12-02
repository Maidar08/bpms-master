/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

/**
 * @author Tamir
 */
public class GetCustomerIDCardInfoInput
{
  private String regNumber;
  private String employeeRegNumber;


  public GetCustomerIDCardInfoInput(String regNumber, String employeeRegNumber)
  {
    this.regNumber = regNumber;
    this.employeeRegNumber = employeeRegNumber;
  }

  public String getRegNumber()
  {
    return regNumber;
  }

  public void setRegNumber(String regNumber)
  {
    this.regNumber = regNumber;
  }

  public String getEmployeeRegNumber()
  {
    return employeeRegNumber;
  }

  public void setEmployeeRegNumber(String employeeRegNumber)
  {
    this.employeeRegNumber = employeeRegNumber;
  }

}
