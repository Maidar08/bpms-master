package mn.erin.domain.bpm.usecase.process;

public class GetProcessRequestsByLoanRequestTypeInput
{
  private final String loanRequestType;
  private final String groupId;
  private final String channelType;

  public GetProcessRequestsByLoanRequestTypeInput(String loanRequestType, String groupId, String channelType)
  {
    this.loanRequestType = loanRequestType;
    this.groupId = groupId;
    this.channelType = channelType;
  }

  public String getLoanRequestType()
  {
    return loanRequestType;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getChannelType()
  {
    return channelType;
  }

  public GetProcessRequestsByLoanRequestTypeInput getProcessRequestsByLoanRequestTypeInput()
  {
    return this;
  }
}
