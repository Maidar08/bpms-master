package mn.erin.domain.bpm.model.loan_contract;

public class LinkageCollateralInfo
{
  private String collateralId;
  private String collateralCode;
  private String collateralType;
  private String currency;
  private Double apportionedAmount;
  private String apportioningMethod;
  private String collateralNatureInd;
  private String withdrawReasonCode;
  private boolean checkSelectedCollateral;

  public LinkageCollateralInfo(String collateralId, String collateralCode, String collateralType, String currency, Double apportionedAmount,
      String apportioningMethod, String collateralNatureInd, String withdrawReasonCode, boolean checkSelectedCollateral)
  {
    this.collateralId = collateralId;
    this.collateralCode = collateralCode;
    this.collateralType = collateralType;
    this.currency = currency;
    this.apportionedAmount = apportionedAmount;
    this.apportioningMethod = apportioningMethod;
    this.collateralNatureInd = collateralNatureInd;
    this.withdrawReasonCode = withdrawReasonCode;
    this.checkSelectedCollateral = checkSelectedCollateral;
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public void setCollateralId(String collateralId)
  {
    this.collateralId = collateralId;
  }

  public String getCollateralCode()
  {
    return collateralCode;
  }

  public void setCollateralCode(String collateralCode)
  {
    this.collateralCode = collateralCode;
  }

  public String getCollateralType()
  {
    return collateralType;
  }

  public void setCollateralType(String collateralType)
  {
    this.collateralType = collateralType;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public Double getApportionedAmount()
  {
    return apportionedAmount;
  }

  public void setApportionedAmount(Double apportionedAmount)
  {
    this.apportionedAmount = apportionedAmount;
  }

  public String getApportioningMethod()
  {
    return apportioningMethod;
  }

  public void setApportioningMethod(String apportioningMethod)
  {
    this.apportioningMethod = apportioningMethod;
  }

  public String getCollateralNatureInd()
  {
    return collateralNatureInd;
  }

  public void setCollateralNatureInd(String collateralNatureInd)
  {
    this.collateralNatureInd = collateralNatureInd;
  }

  public String getWithdrawReasonCode()
  {
    return withdrawReasonCode;
  }

  public void setWithdrawReasonCode(String withdrawReasonCode)
  {
    this.withdrawReasonCode = withdrawReasonCode;
  }

  public boolean isCheckSelectedCollateral()
  {
    return checkSelectedCollateral;
  }

  public void setCheckSelectedCollateral(boolean checkSelectedCollateral)
  {
    this.checkSelectedCollateral = checkSelectedCollateral;
  }
}
