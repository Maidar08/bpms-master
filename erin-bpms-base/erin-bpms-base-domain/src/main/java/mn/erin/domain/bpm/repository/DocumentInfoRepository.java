/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.repository;

import java.util.Collection;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.document.DocumentInfo;

/**
 * @author Tamir
 */
public interface DocumentInfoRepository extends Repository<DocumentInfo>
{
  /**
   * Find all by sub type value.
   *
   * @return if sub type is true returns sub type document infos
   * otherwise returns main type document infos.
   */
  Collection<DocumentInfo> findBasicDocuments();

  /**
   * Finds by parent document id.
   *
   * @return found document infos
   */
  Collection<DocumentInfo> findByParentId(String parentId);

  /**
   * Finds by document info name.
   *
   * @return found document infos.
   */
  DocumentInfo findByName(String name);
}
