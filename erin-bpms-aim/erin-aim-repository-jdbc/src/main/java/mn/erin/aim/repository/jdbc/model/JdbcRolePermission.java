package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("AIM_ROLE_PERMISSION")
public class JdbcRolePermission
{
  @Id
  String roleId;
  String permissionId;

  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
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
