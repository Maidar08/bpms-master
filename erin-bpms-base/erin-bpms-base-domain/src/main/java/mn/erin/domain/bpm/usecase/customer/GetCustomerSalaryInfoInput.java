/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetCustomerSalaryInfoInput
{
  private String regNumber;
  private String employeeRegNumber;

  private Integer month;

  public GetCustomerSalaryInfoInput(String regNumber, String employeeRegNumber, Integer month)
  {
    this.regNumber = Objects.requireNonNull(regNumber, "Register number is required!");
    this.employeeRegNumber = Objects.requireNonNull(employeeRegNumber, "Employee register number is required!");
    this.month = Objects.requireNonNull(month, "Salary month is required!");
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

  public Integer getMonth()
  {
    return month;
  }

  public void setMonth(Integer month)
  {
    this.month = month;
  }
}
