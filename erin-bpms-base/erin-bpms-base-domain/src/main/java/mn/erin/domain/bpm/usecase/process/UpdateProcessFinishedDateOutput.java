package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class UpdateProcessFinishedDateOutput
{
  private final boolean isUpdated;

  public UpdateProcessFinishedDateOutput(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }
}
