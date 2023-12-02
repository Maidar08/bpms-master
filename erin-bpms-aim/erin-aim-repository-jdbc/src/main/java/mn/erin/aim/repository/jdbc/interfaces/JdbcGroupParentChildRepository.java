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

import mn.erin.aim.repository.jdbc.model.JdbcGroupParentChild;

/**
 * @author Zorig
 */
public interface JdbcGroupParentChildRepository extends CrudRepository<JdbcGroupParentChild, String>
{
  @Query(value="Select count(parent_id) from AIM_GROUP_PARENT_CHILD where parent_id = (:parentIdString)")
  int countChildren(@Param("parentIdString") String parentIdString);

  @Query(value="Select count(parent_id) from AIM_GROUP_PARENT_CHILD where parent_id = (:parentIdString) AND child_id = (:childIdString)")
  int checkParentChild(@Param("parentIdString") String parentIdString, @Param("childIdString") String childIdString);

  /*
  @Query(value="Select parent_id from GROUP_PARENT_CHILD where child_id = (:childIdString)")
  String getParentIdByGroupId(@Param("childIdString") String childIdString);
   */

  @Query(value="Select parent_id from AIM_GROUP_PARENT_CHILD where child_id = (:childIdString)")
  String getParentIdByChildGroupId(@Param("childIdString") String childIdString);

  @Query(value="Select child_id from AIM_GROUP_PARENT_CHILD where parent_id = (:parentIdString)")
  List<String> getAllChildrenByParentId(@Param("parentIdString") String parentIdString);

  @Modifying
  @Query(value = "Delete from AIM_GROUP_PARENT_CHILD where parent_id = (:parentIdString)")
  void deleteByParentId(@Param("parentIdString") String parentIdString);

  @Modifying
  @Query(value = "Delete from AIM_GROUP_PARENT_CHILD where parent_id = (:parentIdString) and child_id = (:childIdString)")
  void deleteByParentIdAndChildId(@Param("parentIdString") String parentIdString, @Param("childIdString") String childIdString);

  @Modifying
  @Query(value = "INSERT INTO AIM_GROUP_PARENT_CHILD(PARENT_ID, CHILD_ID) VALUES(:parentId, :childId)")
  int insert(@Param("parentId") String parentId, @Param("childId") String childId);
}
