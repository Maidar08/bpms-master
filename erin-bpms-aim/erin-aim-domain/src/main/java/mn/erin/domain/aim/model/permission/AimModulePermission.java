/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.model.permission;

import mn.erin.domain.aim.constant.AimConstants;

/**
 * Represents AIM module specific permission
 *
 * @author EBazarragchaa
 */
public class AimModulePermission extends Permission
{
  public AimModulePermission(String actionId)
  {
    super(AimConstants.APPLICATION_ID, AimConstants.MODULE_ID, actionId);
  }
}
