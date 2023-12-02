package mn.erin.domain.aim.service;

/**
 * Responsible for checking user authorization.
 *
 * @author EBazarragchaa
 */
public interface AuthorizationService
{
  /**
   * Returns true if a user has a given permission
   *
   * @param userId           the user id
   * @param permissionString the permission string in form of 'applicationId.moduleId.actionId'
   * @return true or false
   */
  Boolean hasPermission(String userId, String permissionString);
}
