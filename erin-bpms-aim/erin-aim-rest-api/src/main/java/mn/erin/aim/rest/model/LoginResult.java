package mn.erin.aim.rest.model;

import java.util.Set;

public class LoginResult
{
  private final String ticket;
  private String userName;
  private String group;
  private Set<RestPermission> permissions;

  public LoginResult(String ticket)
  {
    this.ticket = ticket;
  }

  public String getTicket()
  {
    return ticket;
  }

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    this.group = group;
  }

  public void setPermissions(Set<RestPermission> permissions)
  {
    this.permissions = permissions;
  }

  public Set<RestPermission> getPermissions()
  {
    return permissions;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getUserName()
  {
    return userName;
  }
}
