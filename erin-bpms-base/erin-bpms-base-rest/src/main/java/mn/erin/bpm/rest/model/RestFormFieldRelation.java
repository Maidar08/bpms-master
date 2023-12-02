package mn.erin.bpm.rest.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestFormFieldRelation
{
  private String updatedFieldID;
  private String operationType;

  private Collection<String> operations;
  private Collection<String> references;

  private Map<String, List<RestFieldProperty>> options;

  public RestFormFieldRelation()
  {

  }

  public RestFormFieldRelation(String updatedFieldID, String operationType, Collection<String> operations, Collection<String> references,
      Map<String, List<RestFieldProperty>> options)
  {
    this.updatedFieldID = updatedFieldID;
    this.operationType = operationType;
    this.operations = operations;
    this.references = references;
    this.options = options;
  }

  public String getUpdatedFieldID()
  {
    return updatedFieldID;
  }

  public void setUpdatedFieldID(String updatedFieldID)
  {
    this.updatedFieldID = updatedFieldID;
  }

  public String getOperationType()
  {
    return operationType;
  }

  public void setOperationType(String operationType)
  {
    this.operationType = operationType;
  }

  public Collection<String> getOperations()
  {
    return operations;
  }

  public void setOperations(Collection<String> operations)
  {
    this.operations = operations;
  }

  public Collection<String> getReferences()
  {
    return references;
  }

  public void setReferences(Collection<String> references)
  {
    this.references = references;
  }

  public Map<String, List<RestFieldProperty>> getOptions()
  {
    return options;
  }

  public void setOptions(Map<String, List<RestFieldProperty>> options)
  {
    this.options = options;
  }
}
