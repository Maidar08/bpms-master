package mn.erin.domain.aim.usecase.user;

/**
 * @author Zorig
 */
public class GetSubGroupUsersByRoleInput
{
  private final String role;

  public GetSubGroupUsersByRoleInput(String role)
  {
    this.role = role;
  }

  public String getRole()
  {
    return role;
  }
}
