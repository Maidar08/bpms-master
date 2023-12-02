/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.math.BigDecimal;

/**
 * @author Tamir
 */
public class RestSalaryInfo
{
  private Integer month;
  private Integer year;

  private BigDecimal amount;
  private BigDecimal salaryFee;

  private boolean isPaidSocialInsurance;

  public RestSalaryInfo()
  {

  }

  public RestSalaryInfo(Integer month, Integer year, BigDecimal amount, BigDecimal salaryFee, boolean isPaidSocialInsurance)
  {
    this.month = month;
    this.year = year;
    this.amount = amount;
    this.salaryFee = salaryFee;
    this.isPaidSocialInsurance = isPaidSocialInsurance;
  }

  public Integer getMonth()
  {
    return month;
  }

  public void setMonth(Integer month)
  {
    this.month = month;
  }

  public Integer getYear()
  {
    return year;
  }

  public void setYear(Integer year)
  {
    this.year = year;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public void setAmount(BigDecimal amount)
  {
    this.amount = amount;
  }

  public BigDecimal getSalaryFee()
  {
    return salaryFee;
  }

  public void setSalaryFee(BigDecimal salaryFee)
  {
    this.salaryFee = salaryFee;
  }

  public boolean isPaidSocialInsurance()
  {
    return isPaidSocialInsurance;
  }

  public void setPaidSocialInsurance(boolean paidSocialInsurance)
  {
    isPaidSocialInsurance = paidSocialInsurance;
  }
}
