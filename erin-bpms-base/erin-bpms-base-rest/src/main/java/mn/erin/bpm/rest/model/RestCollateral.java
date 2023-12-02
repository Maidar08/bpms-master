package mn.erin.bpm.rest.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RestCollateral
{
  private String collateralId;
  private String accountId;

  private String name;
  private String currencyType;

  private Date startDate;
  private Date endDate;

  private List<String> ownerNames;
  private BigDecimal amountOfAssessment;

  private String customerName;
  private String liableNumber;

  private BigDecimal hairCut;
  private BigDecimal availableAmount;

  private String description;
  private String linkedReferenceNumber;
  private Date revalueDate;

  private String type;

  private boolean isCreatedOnBpms;

  public RestCollateral()
  {
    // no needed to do something
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public void setCollateralId(String collateralId)
  {
    this.collateralId = collateralId;
  }

  public String getAccountId()
  {
    return accountId;
  }

  public void setAccountId(String accountId)
  {
    this.accountId = accountId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getCurrencyType()
  {
    return currencyType;
  }

  public void setCurrencyType(String currencyType)
  {
    this.currencyType = currencyType;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public List<String> getOwnerNames()
  {
    return ownerNames;
  }

  public void setOwnerNames(List<String> ownerNames)
  {
    this.ownerNames = ownerNames;
  }

  public BigDecimal getAmountOfAssessment()
  {
    return amountOfAssessment;
  }

  public void setAmountOfAssessment(BigDecimal amountOfAssessment)
  {
    this.amountOfAssessment = amountOfAssessment;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }

  public String getLiableNumber()
  {
    return liableNumber;
  }

  public void setLiableNumber(String liableNumber)
  {
    this.liableNumber = liableNumber;
  }

  public BigDecimal getHairCut()
  {
    return hairCut;
  }

  public void setHairCut(BigDecimal hairCut)
  {
    this.hairCut = hairCut;
  }

  public BigDecimal getAvailableAmount()
  {
    return availableAmount;
  }

  public void setAvailableAmount(BigDecimal availableAmount)
  {
    this.availableAmount = availableAmount;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getLinkedReferenceNumber()
  {
    return linkedReferenceNumber;
  }

  public void setLinkedReferenceNumber(String linkedReferenceNumber)
  {
    this.linkedReferenceNumber = linkedReferenceNumber;
  }

  public Date getRevalueDate()
  {
    return revalueDate;
  }

  public void setRevalueDate(Date revalueDate)
  {
    this.revalueDate = revalueDate;
  }

  public boolean isCreatedOnBpms()
  {
    return isCreatedOnBpms;
  }

  public void setCreatedOnBpms(boolean createdOnBpms)
  {
    isCreatedOnBpms = createdOnBpms;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}
