package mn.erin.domain.aim.repository;

import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UserRepository extends Repository<User>
{
  /**
   * Lists all users in the repository
   *
   * @param tenantId The ID of the tenant
   * @return The list of all users
   */
  List<User> getAllUsers(TenantId tenantId);

  /**
   * Creates a new user
   *
   * @param tenantId    The tenant ID of the user
   * @param userInfo    The info of the user
   * @param contactInfo The contact info of the user (email and phone number)
   * @return A new user object
   */
  User createUser(TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo) throws AimRepositoryException;

  /**
   * Gets a user by ID
   *
   * @param tenantId The tenant ID of the user
   * @param userId   The user ID
   * @return The user object
   * @throws AimRepositoryException If the user doesn't exist
   */
  User getUserByTenantId(TenantId tenantId, UserId userId) throws AimRepositoryException;

  /**
   * Updates a user's info
   *
   * @param userId      The ID of the user whose information will be updated
   * @param newUserInfo New user info
   * @return updated user
   */
  User updateUserInfo(UserId userId, UserInfo newUserInfo) throws AimRepositoryException;

  /**
   * Updates a user's contact info
   *
   * @param userId      The ID of the user whose contact info will be updated
   * @param contactInfo New contact info
   * @return updated user
   */
  User updateContactInfo(UserId userId, ContactInfo contactInfo) throws AimRepositoryException;

  /**
   * Changes the tenant of a user
   *
   * @param userId      The ID of the user whose tenant will be changed
   * @param newTenantId A new tenant ID
   * @return An updated user
   */
  User changeTenant(UserId userId, TenantId newTenantId) throws AimRepositoryException;

  /**
   * Deletes a user
   *
   * @param userId The ID of the user
   * @return true if deleted; otherwise, false
   */
  boolean deleteUser(UserId userId) throws AimRepositoryException;
}
