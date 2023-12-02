package mn.erin.domain.bpm.usecase.process;

/**
 * @author Tamir
 */
public class DeleteProcessParametersOutput
{
  private int numDeleted;

  public DeleteProcessParametersOutput(int numDeleted)
  {
    this.numDeleted = numDeleted;
  }

  public int getNumDeleted()
  {
    return numDeleted;
  }

  public void setNumDeleted(int numDeleted)
  {
    this.numDeleted = numDeleted;
  }
}
