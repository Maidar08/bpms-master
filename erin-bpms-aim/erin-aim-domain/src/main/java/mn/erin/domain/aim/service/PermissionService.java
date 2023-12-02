package mn.erin.domain.aim.service;

import java.util.Collection;

/**
 * Presents an Open Host Service for the permission model. This service is supposed to be used by external domains.
 *
 * @author EBazarragchaa
 */
public interface PermissionService
{
  /**
   * Returns all found permission strings defined by @AuthorizedUseCase classes
   *
   * @return collection of permission strings or empty collection
   */
  Collection<String> findAllPermissions();

  /**
   * Returns all available application ids
   *
   * @return collection of application ids or empty collection
   */
  Collection<String> findAllPermittedApplications();

  /**
   * Returns all available module ids to an application
   *
   * @param applicationId the application id
   * @return collection of module ids or empty collection
   */
  Collection<String> findAllPermittedModules(String applicationId);
}
