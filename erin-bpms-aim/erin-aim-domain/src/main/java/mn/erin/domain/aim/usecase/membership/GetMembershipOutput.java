package mn.erin.domain.aim.usecase.membership;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetMembershipOutput
{
  private String membershipId;
  private String userId;
  private String groupId;
  private String roleId;
  private String tenantId;

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getMembershipId()
  {
    return membershipId;
  }

  public void setMembershipId(String membershipId)
  {
    this.membershipId = membershipId;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
  }
}
