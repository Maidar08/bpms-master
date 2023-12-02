package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class GetUsersInGroup extends AuthorizedUseCase<Void, GetUsersInGroupOutput>
{
  private final UserRepository userRepository;
  private final MembershipRepository membershipRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetUsersInGroup()
  {
    super();
    this.userRepository = null;
    this.membershipRepository = null;
    this.tenantIdProvider = null;
  }

  public GetUsersInGroup(AuthenticationService authenticationService,
      AuthorizationService authorizationService, UserRepository userRepository,
      MembershipRepository membershipRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.userRepository =  Objects.requireNonNull(userRepository, "UserRepository cannot be null!");
    this.membershipRepository =  Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "MembershipRepository cannot be null!");
  }

  private static final Permission permission = new AimModulePermission("GetUsersInGroup");

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUsersInGroupOutput executeImpl(Void input) throws UseCaseException
  {
    try
    {
      String currentUserId = authenticationService.getCurrentUserId();
      String tenantId = tenantIdProvider.getCurrentUserTenantId();

      List<Membership> memberships =  membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(currentUserId));

      //check if empty and then handle it
      if (memberships.isEmpty())
      {
        throw new UseCaseException("Current user does not have any memberships!");
      }

      Membership membership = memberships.get(0);

      GroupId groupId = membership.getGroupId();

      List<Membership> membershipsFilteredByGroup = membershipRepository.listAllByGroupId(TenantId.valueOf(tenantId), groupId);

      List<User> usersToReturn = new ArrayList<>();

      for (Membership membership1 : membershipsFilteredByGroup)
      {
        User user = userRepository.findById(membership1.getUserId());

        if (user != null)
        {
          usersToReturn.add(user);
        }
      }

      return new GetUsersInGroupOutput(usersToReturn);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
