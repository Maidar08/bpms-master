package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class GetGroupInput
{
  private final String id;

  public GetGroupInput(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }
}
