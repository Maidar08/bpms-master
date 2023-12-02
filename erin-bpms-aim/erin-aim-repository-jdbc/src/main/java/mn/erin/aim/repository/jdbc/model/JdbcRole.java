package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("AIM_ROLE")
public class JdbcRole
{
  @Id
  String roleId;
  String tenantId;
  String name;

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

  @Override
  public String toString()
  {
    return "JdbcRole{" +
        "roleId=" + roleId +
        ", tenantId='" + tenantId + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
