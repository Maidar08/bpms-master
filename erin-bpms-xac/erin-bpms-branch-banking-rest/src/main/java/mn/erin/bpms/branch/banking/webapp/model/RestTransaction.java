package mn.erin.bpms.branch.banking.webapp.model;

/**
 * @author Tamir
 */
public class RestTransaction
{
  private String transactionId;
  private String transactionDate;

  private String transactionAmount;
  private String transactionCCY;

  private String branchId;
  private String accountId;
  private String userId;

  private String type;
  private String subType;
  private String status;
  private String particulars;

  public RestTransaction()
  {
  }

  public RestTransaction(String transactionId, String transactionDate, String transactionAmount, String transactionCCY, String branchId,
      String accountId, String userId, String type, String subType, String status, String particulars)
  {
    this.transactionId = transactionId;
    this.transactionDate = transactionDate;
    this.transactionAmount = transactionAmount;
    this.transactionCCY = transactionCCY;
    this.branchId = branchId;
    this.accountId = accountId;
    this.userId = userId;
    this.type = type;
    this.subType = subType;
    this.status = status;
    this.particulars = particulars;
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

  public String getTransactionAmount()
  {
    return transactionAmount;
  }

  public void setTransactionAmount(String transactionAmount)
  {
    this.transactionAmount = transactionAmount;
  }

  public String getTransactionCCY()
  {
    return transactionCCY;
  }

  public void setTransactionCCY(String transactionCCY)
  {
    this.transactionCCY = transactionCCY;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  public String getAccountId()
  {
    return accountId;
  }

  public void setAccountId(String accountId)
  {
    this.accountId = accountId;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getSubType()
  {
    return subType;
  }

  public void setSubType(String subType)
  {
    this.subType = subType;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getParticulars()
  {
    return particulars;
  }

  public void setParticulars(String particulars)
  {
    this.particulars = particulars;
  }
}
