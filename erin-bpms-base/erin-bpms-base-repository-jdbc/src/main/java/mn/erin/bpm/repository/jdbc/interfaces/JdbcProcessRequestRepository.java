package mn.erin.bpm.repository.jdbc.interfaces;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequest;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestJoined;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestParameter;

/**
 * @author EBazarragchaa
 */
public interface JdbcProcessRequestRepository extends CrudRepository<JdbcProcessRequest, String>
{

  @Query(
      "SELECT * FROM PROCESS_REQUEST INNER JOIN PROCESS_REQUEST_PARAMETER ON PROCESS_REQUEST.PROCESS_REQUEST_ID = PROCESS_REQUEST_PARAMETER.PROCESS_REQUEST_ID "
          + "WHERE (PROCESS_REQUEST.CREATED_TIME BETWEEN :firstTime AND :secondTime) AND PROCESS_REQUEST_PARAMETER.PARAMETER_NAME = "
          + "'cifNumber' AND PROCESS_REQUEST_PARAMETER.PARAMETER_VALUE = :parameterValue AND PROCESS_REQUEST.PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequestJoined> getJdbcProcessRequestsByCreatedDate(@Param(value = "firstTime") Date firstTime, @Param(value = "secondTime") Date secondTime,
      @Param(value = "parameterValue") String parameterValue);

  @Modifying
  @Query(value = "Insert into PROCESS_REQUEST(PROCESS_REQUEST_ID, PROCESS_TYPE_ID, GROUP_ID, REQUESTED_USER_ID, PROCESS_REQUEST_STATE, CREATED_TIME ) VALUES ( :processRequestId, :processType, :groupId, :requestedUserId, :processRequestState, :createdTime )")
  int insert(@Param("processRequestId") String processRequestId, @Param("processType") String processType, @Param("groupId") String groupId,
      @Param("requestedUserId") String requestedUserId, @Param("processRequestState") String processRequestState,
      @Param("createdTime") LocalDateTime createdTime);

  @Query("SELECT PROCESS_REQUEST_SEQ.nextval from dual")
  String getCurrentIncrementId();

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE ASSIGNED_USER_ID = (:assignedUserId) AND PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequest> getJdbcProcessRequestsByAssignedUserId(@Param(value = "assignedUserId") String assignedUserId);

  @Query(value = "SELECT process_request_id,  process_type_id, group_id, requested_user_id, created_time, assigned_user_id, process_instance_id, assigned_time, process_request_state, null as parameter_name, null as parameter_value, null as parameter_type \n"
      + "FROM PROCESS_REQUEST WHERE process_request.assigned_user_id = (:assignedUserId)  and process_request_state != 'DELETED' and created_time >= (:startDate) and created_time < (:endDate) + 1\n"
      + "union\n"
      + "select process_request_id, null, null, null, null, null, null, null, null,  parameter_name, parameter_value, parameter_type \n"
      + "from PROCESS_REQUEST_PARAMETER WHERE process_request_id in \n"
      + "(select process_request_id from process_request where process_request.assigned_user_id = (:assignedUserId)  and process_request_state != 'DELETED'  and created_time >= (:startDate) and created_time < (:endDate) + 1)")
  List<JdbcProcessRequestJoined> getProcessRequestsByAssignedUserId(@Param(value = "assignedUserId") String assignedUserId, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);

  @Query(value = "SELECT PROCESS_REQUEST.* \n"
      + "FROM PROCESS_REQUEST JOIN \n"
      + "(SELECT PROCESS_REQUEST_ID FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_VALUE = (:searchKey)) P\n"
      + "ON P.PROCESS_REQUEST_ID = PROCESS_REQUEST.PROCESS_REQUEST_ID"
      + "WHERE PROCESS_REQUEST.PROCESS_REQUEST_STATE != 'DELETED'\n")
  List<JdbcProcessRequest> getJdbcProcessRequestsBySearchKey(@Param(value = "searchKey") String searchKey);

  @Query("SELECT * FROM PROCESS_REQUEST INNER JOIN PROCESS_REQUEST_PARAMETER ON PROCESS_REQUEST.PROCESS_REQUEST_ID = PROCESS_REQUEST_PARAMETER.PROCESS_REQUEST_ID WHERE PROCESS_REQUEST.PROCESS_REQUEST_ID = (:processRequestId)")
  List<JdbcProcessRequestJoined> getJoinedProcessRequestsByProcessRequestId(@Param("processRequestId") String processRequestId);

