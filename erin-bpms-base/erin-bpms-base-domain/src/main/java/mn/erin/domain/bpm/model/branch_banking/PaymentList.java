package mn.erin.domain.bpm.model.branch_banking;

import java.math.BigDecimal;

public class PaymentList
{
  private BigDecimal paymentAmount;
  private Integer paymentAccountNumber;
  private String paymentAccountName;
  private BankAccountInfo bankAccountInfo;

  public PaymentList(BigDecimal paymentAmount, Integer paymentAccountNumber, String paymentAccountName,
      BankAccountInfo bankAccountInfo)
  {
    this.paymentAmount = paymentAmount;
    this.paymentAccountNumber = paymentAccountNumber;
    this.paymentAccountName = paymentAccountName;
    this.bankAccountInfo = bankAccountInfo;
  }
  public BigDecimal getPaymentAmount() { return paymentAmount; }

  public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }

  public Integer getPaymentAccountNumber() { return paymentAccountNumber; }

  public void setPaymentAccountNumber(Integer paymentAccountNumber) { this.paymentAccountNumber = paymentAccountNumber; }

  public String getPaymentAccountName() { return paymentAccountName; }

  public void setPaymentAccountName(String paymentAccountName) { this.paymentAccountName = paymentAccountName; }

  public BankAccountInfo getBankAccountInfo() { return bankAccountInfo; }

  public void setBankAccountInfo(BankAccountInfo bankAccountInfo) { this.bankAccountInfo = bankAccountInfo; }
}
