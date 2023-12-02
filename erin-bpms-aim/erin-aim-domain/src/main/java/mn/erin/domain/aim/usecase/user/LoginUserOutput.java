package mn.erin.domain.aim.usecase.user;

import java.util.List;

/**
 * @author Tamir
 */
public class LoginUserOutput
{
  private String token;
  private String roleId;
  private List<String> groups;
  private List<String> permissions;

  public LoginUserOutput(String token, String roleId, List<String> groups, List<String> permissions)
  {
    this.token = token;
    this.roleId = roleId;
    this.groups = groups;
    this.permissions = permissions;
  }

  public String getToken()
  {
    return token;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public List<String> getGroups()
  {
    return groups;
  }

  public void setGroup(List<String> groups)
  {
    this.groups = groups;
  }

  public List<String> getPermissions()
  {
    return permissions;
  }

  public void setPermissions(List<String> permissions)
  {
    this.permissions = permissions;
  }
  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
  }
}
