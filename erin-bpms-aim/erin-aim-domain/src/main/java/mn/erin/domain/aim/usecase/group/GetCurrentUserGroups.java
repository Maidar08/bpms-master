package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCurrentUserGroups extends AbstractUseCase<Void, List<GroupTree>>
{
  private static final String APPLICATION_ID = "platform";
  private static final String MODULE_ID = "aim";

  private static final Permission[] GROUP_PERMISSIONS = {
      new Permission(APPLICATION_ID, MODULE_ID, "GetGroupSubTree"), // READ
      new Permission(APPLICATION_ID, MODULE_ID, "CreateGroup"), // CREATE
      new Permission(APPLICATION_ID, MODULE_ID, "DeleteGroup"), // DELETE
      new Permission(APPLICATION_ID, MODULE_ID, "RenameGroup") // RENAME
  };

  private final AuthenticationService authenticationService;
  private final TenantIdProvider tenantIdProvider;
  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;

  private GetGroupSubTree getGroupSubTree;
  private GetAllGroupsSubTree getAllGroupsSubTree;

  public GetCurrentUserGroups(AuthenticationService authenticationService, AuthorizationService authorizationService, TenantIdProvider tenantIdProvider,
      GroupRepository groupRepository, MembershipRepository membershipRepository, RoleRepository roleRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
    this.roleRepository = Objects.requireNonNull(roleRepository, "RoleRepository cannot be null!");
    Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
    this.getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, groupRepository);
    this.getAllGroupsSubTree = new GetAllGroupsSubTree(groupRepository, getGroupSubTree, tenantIdProvider);
  }

  @Override
  public List<GroupTree> execute(Void input) throws UseCaseException
  {
    String currentUser = authenticationService.getCurrentUserId();
    validateNotNull(currentUser, "Current user not found!");

    if (AimConstants.ADMIN_USER_ID.equals(currentUser))
    {
      GetAllGroupsSubTreeOuput output = getAllGroupsSubTree.execute(null);
      return output.getGroupTreeList();
    }

    try
    {
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()), UserId.valueOf(currentUser));

      List<GroupTree> result = new ArrayList<>();

      for (Membership membership : memberships)
      {
        GetGroupSubTreeInput getGroupSubTreeInput = new GetGroupSubTreeInput(membership.getGroupId().getId());
        GetGroupSubTreeOutput output = getGroupSubTree.execute(getGroupSubTreeInput);
        GroupTree groupTree = output.getGroupTree();
        Role role = roleRepository.findById(membership.getRoleId());
        addGroupPermission(groupTree, role);
        result.add(groupTree);
      }

      return result;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }

  private void addGroupPermission(GroupTree groupTree, Role role)
  {
    Collection<Permission> permissions = role.getPermissions();

    Collection<Permission> groupPermissions = new ArrayList<>();

    for (Permission permission : permissions)
    {
      for (Permission groupPermission : GROUP_PERMISSIONS)
      {
        if (permission.equals(groupPermission))
        {
          groupPermissions.add(groupPermission);
        }
      }
    }

    groupTree.setPermissions(groupPermissions);
  }
}
