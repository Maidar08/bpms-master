package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
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
public class GetUsersByRole extends AuthorizedUseCase<GetUsersByRoleInput, GetUsersByRoleOutput>
{
  private static final Permission permission = new AimModulePermission("GetUsersByRole");

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetUsersByRole()
  {
    this.membershipRepository = null;
    this.userRepository = null;
    this.tenantIdProvider = null;
  }

  public GetUsersByRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, UserRepository userRepository, TenantIdProvider tenantIdProvider)
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
    if (StringUtils.isBlank(input.getRoleId()) || input == null)
    {
      throw new UseCaseException("Invalid Inputs!");
    }

    try
    {
      List<User> usersByRole = new ArrayList<>();

      Collection<Membership> memberships = membershipRepository.listAllByRole(RoleId.valueOf(input.getRoleId()));
      Collection<User> allUsers = userRepository.getAllUsers(TenantId.valueOf("xac"));

      for (Membership membership : memberships)
      {
        User currentUserToAdd = findUserById(allUsers, membership.getUserId().getId());
        if (currentUserToAdd != null)
        {
          usersByRole.add(currentUserToAdd);
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
}
