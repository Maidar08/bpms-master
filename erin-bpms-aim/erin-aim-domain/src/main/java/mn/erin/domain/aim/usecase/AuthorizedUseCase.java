package mn.erin.domain.aim.usecase;

import java.util.Objects;

import mn.erin.domain.aim.annotation.Authorized;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * Presents an authorized use case which shall be executed only by authorized users.
 * Users are authorized if they they have permission to this use case as action id.
 *
 * @author EBazarragchaa
 */
@Authorized()
public abstract class AuthorizedUseCase<I, O> extends AbstractUseCase<I, O>
{
  protected final AuthenticationService authenticationService;
  protected final AuthorizationService authorizationService;

  public AuthorizedUseCase()
  {
    this.authenticationService = null;
    this.authorizationService = null;
  }

  public AuthorizedUseCase(AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
  }

  /**
   * Every use case needs a permission to be executed
   *
   * @return the permission to the use case
   */
  public abstract Permission getPermission();

  protected abstract O executeImpl(I input) throws UseCaseException;

  private void checkPermission() throws UseCaseException
  {
    Permission useCasePermission = getPermission();
    String permissionString = useCasePermission.getPermissionString();
    String currentUser = authenticationService.getCurrentUserId();

    validateNotNull(currentUser, "Current user for authorized use case [" + permissionString + "] is null!");

    if (permissionString.contains("*") || permissionString.contains("aim"))
    {
      if (Boolean.FALSE.equals(authorizationService.hasPermission(currentUser, permissionString)))
      {
        throw new UseCaseException("Current user [" + currentUser + "] does not have a permission [" + permissionString + "]!");
      }
    }
  }

  @Override
  public O execute(I input) throws UseCaseException
  {
    // check current user permission
    checkPermission();

    return executeImpl(input);
  }
}
