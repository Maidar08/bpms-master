package mn.erin.domain.aim.usecase.role;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateRoleOutput
{
  private final String roleId;

  public CreateRoleOutput(String roleId)
  {
    this.roleId = roleId;
  }

  public String getRoleId()
  {
    return roleId;
  }
}
