package mn.erin.domain.aim.usecase.group;

import mn.erin.domain.aim.model.group.Group;

/**
 * @author Zorig
 */
public class MoveGroupSiblingOutput
{
  private final Group updatedGroup;

  public MoveGroupSiblingOutput(Group updatedGroup)
  {
    this.updatedGroup = updatedGroup;
  }

  public Group getUpdatedGroup()
  {
    return updatedGroup;
  }
}
