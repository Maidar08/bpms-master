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

import mn.erin.aim.repository.jdbc.model.JdbcRole;
import mn.erin.aim.repository.jdbc.model.JdbcRolePermissionJoined;

/**
 * @author Zorig
 */
public interface JdbcRoleRepository extends CrudRepository<JdbcRole, String>
{
  @Query(value="Select * from AIM_ROLE where tenant_id = (:tenantIdString)")
  List<JdbcRole> findJdbcRolesByTenantId(@Param("tenantIdString") String tenantIdString);

  @Modifying
  @Query(value="INSERT INTO AIM_ROLE(ROLE_ID, TENANT_ID, NAME) VALUES(:roleId, :tenantId, :name)")
  int insert(@Param("roleId") String roleId, @Param("tenantId") String tenantId, @Param("name") String name);

  @Query(value = "SELECT * FROM AIM_ROLE INNER JOIN AIM_ROLE_PERMISSION ON AIM_ROLE.ROLE_ID = AIM_ROLE_PERMISSION.ROLE_ID WHERE AIM_ROLE.ROLE_ID = :roleId")
  List<JdbcRolePermissionJoined> getJoinedRolePermissionByRoleId(@Param("roleId") String roleId);
}
