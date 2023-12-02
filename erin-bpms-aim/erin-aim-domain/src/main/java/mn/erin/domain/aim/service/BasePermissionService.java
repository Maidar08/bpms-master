package mn.erin.domain.aim.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.annotation.Authorized;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.model.ApplicationModule;

/**
 * Represents a base permission service with auto scanning for @Authorized classes
 *
 * @author EBazarragchaa
 */
public abstract class BasePermissionService implements PermissionService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BasePermissionService.class);

  private Reflections reflections = new Reflections("mn.erin.domain");
  private Map<ApplicationModule, Set<Permission>> permissions;

  @Override
  public Collection<String> findAllPermissions()
  {
    this.permissions = new LinkedHashMap<>();
    Set<String> permissionStrings = new TreeSet<>();
    Set<Class<?>> authorizedUseCaseClasses = reflections.getTypesAnnotatedWith(Authorized.class);
    for (Class<?> clz : authorizedUseCaseClasses)
    {
      Class<AuthorizedUseCase> authorizedUseCaseClass = (Class<AuthorizedUseCase>) clz;

      AuthorizedUseCase authorizedUseCase = createNewInstanceOfAuthorizedUseCase(authorizedUseCaseClass);
      if (null != authorizedUseCase)
      {
        Permission permission = authorizedUseCase.getPermission();
        if (null != permission && !permissionStrings.contains(permission.getPermissionString()))
        {
          permissionStrings.add(permission.getPermissionString());
        }
        else
        {
          LOGGER.warn(authorizedUseCaseClass.getName() + " doesn't have a permission!");
        }
      }
    }

    return Collections.unmodifiableSet(permissionStrings);
  }

  @Override
  public Collection<String> findAllPermittedApplications()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> findAllPermittedModules(String applicationId)
  {
    throw new UnsupportedOperationException();
  }

  private static AuthorizedUseCase createNewInstanceOfAuthorizedUseCase(Class<AuthorizedUseCase> authorizedUseCaseClass)
  {
    try
    {
      return authorizedUseCaseClass.getDeclaredConstructor().newInstance();
    }
    catch (Exception e)
    {
      return null;
    }
  }
}
