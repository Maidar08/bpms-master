/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author EBazarragchaa
 */
public class AuthorizedUseCaseTest
{
  private AuthenticationService mockAuthenticationService;
  private AuthorizationService mockAuthorizationService;

  @Before
  public void setup()
  {
    mockAuthenticationService = Mockito.mock(AuthenticationService.class);
    mockAuthorizationService = Mockito.mock(AuthorizationService.class);
  }

  @Test(expected = NullPointerException.class)
  public void ctorThrowsExceptionUserRepositoryNull()
  {
    createAuthorizedUseCase(null, mockAuthorizationService);
  }

  @Test(expected = NullPointerException.class)
  public void ctorThrowsExceptionPermissionServiceNull()
  {
    createAuthorizedUseCase(mockAuthenticationService, null);
  }

  @Test
  public void ctorCreatesNewInstance()
  {
    AuthorizedUseCase useCase = createAuthorizedUseCase(mockAuthenticationService, mockAuthorizationService);

    Assert.assertNotNull(useCase);
  }

  @Test
  public void getPermissionNotNull()
  {
    Assert.assertNotNull(createAuthorizedUseCase(mockAuthenticationService, mockAuthorizationService).getPermission());
  }

  @Test(expected = UseCaseException.class)
  public void executeThrowsExceptionCurrentUserNull() throws UseCaseException
  {
    Mockito.when(mockAuthenticationService.getCurrentUserId()).thenReturn(null);
    createAuthorizedUseCase(mockAuthenticationService, mockAuthorizationService).execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void executeThrowsExceptionCurrentUserDoesntHavePermission() throws UseCaseException
  {
    Mockito.when(mockAuthenticationService.getCurrentUserId()).thenReturn("admin");
    Mockito.when(mockAuthorizationService.hasPermission("admin", "admin.aim.testAuthorizedUseCase")).thenReturn(false);
    createAuthorizedUseCase(mockAuthenticationService, mockAuthorizationService).execute(null);
  }

  @Test
  public void executeSucceed() throws UseCaseException
  {
    Mockito.when(mockAuthenticationService.getCurrentUserId()).thenReturn("admin");
    Mockito.when(mockAuthorizationService.hasPermission("admin", "admin.aim.testAuthorizedUseCase")).thenReturn(true);
    AuthorizedUseCase useCase = createAuthorizedUseCase(mockAuthenticationService, mockAuthorizationService);

    Assert.assertNotNull(useCase);
    useCase.execute(null);
  }

  private AuthorizedUseCase createAuthorizedUseCase(AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    return new TestAuthorizedUseCase(authenticationService, authorizationService);
  }
}
