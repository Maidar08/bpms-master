package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;

/**
 * author Naranbaatar Avir.
 */
@Repository
public class MemoryMembershipRepository implements MembershipRepository
{

  public static final String HR_MANAGER = "hrManager";
  public static final String DIRECTOR = "director";

  public static final String GROUP_ID = "123";

  private List<Membership> memberships = new ArrayList<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryMembershipRepository.class);

  public MemoryMembershipRepository()
  {
    try
    {
      Membership membership1 = create(UserId.valueOf(HR_MANAGER), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership2 = create(UserId.valueOf(DIRECTOR), GroupId.valueOf(GROUP_ID), RoleId.valueOf(DIRECTOR), new TenantId("xac"));

      Membership membership = create(UserId.valueOf("tamir"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership3 = create(UserId.valueOf("altansoyombo"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership4 = create(UserId.valueOf("lhagvaa"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership5 = create(UserId.valueOf("zambaga"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership6 = create(UserId.valueOf("otgoo"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership7 = create(UserId.valueOf("admin"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));
      Membership membership8 = create(UserId.valueOf("ebank"), GroupId.valueOf(GROUP_ID), RoleId.valueOf(HR_MANAGER), new TenantId("xac"));

      memberships.add(membership);
      memberships.add(membership1);
      memberships.add(membership2);
      memberships.add(membership3);
      memberships.add(membership4);
      memberships.add(membership5);
      memberships.add(membership6);
      memberships.add(membership7);
      memberships.add(membership8);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public Membership create(UserId userId, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException
  {
    String membershipId = String.valueOf(System.currentTimeMillis());
    Membership membership = new Membership(MembershipId.valueOf(membershipId), userId, groupId, roleId);
    membership.setTenantId(tenantId);
    memberships.add(membership);

    return membership;
  }

  @Override
  public Membership findByUserId(UserId userId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Membership> listAllByUserId(TenantId tenantId, UserId userId)
  {
    MemoryUserRepository memoryUserRepository = new MemoryUserRepository();
    User user;
    List<Membership> membershipList = new ArrayList<>();

    try
    {
      user = memoryUserRepository.getUserByTenantId(tenantId, userId);
    }
    catch (AimRepositoryException e)
    {
      LOGGER.error("User not found", e);
      return membershipList;
    }

    for (Membership membership : memberships)
    {
      if (membership.getUserId().equals(userId) && user.getUserId().equals(userId))
      {
        membershipList.add(membership);
      }
    }
    return membershipList;
  }

  @Override
  public List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Membership> listAllBy(TenantId tenantId, GroupId groupId, RoleId roleId) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(MembershipId membershipId)
  {
    return memberships.removeIf(membership -> membership.sameIdentityAs(membership));
  }

  @Override
  public Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException
  {
    return null;
  }
}
