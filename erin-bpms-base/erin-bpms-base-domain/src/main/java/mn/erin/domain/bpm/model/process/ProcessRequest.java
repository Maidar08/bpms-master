/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.process;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.model.Entity;

/**
 * Represents a request to start new process with a given process type.
 *
 * @author EBazarragchaa
 */
public class ProcessRequest implements Entity<ProcessRequest>
{
  private final ProcessRequestId id;
  private final ProcessTypeId processTypeId;
  private final GroupId groupId;
  private final String requestedUserId;
  private final LocalDateTime createdTime;
  private UserId assignedUserId;
  private String processInstanceId;
  private LocalDateTime assignedTime;
  private ProcessRequestState state;
  private Map<String, Serializable> parameters;

  public ProcessRequest(ProcessRequestId id, ProcessTypeId processTypeId, GroupId groupId, String requestedUserId, LocalDateTime createdTime,
      ProcessRequestState state, Map<String, Serializable> parameters)
  {
    this.id = Objects.requireNonNull(id, "Process request id is required!");
    this.processTypeId = Objects.requireNonNull(processTypeId, "Process type is required!");
    this.groupId = Objects.requireNonNull(groupId, "Process user group id required!");
    this.requestedUserId = Objects.requireNonNull(requestedUserId, "Requested user id is required!");
    this.createdTime = Objects.requireNonNull(createdTime, "Created time is required!");
    this.state = Objects.requireNonNull(state, "State of process request is required!");
    this.parameters = new TreeMap<>(parameters);
  }

  public ProcessRequest(ProcessRequestId id, ProcessTypeId processTypeId, GroupId groupId, String requestedUserId, LocalDateTime createdTime,
      UserId assignedUserId, String processInstanceId, LocalDateTime assignedTime, ProcessRequestState state)
  {
    this.id = id;
    this.processTypeId = processTypeId;
    this.groupId = groupId;
    this.requestedUserId = requestedUserId;
    this.createdTime = createdTime;
    this.assignedUserId = assignedUserId;
    this.processInstanceId = processInstanceId;
    this.assignedTime = assignedTime;
    this.state = state;
  }

  public ProcessRequest(ProcessRequestId id, ProcessTypeId processTypeId, GroupId groupId, String requestedUserId, LocalDateTime createdTime)
  {
    this.id = id;
    this.processTypeId = processTypeId;
    this.groupId = groupId;
    this.requestedUserId =  requestedUserId;
    this.createdTime = createdTime;
  }

  public Map<String, Serializable> getParameters()
  {
    return Collections.unmodifiableMap(parameters);
  }

  public void addParameterValue(String name, Serializable value)
  {
    parameters.put(name, value);
  }

  public Serializable getParameterValue(String name)
  {
    return parameters.get(name);
  }

  public void setAssignedUserId(UserId assignedUserId)
  {
    this.assignedUserId = assignedUserId;
  }

  public void setAssignedTime(LocalDateTime assignedTime)
  {
    this.assignedTime = assignedTime;
  }

  public ProcessRequestId getId()
  {
    return id;
  }

  public ProcessRequestState getState()
  {
    return state;
  }

  public void setState(ProcessRequestState state)
  {
    this.state = state;
  }

  public ProcessTypeId getProcessTypeId()
  {
    return processTypeId;
  }

  public GroupId getGroupId()
  {
    return groupId;
  }

  public LocalDateTime getCreatedTime()
  {
    return createdTime;
  }

  public UserId getAssignedUserId()
  {
    return assignedUserId;
  }

  public LocalDateTime getAssignedTime()
  {
    return assignedTime;
  }

  public String getRequestedUserId()
  {
    return requestedUserId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public void addParameterToMap(String key, Serializable value)
  {
    this.parameters.put(key, value);
  }

  @Override
  public boolean sameIdentityAs(ProcessRequest other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
