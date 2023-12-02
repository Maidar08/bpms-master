package mn.erin.domain.bpm.model.contract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;

/**
 * @author Temuulen Naranbold
 */
public class LoanContractParameter implements Entity<LoanContractParameter>, Serializable
{
  private ProcessInstanceId instanceId;
  private String defKey;
  private Map<String, Object> parameterValue;
  private String parameterValueType;
  private ParameterEntityType parameterEntityType;
  private Map<String, Object> tableData = new HashMap<>();

  public LoanContractParameter(ProcessInstanceId instanceId, String defKey, Map<String, Object> parameterValue, String parameterValueType,
      ParameterEntityType parameterEntityType)
  {
    this.instanceId = instanceId;
    this.defKey = defKey;
    this.parameterValue = parameterValue;
    this.parameterValueType = parameterValueType;
    this.parameterEntityType = parameterEntityType;
  }

  public ProcessInstanceId getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(ProcessInstanceId instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getDefKey()
  {
    return defKey;
  }

  public void setDefKey(String defKey)
  {
    this.defKey = defKey;
  }

  public Map<String, Object> getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Map<String, Object> parameterValue)
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

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }

  public void setParameterEntityType(ParameterEntityType parameterEntityType)
  {
    this.parameterEntityType = parameterEntityType;
  }

  public Map<String, Object> getTableData()
  {
    return tableData;
  }

  public void setTableData(Map<String, Object> tableData)
  {
    this.tableData = tableData;
  }

  @Override
  public boolean sameIdentityAs(LoanContractParameter other)
  {
    return other != null && other.instanceId.equals(this.instanceId);
  }
}
