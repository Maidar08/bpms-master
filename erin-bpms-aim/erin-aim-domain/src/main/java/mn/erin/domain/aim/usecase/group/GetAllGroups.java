package mn.erin.domain.aim.usecase.group;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetAllGroups extends AuthorizedUseCase<Void, List<Group>>
{
  private static final Permission permission = new AimModulePermission("GetAllGroups");
  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetAllGroups(AuthenticationService authenticationService,
    AuthorizationService authorizationService, GroupRepository groupRepository,
    TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  public List<Group> executeImpl(Void input) throws UseCaseException
  {
    try
    {
      TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
      return groupRepository.getAllRootGroups(tenantId);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }
}
