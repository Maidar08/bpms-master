package mn.erin.domain.aim.service;

/**
 * Only Responsible for authenticating a user to a tenant using user id and password. An authenticated user shall have a valid session id.
 *
 * @author EBazarragchaa
 */
public interface AuthenticationService
{
  /**
   * Authenticates a user to a tenant.
   *
   * @param tenantId the tenant id can represent a company or system
   * @param userId   the user id
   * @param password the user password
   * @return valid token string, otherwise null
   */
  String authenticate(String tenantId, String userId, String password);

  /**
   * Authenticates a user to a tenant.
   *
   * @param tenantId the tenant id can represent a company or system
   * @param userId   the user id
   * @param password the user password
   * @param killPreviousSession boolean to kill previous session
   * @return valid token string, otherwise null
   */
  String authenticate(String tenantId, String userId, String password, boolean killPreviousSession);

  String logout();

  /**
   * Returns current authenticated user id
   *
   * @return user id or null
   */
  String getCurrentUserId();
  /**
   * Returns current user authenticated state
   *
   * @return authentication true or false
   */
  boolean isCurrentUserAuthenticated();
  /**
   * Returns current user token
   *
   * @return token
   */
  String getToken();
}
