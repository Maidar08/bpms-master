package mn.erin.domain.aim.usecase.group;

import mn.erin.domain.aim.model.group.Group;

/**
 * @author Zorig
 */
public class RenameGroupOutput
{
  private final Group renamedGroup;

  public RenameGroupOutput(Group renamedGroup)
  {
    this.renamedGroup = renamedGroup;
  }

  public Group getRenamedGroup()
  {
    return renamedGroup;
  }
}
