package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class GetUnassignedRequestsByChannelInput
{
  private final String channelType;
  private final String groupId;

  public GetUnassignedRequestsByChannelInput(String channelType, String groupId)
  {
    this.channelType = channelType;
    this.groupId = groupId;
  }

  public String getChannelType()
  {
    return channelType;
  }

  public String getGroupId()
  {
    return groupId;
  }
}
