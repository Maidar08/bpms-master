package mn.erin.domain.bpm.model.branch_banking;

public class AccountInfo
{
  private String transactionCcy;
  private String no;
  private String accountId;
  private String amount;
  private String customerName;
  private String accountCcy;
  private boolean checked;

  public AccountInfo()
  {

  }

  public AccountInfo(String transactionCcy, String no, String accountId, String amount, String customerName, String accCurrency, boolean checked)
  {
    this.transactionCcy = transactionCcy;
    this.no = no;
    this.accountId = accountId;
    this.amount = amount;
    this.customerName = customerName;
    this.accountCcy = accCurrency;
    this.checked = checked;
  }

  public String getAccountId()
  {
    return accountId;
  }

  public void setAccountId(String accountId)
  {
    this.accountId = accountId;
  }

  public String getCustomerName()
  {
    return customerName;
  }

  public void setCustomerName(String customerName)
  {
    this.customerName = customerName;
  }

  public String getTransactionCcy()
  {
    return transactionCcy;
  }

  public void setTransactionCcy(String transactionCcy)
  {
    this.transactionCcy = transactionCcy;
  }

  public String getAccountCcy()
  {
    return accountCcy;
  }

  public void setAccountCcy(String accountCcy)
  {
    this.accountCcy = accountCcy;
  }

  public String getNo()
  {
    return no;
  }

  public void setNo(String no)
  {
    this.no = no;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public boolean getChecked()
  {
    return checked;
  }

  public void setChecked(boolean checked)
  {
    this.checked = checked;
  }
}
