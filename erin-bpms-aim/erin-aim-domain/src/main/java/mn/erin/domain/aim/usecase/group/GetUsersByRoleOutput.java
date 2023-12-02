package mn.erin.domain.aim.usecase.group;

import java.util.Collection;
import java.util.Collections;

import mn.erin.domain.aim.model.user.User;

/**
 * @author Zorig
 */
public class GetUsersByRoleOutput
{
  private final Collection<User> users;
  private final Collection<User> allUsers;

  public GetUsersByRoleOutput(Collection<User> users, Collection<User> allUsers)
  {
    this.users = users;
    this.allUsers = allUsers;
  }

  public Collection<User> getAllUsers()
  {
    return Collections.unmodifiableCollection(allUsers);
  }

  public Collection<User> getUsersByRole()
  {
    return Collections.unmodifiableCollection(users);
  }
}
