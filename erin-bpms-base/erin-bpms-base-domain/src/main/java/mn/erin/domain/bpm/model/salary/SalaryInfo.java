/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.salary;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tamir
 */
public class SalaryInfo implements Serializable
{
  private BigDecimal amount;
  private BigDecimal salaryFee;

  private Organization organization;
  private boolean isPaidSocialInsurance;

  private Integer year;
  private Integer month;

  public SalaryInfo(BigDecimal amount, BigDecimal salaryFee, Organization organization, boolean isPaidSocialInsurance, Integer year, Integer month)
  {
    this.amount = amount;
    this.salaryFee = salaryFee;
    this.organization = organization;
    this.isPaidSocialInsurance = isPaidSocialInsurance;
    this.year = year;
    this.month = month;
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

  public Organization getOrganization()
  {
    return organization;
  }

  public void setOrganization(Organization organization)
  {
    this.organization = organization;
  }

  public boolean isPaidSocialInsurance()
  {
    return isPaidSocialInsurance;
  }

  public void setPaidSocialInsurance(boolean paidSocialInsurance)
  {
    isPaidSocialInsurance = paidSocialInsurance;
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
}
