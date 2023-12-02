package mn.erin.bpm.repository.jdbc.model;

import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public class JdbcProcessJoinedForLarge
{
  @Id
  String processInstanceId;
  LocalDateTime startedDate;
  LocalDateTime finishedDate;
  String parameterName;
  Clob parameterValue;
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

  public Clob getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Clob parameterValue)
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

  public String getParameterValueAsString() throws SQLException
  {
    return parameterValue.getSubString(1, (int) parameterValue.length());
  }
}
