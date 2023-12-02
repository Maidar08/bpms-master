package mn.erin.bpm.repository.jdbc.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcess;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessJoined;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessJoinedForLarge;

/**
 * @author Zorig
 */
public interface JdbcProcessRepository extends CrudRepository<JdbcProcess, String>
{
  @Modifying
  @Query(value = "INSERT INTO PROCESS(PROCESS_INSTANCE_ID, STARTED_DATE, FINISHED_DATE) VALUES(:processInstanceId, :startedDate, :finishedDate)")
  int insert(@Param("processInstanceId") String processInstanceId, @Param("startedDate") LocalDateTime startedDate, @Param("finishedDate") LocalDateTime finishedDate);

  @Modifying
  @Query(value = "INSERT INTO PROCESS(PROCESS_INSTANCE_ID, STARTED_DATE, FINISHED_DATE, CREATED_USER, PROCESS_TYPE_CATEGORY) VALUES(:processInstanceId, :startedDate, :finishedDate, :createdUser, :processTypeCategory)")
  int insert(@Param("processInstanceId") String processInstanceId, @Param("startedDate") LocalDateTime startedDate, @Param("finishedDate") LocalDateTime finishedDate, @Param("createdUser") String createdUser,
      @Param("processTypeCategory") String processTypeCategory);

  @Query(value = "SELECT * FROM PROCESS INNER JOIN PROCESS_PARAMETER ON PROCESS.PROCESS_INSTANCE_ID = PROCESS_PARAMETER.PROCESS_INSTANCE_ID WHERE PROCESS.PROCESS_INSTANCE_ID = (:processInstanceId)")
  List<JdbcProcessJoined> getJoinedProcessByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

  @Query(value = "SELECT * FROM PROCESS INNER JOIN PROCESS_PARAMETER ON PROCESS.PROCESS_INSTANCE_ID = PROCESS_PARAMETER.PROCESS_INSTANCE_ID WHERE PROCESS.PROCESS_INSTANCE_ID = :processInstanceId"
      + " AND PROCESS_PARAMETER.PARAMETER_ENTITY_TYPE = :parameterEntityType")
  List<JdbcProcessJoined> getJoinedProcessByProcessInstanceIdAndEntityType(@Param("processInstanceId") String processInstanceId, @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "SELECT * FROM PROCESS INNER JOIN PROCESS_PARAMETER ON PROCESS.PROCESS_INSTANCE_ID = PROCESS_PARAMETER.PROCESS_INSTANCE_ID WHERE PROCESS_PARAMETER.PARAMETER_NAME = :name"
      + " AND PROCESS_PARAMETER.PARAMETER_ENTITY_TYPE = :parameterEntityType")
  List<JdbcProcessJoined> getJoinedProcessByNameAndEntityType(@Param("name") String name, @Param("parameterEntityType") String parameterEntityType);

  @Query(value = "SELECT * FROM PROCESS INNER JOIN PROCESS_LARGE_PARAMETER ON PROCESS.PROCESS_INSTANCE_ID = PROCESS_LARGE_PARAMETER.PROCESS_INSTANCE_ID WHERE PROCESS.PROCESS_INSTANCE_ID = :processInstanceId"
      + " AND PROCESS_LARGE_PARAMETER.PARAMETER_ENTITY_TYPE = :parameterEntityType")
  List<JdbcProcessJoinedForLarge> getJoinedLargeProcessByProcessInstanceIdAndEntityType(@Param("processInstanceId") String processInstanceId, @Param("parameterEntityType") String parameterEntityType);

  @Modifying
  @Query(value = "UPDATE PROCESS SET FINISHED_DATE = :finishedDate WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  int updateFinishedDate(@Param("processInstanceId") String processInstanceId, @Param("finishedDate") LocalDateTime finishedDate);

  @Modifying
  @Query(value = "DELETE FROM PROCESS where PROCESS_INSTANCE_ID = :processInstanceId")
  int deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

  @Modifying
  @Query(value = "DELETE FROM PROCESS where CREATED_USER = :createdUser AND PROCESS_TYPE_CATEGORY= :processTypeCategory")
  int deleteProcessesByTypeAndUser(@Param("createdUser") String createdUser, @Param("processTypeCategory") String processTypeCategory);

  @Query(value = "SELECT * FROM PROCESS WHERE CREATED_USER = :createdUser AND PROCESS_TYPE_CATEGORY = :processTypeCategory")
  List <JdbcProcess> findByCreatedUser(@Param("createdUser") String createdUser, @Param("processTypeCategory") String processTypeCategory);

  @Query(value = "SELECT * FROM PROCESS WHERE STARTED_DATE BETWEEN :startDate AND :endDate")
  List<JdbcProcess> findProcessesByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
