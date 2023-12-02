package mn.erin.domain.bpm.usecase.process;

import java.util.Date;

public class GetUnassignedRequestsByChannelWithFilterInput
{
  private final String channelType;
  private final String groupId;
  private final Date startDate;
  private final Date endDate;

  public GetUnassignedRequestsByChannelWithFilterInput(String channelType, String groupId, Date startDate, Date endDate)
  {
    this.channelType = channelType;
    this.groupId = groupId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getChannelType()
  {
    return channelType;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }
}
