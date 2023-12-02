package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class UpdateRequestParametersOutput
{
  private final boolean isUpdated;

  public UpdateRequestParametersOutput(boolean isUpdated)
  {
    this.isUpdated = isUpdated;
  }

  public boolean isUpdated()
  {
    return isUpdated;
  }
}
