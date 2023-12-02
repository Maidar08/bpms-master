/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.repository;

import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessType;

/**
 * @author EBazarragchaa
 */
public interface ProcessTypeRepository extends Repository<ProcessType>
{
  /**
   * Creates process type from following parameter.
   *
   * @param processTypeId         Unique id of process type.
   * @param processDefinitionKey  definition key.
   * @param name                  process type name.
   * @param processDefinitionType definition type.
   * @throws AimRepositoryException when there is a SQL insertion error.
   * @return created process type.
   */
  ProcessType create(String processTypeId, String processDefinitionKey, String name, ProcessDefinitionType processDefinitionType) throws
      BpmRepositoryException;

  /**
   * Get process types by category.
   * @return created process types.
   */
  List<ProcessType> findByProcessTypeCategory(String processTypeCategory)  throws BpmRepositoryException;

}
