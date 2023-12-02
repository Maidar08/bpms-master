package mn.erin.domain.aim.usecase.user;

import java.util.List;

import mn.erin.domain.aim.model.user.User;

/**
 * @author Zorig
 */
public class GetParentGroupUsersByRoleOutput
{
  private final List<User> parentGroupUsersByRole;

  public GetParentGroupUsersByRoleOutput(List<User> parentGroupUsersByRole)
  {
    this.parentGroupUsersByRole = parentGroupUsersByRole;
  }

  public List<User> getParentGroupUsersByRole()
  {
    return parentGroupUsersByRole;
  }
}
