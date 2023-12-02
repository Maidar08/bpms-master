/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.base.repository.memory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author EBazarragchaa
 */
@Repository
public class InMemoryProcessRequestRepository implements ProcessRequestRepository
{
  private List<ProcessRequest> processRequestList = new ArrayList<>();
  private static final String ASSIGNED_USER_ID_ADMIN = "admin";
  private static final String REQUESTED_USER_ID_ADMIN = "admin";

  public InMemoryProcessRequestRepository()
  {
    this.processRequestList.add(getDefault());
    this.processRequestList.addAll(getMoreProcessRequests());
  }

  @Override
  public ProcessRequest createProcessRequest(ProcessTypeId processTypeId, GroupId groupId, String userId, Map<String, Serializable> parameters)
  {
    String randomId = UUID.randomUUID().toString();
    ProcessRequest processRequest = new ProcessRequest(ProcessRequestId.valueOf(randomId), processTypeId, groupId,
        userId, LocalDateTime.now(), ProcessRequestState.NEW, parameters);
    processRequestList.add(processRequest);
    return processRequest;
  }

  @Override
  public void deleteProcessRequest(ProcessRequestId processRequestId)
  {
    //TODO: nee implementation
  }

  @Override
  public Collection<ProcessRequest> findAllByAssignedUserId(UserId userId) throws BpmRepositoryException
  {
    List<ProcessRequest> processRequestListToReturn = new ArrayList<>();

    for (ProcessRequest processRequest : processRequestList)
    {
      if (processRequest.getAssignedUserId() != null && processRequest.getAssignedUserId().sameValueAs(userId))
      {
        processRequestListToReturn.add(processRequest);
      }
    }
    return processRequestListToReturn;
  }

  @Override
  public Collection<ProcessRequest> findAllBySearchKey(String personNumber) throws BpmRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<ProcessRequest> findAllByUserGroupId(GroupId groupId)
  {
    Collection<ProcessRequest> foundRequests = new ArrayList<>();

    for (ProcessRequest processRequest : processRequestList)
    {
      GroupId existGroupId = processRequest.getGroupId();

      if (groupId.sameValueAs(existGroupId))
      {
        foundRequests.add(processRequest);
      }
    }
    return foundRequests;
  }

  @Override
  public Collection<ProcessRequest> findAllByUserGroupIdDate(GroupId groupId, Date startDate, Date endDate)
      throws BpmRepositoryException
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> findAllByGroupIdAndProcessType(GroupId groupId, String processTypeId) throws BpmRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProcessRequest getByProcessInstanceId(String processInstanceId) throws BpmRepositoryException
  {
    if (processRequestList.isEmpty())
    {
      return getDefault();
    }
    return getDefault();
  }

  @Override
  public Collection<ProcessRequest> findAllByProcessType(String processType, TenantId tenantId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean updateParameters(String processRequestId, Map<String, Serializable> parameters) throws BpmRepositoryException
  {
    return true;
  }

  @Override
  public boolean updateAssignedUser(String processRequestId, UserId userId) throws BpmRepositoryException
  {
    return false;
  }

  @Override
  public boolean updateState(String processRequestId, ProcessRequestState processRequestState)
  {
    for (ProcessRequest processRequest : processRequestList)
    {
      String id = processRequest.getId().getId();

      if (id.equals(processRequestId))
      {
        processRequest.setState(processRequestState);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean updateProcessInstanceId(String processRequestId, String processInstanceId) throws BpmRepositoryException
  {
    return true;
  }

  @Override
  public int updateGroup(String processRequestId, String groupId)
  {
    return 0;
  }

  @Override
  public Collection<ProcessRequest> findAllByCreatedDateInterval(String parameterValue, Date startCreatedDate, Date endCreatedDate)
      throws BpmRepositoryException
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType) throws BpmRepositoryException
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> findAllUnassignedProcessRequestsByChannelType(String channelType, Date startDate, Date endDate)
      throws BpmRepositoryException
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> findAllRequests()
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> findAllRequestsByDate(Date start, Date end)
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> getRequestsByAssignedUserId(String userId, Date startDate, Date endDate)
  {
    return Collections.emptyList();
  }

  @Override
  public int update14daysPassedProcessState(String processTypeId, LocalDateTime startDate, LocalDateTime endDate, String channel, String state)
      throws BpmRepositoryException
  {
    return 0;
  }

  @Override
  public Collection<ProcessRequest> get14daysPassedRequestsAndDelete(String processType, LocalDateTime startDate, LocalDateTime endDate,
      String processTypeId)
  {
    return Collections.emptyList();
  }

  @Override
  public int update24hPassedProcessState(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
      throws BpmRepositoryException
  {
    return 0;
  }

  @Override
  public int update24hPassedRequestStateExcludingConfirmed(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String state)
  {
    return 0;
  }


  @Override
  public String getParameterByName(String requestId, String parameterName)
  {
    return null;
  }

  @Override
  public Collection<ProcessRequest> getDirectOnlineProcessRequests(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType)
      throws BpmRepositoryException
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> getProcessRequestsOnlineLeasing(String cifNumber, LocalDateTime startDate, LocalDateTime endDate, String processType, String productCategory)
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> getGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate)
  {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProcessRequest> getBnplGivenTimePassedProcessRequests(String processType, String cifNumber, LocalDateTime startDate, LocalDateTime endDate)
  {
    return Collections.emptyList();
  }

  @Override
  public Map<String, Serializable> getRequestParametersByRequestId(String requestId)
  {
    return Collections.emptyMap();
  }

  @Override
  public Collection<ProcessRequest> getInstantLoanProcessRequestsByCifNumber(String cifNumber)
  {
    return Collections.emptyList();
  }

  @Override
  public ProcessRequest findById(EntityId entityId)
  {
    for (ProcessRequest processRequest : processRequestList)
    {
      String id = processRequest.getId().getId();

      if (id.equals(entityId.getId()))
      {
        return processRequest;
      }
    }
    return null;
  }

  @Override
  public Collection<ProcessRequest> findAll()
  {
    return processRequestList;
  }

  private ProcessRequest getDefault()
  {
    return this.createProcessRequest("default1", ASSIGNED_USER_ID_ADMIN);
  }

  private List<ProcessRequest> getMoreProcessRequests()
  {
    return Arrays.asList(
        this.createProcessRequest("default2"),
        this.createProcessRequest("default3"),
        this.createProcessRequest("default4"),
        this.createProcessRequest("default5"),
        this.createProcessRequest("default6", ASSIGNED_USER_ID_ADMIN),
        this.createProcessRequest("default7", "Odgavaa")
    );
  }

  private ProcessRequest createProcessRequest(String processRequestId)
  {
    return createProcessRequest(processRequestId, "Assigned User");
  }

  private ProcessRequest createProcessRequest(String processRequestId, String assignedUserId)
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("registerNumber", "ЧП57010310");

    ProcessRequest processRequest = new ProcessRequest(ProcessRequestId.valueOf(processRequestId),
        ProcessTypeId.valueOf("CONSUMPTION_LOAN"), GroupId.valueOf("GR1"), REQUESTED_USER_ID_ADMIN,
        LocalDateTime.now(),
        ProcessRequestState.CONFIRMED,
        parameters);
    processRequest.setAssignedUserId(UserId.valueOf(assignedUserId));
    return processRequest;
  }
}
