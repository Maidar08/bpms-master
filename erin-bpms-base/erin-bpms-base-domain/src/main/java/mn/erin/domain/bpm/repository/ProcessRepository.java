package mn.erin.domain.bpm.repository;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;

/**
 * @author Zorig
 */
public interface ProcessRepository extends Repository<Process>
{
  /**
   * Creates new instance
   *
   * @param processInstanceId the id of the process instance
   * @param startedDate    when process is started
   * @param finishedDate   when process is finished
   * @param parameters    the process parameters, can be null
   * @return the new instance or null
   * @throws BpmRepositoryException when there is a SQL insertion error
   */
  Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate, Map<ParameterEntityType, Map<String, Serializable>> parameters)
      throws BpmRepositoryException;

  /**
   *
   * @param processInstanceId the id of the process instance
   * @param startedDate when process is started
   * @param finishedDate when process is finished
   * @param createdUser  user who created process
   * @param processTypeCategory process type category
   * @param parameters  the process parameters, can be null
   * @return the new process
   * @throws BpmRepositoryException when there is a SQL insertion error
   */

  Process createProcess(String processInstanceId, LocalDateTime startedDate, LocalDateTime finishedDate, String createdUser, String processTypeCategory, Map<ParameterEntityType, Map<String, Serializable>> parameters)
      throws BpmRepositoryException;

  /**
   * Delete process
   *
   * @param processInstanceId the process instance id, not null
   * @throws BpmRepositoryException when there is a SQL error
   */
  void deleteProcess(ProcessInstanceId processInstanceId);

  /**
   * Updates parameter if existent, adds new parameter if non existent
   *
   * @param processInstanceId the id of the process instance
   * @param parameters    the process parameters, can't be null
   * @return int - number of parameters updated
   * @throws BpmRepositoryException when there is a SQL update error, if given id or parameters are null
   */
  int updateParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters)
      throws BpmRepositoryException;

  /**
   * Deletes process parameters by entity type.
   *
   * @param processId Unique id of the process instance
   * @param deleteParams parameters.
   * @return isDeleted.
   */
  int deleteProcessParameters(String processId, Map<ParameterEntityType, List<String>> deleteParams) throws BpmRepositoryException;

  /**
   * Updates parameter if existent, adds new parameter if non existent
   *
   * @param name the name column of the process instance.
   * @param parameters    the process parameters, can't be null
   * @return int - number of parameters updated
   * @throws BpmRepositoryException when there is a SQL update error, if given id or parameters are null
   */
  int updateParametersByName(String name, Map<ParameterEntityType, Map<String, Serializable>> parameters)
      throws BpmRepositoryException;

  /**
   * Updates large sized parameter if existent, adds new parameter if non existent
   *
   * @param processInstanceId the id of the process instance
   * @param parameters    the process parameters, can't be null
   * @return int - number of parameters updated
   * @throws BpmRepositoryException when there is a SQL update error, if given id or parameters are null
   */
  int updateLargeParameters(String processInstanceId, Map<ParameterEntityType, Map<String, Serializable>> parameters)
      throws BpmRepositoryException;

  Process filterLargeParamBySearchKeyFromValue(String searchKey, String additionalSearchKey, ParameterEntityType entity) throws SQLException, BpmRepositoryException;

  /**
   * Deleted process
   *
   * @param processInstanceId the id of the process instance
   * @return true if deleted, false if not
   * @throws BpmRepositoryException when there is a SQL delete error, if given id is null
   */

  boolean deleteProcess(String processInstanceId)
      throws BpmRepositoryException;

  /**
   * Filters parameters of process by entity type
   *
   * @param processInstanceId the id of the process instance
   * @param parameterEntityType entity type of parameter to use as filter
   * @return Process if existent and has existent parameters, null if not
   * @throws BpmRepositoryException when there is a SQL error, if given id is null
   */
  Process filterByInstanceIdAndEntityType(String processInstanceId, ParameterEntityType parameterEntityType)
      throws BpmRepositoryException;

  /**
   * Filters large parameters of process by entity type
   *
   * @param parameterName the name of the process instance
   * @param parameterEntityType entity type of parameter to use as filter
   * @return Process if existent and has existent parameters, null if not
   * @throws BpmRepositoryException when there is a SQL error, if given id is null
   */

  Process filterByParameterNameAndEntityType(String parameterName, ParameterEntityType parameterEntityType)
      throws BpmRepositoryException;

  /**
   * Filter process parameter by paramtere name
   * @param instanceId the id of the process instance
   * @param parameterName the name of the process instance
   * @return Process if existent and has existent parameters, null if not
   */
  Process filterByParameterName(String instanceId, String parameterName);

  /**
   * Filter process parameter by paramtere name
   * @param parameterName the name of the process instance
   * @return Process if existent and has existent parameters, null if not
   */
  Process filterByParameterNameOnly(String parameterName);

  /**
   * Filters large parameters of process by entity type
   *
   * @param processInstanceId the id of the process instance
   * @param parameterEntityType entity type of parameter to use as filter
   * @return Process if existent and has existent parameters, null if not
   * @throws BpmRepositoryException when there is a SQL error, if given id is null
   */
  Process filterLargeParametersByEntityType(String processInstanceId, ParameterEntityType parameterEntityType)
      throws BpmRepositoryException;

  /**
   * Updates process finished date parameter
   *
   * @param processInstanceId the id of the process instance
   * @param finishedDate date when the process was finished
   * @return true if updated, false if not
   * @throws BpmRepositoryException when there is a SQL update error, if given id is null
   */
  boolean updateFinishedDate(String processInstanceId, LocalDateTime finishedDate)
      throws BpmRepositoryException;

  /**
   * Deletes process parameters by entity type
   *
   * @param processInstanceId the id of the process instance
   * @param parameterEntityType entity to delete
   * @return true if deleted, false if not deleted
   * @throws BpmRepositoryException when there is a SQL delete error, if given id is null
   */
  boolean deleteEntity(String processInstanceId, ParameterEntityType parameterEntityType)
      throws BpmRepositoryException;

  /**
   * Get process by entity type
   * @param entityType entity type
   * @return processes
   */
  List<Process> filterByParameterNameAndNameValueAndEntityType(ParameterEntityType entityType, String parameterName, String parameterNameValue);

  /**
   *
   * @param createdUser process created user
   * @return
   */
  Process findProcessByUserAndType(String createdUser, String processTypeCategory);

  List<Process> findProcessesByDate(LocalDate startDate, LocalDate endDate);

  /**
   * Get process parameters by instance id
   * @param instanceId id of the instance
   * @return map of parameters
   */
  Map<String, Object> getProcessParametersByInstanceId(String instanceId);
  /**
   *
   * @param createdUser process created user
   * @param processTypeCategory process type category
   */
  void deleteProcesses(String createdUser, String processTypeCategory );
}
