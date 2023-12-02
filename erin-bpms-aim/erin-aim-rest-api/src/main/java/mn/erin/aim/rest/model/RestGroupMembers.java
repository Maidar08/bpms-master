/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestGroupMembers
{
  private String groupName;
  private List<RestUser> users;

  public RestGroupMembers(String groupName, List<RestUser> users)
  {
    this.groupName = groupName;
    this.users = users;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public List<RestUser> getUsers()
  {
    return users;
  }
}
