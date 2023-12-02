package mn.erin.domain.aim.model.role;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class RoleId extends EntityId
{
  private static final long serialVersionUID = -5363947097771450118L;

  public RoleId(String id)
  {
    super(id);
  }

  public static RoleId valueOf(String id)
  {
    return new RoleId(id);
  }
}
