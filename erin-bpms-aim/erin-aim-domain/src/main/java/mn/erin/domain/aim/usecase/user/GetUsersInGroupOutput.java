package mn.erin.domain.aim.usecase.user;

import java.util.List;

import mn.erin.domain.aim.model.user.User;

/**
 * @author Zorig
 */
public class GetUsersInGroupOutput
{
  private final List<User> userInGroup;

  public GetUsersInGroupOutput(List<User> userInGroup)
  {
    this.userInGroup = userInGroup;
  }

  public List<User> getUserInGroup()
  {
    return userInGroup;
  }


}
