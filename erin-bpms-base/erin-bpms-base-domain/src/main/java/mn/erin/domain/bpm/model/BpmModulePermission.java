/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.bpm.BpmModuleConstants;

/**
 * @author EBazarragchaa
 */
public class BpmModulePermission extends Permission
{
  public BpmModulePermission(String actionId)
  {
    super(BpmModuleConstants.APPLICATION_ID, BpmModuleConstants.MODULE_ID, actionId);
  }
}
