package mn.erin.domain.aim.usecase.user;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllUsersInput
{
  private final String tenantId;

  public GetAllUsersInput(String tenantId)
  {
    this.tenantId = Validate.notBlank(tenantId, "TenantID cannot be null or blank!");
  }

  public String getTenantId()
  {
    return tenantId;
  }
}
