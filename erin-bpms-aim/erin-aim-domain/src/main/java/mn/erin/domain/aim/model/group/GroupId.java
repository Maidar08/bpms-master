package mn.erin.domain.aim.model.group;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class GroupId extends EntityId
{
  private static final long serialVersionUID = -5363947097771450248L;

  public GroupId(String id)
  {
    super(id);
  }

  public static GroupId valueOf(String id)
  {
    return new GroupId(id);
  }
}
