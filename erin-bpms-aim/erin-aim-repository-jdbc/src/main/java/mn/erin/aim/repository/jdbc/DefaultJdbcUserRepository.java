package mn.erin.aim.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Repository;

import mn.erin.aim.repository.jdbc.interfaces.JdbcUserRepository;
import mn.erin.aim.repository.jdbc.model.JdbcUser;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcUserRepository implements UserRepository
{
  private final JdbcUserRepository jdbcUserRepository;

  @Inject
  public DefaultJdbcUserRepository(JdbcUserRepository jdbcUserRepository)
  {
    this.jdbcUserRepository = jdbcUserRepository;
  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    Validate.notNull(tenantId, "Tenant Id is required!");
    List<User> allUsers = new ArrayList<>();

    Iterator<JdbcUser> jdbcUserIterator = jdbcUserRepository.getAllByTenantId(tenantId.getId()).iterator();

    while (jdbcUserIterator.hasNext())
    {
      User userToAdd = convertToUser(jdbcUserIterator.next());
      allUsers.add(userToAdd);
    }

    return allUsers;
  }

  @Override
  public User createUser(TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id is required!");
      Validate.notNull(userInfo, "User Info is required!");
      Validate.notNull(contactInfo, "Contact Info is required!");
      String userId = UUID.randomUUID().toString();
      String email = contactInfo.getEmail();
      String phoneNumber = contactInfo.getPhoneNumber();
      String firstName = userInfo.getFirstName();
      String lastName = userInfo.getLastName();
      String displayName = userInfo.getDisplayName();

      jdbcUserRepository.insert(userId, tenantId.getId(), firstName, lastName, displayName, email, phoneNumber);
      return new User(new UserId(userId), tenantId, userInfo, contactInfo);
    }
    catch (NullPointerException | DuplicateKeyException | DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public User getUserByTenantId(TenantId tenantId, UserId userId) throws AimRepositoryException
  {
    try
    {
      Validate.notNull(tenantId, "Tenant Id is required!");
      Validate.notNull(userId, "User Id is required!");
      JdbcUser jdbcUser = jdbcUserRepository.getByTenantIdAndUserId(tenantId.getId(), userId.getId());
      if (jdbcUser != null)
      {
        return convertToUser(jdbcUser);
      }
      return null;
    }
    catch (DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public User updateUserInfo(UserId userId, UserInfo newUserInfo) throws AimRepositoryException
  {
    Validate.notNull(userId, "User Id is required!");
    String firstName = newUserInfo.getFirstName();
    String lastName = newUserInfo.getLastName();
    String displayName = newUserInfo.getDisplayName();

    try
    {
      int numberOfAffectedRows = jdbcUserRepository.setUserInfoByUserId(userId.getId(), firstName, lastName, displayName);
      if (numberOfAffectedRows == 0)
      {
        return findById(userId);
      }
      return null;
    }
    catch (DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public User updateContactInfo(UserId userId, ContactInfo contactInfo) throws AimRepositoryException
  {
    Validate.notNull(userId, "User Id is required!");
    String email = contactInfo.getEmail();
    String phoneNumber = contactInfo.getPhoneNumber();

    try
    {
      int numberOfAffectedRows = jdbcUserRepository.setContactInfoByUserId(userId.getId(), email, phoneNumber);
      if (numberOfAffectedRows == 1)
      {
        return findById(userId);
      }
      return null;
    }
    catch (DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage());
    }
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId) throws AimRepositoryException
  {
    try
    {
      int numberOfAffectedRows = jdbcUserRepository.setTenantIdByUserId(userId.getId(), newTenantId.getId());
      if (numberOfAffectedRows == 1)
      {
        return findById(userId);
      }
      return null;
    }
    catch (DbActionExecutionException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean deleteUser(UserId userId) throws AimRepositoryException
  {
    try
    {
      jdbcUserRepository.deleteById(userId.getId());
    }
    catch (DbActionExecutionException | IllegalArgumentException | NullPointerException e)
    {
      throw new AimRepositoryException(e.getMessage(), e);
    }

    //check existence after operation
    return !(jdbcUserRepository.existsById(userId.getId()));
  }

  @Override
  public User findById(EntityId entityId)
  {
    Optional<JdbcUser> findJdbcUser = jdbcUserRepository.findById(entityId.getId());
    if (findJdbcUser.isPresent())
    {
      return convertToUser(findJdbcUser.get());
    }
    return null;
  }

  @Override
  public Collection<User> findAll()
  {
    List<User> rolesToReturn = new ArrayList<>();

    Iterator<JdbcUser> returnedJdbcUsers = jdbcUserRepository.findAll().iterator();
    while (returnedJdbcUsers.hasNext())
    {
      rolesToReturn.add(convertToUser(returnedJdbcUsers.next()));
    }

    return rolesToReturn;
  }

  private User convertToUser(JdbcUser jdbcUser)
  {
    UserInfo userInfo = new UserInfo(jdbcUser.getFirstName(), jdbcUser.getLastName());
    ContactInfo contactInfo = new ContactInfo(jdbcUser.getEmail(), jdbcUser.getPhoneNumber());
    User userToReturn = new User(new UserId(jdbcUser.getUserId()), new TenantId(jdbcUser.getTenantId()), userInfo, contactInfo);
    userToReturn.setPassword(jdbcUser.getPassword());
    return userToReturn;
  }
}
