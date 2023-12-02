package mn.erin.aim.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestMembershipRequestBody
{
  private String groupId;
  private String roleId;
  private List<String> users;
  private String tenantId;

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
  }

  public List<String> getUsers()
  {
    return users;
  }

  public void setUsers(List<String> users)
  {
    this.users = users;
  }
}
