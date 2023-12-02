package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetGroupUsersByRole extends AuthorizedUseCase<GetUsersByRoleInput, GetUsersByRoleOutput>
{
  private static final Permission permission = new AimModulePermission("GetGroupUsersByRole");

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetGroupUsersByRole(AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository,
      UserRepository userRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository =  Objects.requireNonNull(membershipRepository, "GroupRepository cannot be null!");
    this.userRepository =  Objects.requireNonNull(userRepository, "UserRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUsersByRoleOutput executeImpl(GetUsersByRoleInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getRoleId()))
    {
      throw new UseCaseException("Invalid Inputs!");
    }

    try
    {
      List<User> usersByRole = new ArrayList<>();

      String tenantId = tenantIdProvider.getCurrentUserTenantId();

      List<Membership> membershipsByUserId = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(authenticationService.getCurrentUserId()));

      if (membershipsByUserId.isEmpty() || membershipsByUserId.get(0).getGroupId() == null)
      {
        return new GetUsersByRoleOutput(Collections.emptyList(), Collections.emptyList());
      }

      GroupId groupId = membershipsByUserId.get(0).getGroupId();

      List<Membership> memberships = membershipRepository.listAllByGroupId(TenantId.valueOf(tenantId), groupId);

      Collection<User> allUsers = userRepository.getAllUsers(TenantId.valueOf(tenantId));

      for (Membership membership : memberships)
      {
        if (doesMatch(membership, input))
        {
          User currentUserToAdd = findUserById(allUsers, membership.getUserId().getId());
          if (currentUserToAdd != null)
          {
            usersByRole.add(currentUserToAdd);
          }
        }
      }

      return new GetUsersByRoleOutput(usersByRole, allUsers);
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

  private boolean doesMatch(Membership membership, GetUsersByRoleInput input)
  {
    if (membership.getRoleId() == null)
    {
      return false;
    }
    final String memberShipRoleId = membership.getRoleId().getId();
    final String roleId = input.getRoleId();
    final List<String> roleIdList = input.getRoleIdList();
    if (roleIdList == null || roleIdList.isEmpty())
    {
      return memberShipRoleId.equals(roleId);
    }
    return roleIdList.contains(memberShipRoleId);
  }
}
