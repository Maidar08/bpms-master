package mn.erin.domain.bpm.usecase.process;

import java.util.Date;

public class GetProcessRequestsByDateInput
{
  private String groupId;
  private Date startCreatedDate;
  private Date endCreatedDate;

  public GetProcessRequestsByDateInput(String groupId, Date startCreatedDate, Date endCreatedDate)
  {
    this.groupId = groupId;
    this.startCreatedDate = startCreatedDate;
    this.endCreatedDate = endCreatedDate;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String parameterValue)
  {
    this.groupId = parameterValue;
  }

  public Date getStartDate()
  {
    return startCreatedDate;
  }

  public void setStartDate(Date startCreatedDate)
  {
    this.startCreatedDate = startCreatedDate;
  }

  public Date getEndDate()
  {
    return endCreatedDate;
  }

  public void setEndDate(Date endCreatedDate)
  {
    this.endCreatedDate = endCreatedDate;
  }
}
