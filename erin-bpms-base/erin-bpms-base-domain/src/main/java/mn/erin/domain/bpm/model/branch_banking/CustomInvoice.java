package mn.erin.domain.bpm.model.branch_banking;

import java.util.List;

public class CustomInvoice
{
  private String branchName;
  private String charge;
  private String declarationDate;
  private String invoiceNumber;
  private String invoiceTypeName;
  private String registerNumber;
  private String taxPayerName;
  private List<PaymentInfo> paymentInfoList;

  public CustomInvoice(String branchName, String charge, String declarationDate, String invoiceNumber, String invoiceTypeName,
      List<PaymentInfo> paymentList, String registerNumber, String taxPayerName)
  {
    this.branchName = branchName;
    this.charge = charge;
    this.declarationDate = declarationDate;
    this.invoiceNumber = invoiceNumber;
    this.invoiceTypeName = invoiceTypeName;
    this.paymentInfoList = paymentList;
    this.registerNumber = registerNumber;
    this.taxPayerName = taxPayerName;
  }

  public String getBranchName()
  {
    return branchName;
  }

  public void setBranchName(String branchName)
  {
    this.branchName = branchName;
  }

  public String getCharge()
  {
    return charge;
  }

  public void setCharge(String charge)
  {
    this.charge = charge;
  }

  public String getDeclarationDate()
  {
    return declarationDate;
  }

  public void setDeclarationDate(String declarationDate)
  {
    this.declarationDate = declarationDate;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber)
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceTypeName()
  {
    return invoiceTypeName;
  }

  public void setInvoiceTypeName(String invoiceTypeName)
  {
    this.invoiceTypeName = invoiceTypeName;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
  }

  public void setRegisterNumber(String registerNumber)
  {
    this.registerNumber = registerNumber;
  }

  public String getTaxPayerName()
  {
    return taxPayerName;
  }

  public void setTaxPayerName(String taxPayerName)
  {
    this.taxPayerName = taxPayerName;
  }

  public List<PaymentInfo> getPaymentInfoList()
  {
    return paymentInfoList;
  }

  public void setPaymentInfoList(List<PaymentInfo> paymentInfoList)
  {
    this.paymentInfoList = paymentInfoList;
  }
}
