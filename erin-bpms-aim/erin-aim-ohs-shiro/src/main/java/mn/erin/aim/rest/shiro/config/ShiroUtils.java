/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import mn.erin.domain.aim.model.permission.Permission;

/**
 * @author EBazarragchaa
 */
public class ShiroUtils
{

  public static String toShiroPermission(Permission aimPermission)
  {
    if (null == aimPermission)
    {
      throw new IllegalArgumentException("Permission could not created from string [" + aimPermission + "]!");
    }

    return aimPermission.getApplicationId() + "." + aimPermission.getModuleId() + ":" + aimPermission.getActionId();
  }
}
