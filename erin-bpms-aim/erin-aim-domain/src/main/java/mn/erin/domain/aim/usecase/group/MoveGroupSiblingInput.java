package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class MoveGroupSiblingInput
{
  private final String groupId;
  private final int nthSibling;

  public MoveGroupSiblingInput(String groupId, int nthSibling)
  {
    this.groupId = groupId;
    this.nthSibling = nthSibling;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }
}
