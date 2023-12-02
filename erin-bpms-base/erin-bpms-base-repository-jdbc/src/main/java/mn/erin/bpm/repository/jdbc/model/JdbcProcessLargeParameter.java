package mn.erin.bpm.repository.jdbc.model;

import java.sql.Clob;

import org.springframework.data.annotation.Id;

/**
 * @author Lkhagvadorj
 */
public class JdbcProcessLargeParameter
{
  @Id
  String processInstanceId;
  String parameterDataType;
  Clob parameterValue;
  String parameterEntityType;
  String parameterName;

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getParameterDataType()
  {
    return parameterDataType;
  }

  public void setParameterDataType(String parameterDataType)
  {
    this.parameterDataType = parameterDataType;
  }

  public Clob getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Clob parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getParameterEntityType()
  {
    return parameterEntityType;
  }

  public void setParameterEntityType(String parameterEntityType)
  {
    this.parameterEntityType = parameterEntityType;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }
}
