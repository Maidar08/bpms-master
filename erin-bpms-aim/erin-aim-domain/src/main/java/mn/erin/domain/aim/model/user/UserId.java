package mn.erin.domain.aim.model.user;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class UserId extends EntityId
{
  private static final long serialVersionUID = -5361947098771450248L;

  public UserId(String id)
  {
    super(id);
  }

  public static UserId valueOf(String id)
  {
    return new UserId(id);
  }
}
