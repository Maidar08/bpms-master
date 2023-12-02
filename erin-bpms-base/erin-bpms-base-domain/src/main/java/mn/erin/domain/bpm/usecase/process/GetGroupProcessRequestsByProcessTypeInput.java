package mn.erin.domain.bpm.usecase.process;

public class GetGroupProcessRequestsByProcessTypeInput
{

  public String groupId;
  public String processTypeId;

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public GetGroupProcessRequestsByProcessTypeInput(String groupId, String processTypeId)
  {
    this.groupId = groupId;
    this.processTypeId = processTypeId;
  }
}
