/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.user;

import org.apache.commons.lang3.Validate;

/**
 * @author EBazarragchaa
 */
public final class LoginUserInput
{
  private final String tenantId;
  private final String userId;
  private final String password;
  private boolean killPreviousSession;

  public LoginUserInput(String tenantId, String userId, String password)
  {
    this.tenantId = Validate.notBlank(tenantId, "Tenant ID is required!");
    this.userId = Validate.notBlank(userId, "User ID is required!");
    this.password = Validate.notBlank(password, "User password is required!");
    killPreviousSession = true;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getPassword()
  {
    return password;
  }

  public boolean isKillPreviousSession()
  {
    return killPreviousSession;
  }

  public void setKillPreviousSession(boolean killPreviousSession)
  {
    this.killPreviousSession = killPreviousSession;
  }
}
