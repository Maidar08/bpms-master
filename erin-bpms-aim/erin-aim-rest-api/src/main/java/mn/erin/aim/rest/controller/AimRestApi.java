/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.aim.rest.model.RestLoginBody;
import mn.erin.aim.rest.model.RestLoginResult;
import mn.erin.aim.rest.model.RestPermission;
import mn.erin.domain.aim.constant.AimErrorMessageConstant;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.permission.GetAllPermissions;
import mn.erin.domain.aim.usecase.user.GetCurrentUserLoginBody;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.aim.usecase.user.LoginUserInput;
import mn.erin.domain.aim.usecase.user.LoginUserOutput;
import mn.erin.domain.aim.usecase.user.LogoutUser;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author EBazarragchaa
 */

@Api("AIM")
@RequestMapping(value = "/aim", name = "Provides access and identity management features")
@RestController
public class AimRestApi extends BaseAimRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AimRestApi.class);

  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;
  private final EncryptionService encryptionService;
  private final ExtSessionInfoCache extSessionInfoCache;
  private Date start;

  @Inject
  public AimRestApi(PermissionService permissionService, AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, RoleRepository roleRepository, TenantIdProvider tenantIdProvider,
      EncryptionService encryptionService, ExtSessionInfoCache extSessionInfoCache)
  {
    this.extSessionInfoCache = extSessionInfoCache;
    this.encryptionService = encryptionService;
    this.permissionService = permissionService;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.membershipRepository = membershipRepository;
    this.roleRepository = roleRepository;
    this.tenantIdProvider = tenantIdProvider;
  }

  @ApiOperation("Login user")
  @PostMapping(value = "/login")
  public ResponseEntity<RestResult> login(@RequestBody RestLoginBody restLoginBody)
  {
    Date startRequest = new Date(System.currentTimeMillis());
    LOGGER.info("########### Time when start login request on API = [{}]", startRequest);

    if (null == restLoginBody)
    {
      return RestResponse.badRequest("Login rest body cannot be null!");
    }

    String tenantId = restLoginBody.getTenantId();
    String userId = restLoginBody.getUserId();
    String password = restLoginBody.getPassword();

    if (StringUtils.isBlank(tenantId))
    {
      return RestResponse.badRequest("Tenant id cannot be blank!");
    }
    if (StringUtils.isBlank(userId))
    {
      return RestResponse.badRequest("User id cannot be blank!");
    }

    LoginUserInput input = new LoginUserInput(tenantId, userId, password);
    LoginUser loginUser = new LoginUser(authenticationService, membershipRepository, roleRepository, encryptionService, extSessionInfoCache);
    try
    {
      LoginUserOutput output = loginUser.execute(input);
      RestLoginResult result = getRestLoginResult(restLoginBody.getUserId(), output);
      return RestResponse.success(result);
    }
    catch (Exception e)
    {
      LOGGER.error("###### ERROR OCCURRED WHEN USER LOGIN = [{}], REASON FOR THAT = [{}]", userId, e.getMessage());
      LOGGER.error("###### EXCEPTION CAUSE:", e);

      if (e.getMessage().equalsIgnoreCase(AimErrorMessageConstant.AUTHENTICATION_FAILED_USER_DATA_INVALID))
      {
        return RestResponse.unauthorized(e.getMessage());
      }
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Logout user")
  @GetMapping(value = "/logout")
  public ResponseEntity<RestResult> logout()
  {
    LogoutUser logoutUser = new LogoutUser(authenticationService);
    try
    {
      String userId = logoutUser.execute(null);
      return RestResponse.success(userId);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get all permissions")
  @GetMapping(value = "/permissions")
  public ResponseEntity<RestResult> getPermissions()
  {
    GetAllPermissions getAllPermissions = new GetAllPermissions(authenticationService, authorizationService, permissionService);

    try
    {
      Collection<Permission> permissions = getAllPermissions.execute(null);
      return RestResponse.success(permissions);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private Set<RestPermission> getRestPermission(List<String> permissions)
  {
    Set<RestPermission> restPermissions = new HashSet<>();

    for (String permission : permissions)
    {
      restPermissions.add(new RestPermission(permission));
    }
    return restPermissions;
  }

  @ApiOperation("Validates user session")
  @GetMapping(value = "/validate-session")
  public ResponseEntity validate()
  {
    // TODO FIX me
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      return RestResponse.unauthorized("The current user is not authenticated!");
    }
    GetCurrentUserLoginBody getBody = new GetCurrentUserLoginBody(authenticationService, membershipRepository, roleRepository, tenantIdProvider);
    try
    {
      LoginUserOutput output = getBody.execute(null);
      RestLoginResult result = getRestLoginResult(authenticationService.getCurrentUserId(), output);

      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RestLoginResult getRestLoginResult(String userId,LoginUserOutput output)
  {
    RestLoginResult result = new RestLoginResult(output.getToken());

    // todo : refactor implementation, get single group id.
    result.setGroup(output.getGroups().get(0));
    result.setUserName(userId);
    result.setRole(output.getRoleId());
    result.setPermissions(getRestPermission(output.getPermissions()));
    return result;
  }
}
