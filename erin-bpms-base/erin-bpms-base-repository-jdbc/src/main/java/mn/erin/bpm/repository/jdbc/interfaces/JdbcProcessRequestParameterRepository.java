package mn.erin.bpm.repository.jdbc.interfaces;

import java.io.Serializable;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestParameter;

/**
 * @author EBazarragchaa
 */
public interface JdbcProcessRequestParameterRepository extends CrudRepository<JdbcProcessRequestParameter, String>
{
  @Modifying
  @Query(value="Insert into PROCESS_REQUEST_PARAMETER(PROCESS_REQUEST_ID, PARAMETER_NAME, PARAMETER_VALUE, PARAMETER_TYPE ) VALUES ( :processRequestId, :parameterName, :parameterValue, :parameterType )")
  int insert(@Param("processRequestId") String processRequestId, @Param("parameterName") String parameterName, @Param("parameterValue") Serializable parameterValue, @Param("parameterType") Serializable parameterType);

  @Query(value = "Select * FROM PROCESS_REQUEST_PARAMETER WHERE PROCESS_REQUEST_ID = :processRequestId AND PARAMETER_NAME = :parameterName")
  JdbcProcessRequestParameter getByParameterName(@Param("processRequestId") String processRequestId, @Param("parameterName") String parameterName);

  @Modifying
  @Query(value = "UPDATE PROCESS_REQUEST_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PROCESS_REQUEST_ID = :processRequestId AND PARAMETER_NAME = :parameterName")
  int updateParameter(@Param("processRequestId") String processRequestId, @Param("parameterName") String parameterName, @Param("parameterValue") Serializable parameterValue);

  @Modifying
  @Query(value = "DELETE FROM PROCESS_REQUEST_PARAMETER where PROCESS_REQUEST_ID = :processRequestId")
  int deleteByProcessRequestId(@Param("processRequestId") String processRequestId);
}
