package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class UpdateProcessParametersOutput
{
  private final int numUpdated;

  public UpdateProcessParametersOutput(int numUpdated)
  {
    this.numUpdated = numUpdated;
  }

  public int getNumUpdated()
  {
    return numUpdated;
  }
}
