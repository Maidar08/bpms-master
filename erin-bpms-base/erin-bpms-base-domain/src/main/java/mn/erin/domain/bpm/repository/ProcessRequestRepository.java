/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;

/**
 * Responsible for persisting {@link ProcessRequest} entities.
 *
 * @author EBazarragchaa
 */
public interface ProcessRequestRepository extends Repository<ProcessRequest>
{
  /**
   * Creates new instance
   *
   * @param processTypeId the process type id, not null
   * @param groupId       the user group id, not null
   * @param userId        the user id
   * @param parameters    the request parameters, can be null
   * @return the new instance or null
   * @throws BpmRepositoryException when there is a SQL insertion error
   */
  ProcessRequest createProcessRequest(ProcessTypeId processTypeId, GroupId groupId, String userId, Map<String, Serializable> parameters)
    throws BpmRepositoryException;

  /**
   * Delete process
   *
   * @param processRequestId the process request id, not null
   * @throws BpmRepositoryException when there is a SQL error
   */
  void   deleteProcessRequest(ProcessRequestId processRequestId);

  /**
   * Returns all existing process requests assigned to a user (Branch specialist, HR specialist, etc.)
   *
   * @param userId assigned user id that will be used to search process requests.
   * @return collection of entities or empty collection
   */
  Collection<ProcessRequest> findAllByAssignedUserId(UserId userId) throws BpmRepositoryException;

  /**
   * Returns all existing process requests by customer number
   *
   * @param searchKey search key that will be used to search process requests.
   * @return collection of entities or empty collection
   */
  Collection<ProcessRequest> findAllBySearchKey(String searchKey) throws BpmRepositoryException;

  /**
   * Returns all existing process requests to a given user group (Branch number, Department number etc.)
   *
   * @return collection of entities or empty collection
   */
  Collection<ProcessRequest> findAllByUserGroupId(GroupId groupId) throws BpmRepositoryException;

  Collection<ProcessRequest> findAllByUserGroupIdDate(GroupId groupId, Date startDate, Date endDate) throws BpmRepositoryException;

  /**
   * Returns all existing process requests to a given user group (Branch number, Department number etc.) and process type (loan, organization)
   *
   * @return collection of entities or empty collection
   */
  Collection<ProcessRequest> findAllByGroupIdAndProcessType(GroupId groupId, String processTypeId) throws BpmRepositoryException;

  /**
   * Returns process request with matching process instance id.
   *
   * @param processInstanceId
   * @return ProcessRequest if it exists, null if it doesn't exist.
   */
  ProcessRequest getByProcessInstanceId(String processInstanceId) throws BpmRepositoryException;

  /**
   * Returns all existing process requests to a given process type (Consumer Loan, Business Loan etc)
   *
   * @return collection of entities or empty collection
   */
  Collection<ProcessRequest> findAllByProcessType(String processType, TenantId tenantId) throws BpmRepositoryException;

  /**
   * Updates process request parameters. If parameter name exists, then updates. If it doesn't exist, then adds new parameter.
   *
   * @param processRequestId id of process request parameters that needs updating
   * @param parameters map of parameter name and value
   * @return true if updated, false if not updated.
   * @throws BpmRepositoryException when inputs are null or there is a SQL update error
   */
  boolean updateParameters(String processRequestId, Map<String, Serializable> parameters) throws BpmRepositoryException;

  /**
   * Updates process request status.
   *
   * @param processRequestId id of the process request
   * @param userId id of the updated user
   * @return returns true if updated.
   * @throws BpmRepositoryException when there is a SQL update error.
   */
  boolean updateAssignedUser(String processRequestId, UserId userId) throws BpmRepositoryException;

  /**
   * Updates process request status.
   *
   * @param processRequestState request state.
   * @return returns true if updated.
   * @throws BpmRepositoryException when there is a SQL update error.
   */
  boolean updateState(String processRequestId, ProcessRequestState processRequestState) throws BpmRepositoryException;

  /**
   * Updates process request with process instance id.
   *
   * @param processInstanceId the process instance id
   * @return returns true if updated.
   * @throws BpmRepositoryException when there is a SQL update error.
   */
  boolean updateProcessInstanceId(String processRequestId, String processInstanceId) throws BpmRepositoryException;

  int updateGroup(String processRequestId, String groupId);

  /**
   * Finds all process request by created date interval with parameter.
   *
   * @param parameterValue   can be parameter value.
   * @param startCreatedDate created date start.
   * @param endCreatedDate   created date end.
   * @return found process requests.
   */
  Collection<ProcessRequest> findAllByCreatedDateInterval(String parameterValue, Date startCreatedDate, Date endCreatedDate) throws BpmRepositoryException;

