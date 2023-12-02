package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class GetAllGroupsSubTreeInput
{
  private final String tenantId;

  public GetAllGroupsSubTreeInput(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getTenantId()
  {
    return tenantId;
  }
}
