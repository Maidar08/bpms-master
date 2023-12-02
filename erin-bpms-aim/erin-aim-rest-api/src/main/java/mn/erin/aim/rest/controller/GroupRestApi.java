package mn.erin.aim.rest.controller;

import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.aim.rest.model.RestGroup;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.usecase.group.CreateGroup;
import mn.erin.domain.aim.usecase.group.CreateGroupInput;
import mn.erin.domain.aim.usecase.group.CreateGroupOutput;
import mn.erin.domain.aim.usecase.group.DeleteGroup;
import mn.erin.domain.aim.usecase.group.DeleteGroupInput;
import mn.erin.domain.aim.usecase.group.DeleteGroupOutput;
import mn.erin.domain.aim.usecase.group.GetAllGroupsSubTree;
import mn.erin.domain.aim.usecase.group.GetAllGroupsSubTreeOuput;
import mn.erin.domain.aim.usecase.group.GetGroupSubTree;
import mn.erin.domain.aim.usecase.group.RenameGroup;
import mn.erin.domain.aim.usecase.group.RenameGroupInput;
import mn.erin.domain.aim.usecase.group.RenameGroupOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Group")
@RequestMapping(value = "/aim/groups", name = "Provides AIM group management features")
@RestController
public class GroupRestApi extends BaseAimRestApi
{
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;

  @Inject
  public GroupRestApi(GroupRepository groupRepository, MembershipRepository membershipRepository, RoleRepository roleRepository)
  {
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
    this.roleRepository = roleRepository;
  }

  @ApiOperation("Creates a new group")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestGroup request)
  {
    CreateGroupInput input = new CreateGroupInput(request.getParentId(), request.getId(), request.getName());
    CreateGroup createGroup = new CreateGroup(authenticationService, authorizationService, groupRepository, tenantIdProvider);

    try
    {
      CreateGroupOutput output = createGroup.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a group")
  @DeleteMapping("/{groupId}")
  public ResponseEntity<RestResult> delete(@PathVariable String groupId)
  {
    DeleteGroupInput input = new DeleteGroupInput(groupId);
    DeleteGroup deleteGroup = new DeleteGroup(authenticationService, authorizationService, groupRepository);

    try
    {
      DeleteGroupOutput output = deleteGroup.execute(input);
      return RestResponse.success(output.isDeleted());
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  //This should have different mapping
  /*@ApiOperation("Gets current user's assigned groups")
  @GetMapping
  public ResponseEntity<RestResult> readAllGroupsOfUser()
  {
    //GetAllGroups getAllGroups = new GetAllGroups(authenticationService, authorizationService, groupRepository, tenantIdProvider);
    //    GetGroupSubTree getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, groupRepository);
    //    GetAllGroupsSubTree getAllGroupsSubTree = new GetAllGroupsSubTree(groupRepository, getGroupSubTree, tenantIdProvider);

    GetCurrentUserGroups getUserGroups = new GetCurrentUserGroups(authenticationService, authorizationService, tenantIdProvider,
        groupRepository, membershipRepository, roleRepository);

    try
    {
      List<GroupTree> groupTreeList = getUserGroups.execute(null);
      return RestResponse.success(groupTreeList);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
   */

  @ApiOperation("Gets ALL groups")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    //GetAllGroups getAllGroups = new GetAllGroups(authenticationService, authorizationService, groupRepository, tenantIdProvider);
    GetGroupSubTree getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, groupRepository);
    GetAllGroupsSubTree getAllGroupsSubTree = new GetAllGroupsSubTree(groupRepository, getGroupSubTree, tenantIdProvider);

    try
    {
      GetAllGroupsSubTreeOuput output = getAllGroupsSubTree.execute(null);
      List<GroupTree> groupTreeList = output.getGroupTreeList();
      return RestResponse.success(groupTreeList);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Renames group")
  @PatchMapping("/{groupId}")
  public ResponseEntity<RestResult> renameGroup(@PathVariable String groupId, @RequestBody RestGroup restGroup) throws UseCaseException
  {
    RenameGroup renameGroup = new RenameGroup(authenticationService, authorizationService, groupRepository);

    try
    {
      String name = restGroup.getName();
      RenameGroupInput input = new RenameGroupInput(groupId, name);
      RenameGroupOutput output = renameGroup.execute(input);
      RestGroup restGroupToReturn = RestGroup.of(output.getRenamedGroup());
      return ResponseEntity.ok(RestEntity.of(restGroupToReturn));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
