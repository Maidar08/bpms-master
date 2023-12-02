package mn.erin.domain.aim.usecase.role;

import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateRoleInput
{
  private final String roleName;

  private String roleDescription;
  private List<String> permissions;

  public CreateRoleInput(String roleName)
  {
    this.roleName = Validate.notBlank(roleName, "Role name cannot be null or blank!");
  }

  public String getRoleName()
  {
    return roleName;
  }

  public String getRoleDescription()
  {
    return roleDescription;
  }

  public void setRoleDescription(String roleDescription)
  {
    this.roleDescription = roleDescription;
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
