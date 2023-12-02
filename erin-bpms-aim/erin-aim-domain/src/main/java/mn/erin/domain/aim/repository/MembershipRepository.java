package mn.erin.domain.aim.repository;

import java.util.Collection;
import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MembershipRepository
{
  /**
   * Creates a new membership
   *
   * @param userId   The ID of the user
   * @param groupId  The ID of the group
   * @param roleId   The ID of the role
   * @param tenantId The ID of the tenant
   * @return A new membership object
   * @throws AimRepositoryException If a user already has a membership in the group
   */
  Membership create(UserId userId, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException;

  /**
   * Returns user membership
   *
   * @param userId the user id
   * @return membership or null
   */
  Membership findByUserId(UserId userId) throws AimRepositoryException;

  /**
   * Lists all memberships of a user in the specified tenant
   *
   * @param tenantId The ID of the tenant
   * @param userId   The ID of the user
   * @return A list of memberships assigned to the user
   */
  List<Membership> listAllByUserId(TenantId tenantId, UserId userId) throws AimRepositoryException;

  /**
   * Lists all memberships of a group in the specified tenant
   *
   * @param tenantId The ID of the tenant
   * @param groupId  The ID of the group
   * @return A list of memberships of the group
   */
  List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId) throws AimRepositoryException;

  /**
   * Lists all memberships of a group in the specified tenant and role.
   *
   * @param tenantId The ID of the tenant
   * @param groupId The ID of the group
   * @param roleId The ID of the role
   * @return A list of memberships of the group
   * @throws AimRepositoryException
   */
  List<Membership> listAllBy(TenantId tenantId, GroupId groupId, RoleId roleId) throws AimRepositoryException;

  /**
   * Deletes a membership
   *
   * @param membershipId The ID of the membership
   * @return true if deleted; otherwise, false
   */
  boolean delete(MembershipId membershipId) throws AimRepositoryException;

  /**
   * Returns all memberships with given role id
   *
   * @param roleId The ID of role
   * @return A collection of Memberships
   */
  Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException;
}
