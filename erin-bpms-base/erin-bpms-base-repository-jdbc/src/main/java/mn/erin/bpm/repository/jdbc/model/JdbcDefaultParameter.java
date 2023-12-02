package mn.erin.bpm.repository.jdbc.model;


import java.sql.Clob;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Odgavaa
 **/

@Table("Direct online default parameters")
public class JdbcDefaultParameter
{
  @Id
  private String processType;
  private String entity;
  private String parameterName;
  private Clob parameterValue;
  private String parameterDataType;

  public JdbcDefaultParameter(String processType, String entity, String parameterName, Clob parameterValue, String parameterDataType)
  {
    this.processType = processType;
    this.entity = entity;
    this.parameterName = parameterName;
    this.parameterValue = parameterValue;
    this.parameterDataType = parameterDataType;
  }

  public String getProcessType()
  {
    return processType;
  }

  public void setProcessType(String processType)
  {
    this.processType = processType;
  }

  public String getEntity() { return entity; }

  public void setEntity(String entity)
  {
    this.entity = entity;
  }

  public String getParameterName()
  {
    return parameterName;
  }

  public void setParameterName(String parameterName)
  {
    this.parameterName = parameterName;
  }

  public String getParameterDataType() { return parameterDataType; }

  public void setParameterDataType(String parameterDataType) { this.parameterDataType = parameterDataType; }

  public Clob getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Clob parameterValue)
  {
    this.parameterValue = parameterValue;
  }
}
