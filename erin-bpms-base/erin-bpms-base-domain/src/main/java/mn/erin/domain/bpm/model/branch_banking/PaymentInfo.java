package mn.erin.domain.bpm.model.branch_banking;

import java.math.BigDecimal;

public class PaymentInfo
{
  private String bankAccountName;
  private String bankAccountNumber;
  private String bankCode;
  private String bankName;
  private String paymentAccountName;
  private String paymentAccountNumber;
  private BigDecimal paymentAmount;
  private String type;

  public PaymentInfo(String bankAccountName, String bankAccountNumber, String bankCode, String bankName, String paymentAccountName,
      String paymentAccountNumber, BigDecimal paymentAmount, String type)
  {
    this.bankAccountName = bankAccountName;
    this.bankAccountNumber = bankAccountNumber;
    this.bankCode = bankCode;
    this.bankName = bankName;
    this.paymentAccountName = paymentAccountName;
    this.paymentAccountNumber = paymentAccountNumber;
    this.paymentAmount = paymentAmount;
    this.type = type;
  }

  public String getBankAccountName()
  {
    return bankAccountName;
  }

  public void setBankAccountName(String bankAccountName)
  {
    this.bankAccountName = bankAccountName;
  }

  public String getBankAccountNumber()
  {
    return bankAccountNumber;
  }

  public void setBankAccountNumber(String bankAccountNumber)
  {
    this.bankAccountNumber = bankAccountNumber;
  }

  public String getBankCode()
  {
    return bankCode;
  }

  public void setBankCode(String bankCode)
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

  public String getPaymentAccountName()
  {
    return paymentAccountName;
  }

  public void setPaymentAccountName(String paymentAccountName)
  {
    this.paymentAccountName = paymentAccountName;
  }

  public String getPaymentAccountNumber()
  {
    return paymentAccountNumber;
  }

  public void setPaymentAccountNumber(String paymentAccountNumber)
  {
    this.paymentAccountNumber = paymentAccountNumber;
  }

  public BigDecimal getPaymentAmount()
  {
    return paymentAmount;
  }

  public void setPaymentAmount(BigDecimal paymentAmount)
  {
    this.paymentAmount = paymentAmount;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}
