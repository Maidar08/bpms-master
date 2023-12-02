package mn.erin.domain.aim.usecase.group;

import java.util.List;

import mn.erin.domain.aim.model.group.GroupTree;

/**
 * @author Zorig
 */
public class GetAllGroupsOutput
{
  private final List<GroupTree> startingTreeGroupNodes;

  public GetAllGroupsOutput(List<GroupTree> startingTreeGroupNodes)
  {
    this.startingTreeGroupNodes = startingTreeGroupNodes;
  }

  public List<GroupTree> getStartingTreeGroupNodes()
  {
    return startingTreeGroupNodes;
  }
}
