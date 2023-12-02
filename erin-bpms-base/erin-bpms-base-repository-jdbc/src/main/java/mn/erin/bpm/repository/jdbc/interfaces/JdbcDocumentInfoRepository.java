/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcDocumentInfo;

/**
 * @author Tamir
 */
public interface JdbcDocumentInfoRepository extends CrudRepository<JdbcDocumentInfo, String>
{
  @Query(value = "Select * FROM DOCUMENT_INFO WHERE DOCUMENT_PARENT_ID is null ORDER BY id")
  List<JdbcDocumentInfo> getBasicDocumentInfos();

  @Query(value = "Select * FROM DOCUMENT_INFO WHERE DOCUMENT_PARENT_ID = :parentId ORDER BY id")
  List<JdbcDocumentInfo> getByParentId(@Param("parentId") String parentId);

  @Query(value = "Select * FROM DOCUMENT_INFO WHERE NAME = :name")
  JdbcDocumentInfo getByName(@Param("name") String name);
}
