package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class SaveSalariesOutput
{
  private final boolean saved;

  public SaveSalariesOutput(boolean saved)
  {
    this.saved = saved;
  }

  public boolean isSaved()
  {
    return saved;
  }
}
