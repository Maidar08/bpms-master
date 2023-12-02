package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

/**
 * @author Zorig
 */
public class JdbcProcessRequestJoined
{
  @Id
  String processRequestId;
  String processTypeId;
  String groupId;
  String requestedUserId;
  LocalDateTime createdTime;
  String assignedUserId;
  String processInstanceId;
  LocalDateTime assignedTime;
  String processRequestState;

  String parameterName;
  String parameterValue;
  String parameterType;

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getRequestedUserId()
  {
    return requestedUserId;
  }

  public void setRequestedUserId(String requestedUserId)
  {
    this.requestedUserId = requestedUserId;
  }

  public LocalDateTime getCreatedTime()
  {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime)
  {
    this.createdTime = createdTime;
  }

  public String getAssignedUserId()
  {
    return assignedUserId;
  }

  public void setAssignedUserId(String assignedUserId)
  {
    this.assignedUserId = assignedUserId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public LocalDateTime getAssignedTime()
  {
    return assignedTime;
  }

  public void setAssignedTime(LocalDateTime assignedTime)
  {
    this.assignedTime = assignedTime;
  }

  public String getProcessRequestState()
  {
    return processRequestState;
  }

  public void setProcessRequestState(String processRequestState)
  {
    this.processRequestState = processRequestState;
  }

  public String getParameterName()
  {
    return parameterName;
  }

  public void setParameterName(String parameterName)
  {
    this.parameterName = parameterName;
  }

  public String getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getParameterType()
  {
    return parameterType;
  }

  public void setParameterType(String parameterType)
  {
    this.parameterType = parameterType;
  }
}
