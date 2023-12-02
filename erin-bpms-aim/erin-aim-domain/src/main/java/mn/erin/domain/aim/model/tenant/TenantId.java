package mn.erin.domain.aim.model.tenant;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class TenantId extends EntityId
{

  private static final long serialVersionUID = -5363947098771450248L;

  public TenantId(String id)
  {
    super(id);
  }

  public static TenantId valueOf(String id)
  {
    return new TenantId(id);
  }
}
