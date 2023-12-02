package mn.erin.domain.aim.usecase.membership;

import java.util.Collections;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateMembershipsInput
{
  private final String groupId;
  private final String roleId;
  private final List<String> users;
  private final String tenantId;

  public CreateMembershipsInput(String groupId, String roleId, List<String> users, String tenantId)
  {
    this.groupId = groupId;
    this.roleId = roleId;
    this.users = users;
    this.tenantId = tenantId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public List<String> getUsers()
  {
    return Collections.unmodifiableList(users);
  }

  public String getTenantId()
  {
    return tenantId;
  }
}
