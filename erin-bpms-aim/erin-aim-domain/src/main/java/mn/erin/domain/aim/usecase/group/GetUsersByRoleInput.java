package mn.erin.domain.aim.usecase.group;

import java.util.List;

/**
 * @author Zorig
 */
public class GetUsersByRoleInput
{
  private final String roleId;
  private List<String> roleIdList;

  public GetUsersByRoleInput(String roleId)
  {
    this.roleId = roleId;
  }

  public String getRoleId()
  {
    return roleId;
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
