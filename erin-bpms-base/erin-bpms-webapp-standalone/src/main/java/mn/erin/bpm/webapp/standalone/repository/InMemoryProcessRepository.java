/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.webapp.standalone.repository;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Tamir
 */
@Repository
public class InMemoryProcessRepository implements ProcessRepository
{
  @Override
  public Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate,
      Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    return null;
  }

  @Override
  public Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate, String createdUser,
      String processTypeCategory, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    return null;
  }

  @Override
  public void deleteProcess(ProcessInstanceId processInstanceId)
  {

  }

  @Override
  public int updateParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    return 0;
  }

  @Override
  public int deleteProcessParameters(String processId, Map<ParameterEntityType, List<String>> parameters)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int updateParametersByName(String name, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    return 0;
  }

  @Override
  public int updateLargeParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters) throws BpmRepositoryException
  {
    return 0;
  }

  @Override
  public Process filterLargeParamBySearchKeyFromValue(String searchKey, String additionalSearchKey,  ParameterEntityType entity) throws SQLException
  {
    return null;
  }

  @Override
  public boolean deleteProcess(String processInstanceId) throws BpmRepositoryException
  {
    return false;
  }

  @Override
  public Process filterByInstanceIdAndEntityType(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    return null;
  }

  @Override
  public Process filterByParameterNameAndEntityType(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    return null;
  }

  @Override
  public Process filterByParameterName(String instanceId, String parameterName)
  {
    return null;
  }

  @Override
  public Process filterByParameterNameOnly(String parameterName)
  {
    return null;
  }

  @Override
  public Process filterLargeParametersByEntityType(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    return null;
  }

  @Override
  public boolean updateFinishedDate(String processInstanceId, LocalDateTime finishedDate) throws BpmRepositoryException
  {
    return false;
  }

  @Override
  public boolean deleteEntity(String processInstanceId, ParameterEntityType parameterEntityType) throws BpmRepositoryException
  {
    return false;
  }

  @Override
  public List<Process> filterByParameterNameAndNameValueAndEntityType(ParameterEntityType entityType, String parameterName, String parameterNameValue)
  {
    return null;
  }

  @Override
  public Process findProcessByUserAndType(String createdUser, String processTypeCategory)
  {
    return null;
  }

  @Override
  public List<Process> findProcessesByDate(LocalDate startDate, LocalDate endDate)
  {
    return null;
  }

  @Override
  public Map<String, Object> getProcessParametersByInstanceId(String instanceId)
  {
    return null;
  }

  @Override
  public void deleteProcesses(String createdUser, String processTypeCategory)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Process findById(EntityId entityId)
  {
    return null;
  }

  @Override
  public Collection<Process> findAll()
  {
    return null;
  }
}
