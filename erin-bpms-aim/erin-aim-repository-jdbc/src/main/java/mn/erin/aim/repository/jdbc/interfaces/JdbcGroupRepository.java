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

import mn.erin.aim.repository.jdbc.model.JdbcGroup;

/**
 * @author Zorig
 */
public interface JdbcGroupRepository extends CrudRepository<JdbcGroup, String>
{
  @Query(value="Select * from \"AIM_GROUP\" where parent_id IS NULL and tenant_id = (:tenantIdString)")
  List<JdbcGroup> getAllRootGroupsByTenantId(@Param("tenantIdString") String tenantIdString);

  @Modifying
  @Query(value = "INSERT INTO \"AIM_GROUP\"(ID, PARENT_ID, TENANT_ID, NAME, NTH_SIBLING) VALUES(:id, :parentId, :tenantId, :name, :nthSibling)")
  int insert(@Param("id") String id, @Param("parentId") String parentId, @Param("tenantId") String tenantId, @Param("name") String name, @Param("nthSibling") int nthSibling);

  @Query(value = "SELECT * FROM \"AIM_GROUP\" WHERE \"ID\" = :id and TENANT_ID = :tenantId")
  JdbcGroup getByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

  @Modifying
  @Query(value = "UPDATE \"AIM_GROUP\" SET NTH_SIBLING = :nthSibling - 1 WHERE PARENT_ID = :parentId AND NTH_SIBLING = :nthSibling")
  int updateNthSiblingByParentIdAndNthSibling(@Param("parentId") String parentId, @Param("nthSibling") int nthSibling);

  @Modifying
  @Query(value = "UPDATE \"AIM_GROUP\" SET NAME = :name WHERE ID = :id")
  void setByName(@Param("id") String id, @Param("name") String name);

  @Query(value="Select * from \"AIM_GROUP\" WHERE ID = :id")
  JdbcGroup getById(@Param("id") String id);
}
