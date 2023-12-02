/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;

import static mn.erin.aim.repository.memory.MemoryMembershipRepository.DIRECTOR;
import static mn.erin.aim.repository.memory.MemoryMembershipRepository.HR_MANAGER;

/**
 * @author EBazarragchaa
 */
@Repository
public class MemoryUserRepository implements UserRepository
{
  private static final String TENANT_ID = "xac";
  private static List<User> users = new ArrayList<>();

  private static final User EBANK = new User(UserId.valueOf("tamir"), TenantId.valueOf(TENANT_ID), "demo");
  private static final User ADMIN = new User(UserId.valueOf(HR_MANAGER), TenantId.valueOf(TENANT_ID), "demo");
  private static final User USER = new User(UserId.valueOf(DIRECTOR), TenantId.valueOf(TENANT_ID), "demo");
  private static final User USER2 = new User(UserId.valueOf("altansoyombo"), TenantId.valueOf(TENANT_ID), "demo");

  private static final User USER3 = new User(UserId.valueOf("odgavaa"), TenantId.valueOf(TENANT_ID), "demo");
  private static final User USER4 = new User(UserId.valueOf("zorig"), TenantId.valueOf(TENANT_ID), "demo");

  private static final User USER5 = new User(UserId.valueOf("lhagvaa"), TenantId.valueOf(TENANT_ID), "demo");
  private static final User USER6 = new User(UserId.valueOf("zambaga"), TenantId.valueOf(TENANT_ID), "demo");
  private static final User USER7 = new User(UserId.valueOf("otgoo"), TenantId.valueOf(TENANT_ID), "demo");

  static
  {
    users.add(EBANK);
    users.add(ADMIN);
    users.add(USER);

    users.add(USER2);
    users.add(USER3);

    users.add(USER4);
    users.add(USER5);

    users.add(USER6);
    users.add(USER7);
  }

  public MemoryUserRepository()
  {
    // system always

  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    List<User> userList = new ArrayList<>();

    for (User user : this.users)
    {
      if (user.getTenantId().equals(tenantId))
      {
        userList.add(user);
      }
    }
    return userList;
  }

  @Override
  public User createUser(TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo)
  {
    String userId = UUID.randomUUID().toString();
    User user = new User(UserId.valueOf(userId), tenantId);
    user.setUserInfo(userInfo);
    user.setContactInfo(contactInfo);
    users.add(user);

    return user;
  }

  @Override
  public User getUserByTenantId(TenantId tenantId, UserId userId) throws AimRepositoryException
  {
    User user = findUserByUserId(userId);
    if (user != null && user.getTenantId().equals(tenantId))
    {
      return user;
    }

    throw new AimRepositoryException("User not found!");
  }

  public boolean deleteUser(UserId userId)
  {
    User user = findUserByUserId(userId);
    if (user != null)
    {
      users.remove(user);
      return true;
    }

    return false;
  }

  @Override
  public User updateUserInfo(UserId userId, UserInfo newUserInfo)
  {
    User user = findUserByUserId(userId);
    if (user != null)
    {
      user.setUserInfo(newUserInfo);
    }

    return user;
  }

  @Override
  public User updateContactInfo(UserId userId, ContactInfo contactInfo)
  {
    User user = findUserByUserId(userId);
    if (user != null)
    {
      user.setContactInfo(contactInfo);
    }

    return user;
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public User findById(EntityId entityId)
  {
    return findUserByUserId((UserId) entityId);
  }

  @Override
  public Collection<User> findAll()
  {
    return Collections.unmodifiableList(users);
  }

  private User findUserByUserId(UserId userId)
  {
    for (User user : users)
    {
      if (user.getUserId().equals(userId))
      {
        return user;
      }
    }

    return null;
  }
}
