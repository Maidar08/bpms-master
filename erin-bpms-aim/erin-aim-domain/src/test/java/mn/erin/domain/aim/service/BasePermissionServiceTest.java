package mn.erin.domain.aim.service;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author EBazarragchaa
 */
public class BasePermissionServiceTest
{
  @Test
  public void findAllPermissionsReturnsStrings()
  {
    BasePermissionService permissionService = createBasePermissionService();

    Assert.assertNotNull(permissionService);
    Assert.assertFalse(permissionService.findAllPermissions().isEmpty());
  }

  private BasePermissionService createBasePermissionService()
  {
    return new BasePermissionService()
    {
      @Override
      public Collection<String> findAllPermittedApplications()
      {
        return null;
      }

      @Override
      public Collection<String> findAllPermittedModules(String applicationId)
      {
        return null;
      }
    };
  }
}
