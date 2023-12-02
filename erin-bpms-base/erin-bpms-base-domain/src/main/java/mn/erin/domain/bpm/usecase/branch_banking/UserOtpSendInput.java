package mn.erin.domain.bpm.usecase.branch_banking;

public class UserOtpSendInput
{
  private String channel;
  private String destination;
  private String instanceId;

  public UserOtpSendInput(String channel, String destination, String instanceId)
  {
    this.channel = channel;
    this.destination = destination;
    this.instanceId = instanceId;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getDestination()
  {
    return destination;
  }

  public void setDestination(String destination)
  {
    this.destination = destination;
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }
}
