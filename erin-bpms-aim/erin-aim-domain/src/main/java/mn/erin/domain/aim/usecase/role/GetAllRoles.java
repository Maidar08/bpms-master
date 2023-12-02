package mn.erin.domain.aim.usecase.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllRoles extends AuthorizedUseCase<Void, List<GetRoleOutput>>
{
  private static final Permission permission = new AimModulePermission("GetAllRoles");
  private final RoleRepository roleRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetAllRoles()
  {
    super();
    this.roleRepository = null;
    this.tenantIdProvider = null;
  }

  public GetAllRoles(AuthenticationService authenticationService, AuthorizationService authorizationService,
      RoleRepository roleRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.roleRepository = Objects.requireNonNull(roleRepository, "RoleRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<GetRoleOutput> executeImpl(Void input) throws UseCaseException
  {
    try
    {
      List<Role> allRoles = roleRepository.listAll(TenantId.valueOf(this.tenantIdProvider.getCurrentUserTenantId()));

      List<GetRoleOutput> result = new ArrayList<>();

      for (Role role : allRoles)
      {
        result.add(new GetRoleOutput(role.getRoleId().getId(), role.getName()));
      }

      return result;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

  }
}
