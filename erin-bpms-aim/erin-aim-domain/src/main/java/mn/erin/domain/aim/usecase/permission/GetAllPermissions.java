package mn.erin.domain.aim.usecase.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * Get all available system permissions
 *
 * @author EBazarragchaa
 */
public class GetAllPermissions extends AuthorizedUseCase<Void, Collection<Permission>>
{
  private static final Permission permission = new AimModulePermission("GetAllPermissions");
  private final PermissionService permissionService;

  public GetAllPermissions()
  {
    super();
    this.permissionService = null;
  }

  public GetAllPermissions(AuthenticationService authenticationService, AuthorizationService authorizationService,
    PermissionService permissionService)
  {
    super(authenticationService, authorizationService);
    this.permissionService = Objects.requireNonNull(permissionService);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  public Collection<Permission> executeImpl(Void input) throws UseCaseException
  {
    Collection<Permission> permissions = new ArrayList<>();
    for (String permissionString : permissionService.findAllPermissions())
    {
      Permission perm = Permission.valueOf(permissionString);
      if (null != perm)
      {
        permissions.add(perm);
      }
    }

    return permissions;
  }
}
