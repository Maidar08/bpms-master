package mn.erin.domain.bpm.usecase.collateral;

/**
 * @author Tamir
 */
public class LinkCollateralsOutput
{
  private final boolean isLinked;

  public LinkCollateralsOutput(boolean isLinked)
  {
    this.isLinked = isLinked;
  }

  public boolean isLinked()
  {
    return isLinked;
  }
}
