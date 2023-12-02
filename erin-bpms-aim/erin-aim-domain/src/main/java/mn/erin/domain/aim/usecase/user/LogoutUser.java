/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.user;

import java.util.Objects;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author EBazarragchaa
 */
public class LogoutUser extends AbstractUseCase<Void, String>
{
  private final AuthenticationService authenticationService;

  public LogoutUser(AuthenticationService authenticationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService is required!");
  }

  @Override
  public String execute(Void input) throws UseCaseException
  {
    return authenticationService.logout();
  }
}
