package mn.erin.domain.aim.usecase.user;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.aim.model.user.User;

/**
 * @author Zorig
 */
public class GetSubGroupUsersByRoleOutput
{
  private final List<User> subgroupUsers;

  public GetSubGroupUsersByRoleOutput(List<User> subgroupUsers)
  {
    this.subgroupUsers = subgroupUsers;
  }

  public List<User> getSubgroupUsers()
  {
    return Collections.unmodifiableList(subgroupUsers);
  }
}
