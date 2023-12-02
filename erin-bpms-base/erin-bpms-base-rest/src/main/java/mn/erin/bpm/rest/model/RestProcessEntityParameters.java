package mn.erin.bpm.rest.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Zorig
 */
public class RestProcessEntityParameters implements Serializable
{
  private String parameterEntityType;
  private String defKey;
  private Map<String, Object> parameters;
  private List<RestCompletedFormField> restFormFields;

  public RestProcessEntityParameters(String parameterEntityType, Map<String, Object> parameters)
  {
    this.parameterEntityType = parameterEntityType;
    this.parameters = parameters;
  }

  public RestProcessEntityParameters(String parameterEntityType, Map<String, Object> parameters, List<RestCompletedFormField> restFormFields)
  {
    this.parameterEntityType = parameterEntityType;
    this.parameters = parameters;
    this.restFormFields = restFormFields;
  }

  public RestProcessEntityParameters()
  {
  }

  public RestProcessEntityParameters(String parameterEntityType, String defKey, Map<String, Object> parameters,
      List<RestCompletedFormField> restFormFields)
  {
    this.parameterEntityType = parameterEntityType;
    this.defKey = defKey;
    this.parameters = parameters;
    this.restFormFields = restFormFields;
  }

  public String getParameterEntityType()
  {
    return parameterEntityType;
  }

  public void setParameterEntityType(String parameterEntityType)
  {
    this.parameterEntityType = parameterEntityType;
  }

  public Map<String, Object> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters)
  {
    this.parameters = parameters;
  }

  public List<RestCompletedFormField> getRestFormFields()
  {
    return restFormFields;
  }

  public void setRestFormFields(List<RestCompletedFormField> restFormFields)
  {
    this.restFormFields = restFormFields;
  }

  public String getDefKey()
  {
    return defKey;
  }

  public void setDefKey(String defKey)
  {
    this.defKey = defKey;
  }
}
