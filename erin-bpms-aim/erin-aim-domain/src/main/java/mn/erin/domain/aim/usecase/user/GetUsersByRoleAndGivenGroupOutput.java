package mn.erin.domain.aim.usecase.user;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.aim.model.user.User;

/**
 * @author Tamir
 */
public class GetUsersByRoleAndGivenGroupOutput
{
  private final List<User> users;

  public GetUsersByRoleAndGivenGroupOutput(List<User> users)
  {
    this.users = users;
  }

  public List<User> getUsers()
  {
    return Collections.unmodifiableList(users);
  }
}
