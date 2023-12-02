package mn.erin.domain.bpm.model.collateral;

import java.math.BigDecimal;
import java.time.LocalDate;

import mn.erin.domain.base.model.ValueObject;

public class CollateralInfo implements ValueObject<CollateralInfo>
{
  private static final long serialVersionUID = 1828245479731206418L;

  private String customerName;
  private String liableNumber;

  // percentage difference between an asset's market value.
  private BigDecimal hairCut;
  private BigDecimal availableAmount;

  private String description;
  private String linkedReferenceNumber;
  private String stateRegistrationNumber;
  private LocalDate revalueDate;

  public CollateralInfo(String customerName, String liableNumber, BigDecimal hairCut, BigDecimal availableAmount)
  {
    this.customerName = customerName;
    this.liableNumber = liableNumber;
    this.hairCut = hairCut;
    this.availableAmount = availableAmount;
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

  public static long getSerialVersionUID()
  {
    return serialVersionUID;
  }

  public String getStateRegistrationNumber()
  {
    return stateRegistrationNumber;
  }

  public void setStateRegistrationNumber(String stateRegistrationNumber)
  {
    this.stateRegistrationNumber = stateRegistrationNumber;
  }

  public LocalDate getRevalueDate()
  {
    return revalueDate;
  }

  public void setRevalueDate(LocalDate revalueDate)
  {
    this.revalueDate = revalueDate;
  }

  @Override
  public boolean sameValueAs(CollateralInfo other)
  {
    return false;
  }
}
