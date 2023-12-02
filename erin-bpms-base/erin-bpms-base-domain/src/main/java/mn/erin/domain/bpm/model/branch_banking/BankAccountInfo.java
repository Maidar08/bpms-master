package mn.erin.domain.bpm.model.branch_banking;

public class BankAccountInfo
{
  private String bankAccountName;
  private int bankAccountNo;
  private int bankCode;
  private String bankName;

  public BankAccountInfo(String bankAccountName, int bankAccountNo, Integer bankCode, String bankName)
  {
    this.bankAccountName = bankAccountName;
    this.bankAccountNo = bankAccountNo;
    this.bankCode = bankCode;
    this.bankName = bankName;
  }

  public String getBankAccountName()
  {
    return bankAccountName;
  }

  public void setBankAccountName(String bankAccountName)
  {
    this.bankAccountName = bankAccountName;
  }

  public int getBankAccountNo()
  {
    return bankAccountNo;
  }

  public void setBankAccountNo(Integer bankAccountNo)
  {
    this.bankAccountNo = bankAccountNo;
  }

  public int getBankCode()
  {
    return bankCode;
  }

  public void setBankCode(Integer bankCode)
  {
    this.bankCode = bankCode;
  }

  public String getBankName()
  {
    return bankName;
  }

  public void setBankName(String bankName)
  {
    this.bankName = bankName;
  }
}
