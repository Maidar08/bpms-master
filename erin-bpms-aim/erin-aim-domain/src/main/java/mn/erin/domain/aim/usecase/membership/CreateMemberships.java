package mn.erin.domain.aim.usecase.membership;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateMemberships extends AuthorizedUseCase<CreateMembershipsInput, List<GetMembershipOutput>>
{
  private static final Permission permission = new AimModulePermission("CreateMemberships");
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateMemberships.class);

  private final MembershipRepository membershipRepository;

  public CreateMemberships()
  {
    super();
    this.membershipRepository = null;
  }

  public CreateMemberships(AuthenticationService authenticationService,
      AuthorizationService authorizationService, MembershipRepository membershipRepository)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<GetMembershipOutput> executeImpl(CreateMembershipsInput input) throws UseCaseException
  {
    validateNotNull(input, "Input is required to create a membership");

    List<GetMembershipOutput> result = new ArrayList<>();

    for (String userId : input.getUsers())
    {
      try
      {
        Membership membership = membershipRepository.create(UserId.valueOf(userId), GroupId.valueOf(input.getGroupId()), RoleId.valueOf(input.getRoleId()), new TenantId(input.getTenantId()));
        result.add(convert(membership));
      }
      catch (AimRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    if (result.isEmpty())
    {
      throw new UseCaseException("No membership has been created in the group with the ID: [" + input.getGroupId() + "]");
    }

    return result;
  }

  private GetMembershipOutput convert(Membership membership)
  {
    GetMembershipOutput output = new GetMembershipOutput();

    output.setMembershipId(membership.getMembershipId().getId());
    output.setGroupId(membership.getGroupId().getId());
    output.setRoleId(membership.getRoleId().getId());
    output.setUserId(membership.getUserId().getId());
    output.setTenantId(membership.getTenantId().getId());

    return output;
  }
}
