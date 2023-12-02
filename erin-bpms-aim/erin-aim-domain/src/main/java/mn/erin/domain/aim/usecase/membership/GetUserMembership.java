/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.membership;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Tamir
 */
public class GetUserMembership extends AuthorizedUseCase<GetUserMembershipsInput, GetMembershipOutput>
{
  private static final Permission permission = new AimModulePermission("GetUserMembership");

  private final MembershipRepository membershipRepository;

  public GetUserMembership(AuthenticationService authenticationService, AuthorizationService authorizationService, MembershipRepository membershipRepository)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetMembershipOutput executeImpl(GetUserMembershipsInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input cannot be null!");

    Membership membership = null;
    try
    {
      membership = membershipRepository.findByUserId(UserId.valueOf(input.getUserId()));
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    if (membership == null)
    {
      throw new UseCaseException("No membership has been found on the user with the ID: [" + input.getUserId() + "]");
    }

    GetMembershipOutput output = new GetMembershipOutput();

    output.setMembershipId(membership.getMembershipId().getId());
    output.setGroupId(membership.getGroupId().getId());
    output.setRoleId(membership.getRoleId().getId());

    return output;
  }
}
