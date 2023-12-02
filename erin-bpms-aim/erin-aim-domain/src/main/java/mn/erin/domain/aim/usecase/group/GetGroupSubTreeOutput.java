package mn.erin.domain.aim.usecase.group;

import mn.erin.domain.aim.model.group.GroupTree;

/**
 * @author Zorig
 */
public class GetGroupSubTreeOutput
{
  private final GroupTree groupTree;

  public GetGroupSubTreeOutput(GroupTree groupTree)
  {
    this.groupTree = groupTree;
  }

  public GroupTree getGroupTree()
  {
    return groupTree;
  }
}
