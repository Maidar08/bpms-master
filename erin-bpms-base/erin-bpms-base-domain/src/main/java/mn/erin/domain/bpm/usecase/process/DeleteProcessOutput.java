package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class DeleteProcessOutput
{
  private final boolean isDeleted;

  public DeleteProcessOutput(boolean isDeleted)
  {
    this.isDeleted = isDeleted;
  }

  public boolean isDeleted()
  {
    return isDeleted;
  }
}
