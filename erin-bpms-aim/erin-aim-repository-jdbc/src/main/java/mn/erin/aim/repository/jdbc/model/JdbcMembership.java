package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("AIM_MEMBERSHIP")
public class JdbcMembership
{
  @Id
  String membershipId;
  String userId;
  String groupId;
  String roleId;
  String tenantId;

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

  @Override
  public String toString()
  {
    return "JdbcGroupMembership{" +
        "membershipId=" + membershipId +
        ", userId='" + userId + '\'' +
        ", groupId='" + groupId + '\'' +
        ", roleId='" + roleId + '\'' +
        '}';
  }
}

