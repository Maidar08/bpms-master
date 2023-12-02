package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class CreateGroupInput
{
  private final String parentId;
  private final String id;
  private String name;

  public CreateGroupInput(String parentId, String id, String name)
  {
    this.parentId = parentId;
    this.id = id;
    this.name = name;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
