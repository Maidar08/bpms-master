/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.aim.usecase.user;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetUserEmailAddressByUserName extends AuthorizedUseCase<String, String>
{
  private static final Permission permission = new AimModulePermission("GetSubGroupUsersByRole");

  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetUserEmailAddressByUserName(UserRepository userRepository, TenantIdProvider tenantIdProvider)
  {
    super();
    this.userRepository = null;
    this.tenantIdProvider = null;
  }

  public GetUserEmailAddressByUserName(AuthenticationService authenticationService,
      AuthorizationService authorizationService, UserRepository userRepository,
      TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.userRepository = userRepository;
    this.tenantIdProvider = tenantIdProvider;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected String executeImpl(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException("User name must not be blank!");
    }

    String tenantId = tenantIdProvider.getCurrentUserTenantId();
    Collection<User> allUsers = userRepository.getAllUsers(TenantId.valueOf(tenantId));

    return getRecipientEmailAddress(input, allUsers);
  }

  private String getRecipientEmailAddress(String userName, Collection<User> allUsers) throws UseCaseException
  {
    for (User user : allUsers)
    {
      if (user.getUserInfo() == null || user.getUserInfo().getUserName() == null)
      {
        continue;
      }
      if (user.getUserInfo().getUserName().equalsIgnoreCase(userName))
      {
        ContactInfo contactInfo = user.getContactInfo();
        if (user.getContactInfo() == null)
        {
          throw new UseCaseException("User does not have contact information!");
        }

        String email = contactInfo.getEmail();

        if (StringUtils.isBlank(email))
        {
          throw new UseCaseException("User's email is blank!");
        }

        return email;
      }
    }
    return null;
  }
}
