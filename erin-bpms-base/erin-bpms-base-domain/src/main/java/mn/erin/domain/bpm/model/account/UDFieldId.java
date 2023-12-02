package mn.erin.domain.bpm.model.account;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class UDFieldId extends EntityId
{
  public UDFieldId(String id)
  {
    super(id);
  }

  public static UDFieldId valueOf(String id)
  {
    return new UDFieldId(id);
  }
}
