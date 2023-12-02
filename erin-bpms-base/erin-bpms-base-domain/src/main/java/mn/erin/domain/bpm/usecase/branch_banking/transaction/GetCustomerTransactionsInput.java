package mn.erin.domain.bpm.usecase.branch_banking.transaction;

/**
 * @author Tamir
 */
public class GetCustomerTransactionsInput
{
  private String userId;
  private String transactionDate;
  private String instanceId;

  public GetCustomerTransactionsInput(String instanceId, String userId, String transactionDate)
  {
    this.instanceId = instanceId;
    this.userId = userId;
    this.transactionDate = transactionDate;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getTransactionDate()
  {
    return transactionDate;
  }

  public void setTransactionDate(String transactionDate)
  {
    this.transactionDate = transactionDate;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }
}
