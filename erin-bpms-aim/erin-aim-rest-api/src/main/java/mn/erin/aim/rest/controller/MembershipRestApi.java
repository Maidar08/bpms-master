package mn.erin.aim.rest.controller;

import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.aim.rest.model.RestMembership;
import mn.erin.aim.rest.model.RestMembershipRequestBody;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.usecase.membership.CreateMemberships;
import mn.erin.domain.aim.usecase.membership.CreateMembershipsInput;
import mn.erin.domain.aim.usecase.membership.DeleteMembership;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.MoveUserGroup;
import mn.erin.domain.aim.usecase.membership.MoveUserGroupInput;
import mn.erin.domain.aim.usecase.membership.MoveUserGroupOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Membership")
@RequestMapping(value = "/aim/memberships", name = "Provides AIM membership management features")
@RestController
public class MembershipRestApi extends BaseAimRestApi
{
  private final MembershipRepository membershipRepository;

  @Inject
  public MembershipRestApi(MembershipRepository membershipRepository)
  {
    this.membershipRepository = membershipRepository;
  }

  @ApiOperation("Create membership")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestMembershipRequestBody request)
  {
    CreateMembershipsInput input = new CreateMembershipsInput(request.getGroupId(), request.getRoleId(), request.getUsers(), request.getTenantId());
    CreateMemberships createMemberships = new CreateMemberships(authenticationService, authorizationService, membershipRepository);

    try
    {
      List<GetMembershipOutput> memberships = createMemberships.execute(input);
      return RestResponse.success(memberships);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a membership")
  @DeleteMapping("/{membershipId}")
  public ResponseEntity<RestResult> delete(@PathVariable String membershipId)
  {
    DeleteMembership deleteMembership = new DeleteMembership(authenticationService, authorizationService, membershipRepository);

    try
    {
      boolean isDeleted = deleteMembership.execute(membershipId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a membership")
  @PatchMapping
  public ResponseEntity<RestResult> moveUserGroup(@RequestBody RestMembership restMembership)
  {
    MoveUserGroupInput input = new MoveUserGroupInput(restMembership.getUserId(), restMembership.getGroupId());
    MoveUserGroup moveUserGroup = new MoveUserGroup(authenticationService, authorizationService, membershipRepository, tenantIdProvider);

    try
    {
      MoveUserGroupOutput output = moveUserGroup.execute(input);
      return RestResponse.success(output.isUpdated());
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
