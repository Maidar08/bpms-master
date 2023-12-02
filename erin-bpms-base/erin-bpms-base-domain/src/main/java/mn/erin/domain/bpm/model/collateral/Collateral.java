package mn.erin.domain.bpm.model.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.currency.CurrencyType;

public class Collateral implements Entity<Collateral>, Serializable
{
  private static final long serialVersionUID = -8478591575934713169L;

  private final CollateralId id;
  private AccountId accountId;

  private String name;
  private CurrencyType currencyType;

  private LocalDate startDate;
  private LocalDate endDate;

  private List<String> ownerNames;
  private BigDecimal amountOfAssessment;

  private CollateralInfo collateralInfo;

  private String type;

  public Collateral(CollateralId id, AccountId accountId, String name, CurrencyType currencyType)
  {
    this.id = id;
    this.accountId = accountId;
    this.name = name;
    this.currencyType = currencyType;
  }

  public CollateralId getId()
  {
    return id;
  }

  public AccountId getAccountId()
  {
    return accountId;
  }

  public void setAccountId(AccountId accountId)
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

  public CurrencyType getCurrencyType()
  {
    return currencyType;
  }

  public void setCurrencyType(CurrencyType currencyType)
  {
    this.currencyType = currencyType;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public void setStartDate(LocalDate startDate)
  {
    this.startDate = startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public void setEndDate(LocalDate endDate)
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

  public CollateralInfo getCollateralInfo()
  {
    return collateralInfo;
  }

  public void setCollateralInfo(CollateralInfo collateralInfo)
  {
    this.collateralInfo = collateralInfo;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  @Override
  public boolean sameIdentityAs(Collateral other)
  {
    return null != other && other.id.equals(this.id);
  }
}
