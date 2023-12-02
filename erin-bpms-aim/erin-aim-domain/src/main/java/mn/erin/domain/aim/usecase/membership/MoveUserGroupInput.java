package mn.erin.domain.aim.usecase.membership;

/**
 * @author Zorig
 */
public class MoveUserGroupInput
{
  private final String userId;
  private final String newGroupId;

  public MoveUserGroupInput(String userId, String newGroupId)
  {
    this.userId = userId;
    this.newGroupId = newGroupId;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getNewGroupId()
  {
    return newGroupId;
  }
}
