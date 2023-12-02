package mn.erin.domain.bpm.usecase.branch_banking.transaction;

/**
 * @author Bilguunbor
 **/
public class GetTransactionEbankListInput
{
  private String accountId;
  private String channelId;
  private String channel;
  private String startDt;
  private String endDt;
  private String instanceId;

  public GetTransactionEbankListInput(String accountId, String channelId,  String channel, String startDt, String endDt, String instanceId)
  {
    this.accountId = accountId;
    this.channelId = channelId;
    this.channel = channel;
    this.startDt = startDt;
    this.endDt = endDt;
    this.instanceId = instanceId;
  }

  public String getAccountId()
  {
    return accountId;
  }

  public void setAccountId(String accountId)
  {
    this.accountId = accountId;
  }

  public String getChannelId()
  {
    return channelId;
  }

  public void setChannelId(String channelId)
  {
    this.channelId = channelId;
  }

  public String getStartDt()
  {
    return startDt;
  }

  public void setStartDt(String startDt)
  {
    this.startDt = startDt;
  }

  public String getEndDt()
  {
    return endDt;
  }

  public void setEndDt(String endDt)
  {
    this.endDt = endDt;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getChannel() { return channel; }

  public void setChannel(String channel) { this.channel = channel; }
}
