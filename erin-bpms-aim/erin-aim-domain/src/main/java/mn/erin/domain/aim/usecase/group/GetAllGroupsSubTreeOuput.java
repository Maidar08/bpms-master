package mn.erin.domain.aim.usecase.group;

import java.util.List;

import mn.erin.domain.aim.model.group.GroupTree;

/**
 * @author Zorig
 */
public class GetAllGroupsSubTreeOuput
{
  private final List<GroupTree> groupTreeList;

  public GetAllGroupsSubTreeOuput(List<GroupTree> groupTreeList)
  {
    this.groupTreeList = groupTreeList;
  }

  public List<GroupTree> getGroupTreeList()
  {
    return groupTreeList;
  }
}
