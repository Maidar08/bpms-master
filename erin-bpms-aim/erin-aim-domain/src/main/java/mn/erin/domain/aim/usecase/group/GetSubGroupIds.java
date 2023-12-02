package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

public class GetSubGroupIds extends AuthorizedUseCase<String, List<String>>
{
  private static final Permission permission = new AimModulePermission("GetSubGroupIds");

  private final GroupRepository groupRepository;

  public GetSubGroupIds()
  {
    super();
    this.groupRepository = null;
  }

  public GetSubGroupIds(AuthenticationService authenticationService, AuthorizationService authorizationService, GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = groupRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<String> executeImpl(String input) throws UseCaseException
  {
    GetGroupSubTree getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, groupRepository);
    GetGroupSubTreeOutput getGroupSubTreeOutput = getGroupSubTree.execute(new GetGroupSubTreeInput(input));//get sub group ids
    GroupTree groupTree = getGroupSubTreeOutput.getGroupTree();

    return collectSubGroupIds(groupTree);
  }

    private List<String> collectSubGroupIds(GroupTree groupTree)
    {
      List<String> groupIds = new ArrayList<>();

      Queue<GroupTree> groupsToTraverse = new LinkedList<>();

      groupsToTraverse.add(groupTree);

      while (!groupsToTraverse.isEmpty())
      {
        GroupTree currentGroupTree = groupsToTraverse.peek();

        List<GroupTree> children = currentGroupTree.getChildren();
        Iterator<GroupTree> childrenIterator = children.iterator();

        while (childrenIterator.hasNext())
        {
          groupsToTraverse.add(childrenIterator.next());
        }

        groupIds.add(currentGroupTree.getId());
        groupsToTraverse.remove(currentGroupTree);
      }
      return groupIds;
    }
}
