package mn.erin.domain.bpm.model.organization;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class OrganizationRequestId extends EntityId
{
  public OrganizationRequestId(String id)
  {
    super(id);
  }

  public static OrganizationRequestId valueOf(String id)
  {
    return new OrganizationRequestId(id);
  }
}