  @Query("SELECT process_request_id,  process_type_id, group_id, requested_user_id, created_time, assigned_user_id, process_instance_id, assigned_time, process_request_state, null as parameter_name, null as parameter_value, null as parameter_type FROM PROCESS_REQUEST WHERE PROCESS_REQUEST.PROCESS_REQUEST_STATE != 'DELETED'\n"
      + "UNION\n"
      + "SELECT process_request_id, null, null, null, null, null, null, null, null,  parameter_name, parameter_value, parameter_type FROM PROCESS_REQUEST_PARAMETER WHERE process_request_id in (SELECT process_request_id FROM process_request WHERE process_request_state != 'DELETED')")
  List<JdbcProcessRequestJoined> getAllProcessRequests();

  @Query("SELECT pr.process_request_id, pr.process_type_id, pr.group_id, pr.requested_user_id, pr.created_time, pr.assigned_user_id, pr.process_instance_id, pr.assigned_time, pr.process_request_state, prp.parameter_name, prp.parameter_value, prp.parameter_type "
      + "FROM PROCESS_REQUEST pr INNER JOIN PROCESS_REQUEST_PARAMETER prp ON pr.PROCESS_REQUEST_ID = prp.PROCESS_REQUEST_ID WHERE (pr.CREATED_TIME >= :firstTime AND  pr.CREATED_TIME < :secondTime + 1)  AND pr.PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequestJoined> getAllProcessRequestsByDate(@Param(value = "firstTime") Date firstTime, @Param(value = "secondTime") Date secondTime);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE PROCESS_TYPE_ID = (:processTypeId) AND PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequest> getJdbcProcessRequestsByProcessTypeId(@Param(value = "processTypeId") String processTypeId);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE GROUP_ID = (:groupId) AND PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequest> getJdbcProcessRequestsByGroupId(@Param(value = "groupId") String groupId);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE GROUP_ID = (:groupId) AND PROCESS_REQUEST_STATE != 'DELETED' AND (PROCESS_REQUEST.CREATED_TIME >= :firstTime AND PROCESS_REQUEST.CREATED_TIME < :secondTime + 1)")
  List<JdbcProcessRequest> getJdbcProcessRequestsByGroupIdAndDate(@Param(value = "groupId") String groupId, @Param(value = "firstTime") Date firstTime, @Param(value = "secondTime") Date secondTime);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE GROUP_ID = (:groupId) AND  PROCESS_REQUEST_STATE !='DELETED' AND PROCESS_TYPE_ID LIKE :processType")
  List<JdbcProcessRequest> getJdbcProcessRequestByGroupIdAndProcessType(@Param(value = "groupId") String groupId,
      @Param(value = "processType") String processType);

  @Modifying
  @Query(value = "UPDATE PROCESS_REQUEST SET ASSIGNED_USER_ID = :updatedAssignedUserId, ASSIGNED_TIME = :assignedTime WHERE PROCESS_REQUEST_ID = :processRequestId")
  int updateAssignedUserId(@Param("processRequestId") String processRequestId, @Param("updatedAssignedUserId") String updatedAssignedUserId,
      @Param("assignedTime") LocalDateTime assignedTime);

  @Modifying
  @Query(value = "UPDATE PROCESS_REQUEST SET PROCESS_REQUEST_STATE = :updatedState WHERE PROCESS_REQUEST_ID = :processRequestId")
  int updateState(@Param("processRequestId") String processRequestId, @Param("updatedState") String updatedState);

  @Modifying
  @Query(value = "UPDATE PROCESS_REQUEST SET GROUP_ID = :groupId WHERE PROCESS_REQUEST_ID = :processRequestId")
  int updateGroup(@Param("processRequestId") String processRequestId, @Param("groupId") String groupId);

