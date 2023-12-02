package mn.erin.bpm.repository.jdbc.interfaces;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcessParameter;

/**
 * @author Zorig
 */
public interface JdbcProcessParameterRepository extends CrudRepository<JdbcProcessParameter, String>
{
  @Modifying
  @Query(value = "INSERT INTO PROCESS_PARAMETER(PROCESS_INSTANCE_ID, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE, PARAMETER_ENTITY_TYPE) VALUES(:processInstanceId, :parameterName, :parameterValue, :parameterDataType, :parameterEntityType)")
  int insert(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName,
      @Param("parameterValue") Serializable parameterValue, @Param("parameterDataType") String parameterDataType,
      @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "Select * FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName")
  JdbcProcessParameter getByParameterName(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName);

  @Query(value = "Select * FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  JdbcProcessParameter getByParameterNameAndEntityType(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName,
      @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "Select * FROM PROCESS_PARAMETER WHERE PARAMETER_NAME = :parameterName")
  JdbcProcessParameter getByParameterNameOnly(@Param("parameterName") String parameterName);

  @Modifying
  @Query(value =
      "UPDATE PROCESS_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName AND PARAMETER_ENTITY_TYPE = :parameterEntityType "
          + "AND PARAMETER_DATA_TYPE = :parameterDataType")
  int updateParameter(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName,
      @Param("parameterValue") Serializable parameterValue, @Param("parameterEntityType") String parameterEntityType,
      @Param("parameterDataType") String parameterDataType);

  @Modifying
  @Query(value = "DELETE PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  int deleteParameter(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName,
      @Param("parameterEntityType") String parameterEntityType);

  @Modifying
  @Query(value = "UPDATE PROCESS_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PARAMETER_NAME = :parameterName")
  int updateParameterValueByName(@Param("parameterName") String parameterName, @Param("parameterValue") Serializable parameterValue);

  @Modifying
  @Query(value = "DELETE FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  int deleteAllByProcessInstanceId(@Param("processInstanceId") String processInstaceId);

  @Modifying
  @Query(value = "DELETE FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  int deleteByEntityType(@Param("processInstanceId") String processInstanceId, @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "SELECT * FROM PROCESS_PARAMETER WHERE PARAMETER_ENTITY_TYPE = :parameterEntityType")
  List<JdbcProcessParameter> getByParameterEntityType(@Param("parameterEntityType") String parameterEntityType);

  @Query(value = "SELECT * FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  List<JdbcProcessParameter> getByProcessInstanceIdAndEntityType(@Param("processInstanceId") String processInstanceId,
      @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "SELECT * FROM PROCESS_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  List<JdbcProcessParameter> getByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
