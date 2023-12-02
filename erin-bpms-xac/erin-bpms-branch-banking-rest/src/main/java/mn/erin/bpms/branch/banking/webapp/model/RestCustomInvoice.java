package mn.erin.bpms.branch.banking.webapp.model;

import java.util.List;

import mn.erin.domain.bpm.model.branch_banking.PaymentInfo;

public class RestCustomInvoice
{
  private String declarationDate;
  private String branchName;
  private String invoiceNumber;
  private String taxPayerName;
  private String registerId;
  private String type;
  private String paymentAmount;
  private String charge;
  private List<PaymentInfo> paymentList;


  public RestCustomInvoice(String declarationDate, String branchName, String invoiceNumber, String taxPayerName, String registerId, String charge,
      String type, String paymentAmount, List<PaymentInfo> paymentList)
  {
    this.declarationDate = declarationDate;
    this.branchName = branchName;
    this.invoiceNumber = invoiceNumber;
    this.taxPayerName = taxPayerName;
    this.registerId = registerId;
    this.charge = charge;
    this.type = type;
    this.paymentAmount = paymentAmount;
    this.paymentList = paymentList;
  }

  public String getDeclarationDate()
  {
    return declarationDate;
  }

  public void setDeclarationDate(String declarationDate)
  {
    this.declarationDate = declarationDate;
  }

  public String getBranchName()
  {
    return branchName;
  }

  public void setBranchName(String branchName)
  {
    this.branchName = branchName;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber)
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getTaxPayerName()
  {
    return taxPayerName;
  }

  public void setTaxPayerName(String taxPayerName)
  {
    this.taxPayerName = taxPayerName;
  }

  public String getRegisterId()
  {
    return registerId;
  }

  public void setRegisterId(String registerId)
  {
    this.registerId = registerId;
  }

  public String getCharge()
  {
    return charge;
  }

  public void setCharge(String charge)
  {
    this.charge = charge;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public List<PaymentInfo> getPaymentList()
  {
    return paymentList;
  }

  public void setPaymentList(List<PaymentInfo> paymentList)
  {
    this.paymentList = paymentList;
  }

  public String getPaymentAmount()
  {
    return paymentAmount;
  }

  public void setPaymentAmount(String paymentAmount)
  {
    this.paymentAmount = paymentAmount;
  }
}
