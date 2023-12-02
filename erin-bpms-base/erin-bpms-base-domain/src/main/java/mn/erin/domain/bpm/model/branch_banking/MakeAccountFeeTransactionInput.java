package mn.erin.domain.bpm.model.branch_banking;

import java.util.List;
import java.util.Map;

public class MakeAccountFeeTransactionInput
{
  private String amount;
  private String currency;
  private String transactionSubType;
  private String instanceId;

  private List<Map<String, Object>> transactionsParameters;

  public MakeAccountFeeTransactionInput(String instanceId, String amount, String currency, String transactionSubType, List<Map<String, Object>> transactionsParameters)
  {
    this.instanceId = instanceId;
    this.amount = amount;
    this.currency = currency;
    this.transactionSubType = transactionSubType;
    this.transactionsParameters = transactionsParameters;
  }

  public String getAmount()
  {
    return amount;
  }

  public void setAmount(String amount)
  {
    this.amount = amount;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public String getTransactionSubType()
  {
    return transactionSubType;
  }

  public void setTransactionSubType()
  {
    this.transactionSubType = transactionSubType;
  }

  public List<Map<String, Object>> getTransactionsParameters()
  {
    return transactionsParameters;
  }

  public void setTransactionsParameters(List<Map<String, Object>> transactionsParameters)
  {
    this.transactionsParameters = transactionsParameters;
  }

  public String getInstanceId(String caseInstanceId)
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }
}
