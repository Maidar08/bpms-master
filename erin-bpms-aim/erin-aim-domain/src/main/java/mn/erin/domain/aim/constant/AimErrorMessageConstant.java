/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.constant;

/**
 * @author Tamir
 */
public final class AimErrorMessageConstant
{
  private AimErrorMessageConstant()
  {
    throw new IllegalStateException("Constants class");
  }

  public static String AUTHENTICATION_FAILED_USER_DATA_INVALID = "Authentication failed: user data is invalid";
  public static final  String USER_ID_CANNOT_BE_NULL = "UserID cannot be null!";
  public static final String TENANT_ID_CANNOT_BE_NULL = "TenantID cannot be null!";
}
