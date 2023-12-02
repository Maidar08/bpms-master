package mn.erin.aim.rest.controller;

import javax.inject.Inject;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
abstract class BaseAimRestApi
{
  @Inject
  protected AuthenticationService authenticationService;

  @Inject
  protected AuthorizationService authorizationService;

  @Inject
  protected TenantIdProvider tenantIdProvider;

  @Inject
  protected PermissionService permissionService;
}
