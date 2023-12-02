package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;


public class GetCurrentUserLoginBody extends AbstractUseCase<Void, LoginUserOutput>
{
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetCurrentUserLoginBody(AuthenticationService authenticationService, MembershipRepository membershipRepository,
      RoleRepository roleRepository, TenantIdProvider tenantIdProvider)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository is required!");
    this.roleRepository = Objects.requireNonNull(roleRepository, "RoleRepository is required!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider is required!");
  }

  @Override
  public LoginUserOutput execute(Void input) throws UseCaseException
  {
    String token = authenticationService.getToken();
    String userId = authenticationService.getCurrentUserId();

    List<String> permissions;
    List<String> groups;

    Membership membership = getMembership(userId);
    String roleId = membership.getRoleId().getId();

    if (AimConstants.ADMIN_USER_ID.equals(userId))
    {
      permissions = getPermissions(membership);
      permissions.add("*");
    }
    else
    {
      permissions = getPermissions(membership);
    }

    groups = getGroups(membership);

    return new LoginUserOutput(token, roleId, groups, permissions);
  }

  private Membership getMembership(String userId) throws UseCaseException
  {
    try
    {
      String tenantId = tenantIdProvider.getCurrentUserTenantId();
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(userId));

      if (memberships.isEmpty())
      {
        throw new UseCaseException("User doesn't have any memberships!");
      }
      return memberships.get(0);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private List<String> getPermissions(Membership membership)
  {
    List<String> permissions = new ArrayList<>();
    RoleId roleId = membership.getRoleId();
    Role role = roleRepository.findById(roleId);

    for (Permission permission : role.getPermissions())
    {
      String permissionString = permission.getPermissionString();

      if (!permissions.contains(permissionString))
      {
        permissions.add(permissionString);
      }
    }
    return permissions;
  }

  private List<String> getGroups(Membership membership)
  {
    List<String> groups = new ArrayList<>();
    String groupId = membership.getGroupId().getId();

    if (!groups.contains(groupId))
    {
      groups.add(groupId);
    }
    return groups;
  }
}
