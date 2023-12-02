package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Tamir
 */
public class GetUsersByRoleAndGivenGroup extends AuthorizedUseCase<GetUsersByRoleAndGivenGroupInput, GetUsersByRoleAndGivenGroupOutput>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetUsersByRoleAndGivenGroup.class);
  private static final Permission permission = new AimModulePermission("GetUsersByRoleAndGivenGroup");

  private final MembershipRepository membershipRepository;
  private final UserRepository userRepository;
  private final TenantIdProvider tenantIdProvider;

  public GetUsersByRoleAndGivenGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, UserRepository userRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
    this.tenantIdProvider = tenantIdProvider;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUsersByRoleAndGivenGroupOutput executeImpl(GetUsersByRoleAndGivenGroupInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Get sub group users by role, group use case input is null!");
    }
    List<User> groupUsersByRole = new ArrayList<>();
    String groupId = input.getGroupId();
    final List<String> roleIdList = input.getRoleIdList();
    if (null == roleIdList || roleIdList.isEmpty())
    {

      String roleId = input.getRoleId();
      groupUsersByRole = getUsersByRoleAndGroup(roleId, groupId);
    }
    else
    {
      for (String roleId : roleIdList)
      {
        groupUsersByRole.addAll(getUsersByRoleAndGroup(roleId, groupId));
      }
    }

    return new GetUsersByRoleAndGivenGroupOutput(groupUsersByRole);
  }

  private List<User> getUsersByRoleAndGroup(String roleId, String groupId) throws UseCaseException
  {
    try
    {
      String tenantId = tenantIdProvider.getCurrentUserTenantId();

      List<Membership> allMembership = membershipRepository
          .listAllBy(TenantId.valueOf(tenantId), GroupId.valueOf(groupId), RoleId.valueOf(roleId));

      LOG.info("####################### FOUND ALL MEMBERSHIP SIZE = [{}] WITH ROLE ID = [{}], GROUP ID = [{}]", allMembership.size(), roleId, groupId);

      List<User> allUsers = new ArrayList<>();

      for (Membership membership : allMembership)
      {
        User subGroupUser = userRepository.findById(membership.getUserId());
        if (subGroupUser != null)
        {
          allUsers.add(subGroupUser);
        }
      }

      LOG.info("#################### FOUND ALL USER SIZE = [{}] WITH ROLE ID = [{}], GROUP ID = [{}]", allUsers.size(), roleId, groupId);
      return allUsers;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
