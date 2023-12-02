package mn.erin.domain.bpm.usecase.process;
/**
 @author Lkahvadorj.A
 **/
public class RemoveProcessOutput
{
  private boolean isRemoved;
  public RemoveProcessOutput(boolean removeMessage)
  {
    this.isRemoved = removeMessage;
  }

  public boolean isRemoved()
  {
    return isRemoved;
  }
}
