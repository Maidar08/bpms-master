package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class GetGroupSubTreeInput
{
  private final String groupId;

  public GetGroupSubTreeInput(String groupId)
  {
    this.groupId = groupId;
  }

  public String getGroupId()
  {
    return groupId;
  }
}
