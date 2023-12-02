package mn.erin.bpm.repository.jdbc.model;

import java.sql.Clob;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Temuulen Naranbold
 */
@Table("LOAN_CONTRACT_PARAMETER")
public class JdbcLoanContractParameter
{
  @Id
  String processInstanceId;
  String defKey;
  Clob parameterValue;
  String parameterValueType;
  String parameterEntityType;

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getDefKey()
  {
    return defKey;
  }

  public void setDefKey(String defKey)
  {
    this.defKey = defKey;
  }

  public Clob getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Clob parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getParameterValueType()
  {
    return parameterValueType;
  }

  public void setParameterValueType(String parameterValueType)
  {
    this.parameterValueType = parameterValueType;
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
