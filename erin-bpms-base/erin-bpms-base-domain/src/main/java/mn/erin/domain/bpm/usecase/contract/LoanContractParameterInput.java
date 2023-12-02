package mn.erin.domain.bpm.usecase.contract;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Temuulen Naranbold
 */
public class LoanContractParameterInput
{
  private String processInstanceId;
  private Map<String, Object> parameterValue = new HashMap<>();
  private String parameterEntityType;
  private String defKey;
  private Map<String, Object> tableMap = new HashMap<>();

  public LoanContractParameterInput()
  {
  }

  public LoanContractParameterInput(String processInstanceId, Map<String, Object> parameterValue, String parameterEntityType, String defKey)
  {
    this.processInstanceId = processInstanceId;
    this.parameterValue = parameterValue;
    this.parameterEntityType = parameterEntityType;
    this.defKey = defKey;
  }

  public LoanContractParameterInput(String processInstanceId, String defKey)
  {
    this.processInstanceId = processInstanceId;
    this.defKey = defKey;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public Map<String, Object> getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(Map<String, Object> parameterValue)
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

  public String getDefKey()
  {
    return defKey;
  }

  public void setDefKey(String defKey)
  {
    this.defKey = defKey;
  }

  public Map<String, Object> getTableMap()
  {
    return tableMap;
  }

  public void setTableMap(Map<String, Object> tableMap)
  {
    this.tableMap = tableMap;
  }
}
