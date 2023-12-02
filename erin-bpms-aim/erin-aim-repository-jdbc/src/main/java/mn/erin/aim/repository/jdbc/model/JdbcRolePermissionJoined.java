package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;

/**
 * @author Zorig
 */
public class JdbcRolePermissionJoined
{
  @Id
  String roleId;
  String tenantId;
  String name;
  String permissionId;

  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getPermissionId()
  {
    return permissionId;
  }

  public void setPermissionId(String permissionId)
  {
    this.permissionId = permissionId;
  }
}
