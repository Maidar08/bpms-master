package mn.erin.domain.bpm.usecase.process;

public class GetProcessRequestBySearchKeyInput extends GetProcessRequestsByLoanRequestTypeInput
{
  private final String searchKey;

  public GetProcessRequestBySearchKeyInput(String searchKey, String loanRequestType, String groupId, String channelType) {
    super(loanRequestType, groupId, channelType);
    this.searchKey = searchKey;
  }

  public String getSearchKey()
  {
    return searchKey;
  }
}
