package mn.erin.aim.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.aim.repository.jdbc.interfaces.JdbcMembershipRepository;
import mn.erin.aim.repository.jdbc.model.JdbcMembership;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcMembershipRepository implements MembershipRepository
{
  private final JdbcMembershipRepository jdbcMembershipRepository;

  @Inject
  public DefaultJdbcMembershipRepository(JdbcMembershipRepository jdbcMembershipRepository)
  {
    this.jdbcMembershipRepository = jdbcMembershipRepository;
  }

  @Override
  public Membership create(UserId userId, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(userId, "User Id should not be null!");
      Validate.notNull(groupId, "Group Id should not be null!");
      Validate.notNull(roleId, "Role Id should not be null!");
      Validate.notNull(tenantId, "Tenant Id should not be null!");

      String membershipId = UUID.randomUUID().toString();
      jdbcMembershipRepository.insert(membershipId, userId.getId(), groupId.getId(), roleId.getId(), tenantId.getId());
      Membership membershipToReturn = new Membership(MembershipId.valueOf(membershipId), userId, groupId, roleId);
      membershipToReturn.setTenantId(tenantId);
      return membershipToReturn;
    }
    catch (DuplicateKeyException | DbActionExecutionException | NullPointerException | IllegalArgumentException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public Membership findByUserId(UserId userId) throws AimRepositoryException
  {
    Validate.notNull(userId, "User Id is required!");
    try
    {
      List<JdbcMembership> memberships = jdbcMembershipRepository.getJdbcMembershipByUserId(userId.getId());

      if (memberships.isEmpty())
      {
        return null;
      }

      return convertToGroupMembership(memberships.get(0));
    }
    catch (DbActionExecutionException | NullPointerException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public List<Membership> listAllByUserId(TenantId tenantId, UserId userId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id is required!");
      Validate.notNull(userId, "User Id is required!");

      List<Membership> membershipListToReturn = new ArrayList<>();

      Iterator<JdbcMembership> jdbcMembershipIterator = jdbcMembershipRepository.getJdbcMembershipByUserIdAndTenantId(userId.getId(), tenantId.getId())
          .iterator();
      while (jdbcMembershipIterator.hasNext())
      {
        membershipListToReturn.add(convertToGroupMembership(jdbcMembershipIterator.next()));
      }
      return membershipListToReturn;
    }
    catch (DbActionExecutionException | NullPointerException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id is required!");
      Validate.notNull(groupId, "User Id is required!");

      List<Membership> membershipListToReturn = new ArrayList<>();

      Iterator<JdbcMembership> jdbcMembershipIterator = jdbcMembershipRepository.getJdbcMembershipByGroupIdAndTenantId(groupId.getId(), tenantId.getId())
          .iterator();
      while (jdbcMembershipIterator.hasNext())
      {
        membershipListToReturn.add(convertToGroupMembership(jdbcMembershipIterator.next()));
      }
      return membershipListToReturn;
    }
    catch (NullPointerException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public List<Membership> listAllBy(TenantId tenantId, GroupId groupId, RoleId roleId) throws AimRepositoryException
  {
    Validate.notNull(tenantId, "Tenant Id is required!");
    Validate.notNull(groupId, "User Id is required!");
    Validate.notNull(roleId, "Role Id is required!");

    try
    {
      List<Membership> membershipListToReturn = new ArrayList<>();

      Iterator<JdbcMembership> jdbcMembershipIterator = jdbcMembershipRepository
          .getJdbcMembershipByGroupIdAndTenantIdAndRoleId(groupId.getId(), tenantId.getId(), roleId.getId())
          .iterator();
      while (jdbcMembershipIterator.hasNext())
      {
        membershipListToReturn.add(convertToGroupMembership(jdbcMembershipIterator.next()));
      }
      return membershipListToReturn;
    }
    catch (Exception ex)
    {
      throw new AimRepositoryException(ex.getMessage(), ex);
    }
  }

  @Override
  public boolean delete(MembershipId membershipId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(membershipId, "Membership Id is required!");
      jdbcMembershipRepository.deleteById(membershipId.getId());
    }
    catch (DbActionExecutionException | NullPointerException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }

    return !(jdbcMembershipRepository.existsById(membershipId.getId()));
  }

  @Override
  public Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException
  {
    if (roleId == null)
    {
      throw new AimRepositoryException("Role Id must not be null!");
    }

    List<Membership> memberships = new ArrayList<>();

    try
    {
      Collection<JdbcMembership> jdbcMemberships = jdbcMembershipRepository.getJdbcMembershipByRoleId(roleId.getId());

      for (JdbcMembership jdbcMembership : jdbcMemberships)
      {
        memberships.add(convertToGroupMembership(jdbcMembership));
      }
      return memberships;
    }
    catch (Exception e)
    {
      throw new AimRepositoryException(e.getMessage());
    }
  }

  private Membership convertToGroupMembership(JdbcMembership jdbcMembership)
  {
    MembershipId groupMembershipId = MembershipId.valueOf(jdbcMembership.getMembershipId());
    UserId userId = new UserId(jdbcMembership.getUserId());
    GroupId groupId = new GroupId(jdbcMembership.getGroupId());
    RoleId roleId = new RoleId(jdbcMembership.getRoleId());
    TenantId tenantId = new TenantId(jdbcMembership.getTenantId());
    Membership groupMembershipToReturn = new Membership(groupMembershipId, userId, groupId, roleId);
    groupMembershipToReturn.setTenantId(tenantId);

    return groupMembershipToReturn;
  }
}