  /**
   * Finds all process requests that are unassigned and by channelType parameter.
   *
   * @param channelType channel of process request parameter
   * @return found process requests.
   */
  Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType) throws BpmRepositoryException;

  /**
   * Finds all process requests that are unassigned and by channelType parameter.
   *
   * @param channelType channel of process request parameter,
   * @param startDate
   * @param endDate
   * @return found process requests.
   */
  Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType, Date startDate, Date endDate) throws BpmRepositoryException;

  /**
   * Finds all requests
   *
   * @return found process requests.
   */
  Collection<ProcessRequest> findAllRequests();

  Collection<ProcessRequest> findAllRequestsByDate(Date start, Date end);

  Collection<ProcessRequest> getRequestsByAssignedUserId(String userId, Date startDate, Date endDate);

  /**
   * Updates all requests state to DELETED which are passed 14 days, filtering with  processType product and channel
   * @param processTypeId process type
   * @param startDate Start date of the request
   * @param endDate End date of the request
   * @param channel Channel of where request created
   * @param state Changing state of request
   * @return Returns the updated requests count
   * @throws BpmRepositoryException If SQL throws error
   */
  int update14daysPassedProcessState(String processTypeId,LocalDateTime startDate, LocalDateTime endDate, String channel, String state)
      throws BpmRepositoryException;



  /**
   * Deletes all requests state to DELETED which are passed 14 days, filtering with  processType product and channel
   * @param processType process type
   * @param startDate Start date of the request
   * @param endDate End date of the request
   * @param processTypeId processTypeId
   * @return null
   */
  Collection<ProcessRequest> get14daysPassedRequestsAndDelete(String processType, LocalDateTime startDate, LocalDateTime endDate,
      String processTypeId);



  /**
   * Updates all requests state to DELETED which are passed 24 hour, filtering with product and channel
   *
   * @param cifNumber Customer number
   * @param startDate Start date of the request
   * @param endDate End date of the request
   * @param state Changing state of request
   * @throws BpmRepositoryException If SQL throws error
   * @return Returns the updated requests count
   */
  int update24hPassedProcessState(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
      throws BpmRepositoryException;

  /**
   * Updates SYSTEM_FAILED, ORG_REJECTED, AMOUNT_REJECTED, REJECTED requests state to DELETED which are passed 24 hour, filtering with product and channel
   *
   * @param cifNumber Customer number
   * @param startDate Start date of the request
   * @param endDate End date of the request
   * @param state Changing state of request
   * @throws BpmRepositoryException If SQL throws error
   * @return Returns the updated requests count
   */
  int update24hPassedRequestStateExcludingConfirmed(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
      throws BpmRepositoryException;

  /**
   * Get process parameter value by name
   *
   * @param requestId id of process request
   * @param parameterName name of the parameter
   * @return object value
   */
  String getParameterByName(String requestId, String parameterName);
  /**
   *  Get process requests that have passed given time input.
   *
   * @param processType process type id
   * @param cifNumber customer cif number
   * @param startDate given start date input
   * @param endDate given end date input
   * @return process requests that have passed given time input.
   */
  Collection<ProcessRequest> getDirectOnlineProcessRequests(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType) throws BpmRepositoryException;

  /**
   *  Get Online Leasing process requests that have passed given time input.
   *
   * @param processType process type id
   * @param cifNumber customer cif number
   * @param startDate given start date input
   * @param endDate given end date input
   * @param productCategory given product category input
   * @return Online Leasing type process requests that have passed given time input.
   */
  Collection<ProcessRequest> getProcessRequestsOnlineLeasing(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String productCategory) throws BpmRepositoryException;

  /**
   * 
   * @param processType process type id
   * @param cifNumber customer cif  number
   * @param startDate process started date
   * @param endDate process end date
   * @return
   */
  Collection<ProcessRequest> getGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate);

  /**
   *  Get BNPL process requests that have passed given time input.
   *
   * @param processType process type id
   * @param cifNumber customer cif number
   * @param startDate given start date input
   * @param endDate given end date input
   * @return BNPL type process requests that have passed given time input.
   */
  Collection<ProcessRequest> getBnplGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate);

  /**
   *
   * @param requestId
   * @return parameters
   */
  Map<String, Serializable> getRequestParametersByRequestId(String requestId);

  Collection<ProcessRequest> getInstantLoanProcessRequestsByCifNumber(String cifNumber);
}

