package mn.erin.domain.aim.usecase.membership;

/**
 * @author Zorig
 */
public class MoveUserGroupOutput
{
  private final boolean isUpdated;

  public MoveUserGroupOutput(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }
}
