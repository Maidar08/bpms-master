package mn.erin.domain.aim.model.role;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;

/**
 * @author Zorig
 */
public class Role implements Entity<Role>
{
  private final RoleId roleId;
  private final TenantId tenantId;
  private final String name;
  private String description;
  private Collection<Permission> permissions;

  public Role(RoleId roleId, TenantId tenantId, String name)
  {
    this.roleId = roleId;
    this.tenantId = tenantId;
    this.name = name;
    this.permissions = new HashSet<>();
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public Collection<Permission> getPermissions()
  {
    return Collections.unmodifiableCollection(permissions);
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setPermissions(Collection<Permission> permissions)
  {
    this.permissions = permissions;
  }

  public RoleId getRoleId()
  {
    return roleId;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public void addPermission(Permission permission)
  {
    permissions.add(permission);
  }

  public void removePermission(Permission permission)
  {
    permissions.remove(permission);
  }


  @Override
  public boolean sameIdentityAs(Role other)
  {
    return other.roleId.equals(this.roleId);
  }
}
