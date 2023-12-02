/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.service.TenantIdProvider;

/**
 * @author EBazarragchaa
 */
public class ShiroTenantIdProvider implements TenantIdProvider
{
  @Override
  public String getCurrentUserTenantId()
  {
    Subject currentUser = SecurityUtils.getSubject();
    return (String) currentUser.getSession().getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);
  }

  @Override
  public String getUserTenantId(String userId)
  {
    throw new UnsupportedOperationException();
  }
}