  @Modifying
  @Query(value = "UPDATE PROCESS_REQUEST SET PROCESS_INSTANCE_ID = :processInstanceId WHERE PROCESS_REQUEST_ID = :processRequestId")
  int updateProcessInstanceId(@Param("processRequestId") String processRequestId, @Param("processInstanceId") String processInstanceId);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE PROCESS_INSTANCE_ID = (:processInstanceId)")
  JdbcProcessRequest getJdbcProcessRequestByProcessInstanceId(@Param(value = "processInstanceId") String processInstanceId);

  @Query(
      "SELECT * FROM PROCESS_REQUEST INNER JOIN PROCESS_REQUEST_PARAMETER ON PROCESS_REQUEST.PROCESS_REQUEST_ID = PROCESS_REQUEST_PARAMETER.PROCESS_REQUEST_ID "
          + "WHERE (PROCESS_REQUEST.ASSIGNED_USER_ID IS NULL) AND PROCESS_REQUEST_PARAMETER.PARAMETER_NAME = "
          + "'channel' AND PROCESS_REQUEST_PARAMETER.PARAMETER_VALUE = :channel AND PROCESS_REQUEST.PROCESS_REQUEST_STATE != 'DELETED'")
  List<JdbcProcessRequestJoined> getUnassignedProcessRequestsByChannel(@Param(value = "channel") String channel);

  @Query(
      "SELECT * FROM PROCESS_REQUEST INNER JOIN PROCESS_REQUEST_PARAMETER ON PROCESS_REQUEST.PROCESS_REQUEST_ID = PROCESS_REQUEST_PARAMETER.PROCESS_REQUEST_ID "
          + "WHERE (PROCESS_REQUEST.ASSIGNED_USER_ID IS NULL) AND PROCESS_REQUEST_PARAMETER.PARAMETER_NAME = "
          + "'channel' AND PROCESS_REQUEST_PARAMETER.PARAMETER_VALUE = :channel AND PROCESS_REQUEST.PROCESS_REQUEST_STATE != 'DELETED' "
          + "AND (PROCESS_REQUEST.CREATED_TIME >= :firstTime AND PROCESS_REQUEST.CREATED_TIME < :secondTime + 1)")
  List<JdbcProcessRequestJoined> getUnassignedProcessRequestsByChannel(@Param(value = "channel") String channel, @Param(value = "firstTime") Date firstTime, @Param(value = "secondTime") Date secondTime );

  @Modifying
  @Query(value = "DELETE FROM PROCESS_REQUEST where PROCESS_REQUEST_ID = :processRequestId")
  int deleteByProcessRequestId(@Param("processRequestId") String processRequestId);

  @Query(value = "SELECT * FROM PROCESS_REQUEST REQUEST "
      + "INNER JOIN PROCESS_REQUEST_PARAMETER CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "WHERE (REQUEST.CREATED_TIME BETWEEN :starDate AND :endDate) AND REQUEST.PROCESS_TYPE_ID = :processType "
      + "AND (CIF.PARAMETER_NAME = 'cifNumber' AND CIF.PARAMETER_VALUE = :cifNumber) ")
  List<JdbcProcessRequestJoined> getProcessRequestsOnlineDirect(@Param(value = "cifNumber") String cifNumber,
      @Param(value = "starDate") LocalDateTime starDate,
      @Param(value = "endDate") LocalDateTime endDate, @Param(value = "processType") String processType);

