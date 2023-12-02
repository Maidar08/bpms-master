package mn.erin.domain.aim.usecase.group;

import mn.erin.domain.aim.model.group.Group;

/**
 * @author Zorig
 */
public class MoveGroupParentOutput
{
  private final Group movedGroup;

  public MoveGroupParentOutput(Group movedGroup)
  {
    this.movedGroup = movedGroup;
  }

  public Group getMovedGroup()
  {
    return movedGroup;
  }
}
