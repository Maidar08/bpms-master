package mn.erin.domain.bpm.model.contract;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class ContractId extends EntityId
{
  public ContractId(String id)
  {
    super(id);
  }

  public static ContractId valueOf(String id)
  {
    return new ContractId(id);
  }
}
