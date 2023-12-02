package mn.erin.domain.aim.usecase.group;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetGroupSubTree extends AuthorizedUseCase<GetGroupSubTreeInput, GetGroupSubTreeOutput>
{
  private static final Permission permission = new AimModulePermission("GetGroupSubTree");
  private final GroupRepository groupRepository;

  public GetGroupSubTree()
  {
    super();
    this.groupRepository = null;
  }

  public GetGroupSubTree(AuthenticationService authenticationService, AuthorizationService authorizationService,
      GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetGroupSubTreeOutput executeImpl(GetGroupSubTreeInput input) throws UseCaseException
  {
    String groupId = Validate.notNull(input.getGroupId());

    Collection<Group> groups = groupRepository.findAll();

    //queue of id strings
    Queue<String> groupsToTraverse = new PriorityQueue<>();
    //hashmap used to keep track/link parent to children
    Map<String, GroupTree> map = new HashMap<>();

    groupsToTraverse.add(groupId);

    String groupIdToDo;

    while (!groupsToTraverse.isEmpty())
    {
      groupIdToDo = groupsToTraverse.peek();

      Group mappedGroup = findGroupById(groups, groupIdToDo);

      String parentId = null;

      if(null == mappedGroup)
      {
        //remove peek
        groupsToTraverse.remove(groupIdToDo);
        continue;
      }

      if (mappedGroup.getParent() != null)
      {
        parentId = mappedGroup.getParent().getId();
      }

      GroupTree currentGroup = new GroupTree(mappedGroup.getId().getId(), parentId, mappedGroup.getTenantId().getId(), mappedGroup.getNumber());
      currentGroup.setName(mappedGroup.getName());
      currentGroup.setNthSibling(mappedGroup.getNthSibling());

      //load queue for other iterations
      Iterator<GroupId> childrenIterator = mappedGroup.getChildren().iterator();
      while (childrenIterator.hasNext())
      {
        String nextChild = childrenIterator.next().getId();
        groupsToTraverse.add(nextChild);
      }

      // set/connect child(s) with parent
      if (currentGroup.getParent() != null &&  !currentGroup.getId().equals(groupId))
      {
        GroupTree parentObject = map.get(currentGroup.getParent());
        parentObject.addChild(currentGroup);
      }

      map.put(groupIdToDo, currentGroup);

      //remove peek
      groupsToTraverse.remove(groupIdToDo);
    }

    return new GetGroupSubTreeOutput(map.get(groupId));
  }

  private Group findGroupById(Collection<Group> groups, String groupId)
  {
    for (Group group : groups)
    {
      if (group.getId().getId().equals(groupId))
      {
        return group;
      }
    }
    return null;
  }
}


