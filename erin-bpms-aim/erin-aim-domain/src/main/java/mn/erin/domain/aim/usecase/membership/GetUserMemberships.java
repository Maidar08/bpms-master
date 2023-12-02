package mn.erin.domain.aim.usecase.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserMemberships extends AuthorizedUseCase<GetUserMembershipsInput, List<GetMembershipOutput>>
{
  private static final Permission permission = new AimModulePermission("GetUserMemberships");
  private final MembershipRepository membershipRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetUserMemberships()
  {
    super();
    this.membershipRepository = null;
    this.tenantIdProvider = null;
  }

  public GetUserMemberships(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected List<GetMembershipOutput> executeImpl(GetUserMembershipsInput input) throws UseCaseException
  {
    validateNotNull(input, "Input is required to get memberships");
    try
    {
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()),
          UserId.valueOf(input.getUserId()));

      List<GetMembershipOutput> result = new ArrayList<>();

      for (Membership membership : memberships)
      {
        result.add(convert(membership));
      }

      return result;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }

  private GetMembershipOutput convert(Membership membership)
  {
    GetMembershipOutput output = new GetMembershipOutput();
    output.setMembershipId(membership.getMembershipId().getId());
    output.setGroupId(membership.getGroupId().getId());
    output.setRoleId(membership.getRoleId().getId());

    return output;
  }
}
