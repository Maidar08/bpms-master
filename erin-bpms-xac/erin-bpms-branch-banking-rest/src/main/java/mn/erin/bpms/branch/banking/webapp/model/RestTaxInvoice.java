package mn.erin.bpms.branch.banking.webapp.model;

/**
 * @author Tamir
 */
public class RestTaxInvoice
{
  private String invoiceNumber;
  private String invoiceDate;

  private String invoiceType;
  private String amount;
  private String processId;

  public RestTaxInvoice(String taxId, String date, String taxType, String taxTotalAmount, String processId)
  {
    this.invoiceNumber = taxId;
    this.invoiceDate = date;
    this.invoiceType = taxType;
    this.amount = taxTotalAmount;
    this.processId = processId;
  }

  public String getInvoiceNumber()
  {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber)
  {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceDate()
  {
    return invoiceDate;
  }

  public void setInvoiceDate(String invoiceDate)
  {
    this.invoiceDate = invoiceDate;
  }

  public String getInvoiceType()
  {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType)
  {
    this.invoiceType = invoiceType;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public String getProcessId()
  {
    return processId;
  }

  public void setProcessId(String processId)
  {
    this.processId = processId;
  }

}
