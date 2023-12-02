/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.provider.ExtSessionInfo;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.AUTHENTICATION_FAILED_USER_DATA_INVALID;

/**
 * @author EBazarragchaa
 */
public class LoginUser extends AbstractUseCase<LoginUserInput, LoginUserOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginUser.class);

  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;
  private final EncryptionService encryptionService;
  private final ExtSessionInfoCache extSessionInfoCache;

  public  LoginUser(AuthenticationService authenticationService, MembershipRepository membershipRepository,
    RoleRepository roleRepository, EncryptionService encryptionService, ExtSessionInfoCache extSessionInfoCache)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository is required!");
    this.roleRepository = Objects.requireNonNull(roleRepository, "RoleRepository is required!");
    this.encryptionService = encryptionService;
    this.extSessionInfoCache = extSessionInfoCache;
  }

  @Override
  public LoginUserOutput execute(LoginUserInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Login input data is required!");
    }

    String userId = input.getUserId();
    String token = null;
    String roleId;

    try
    {
      // 1. authenticate
      token = authenticationService.authenticate(input.getTenantId(), userId, input.getPassword(), input.isKillPreviousSession());
      roleId = membershipRepository.findByUserId(UserId.valueOf(userId)).getRoleId().getId();

      if (null == token)
      {
        LOGGER.error("########## Authentication is null with USER ID = [{}]", userId);
        throw new UseCaseException(AUTHENTICATION_FAILED_USER_DATA_INVALID);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("########### ERROR OCCURRED DURING USER AUTHENTICATE = [{}], REASON FOR THAT = [{}]", userId, e.getMessage());
      LOGGER.error("########### CAUSE :", e);

      throw new UseCaseException(AUTHENTICATION_FAILED_USER_DATA_INVALID);
    }

    String tenantId = input.getTenantId();
    List<String> permissions;
    List<String> groups;

    // 2. check membership access, exclude 'admin' user
    List<Membership> memberships = getMembership(tenantId, userId);

    if (AimConstants.ADMIN_USER_ID.equals(userId))
    {
      permissions = getPermissions(memberships);
      permissions.add("*");
    }
    else
    {
      permissions = getPermissions(memberships);
    }

    groups = getGroups(memberships);

    setSessionInfo(input.getUserId(), input.getPassword());

    return new LoginUserOutput(token, roleId, groups, permissions);
  }

  private void setSessionInfo(String userId, String password)
  {
    String encryptedPassword = encryptionService.encrypt(password);
    extSessionInfoCache.setSessionInfo(new ExtSessionInfo(userId, encryptedPassword, null));
  }

  private List<Membership> getMembership(String tenantId, String userId) throws UseCaseException
  {
    try
    {
      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf(tenantId), UserId.valueOf(userId));

      if (memberships.isEmpty())
      {
        throw new UseCaseException("User doesn't have any memberships!");
      }

      return memberships;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private List<String> getPermissions(List<Membership> memberships)
  {
    List<String> permissions = new ArrayList<>();

    for (Membership membership : memberships)
    {
      RoleId roleId = membership.getRoleId();
      Role role = roleRepository.findById(roleId);

      for (Permission permission : role.getPermissions())
      {
        String permissionString = permission.getPermissionString();

        if (!permissions.contains(permissionString))
        {
          permissions.add(permissionString);
        }
      }
    }
    return permissions;
  }

  private List<String> getGroups(List<Membership> memberships)
  {
    List<String> groups = new ArrayList<>();

    for (Membership membership : memberships)
    {
      String groupId = membership.getGroupId().getId();

      if (!groups.contains(groupId))
      {
        groups.add(groupId);
      }
    }
    return groups;
  }
}
