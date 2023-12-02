package mn.erin.domain.bpm.model.directOnline;

import java.io.Serializable;
import java.math.BigDecimal;

public class DanInfo implements Serializable
{
  private String domName;

  private Integer month;

  private String orgName;
  private String orgSiID;

  private Boolean paid;
  private BigDecimal salaryAmount;
  private BigDecimal salaryFee;
  private Integer year;

  public DanInfo()
  {

  }

  public String getDomName()
  {
    return domName;
  }

  public void setDomName(String domName)
  {
    this.domName = domName;
  }

  public Integer getMonth()
  {
    return month;
  }

  public void setMonth(Integer month)
  {
    this.month = month;
  }

  public String getOrgName()
  {
    return orgName;
  }

  public void setOrgName(String orgName)
  {
    this.orgName = orgName;
  }

  public String getOrgSiID()
  {
    return orgSiID;
  }

  public void setOrgSiID(String orgSiID)
  {
    this.orgSiID = orgSiID;
  }

  public Boolean getPaid()
  {
    return paid;
  }

  public void setPaid(Boolean paid)
  {
    this.paid = paid;
  }

  public BigDecimal getSalaryAmount()
  {
    return salaryAmount;
  }

  public void setSalaryAmount(BigDecimal salaryAmount)
  {
    this.salaryAmount = salaryAmount;
  }

  public BigDecimal getSalaryFee()
  {
    return salaryFee;
  }

  public void setSalaryFee(BigDecimal salaryFee)
  {
    this.salaryFee = salaryFee;
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

