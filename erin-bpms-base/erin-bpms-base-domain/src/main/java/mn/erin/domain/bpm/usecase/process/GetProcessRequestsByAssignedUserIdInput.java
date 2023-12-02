package mn.erin.domain.bpm.usecase.process;

import java.util.Date;

/**
 * @author Zorig
 */
public class GetProcessRequestsByAssignedUserIdInput
{
  private final String assignedUserId;
  private Date startDate;
  private Date endDate;

  public GetProcessRequestsByAssignedUserIdInput(String assignedUserId)
  {
    this.assignedUserId = assignedUserId;
  }

  public GetProcessRequestsByAssignedUserIdInput(String assignedUserId, Date startDate, Date endDate)
  {
    this.assignedUserId = assignedUserId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getAssignedUserId()
  {
    return assignedUserId;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }
}
