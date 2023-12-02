package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class DeleteGroupOutput
{
  private final boolean deleted;

  public DeleteGroupOutput(boolean deleted)
  {
    this.deleted = deleted;
  }

  public boolean isDeleted()
  {
    return deleted;
  }
}
