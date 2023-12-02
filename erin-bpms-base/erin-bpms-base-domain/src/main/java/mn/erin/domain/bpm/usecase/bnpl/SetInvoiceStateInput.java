package mn.erin.domain.bpm.usecase.bnpl;

public class SetInvoiceStateInput
{
  private final String invoiceNum;

  public String getInvoiceNum()
  {
    return invoiceNum;
  }

  public String getInvoiceUpdateState()
  {
    return invoiceUpdateState;
  }

  private final  String invoiceUpdateState;

  public SetInvoiceStateInput(String invoiceNum, String invoiceUpdateState)
  {
    this.invoiceNum = invoiceNum;
    this.invoiceUpdateState = invoiceUpdateState;
  }
}
