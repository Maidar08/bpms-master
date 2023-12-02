package mn.erin.aim.rest.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestMembership
{
  private String membershipId;
  private String groupId;
  private String roleId;
  private String userId;

  public String getMembershipId()
  {
    return membershipId;
  }

  public void setMembershipId(String membershipId)
  {
    this.membershipId = membershipId;
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

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }
}
