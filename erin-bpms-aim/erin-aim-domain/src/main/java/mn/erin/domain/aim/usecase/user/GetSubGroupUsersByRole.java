package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
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
import mn.erin.domain.aim.usecase.group.GetGroupSubTree;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeInput;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeOutput;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetSubGroupUsersByRole extends AuthorizedUseCase<GetSubGroupUsersByRoleInput, GetSubGroupUsersByRoleOutput>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetSubGroupUsersByRole.class);

  private static final Permission permission = new AimModulePermission("GetSubGroupUsersByRole");

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;
  private final GroupRepository groupRepository;

  public GetSubGroupUsersByRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, UserRepository userRepository, TenantIdProvider tenantIdProvider, GroupRepository groupRepository)
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
  protected GetSubGroupUsersByRoleOutput executeImpl(GetSubGroupUsersByRoleInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getRole()))
    {
      throw new UseCaseException("Invalid Role Input!");
    }

    String role = input.getRole();
    List<User> allSubGroupUsers = getSubGroupUsers(role);

    return new GetSubGroupUsersByRoleOutput(allSubGroupUsers);
  }

  private List<User> getSubGroupUsers(String role) throws UseCaseException
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

      LOG.info("####################### GETS GROUP SUB TREE BY GROUP ID = [{}]", groupId.getId());

      GetGroupSubTree getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, groupRepository);
      GetGroupSubTreeOutput getGroupSubTreeOutput = getGroupSubTree.execute(new GetGroupSubTreeInput(groupId.getId()));
      GroupTree groupTree = getGroupSubTreeOutput.getGroupTree();

      List<String> subGroupIds = collectSubGroupIds(groupTree);

      String subGroupIdStr = subGroupIds.stream().map(Object::toString)
          .collect(Collectors.joining(","));

      LOG.info("####################### FOUND SUB GROUP IDs = [{}]", subGroupIdStr);

      List<Membership> allMembership = new ArrayList<>();

      for (String subGroupId : subGroupIds)
      {
        List<Membership> returnedMembershipsByGroup = membershipRepository
            .listAllBy(TenantId.valueOf(tenantId), GroupId.valueOf(subGroupId), RoleId.valueOf(role));
        allMembership.addAll(returnedMembershipsByGroup);
      }

      LOG.info("####################### FOUND ALL MEMBERSHIP SIZE = [{}] WITH ROLE ID = [{}]", allMembership.size(), role);

      List<User> allSubGroupUsers = new ArrayList<>();

      for (Membership membership : allMembership)
      {
        User subGroupUser = userRepository.findById(membership.getUserId());
        if (subGroupUser != null)
        {
          allSubGroupUsers.add(subGroupUser);
        }
      }

      LOG.info("#################### FOUND ALL USER = [{}] OF SUB GROUP WITH ROLE ID = [{}]", allSubGroupUsers.size(), role);
      return allSubGroupUsers;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
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
