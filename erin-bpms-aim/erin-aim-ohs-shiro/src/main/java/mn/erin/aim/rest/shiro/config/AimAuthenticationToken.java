/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import org.apache.commons.lang3.Validate;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author EBazarragchaa
 */
public class AimAuthenticationToken extends UsernamePasswordToken
{
  private final String tenantId;

  public AimAuthenticationToken(String tenantId, String userName, String password)
  {
    super(userName, password);
    this.tenantId = Validate.notBlank(tenantId, "Tenant ID is required!");
  }

  public String getTenantId()
  {
    return tenantId;
  }
}