  @Query(value = "SELECT * FROM PROCESS_REQUEST REQUEST "
      + "INNER JOIN (SELECT * FROM "
      + "(SELECT * FROM PROCESS_REQUEST_PARAMETER where PROCESS_REQUEST_ID in "
      + "(SELECT PROCESS_REQUEST_ID FROM PROCESS_REQUEST_PARAMETER "
      + "where PARAMETER_NAME = 'cifNumber' AND PARAMETER_VALUE = :cifNumber)) "
      + "where PARAMETER_NAME = 'productCategory' AND PARAMETER_VALUE = :productCategory) CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "WHERE (REQUEST.CREATED_TIME BETWEEN :starDate AND :endDate) AND REQUEST.PROCESS_TYPE_ID = :processType ")
  List<JdbcProcessRequestJoined> getProcessRequestsOnlineLeasing(@Param(value = "cifNumber") String cifNumber, @Param(value = "starDate") LocalDateTime starDate,
      @Param(value = "endDate") LocalDateTime endDate, @Param(value = "processType") String processType, @Param(value = "productCategory") String productCategory);

  @Query(value = "SELECT * FROM PROCESS_REQUEST REQUEST "
      + "INNER JOIN PROCESS_REQUEST_PARAMETER CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "INNER JOIN PROCESS_REQUEST_PARAMETER PRODUCT "
      + "ON REQUEST.PROCESS_REQUEST_ID = PRODUCT.PROCESS_REQUEST_ID "
      + "WHERE (REQUEST.CREATED_TIME BETWEEN :starDate AND :endDate) "
      + "AND (CIF.PARAMETER_NAME = 'cifNumber' AND CIF.PARAMETER_VALUE = :cifNumber) "
      + "AND (PRODUCT.PARAMETER_NAME = 'loanProduct' AND PRODUCT.PARAMETER_VALUE = :product) "
      + "AND (REQUEST.PROCESS_REQUEST_STATE = :state)")
  List<JdbcProcessRequestJoined> getProcessRequestsOnlineDirectByState(@Param(value = "cifNumber") String cifNumber, @Param(value = "starDate") LocalDateTime starDate,
      @Param(value = "endDate") LocalDateTime endDate, @Param(value = "product") String product, @Param(value = "product") String state);

  @Modifying
  @Query(value = "UPDATE (SELECT PROCESS_REQUEST_STATE FROM (SELECT * FROM PROCESS_REQUEST "
      + "WHERE (PROCESS_REQUEST_STATE != 'DELETED' AND PROCESS_REQUEST_STATE != 'COMPLETED' "
      + "AND PROCESS_REQUEST_STATE != 'DISBURSED' AND PROCESS_REQUEST_STATE != 'ORG_REJECTED'"
      + "AND PROCESS_REQUEST_STATE != 'REJECTED' AND PROCESS_REQUEST_STATE != 'SCORING_REJECTED')"
      + "AND CREATED_TIME NOT BETWEEN :startDate AND :endDate) REQUEST "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'cifNumber' AND PARAMETER_VALUE = :cifNumber) CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'processTypeId' AND PARAMETER_VALUE = :processType) PROCESS_TYPE "
      + "ON REQUEST.PROCESS_REQUEST_ID = PROCESS_TYPE.PROCESS_REQUEST_ID) REQUEST "
      + "SET REQUEST.PROCESS_REQUEST_STATE = :state ")
  int updateProcessState(@Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "processType") String processType, @Param(value = "state") String state);

  @Modifying
  @Query(value = "UPDATE (SELECT PROCESS_REQUEST_STATE FROM (SELECT * FROM PROCESS_REQUEST "
      + "WHERE (PROCESS_REQUEST_STATE != 'DELETED' AND PROCESS_REQUEST_STATE != 'COMPLETED' "
      + "AND PROCESS_REQUEST_STATE != 'DISBURSED' AND PROCESS_REQUEST_STATE != 'REJECTED')"
      + "AND CREATED_TIME NOT BETWEEN :startDate AND :endDate) REQUEST "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'cifNumber' AND PARAMETER_VALUE = :cifNumber) CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'processTypeId' AND PARAMETER_VALUE = :processType) PROCESS_TYPE "
      + "ON REQUEST.PROCESS_REQUEST_ID = PROCESS_TYPE.PROCESS_REQUEST_ID) REQUEST "
      + "SET REQUEST.PROCESS_REQUEST_STATE = :state ")
  int updateOnlineLeasingProcessState(@Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "processType") String processType, @Param(value = "state") String state);

  @Modifying
  @Query(value = "UPDATE (SELECT PROCESS_REQUEST_STATE FROM (SELECT * FROM PROCESS_REQUEST "
      + "WHERE (PROCESS_REQUEST_STATE != 'DELETED' AND PROCESS_REQUEST_STATE != 'COMPLETED' "
      + "AND PROCESS_REQUEST_STATE != 'DISBURSED' AND PROCESS_REQUEST_STATE != 'DISBURSE_FAILED'"
      + "AND PROCESS_REQUEST_STATE != 'TRANSACTION_FAILED' AND PROCESS_REQUEST_STATE != 'AMOUNT_BLOCKED_FAILED'"
      + "AND PROCESS_REQUEST_STATE != 'PROCESSING' AND PROCESS_REQUEST_STATE != 'FILE_UPLOAD_FAILED'"
      + "AND PROCESS_REQUEST_STATE != 'LOAN_ACCOUNT_FAILED' AND PROCESS_REQUEST_STATE != 'AMOUNT_BLOCKED')"
      + "AND CREATED_TIME NOT BETWEEN :startDate AND :endDate) REQUEST "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'cifNumber' AND PARAMETER_VALUE = :cifNumber) CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'processTypeId' AND PARAMETER_VALUE = :processType) PROCESS_TYPE "
      + "ON REQUEST.PROCESS_REQUEST_ID = PROCESS_TYPE.PROCESS_REQUEST_ID) REQUEST "
      + "SET REQUEST.PROCESS_REQUEST_STATE = :state ")
  int updateBnplProcessState(@Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "processType") String processType, @Param(value = "state") String state);

  @Modifying
  @Query(value = "UPDATE (SELECT PROCESS_REQUEST_STATE FROM (SELECT * FROM PROCESS_REQUEST "
      + "WHERE (PROCESS_REQUEST_STATE in ('SYSTEM_FAILED','ORG_REJECTED','AMOUNT_REJECTED','REJECTED') "
      + "AND CREATED_TIME NOT BETWEEN :startDate AND :endDate)) REQUEST "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'cifNumber' AND PARAMETER_VALUE = :cifNumber) CIF "
      + "ON REQUEST.PROCESS_REQUEST_ID = CIF.PROCESS_REQUEST_ID "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'processTypeId' AND PARAMETER_VALUE = :processType) PROCESS_TYPE "
      + "ON REQUEST.PROCESS_REQUEST_ID = PROCESS_TYPE.PROCESS_REQUEST_ID) REQUEST "
      + "SET REQUEST.PROCESS_REQUEST_STATE = :state ")
  int update24hPassedRequestStateExcludingConfirmed(@Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "processType") String processType, @Param(value = "state") String state);

  @Modifying
  @Query(value = "UPDATE (SELECT PROCESS_REQUEST_STATE FROM (SELECT * FROM PROCESS_REQUEST "
      + "WHERE (PROCESS_TYPE_ID = :processType "
      + "AND PROCESS_REQUEST_STATE = 'ORG_REJECTED' OR PROCESS_REQUEST_STATE = 'SCORING_REJECTED' OR PROCESS_REQUEST_STATE = 'REJECTED')"
      + "AND CREATED_TIME NOT BETWEEN :startDate AND :endDate) REQUEST "
      + "INNER JOIN (SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PARAMETER_NAME = 'loanProduct' AND PARAMETER_VALUE = :product) PRODUCT "
      + "ON REQUEST.PROCESS_REQUEST_ID = PRODUCT.PROCESS_REQUEST_ID) REQUEST "
      + "SET REQUEST.PROCESS_REQUEST_STATE = :state ")
  int updateProcessState14days(@Param(value = "processType") String processType,  @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "product") String product, @Param(value = "state") String state);


  @Query(value = "SELECT * FROM PROCESS_REQUEST "
      + " WHERE PROCESS_REQUEST_ID IN "
      + " (SELECT P1.PROCESS_REQUEST_ID from PROCESS_REQUEST_PARAMETER p1 "
      + " INNER JOIN process_request_parameter p2 "
      + " ON P1.PROCESS_REQUEST_ID = P2.PROCESS_REQUEST_ID "
      + " WHERE (P1.PARAMETER_NAME = 'loanProduct' AND P1.PARAMETER_VALUE = 'EB71') AND (P2.PARAMETER_NAME = 'processTypeId' AND P2.PARAMETER_VALUE = 'onlineSalary')) "
      + " AND PROCESS_TYPE_ID = :processType "
      + " AND CREATED_TIME NOT BETWEEN :startDate AND :endDate "
      + " AND (PROCESS_REQUEST_STATE = 'ORG_REJECTED' OR PROCESS_REQUEST_STATE = 'SCORING_REJECTED' "
      + " OR PROCESS_REQUEST_STATE = 'REJECTED' OR PROCESS_REQUEST_STATE = 'DELETED')")
  List<JdbcProcessRequest> get14daysPassedRequests(@Param(value = "processType") String processType, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate,
      @Param(value = "processTypeId") String processTypeId);

  @Query(value =  " SELECT * FROM PROCESS_REQUEST WHERE PROCESS_REQUEST_ID IN (SELECT P1.PROCESS_REQUEST_ID from PROCESS_REQUEST_PARAMETER p1 "
      + " INNER JOIN process_request_parameter p2  ON P1.PROCESS_REQUEST_ID = P2.PROCESS_REQUEST_ID WHERE (P1.PARAMETER_NAME = 'cifNumber' "
      + " AND P1.PARAMETER_VALUE = :cifNumber) "
      + " AND PROCESS_TYPE_ID = :processType AND (PROCESS_REQUEST_STATE != 'DELETED' AND PROCESS_REQUEST_STATE != 'COMPLETED' "
      + " AND PROCESS_REQUEST_STATE != 'DISBURSED' AND PROCESS_REQUEST_STATE != 'ORG_REJECTED' "
      + " AND PROCESS_REQUEST_STATE != 'REJECTED' AND PROCESS_REQUEST_STATE != 'SCORING_REJECTED') "
      + " AND CREATED_TIME NOT BETWEEN :startDate AND :endDate)")
  List<JdbcProcessRequest> getGivenTimePassedRequests(@Param(value = "processType") String processType, @Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate);

  @Query(value =  " SELECT * FROM PROCESS_REQUEST WHERE PROCESS_REQUEST_ID IN (SELECT P1.PROCESS_REQUEST_ID from PROCESS_REQUEST_PARAMETER p1 "
      + " INNER JOIN process_request_parameter p2  ON P1.PROCESS_REQUEST_ID = P2.PROCESS_REQUEST_ID WHERE (P1.PARAMETER_NAME = 'cifNumber' "
      + " AND P1.PARAMETER_VALUE = :cifNumber) "
      + " AND PROCESS_TYPE_ID = :processType AND (PROCESS_REQUEST_STATE != 'DELETED' AND PROCESS_REQUEST_STATE != 'COMPLETED' AND PROCESS_REQUEST_STATE != 'DISBURSE_FAILED'"
      + " AND PROCESS_REQUEST_STATE != 'DISBURSED' AND PROCESS_REQUEST_STATE != 'TRANSACTION_FAILED' AND PROCESS_REQUEST_STATE != 'AMOUNT_BLOCKED_FAILED' AND PROCESS_REQUEST_STATE != 'PROCESSING'"
      + " AND PROCESS_REQUEST_STATE != 'FILE_UPLOAD_FAILED' AND PROCESS_REQUEST_STATE != 'LOAN_ACCOUNT_FAILED' AND PROCESS_REQUEST_STATE != 'AMOUNT_BLOCKED') "
      + " AND CREATED_TIME NOT BETWEEN :startDate AND :endDate)")
  List<JdbcProcessRequest> getBnplGivenTimePassedRequests(@Param(value = "processType") String processType, @Param(value = "cifNumber") String cifNumber, @Param(value = "startDate") LocalDateTime startDate, @Param(value = "endDate") LocalDateTime endDate);

  @Query(value = "SELECT * FROM PROCESS_REQUEST_PARAMETER WHERE PROCESS_REQUEST_ID = :processRequestId" )
  List<JdbcProcessRequestParameter> getProcessParameters(@Param(value = "processRequestId") String requestId);

  @Query(value = "SELECT * FROM PROCESS_REQUEST WHERE PROCESS_REQUEST_ID IN (SELECT P1.PROCESS_REQUEST_ID from PROCESS_REQUEST_PARAMETER p1 "
      + " INNER JOIN process_request_parameter p2 ON P1.PROCESS_REQUEST_ID = P2.PROCESS_REQUEST_ID WHERE (P1.PARAMETER_NAME = 'cifNumber' "
      + " AND P1.PARAMETER_VALUE = :cifNumber))"
      + " AND PROCESS_TYPE_ID = :processType AND (PROCESS_REQUEST_STATE = 'DISBURSED' OR PROCESS_REQUEST_STATE = 'COMPLETED')")
  List<JdbcProcessRequest> getInstantLoanRequestsByCifNumber(@Param(value = "cifNumber") String cifNumber, @Param(value = "processType") String processType);
}
