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

import mn.erin.aim.repository.jdbc.model.JdbcMembership;

/**
 * @author Zorig
 */
public interface JdbcMembershipRepository extends CrudRepository<JdbcMembership,String>
{
  @Query(value="Select * from AIM_MEMBERSHIP where group_id = (:groupIdString) and tenant_id = (:tenantIdString)")
  List<JdbcMembership> getJdbcMembershipByGroupIdAndTenantId(@Param("groupIdString") String groupIdString, @Param("tenantIdString") String tenantIdString);

  @Query(value="Select * from AIM_MEMBERSHIP where group_id = (:groupIdString) and tenant_id = (:tenantIdString) and role_id = (:roleIdString)")
  List<JdbcMembership> getJdbcMembershipByGroupIdAndTenantIdAndRoleId(@Param("groupIdString") String groupIdString, @Param("tenantIdString") String tenantIdString, @Param("roleIdString") String roleIdString);

  @Query(value="Select * from AIM_MEMBERSHIP where user_id = (:userIdString) and tenant_id = (:tenantIdString)")
  List<JdbcMembership> getJdbcMembershipByUserIdAndTenantId(@Param("userIdString") String userIdString, @Param("tenantIdString") String tenantIdString);

  @Query(value = "SELECT * FROM AIM_MEMBERSHIP WHERE USER_ID = :userId")
  List<JdbcMembership> getJdbcMembershipByUserId(@Param("userId") String userId);

  @Modifying
  @Query(value = "INSERT INTO AIM_MEMBERSHIP(MEMBERSHIP_ID, USER_ID, GROUP_ID, ROLE_ID, TENANT_ID) VALUES(:membershipId, :userId, :groupId, :roleId, :tenantId)")
  int insert(@Param("membershipId") String membershipId, @Param("userId") String userId, @Param("groupId") String groupId, @Param("roleId") String roleId, @Param("tenantId") String tenantId);

  @Query(value = "SELECT * FROM AIM_MEMBERSHIP WHERE ROLE_ID = :roleId")
  List<JdbcMembership> getJdbcMembershipByRoleId(@Param("roleId") String roleId);

}
