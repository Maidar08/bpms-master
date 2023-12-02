package mn.erin.domain.aim.model.membership;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MembershipId extends EntityId
{
  private  MembershipId(String id)
  {
    super(id);
  }

  public static MembershipId valueOf(String id)
  {
    return new MembershipId(id);
  }
}
