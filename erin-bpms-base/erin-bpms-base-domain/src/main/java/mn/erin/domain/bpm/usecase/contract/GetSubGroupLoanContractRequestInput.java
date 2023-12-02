package mn.erin.domain.bpm.usecase.contract;

import java.util.Date;

public class GetSubGroupLoanContractRequestInput
{
  private String groupId;
  private Date startDate;
  private Date endDate;

  public GetSubGroupLoanContractRequestInput(String groupId, Date startDate, Date endDate)
  {
    this.groupId = groupId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
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
