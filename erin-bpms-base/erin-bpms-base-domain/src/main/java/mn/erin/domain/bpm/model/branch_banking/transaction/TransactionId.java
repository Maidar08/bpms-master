package mn.erin.domain.bpm.model.branch_banking.transaction;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class TransactionId extends EntityId
{
  public TransactionId(String id)
  {
    super(id);
  }

  public static TransactionId valueOf(String id)
  {
    return new TransactionId(id);
  }
}
