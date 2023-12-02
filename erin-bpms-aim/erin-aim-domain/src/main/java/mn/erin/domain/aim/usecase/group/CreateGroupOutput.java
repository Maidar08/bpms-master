package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class CreateGroupOutput
{
  private final String id;
  private final int nthSibling;

  public CreateGroupOutput(String id, int nthSibling)
  {
    this.id = id;
    this.nthSibling = nthSibling;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }

  public String getId()
  {
    return id;
  }
}
