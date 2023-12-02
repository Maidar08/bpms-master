package mn.erin.domain.aim.usecase.user;

/**
 * @author Zorig
 */
public class GetParentGroupUsersByRoleInput
{
  private final String role;

  public GetParentGroupUsersByRoleInput(String role)
  {
    this.role = role;
  }

  public String getRole()
  {
    return role;
  }
}
