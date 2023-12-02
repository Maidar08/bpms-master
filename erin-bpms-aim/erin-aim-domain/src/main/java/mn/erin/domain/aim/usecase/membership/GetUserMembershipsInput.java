package mn.erin.domain.aim.usecase.membership;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserMembershipsInput
{
  private final String userId;

  public GetUserMembershipsInput(String userId)
  {
    this.userId = Validate.notBlank(userId, "UserID cannot be null or blank");
  }

  public String getUserId()
  {
    return userId;
  }
}
