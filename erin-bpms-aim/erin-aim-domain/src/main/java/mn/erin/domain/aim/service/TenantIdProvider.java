package mn.erin.domain.aim.service;

/**
 * Provides the tenant id of a user.
 *
 * @author EBazarragchaa
 */
public interface TenantIdProvider
{
  /**
   * Returns the current user tenant id
   *
   * @return the tenant id or null if the current user is not available.
   */
  String getCurrentUserTenantId();

  /**
   * Returns the tenant id to a given user id
   *
   * @param userId the user id
   * @return the user tenant id or null if the user is not available.
   */
  String getUserTenantId(String userId);
}
