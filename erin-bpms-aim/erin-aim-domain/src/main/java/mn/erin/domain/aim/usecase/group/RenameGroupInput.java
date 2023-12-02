package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class RenameGroupInput
{
  private final String groupId;
  private final String name;

  public RenameGroupInput(String groupId, String name)
  {
    this.groupId = groupId;
    this.name = name;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getName()
  {
    return name;
  }
}
