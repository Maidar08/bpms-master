package mn.erin.domain.aim.usecase.membership;

import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeleteMembership extends AuthorizedUseCase<String, Boolean>
{
  private static final Permission permission = new AimModulePermission("DeleteMembership");

  private final MembershipRepository membershipRepository;

  public DeleteMembership()
  {
    super();
    this.membershipRepository = null;
  }

  public DeleteMembership(AuthenticationService authenticationService,
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
  protected Boolean executeImpl(String membershipId) throws UseCaseException
  {
    validateNotBlank(membershipId, "MembershipId cannot be null or blank!");

    try
    {
      return membershipRepository.delete(MembershipId.valueOf(membershipId));
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
