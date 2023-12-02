package mn.erin.domain.bpm.model.branch_banking.transaction;

import mn.erin.domain.base.model.Entity;

/**
 * @author Bilguunbor
 **/

public class ETransaction implements Entity<ETransaction>
{
  private String payerUserId;
  private String transactionId;
  private String transactionDate;
  private String channelId;
  private String payerAccName;
  private String payerBranchId;
  private String payerAccountId;
  private String transactionAmount;
  private String transactionCcy;
  private String transactionDesc;
  private String recipientBankName;
  private String recipientBranchName;
  private String recipientAccountName;
  private String recipientAccountId;
  private String recipientAmount;
  private String recipientCcy;
  private String rate;
  private String fee;

  public ETransaction(String payerUserId, String transactionId, String transactionDate, String channelId, String payerAccName, String payerBranchId,
      String payerAccountId, String transactionAmount, String transactionCcy, String transactionDesc, String recipientBankName,
      String recipientBranchName, String recipientAccountName, String recipientAccountId, String recipientAmount, String recipientCcy, String rate,
      String fee)
  {
    this.payerUserId = payerUserId;
    this.transactionId = transactionId;
    this.transactionDate = transactionDate;
    this.channelId = channelId;
    this.payerAccName = payerAccName;
    this.payerBranchId = payerBranchId;
    this.payerAccountId = payerAccountId;
    this.transactionAmount = transactionAmount;
    this.transactionCcy = transactionCcy;
    this.transactionDesc = transactionDesc;
    this.recipientBankName = recipientBankName;
    this.recipientBranchName = recipientBranchName;
    this.recipientAccountName = recipientAccountName;
    this.recipientAccountId = recipientAccountId;
    this.recipientAmount = recipientAmount;
    this.recipientCcy = recipientCcy;
    this.rate = rate;
    this.fee = fee;
  }

  public String getPayerUserId()
  {
    return payerUserId;
  }

  public void setPayerUserId(String payerUserId)
  {
    this.payerUserId = payerUserId;
  }

  public String getTransactionId()
  {
    return transactionId;
  }

  public void setTransactionId(String transactionId)
  {
    this.transactionId = transactionId;
  }

  public String getTransactionDate()
  {
    return transactionDate;
  }

  public void setTransactionDate(String transactionDate)
  {
    this.transactionDate = transactionDate;
  }

  public String getChannelId()
  {
    return channelId;
  }

  public void setChannelId(String channelId)
  {
    this.channelId = channelId;
  }

  public String getPayerAccName()
  {
    return payerAccName;
  }

  public void setPayerAccName(String payerAccName)
  {
    this.payerAccName = payerAccName;
  }

  public String getPayerBranchId()
  {
    return payerBranchId;
  }

  public void setPayerBranchId(String payerBranchId)
  {
    this.payerBranchId = payerBranchId;
  }

  public String getPayerAccountId()
  {
    return payerAccountId;
  }

  public void setPayerAccountId(String payerAccountId)
  {
    this.payerAccountId = payerAccountId;
  }

  public String getTransactionAmount()
  {
    return transactionAmount;
  }

  public void setTransactionAmount(String transactionAmount)
  {
    this.transactionAmount = transactionAmount;
  }

  public String getTransactionCcy()
  {
    return transactionCcy;
  }

  public void setTransactionCcy(String transactionCcy)
  {
    this.transactionCcy = transactionCcy;
  }

  public String getTransactionDesc()
  {
    return transactionDesc;
  }

  public void setTransactionDesc(String transactionDesc)
  {
    this.transactionDesc = transactionDesc;
  }

  public String getRecipientBankName()
  {
    return recipientBankName;
  }

  public void setRecipientBankName(String recipientBankName)
  {
    this.recipientBankName = recipientBankName;
  }

  public String getRecipientBranchName()
  {
    return recipientBranchName;
  }

  public void setRecipientBranchName(String recipientBranchName)
  {
    this.recipientBranchName = recipientBranchName;
  }

  public String getRecipientAccountName()
  {
    return recipientAccountName;
  }

  public void setRecipientAccountName(String recipientAccountName)
  {
    this.recipientAccountName = recipientAccountName;
  }

  public String getRecipientAccountId()
  {
    return recipientAccountId;
  }

  public void setRecipientAccountId(String recipientAccountId)
  {
    this.recipientAccountId = recipientAccountId;
  }

  public String getRecipientAmount()
  {
    return recipientAmount;
  }

  public void setRecipientAmount(String recipientAmount)
  {
    this.recipientAmount = recipientAmount;
  }

  public String getRecipientCcy()
  {
    return recipientCcy;
  }

  public void setRecipientCcy(String recipientCcy)
  {
    this.recipientCcy = recipientCcy;
  }

  public String getRate()
  {
    return rate;
  }

  public void setRate(String rate)
  {
    this.rate = rate;
  }

  public void setFee(String fee)
  {
    this.fee = fee;
  }

  public String getFee()
  {
    return fee;
  }

  @Override
  public boolean sameIdentityAs(ETransaction other)
  {
    return false;
  }
}
