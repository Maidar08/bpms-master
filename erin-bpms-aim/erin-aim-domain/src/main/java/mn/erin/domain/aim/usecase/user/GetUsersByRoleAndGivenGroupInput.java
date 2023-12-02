package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tamir
 */
public class GetUsersByRoleAndGivenGroupInput
{
  private final String roleId;
  private final String groupId;
  private List<String> roleIdList = new ArrayList<>();

  public GetUsersByRoleAndGivenGroupInput(String roleId, String groupId)
  {
    this.roleId = roleId;
    this.groupId = groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public List<String> getRoleIdList()
  {
    return roleIdList;
  }

  public void setRoleIdList(List<String> roleIdList)
  {
    this.roleIdList = roleIdList;
  }
}
