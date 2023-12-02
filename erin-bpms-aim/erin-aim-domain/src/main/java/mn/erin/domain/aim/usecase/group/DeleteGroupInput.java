package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class DeleteGroupInput
{
  private final String id;

  public DeleteGroupInput(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }
}
