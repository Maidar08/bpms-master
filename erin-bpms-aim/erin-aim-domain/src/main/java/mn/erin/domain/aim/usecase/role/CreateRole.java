package mn.erin.domain.aim.usecase.role;

import java.util.ArrayList;
import java.util.Collection;
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
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateRole extends AuthorizedUseCase<CreateRoleInput, CreateRoleOutput>
{
  private static final Permission permission = new AimModulePermission("CreateRole");

  private final RoleRepository roleRepository;
  private final TenantIdProvider tenantIdProvider;
  private final PermissionService permissionService;

  public CreateRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
    RoleRepository roleRepository, TenantIdProvider tenantIdProvider, PermissionService permissionService)
  {
    super(authenticationService, authorizationService);
    this.roleRepository = Objects.requireNonNull(roleRepository, "RoleRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
    this.permissionService = Objects.requireNonNull(permissionService, "PermissionService cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected CreateRoleOutput executeImpl(CreateRoleInput input) throws UseCaseException
  {
    validateNotNull(input, "Input is required to create a role");

    Collection<String> allPermissions = permissionService.findAllPermissions();
    List<Permission> permissionList = new ArrayList<>();

    for (String permissionString : input.getPermissions())
    {
      boolean exists = allPermissions.stream().anyMatch(p -> p.equals(p));
      if (!exists)
      {
        throw new UseCaseException("Unknown permission: [" + permissionString + "]");
      }
      else
      {
        permissionList.add(permission);
      }
    }

    try
    {
      Role role = roleRepository.create(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()), input.getRoleName(),
        input.getRoleDescription(), permissionList);

      return new CreateRoleOutput(role.getRoleId().getId());
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
