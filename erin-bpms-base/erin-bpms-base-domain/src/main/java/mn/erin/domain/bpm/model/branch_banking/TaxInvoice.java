package mn.erin.domain.bpm.model.branch_banking;

import java.io.Serializable;

import mn.erin.domain.base.model.Entity;

public class TaxInvoice implements Entity<TaxInvoice>, Serializable
{
  private String invoiceNumber;
  private String invoiceYear;
  private String invoiceType;
  private String invoiceAmount;

  private String taxPayerName;
  private String taxAccountName;
  private String taxTypeCode;
  private String assetDetail;
  private String period;
  private String payFull;
  private String tin;
  private String taxAccount;
  private String pin;
  private String stateAccount;
  private String stateAccountName;
  private String payMore;
  private String amount;
  private String branchCode;
  private String branchName;
  private String periodType;
  private String subBranchName;
  private String ccy;
  private String taxTypeName;
  private String year;
  private String subBranchCode;




  public TaxInvoice(String invoiceNumber, String invoiceDate, String invoiceType, String amount)
  {
    this.invoiceNumber = invoiceNumber;
    this.invoiceYear = invoiceDate;
    this.invoiceType = invoiceType;
    this.invoiceAmount = amount;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber)
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceYear()
  {
    return invoiceYear;
  }

  public void setInvoiceYear(String invoiceYear)
  {
    this.invoiceYear = invoiceYear;
  }

  public String getInvoiceType()
  {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType)
  {
    this.invoiceType = invoiceType;
  }

  public String getInvoiceAmount()
  {
    return invoiceAmount;
  }

  public void setInvoiceAmount(String invoiceAmount)
  {
    this.invoiceAmount = invoiceAmount;
  }

  public String getTaxPayerName()
  {
    return taxPayerName;
  }

  public void setTaxPayerName(String taxPayerName)
  {
    this.taxPayerName = taxPayerName;
  }

  public String getTaxAccountName()
  {
    return taxAccountName;
  }

  public void setTaxAccountName(String taxAccountName)
  {
    this.taxAccountName = taxAccountName;
  }

  public String getTaxTypeCode()
  {
    return taxTypeCode;
  }

  public void setTaxTypeCode(String taxTypeCode)
  {
    this.taxTypeCode = taxTypeCode;
  }

  public String getAssetDetail()
  {
    return assetDetail;
  }

  public void setAssetDetail(String assetDetail)
  {
    this.assetDetail = assetDetail;
  }

  public String getPeriod()
  {
    return period;
  }

  public void setPeriod(String period)
  {
    this.period = period;
  }

  public String getPayFull()
  {
    return payFull;
  }

  public void setPayFull(String payFull)
  {
    this.payFull = payFull;
  }

  public String getTin()
  {
    return tin;
  }

  public void setTin(String tin)
  {
    this.tin = tin;
  }

  public String getTaxAccount()
  {
    return taxAccount;
  }

  public void setTaxAccount(String taxAccount)
  {
    this.taxAccount = taxAccount;
  }

  public String getPin()
  {
    return pin;
  }

  public void setPin(String pin)
  {
    this.pin = pin;
  }

  public String getStateAccount()
  {
    return stateAccount;
  }

  public void setStateAccount(String stateAccount)
  {
    this.stateAccount = stateAccount;
  }

  public String getStateAccountName()
  {
    return stateAccountName;
  }

  public void setStateAccountName(String stateAccountName)
  {
    this.stateAccountName = stateAccountName;
  }

  @Override
  public boolean sameIdentityAs(TaxInvoice other)
  {
    return false;
  }

  public String getPayMore()
  {
    return payMore;
  }

  public void setPayMore(String payMore)
  {
    this.payMore = payMore;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public String getBranchCode()
  {
    return branchCode;
  }

  public void setBranchCode(String branchCode)
  {
    this.branchCode = branchCode;
  }

  public String getBranchName()
  {
    return branchName;
  }

  public void setBranchName(String branchName)
  {
    this.branchName = branchName;
  }

  public String getPeriodType()
  {
    return periodType;
  }

  public void setPeriodType(String periodType)
  {
    this.periodType = periodType;
  }

  public String getSubBranchName()
  {
    return subBranchName;
  }

  public void setSubBranchName(String subBranchName)
  {
    this.subBranchName = subBranchName;
  }

  public String getCcy()
  {
    return ccy;
  }

  public void setCcy(String ccy)
  {
    this.ccy = ccy;
  }

  public String getTaxTypeName()
  {
    return taxTypeName;
  }

  public void setTaxTypeName(String taxTypeName)
  {
    this.taxTypeName = taxTypeName;
  }

  public String getYear()
  {
    return year;
  }

  public void setYear(String year)
  {
    this.year = year;
  }

  public String getSubBranchCode()
  {
    return subBranchCode;
  }

  public void setSubBranchCode(String subBranchCode)
  {
    this.subBranchCode = subBranchCode;
  }




}
