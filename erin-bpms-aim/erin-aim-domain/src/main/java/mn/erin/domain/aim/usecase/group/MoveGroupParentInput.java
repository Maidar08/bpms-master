package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class MoveGroupParentInput
{
  private final String groupId;
  private final String parentId;

  public MoveGroupParentInput(String groupId, String parentId)
  {
    this.groupId = groupId;
    this.parentId = parentId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getParentId()
  {
    return parentId;
  }
}
