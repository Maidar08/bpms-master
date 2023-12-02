package mn.erin.aim.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestRole
{
  private String roleName;
  private String description;

  private List<String> permissions;

  public String getRoleName()
  {
    return roleName;
  }

  public void setRoleName(String roleName)
  {
    this.roleName = roleName;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public List<String> getPermissions()
  {
    return permissions;
  }

  public void setPermissions(List<String> permissions)
  {
    this.permissions = permissions;
  }
}
