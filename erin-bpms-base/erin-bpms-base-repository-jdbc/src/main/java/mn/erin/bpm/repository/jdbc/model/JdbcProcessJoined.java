package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

/**
 * @author Zorig
 */
public class JdbcProcessJoined
{
  @Id
  String processInstanceId;
  LocalDateTime startedDate;
  LocalDateTime finishedDate;
  String parameterName;
  String parameterValue;
  String parameterDataType;
  String parameterEntityType;

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public LocalDateTime getStartedDate()
  {
    return startedDate;
  }

  public void setStartedDate(LocalDateTime startedDate)
  {
    this.startedDate = startedDate;
  }

  public LocalDateTime getFinishedDate()
  {
    return finishedDate;
  }

  public void setFinishedDate(LocalDateTime finishedDate)
  {
    this.finishedDate = finishedDate;
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

  public String getParameterDataType()
  {
    return parameterDataType;
  }

  public void setParameterDataType(String parameterDataType)
  {
    this.parameterDataType = parameterDataType;
  }

  public String getParameterEntityType()
  {
    return parameterEntityType;
  }

  public void setParameterEntityType(String parameterEntityType)
  {
    this.parameterEntityType = parameterEntityType;
  }
}
