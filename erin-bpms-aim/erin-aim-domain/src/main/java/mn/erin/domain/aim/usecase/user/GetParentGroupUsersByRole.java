package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetUsersByRole;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleInput;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleOutput;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetParentGroupUsersByRole extends AuthorizedUseCase<GetParentGroupUsersByRoleInput, GetParentGroupUsersByRoleOutput>
{
  private static final Permission permission = new AimModulePermission("GetParentGroupUsersByRole");

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;
  private final GroupRepository groupRepository;

  public GetParentGroupUsersByRole()
  {
    super();
    this.membershipRepository = null;
    this.userRepository = null;
    this.tenantIdProvider = null;
    this.groupRepository = null;
  }

  public GetParentGroupUsersByRole(AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository,
      UserRepository userRepository, TenantIdProvider tenantIdProvider, GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
    this.tenantIdProvider = tenantIdProvider;
    this.groupRepository = groupRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetParentGroupUsersByRoleOutput executeImpl(GetParentGroupUsersByRoleInput input) throws UseCaseException
  {
    //validations
    //null checks/handling
    //testing, edge cases

    if (input == null || StringUtils.isBlank(input.getRole()))
    {
      throw new UseCaseException("Invalid Role Input!");
    }

    GetUsersByRole getUsersByRole = new GetUsersByRole(authenticationService, authorizationService, membershipRepository, userRepository, tenantIdProvider);
    GetUsersByRoleInput getUsersByRoleInput = new GetUsersByRoleInput(input.getRole());
    GetUsersByRoleOutput getUsersByRoleOutput = getUsersByRole.execute(getUsersByRoleInput);

    Collection<User> allUsers = getUsersByRoleOutput.getAllUsers();

    Collection<User> allUsersByRole = getUsersByRoleOutput.getUsersByRole();
    List<User> allParentGroupUsers = getParentGroupUsers(allUsers);

    List<User> allParentGroupUsersByRole = new ArrayList<>();

    for (User userByRole : allUsersByRole)
    {
      for (User parentGroupUser : allParentGroupUsers)
      {
        if (userByRole.getUserId().getId().equals(parentGroupUser.getUserId().getId()))
        {
          allParentGroupUsersByRole.add(userByRole);
        }
      }
    }
    return new GetParentGroupUsersByRoleOutput(allParentGroupUsersByRole);
  }

  private List<User> getParentGroupUsers(Collection<User> allUsers) throws UseCaseException
  {
    try
    {
      String tenantId = tenantIdProvider.getCurrentUserTenantId();
      String userId = authenticationService.getCurrentUserId();

      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(userId));

      if (memberships.isEmpty())
      {
        return Collections.emptyList();
      }

      GroupId groupId = memberships.get(0).getGroupId();

      List<String> parentGroupIds = collectParentGroupIds(groupId);
      List<Membership> allMembershipsByGroup = new ArrayList<>();
      for (String parentGroupId : parentGroupIds)
      {
        List<Membership> returnedMembershipsByGroup = membershipRepository.listAllByGroupId(TenantId.valueOf(tenantId), GroupId.valueOf(parentGroupId));
        allMembershipsByGroup.addAll(returnedMembershipsByGroup);
      }

      List<User> allSubGroupUsers = new ArrayList<>();

      for (Membership membership : allMembershipsByGroup)
      {
        User subGroupUser = findUserById(allUsers, membership.getUserId().getId());
        if (subGroupUser != null)
        {
          allSubGroupUsers.add(subGroupUser);
        }
      }

      return allSubGroupUsers;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private User findUserById(Collection<User> allUsers, String userId)
  {
    for (User user : allUsers)
    {
      if (user.getUserId().getId().equalsIgnoreCase(userId))
      {
        return user;
      }
    }

    return null;
  }

  private List<String> collectParentGroupIds(GroupId groupId)
  {
    List<String> groupIds = new ArrayList<>();

    Queue<GroupId> groupsToTraverse = new LinkedList<>();
    groupsToTraverse.add(groupId);

    while (!groupsToTraverse.isEmpty())
    {
      GroupId currentGroupId = groupsToTraverse.peek();
      Group currentGroup = groupRepository.findById(currentGroupId);

      if (currentGroup.getParent() != null)
      {
        groupsToTraverse.add(currentGroup.getParent());
      }

      groupIds.add(currentGroupId.getId());
      groupsToTraverse.remove(currentGroupId);
    }
    return groupIds;
  }
}
