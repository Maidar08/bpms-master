package mn.erin.domain.aim.usecase.membership;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class MoveUserGroup extends AuthorizedUseCase<MoveUserGroupInput, MoveUserGroupOutput>
{
  private static final Permission permission = new AimModulePermission("MoveUserGroup");
  private static final Logger LOGGER = LoggerFactory.getLogger(MoveUserGroup.class);

  private final MembershipRepository membershipRepository;
  private final TenantIdProvider tenantIdProvider;

  public MoveUserGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected MoveUserGroupOutput executeImpl(MoveUserGroupInput input) throws UseCaseException
  {
    validateNotNull(input, "Input must not be null");
    validateNotNull(input.getUserId(), "User Id input must not be null");
    validateNotNull(input.getNewGroupId(), "New Group Id input must not be null");

    TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
    UserId userId = new UserId(input.getUserId());
    GroupId newGroupId = new GroupId(input.getNewGroupId());

    //TODO:logic doesn't make sense after allowing multiple memberships per user, implement a different way when needed
    try
    {
      List<Membership> membershipList = membershipRepository.listAllByUserId(tenantId, userId);
      Iterator<Membership> membershipIterator = membershipList.iterator();

      Membership currentMembership = membershipIterator.next();
      MembershipId membershipIdToUpdate = MembershipId.valueOf(currentMembership.getMembershipId().getId());
      RoleId roleId = currentMembership.getRoleId();

      membershipRepository.delete(membershipIdToUpdate);
      membershipRepository.create(userId, newGroupId, roleId, tenantId);
    }
    catch (Exception e)
    {
      throw new UseCaseException("Problem Updating User Group");
    }

    return new MoveUserGroupOutput(true);
  }
}
