package mn.erin.bpm.repository.jdbc.interfaces;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcessLargeParameter;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessParameter;

/**
 * @author Lkhagvadorj
 */

public interface JdbcProcessLargeParameterRepository extends CrudRepository<JdbcProcessParameter, String>
{
  @Modifying
  @Query(value = "INSERT INTO PROCESS_LARGE_PARAMETER(PROCESS_INSTANCE_ID, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_DATA_TYPE, PARAMETER_ENTITY_TYPE) VALUES(:processInstanceId, :parameterName, :parameterValue, :parameterDataType, :parameterEntityType)")
  int insert(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName, @Param("parameterValue") Serializable parameterValue, @Param("parameterDataType") String parameterDataType, @Param("parameterEntityType") String parameterEntityType);

  @Query(value="Select * FROM PROCESS_LARGE_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName")
  JdbcProcessLargeParameter getByParameterName(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName);

  @Query(value = ("Select * FROM PROCESS_LARGE_PARAMETER WHERE PARAMETER_ENTITY_TYPE = :entity"))
  List<JdbcProcessLargeParameter> getByEntityAndSearchFromValueByKey(@Param("entity") String entity);

  @Modifying
  @Query(value = "UPDATE PROCESS_LARGE_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_NAME = :parameterName AND PARAMETER_ENTITY_TYPE = :parameterEntityType "
      + "AND PARAMETER_DATA_TYPE = :parameterDataType")
  int updateParameter(@Param("processInstanceId") String processInstanceId, @Param("parameterName") String parameterName, @Param("parameterValue") Serializable parameterValue, @Param("parameterEntityType") String parameterEntityType, @Param("parameterDataType") String parameterDataType);

  @Modifying
  @Query(value = "DELETE FROM PROCESS_LARGE_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  int deleteAllByProcessInstanceId(@Param("processInstanceId") String processInstaceId);

  @Modifying
  @Query(value = "DELETE FROM PROCESS_LARGE_PARAMETER WHERE PROCESS_INSTANCE_ID = :processInstanceId AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  int deleteByEntityType(@Param("processInstanceId") String processInstanceId, @Param("parameterEntityType") String parameterEntityType);

}
