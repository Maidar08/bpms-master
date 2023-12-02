package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class UpdateAssignedUserOutput
{
  private final boolean isUpdated;

  public UpdateAssignedUserOutput(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }
}
