/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.aim.repository.jdbc.model.JdbcUser;

/**
 * @author Zorig
 */
public interface JdbcUserRepository extends CrudRepository<JdbcUser, String>
{
  @Modifying
  @Query(value="Update AIM_USER set first_name = (:firstNameString), last_name=(:lastNameString), display_name=(:displayNameString) where user_id = (:userIdString)")
  int setUserInfoByUserId(@Param("userIdString") String userIdString, @Param("firstNameString") String firstNameString, @Param("lastNameString") String lastNameString, @Param("displayNameString") String displayNameString);

  @Modifying
  @Query(value="Update AIM_USER set phone_number = (:phoneNumberString), email=(:emailString) where user_id = (:userIdString)")
  int setContactInfoByUserId(@Param("userIdString") String userIdString, @Param("emailString") String emailString, @Param("phoneNumberString") String phoneNumberString);

  @Modifying
  @Query(value="Update AIM_USER set tenant_id = (:tenantIdString) where user_id = (:userIdString)")
  int setTenantIdByUserId(@Param("userIdString") String userIdString, @Param("tenantString") String tenantIdString);

  @Query(value = "SELECT * FROM AIM_USER WHERE TENANT_ID = :tenantId")
  List<JdbcUser> getAllByTenantId(@Param("tenantId") String tenantId);

  @Query(value = "SELECT * FROM AIM_USER WHERE TENANT_ID = :tenantId AND USER_ID = :userId")
  JdbcUser getByTenantIdAndUserId(@Param("tenantId") String tenantId, @Param("userId") String userId);

  @Modifying
  @Query(value="INSERT INTO AIM_USER(USER_ID, TENANT_ID, FIRST_NAME, LAST_NAME, DISPLAY_NAME, EMAIL, PHONE_NUMBER) VALUES(:userId, :tenantId, :firstName, :lastName, :displayName, :email, :phoneNumber)")
  int insert(@Param("userId") String userId, @Param("tenantId") String tenantId, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("displayName") String displayName, @Param("email") String email, @Param("phoneNumber") String phoneNumber);

}
