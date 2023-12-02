package mn.erin.aim.rest.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.aim.rest.model.RestMembership;
import mn.erin.aim.rest.model.RestUser;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.usecase.group.GetGroupUsersByRole;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleInput;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleOutput;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMemberships;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRole;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleInput;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleOutput;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRole;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRoleInput;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRoleOutput;
import mn.erin.domain.aim.usecase.user.GetUserOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("User")
@RequestMapping(value = "/aim/users", name = "Provides AIM user management features")
@RestController
public class UserRestApi extends BaseAimRestApi
{
  private final UserRepository userRepository;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;
  private final Environment environment;
  private static String ERROR_MSG_INVALID_ROLE_ID = "Invalid Role Id Input!";
  private static String BRANCH_SPECIALIST = "branch_specialist";
  private static String BANKER = "banker";

  @Inject
  public UserRestApi(UserRepository userRepository, MembershipRepository membershipRepository, GroupRepository groupRepository, Environment environment)
  {
    this.userRepository = userRepository;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;
    this.environment = environment;
  }

  @ApiOperation("Get all users")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    GetAllUsers getAllUsers = new GetAllUsers(authenticationService, authorizationService,
        userRepository, tenantIdProvider);

    List<RestUser> response = new ArrayList<>();

    try
    {
      List<GetUserOutput> allUsers = getAllUsers.execute(null);

      for (GetUserOutput user : allUsers)
      {
        response.add(toUserResponseModel(user));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get all users by role id")
  @GetMapping("/role/{roleId}")
  public ResponseEntity<RestResult> getAllUserByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(ERROR_MSG_INVALID_ROLE_ID);
    }

    GetGroupUsersByRole getUsersByRole = new GetGroupUsersByRole(authenticationService, authorizationService, membershipRepository, userRepository, tenantIdProvider);

    List<RestUser> response = new ArrayList<>();

    try
    {
      final GetUsersByRoleInput input = new GetUsersByRoleInput(roleId);
      if (roleId.equals(BRANCH_SPECIALIST))
      {
        input.setRoleIdList(Arrays.asList(Objects.requireNonNull(environment.getProperty("ebank.transfer.roles")).split(",")));
      }
      GetUsersByRoleOutput output = getUsersByRole.execute(input);
      Collection<User> users =  output.getUsersByRole();

      for (User user : users)
      {
        response.add(toUserResponseModelFromUserEntity(user));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get sub group users by role id")
  @GetMapping("/role/{roleId}/sub-groups")
  public ResponseEntity<RestResult> getSubGroupUsersByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(ERROR_MSG_INVALID_ROLE_ID);
    }

    GetSubGroupUsersByRole getSubGroupUsersByRole = new GetSubGroupUsersByRole(authenticationService, authorizationService, membershipRepository, userRepository, tenantIdProvider, groupRepository);

    List<RestUser> response = new ArrayList<>();

    try
    {
      GetSubGroupUsersByRoleOutput output = getSubGroupUsersByRole.execute(new GetSubGroupUsersByRoleInput(roleId));
      Collection<User> users =  output.getSubgroupUsers();

      for (User user : users)
      {
        response.add(toUserResponseModelFromUserEntity(user));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get parent group users by role id")
  @GetMapping("/role/{roleId}/parent-groups")
  public ResponseEntity<RestResult> getParentGroupUsersByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(ERROR_MSG_INVALID_ROLE_ID);
    }

    GetParentGroupUsersByRole getParentGroupUsersByRole = new GetParentGroupUsersByRole(authenticationService, authorizationService, membershipRepository, userRepository, tenantIdProvider, groupRepository);

    List<RestUser> response = new ArrayList<>();

    try
    {
      GetParentGroupUsersByRoleOutput output = getParentGroupUsersByRole.execute(new GetParentGroupUsersByRoleInput(roleId));
      Collection<User> users =  output.getParentGroupUsersByRole();

      for (User user : users)
      {
        response.add(toUserResponseModelFromUserEntity(user));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RestUser toUserResponseModel(GetUserOutput user) throws UseCaseException
  {
    RestUser model = new RestUser();
    model.setUserId(user.getId());
    model.setEmail(user.getEmail());
    model.setPhoneNumber(user.getPhoneNumber());

    GetUserMembershipsInput input = new GetUserMembershipsInput(user.getId());
    GetUserMemberships getUserMemberships = new GetUserMemberships(authenticationService, authorizationService,
        membershipRepository, tenantIdProvider);
    List<GetMembershipOutput> memberships = getUserMemberships.execute(input);
    model.setMemberships(toMembershipResponse(memberships));

    return model;
  }

  private RestUser toUserResponseModelFromUserEntity(User user) throws UseCaseException
  {
    RestUser model = new RestUser();
    model.setUserId(user.getUserId().getId());
    model.setFirstName(user.getUserInfo().getDisplayName());
    /*model.setFirstName(user.getUserInfo().getFirstName());
    model.setLastName(user.getUserInfo().getLastName());
    model.setEmail(user.getContactInfo().getEmail());
    model.setPhoneNumber(user.getContactInfo().getPhoneNumber());*/

    return model;
  }

  private List<RestMembership> toMembershipResponse(List<GetMembershipOutput> memberships)
  {
    return memberships.stream()
        .map(membership -> {
          RestMembership gmr = new RestMembership();
          gmr.setMembershipId(membership.getMembershipId());
          gmr.setUserId(membership.getUserId());
          gmr.setGroupId(membership.getGroupId());
          gmr.setRoleId(membership.getRoleId());
          return gmr;
        })
        .collect(Collectors.toList());
  }
}
