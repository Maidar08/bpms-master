package mn.erin.domain.aim.usecase.role;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetRoleOutput
{
  private final String roleId;
  private final String roleName;

  public GetRoleOutput(String roleId, String roleName)
  {
    this.roleId = roleId;
    this.roleName = roleName;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public String getRoleName()
  {
    return roleName;
  }
}
