package mn.erin.domain.bpm.model.collateral;

import mn.erin.domain.base.model.EntityId;

public class CollateralId extends EntityId
{

  public CollateralId(String id)
  {
    super(id);
  }

  public static CollateralId valueOf(String id)
  {
    return new CollateralId(id);
  }
}
