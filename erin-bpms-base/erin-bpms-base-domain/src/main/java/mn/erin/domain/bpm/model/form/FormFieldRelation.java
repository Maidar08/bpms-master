package mn.erin.domain.bpm.model.form;

import java.util.Collection;
import java.util.Map;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Tamir
 */
public class FormFieldRelation implements ValueObject<FormFieldRelation>
{
  private static final long serialVersionUID = 7896808691771968516L;

  private String updatedFieldID;
  private String operationType;

  private Collection<String> operations;
  private Collection<String> references;

  private Map<String, Collection<FieldProperty>> options;

  public FormFieldRelation(String updatedFieldID, Collection<String> references, Collection<String> operations,
      String operationType, Map<String, Collection<FieldProperty>> options)
  {
    this.updatedFieldID = updatedFieldID;
    this.references = references;
    this.operations = operations;
    this.operationType = operationType;
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

  public Collection<String> getReferences()
  {
    return references;
  }

  public void setReferences(Collection<String> references)
  {
    this.references = references;
  }

  public Collection<String> getOperations()
  {
    return operations;
  }

  public void setOperations(Collection<String> operations)
  {
    this.operations = operations;
  }

  public String getOperationType()
  {
    return operationType;
  }

  public void setOperationType(String operationType)
  {
    this.operationType = operationType;
  }

  public Map<String, Collection<FieldProperty>> getOptions()
  {
    return options;
  }

  public void setOptions(Map<String, Collection<FieldProperty>> options)
  {
    this.options = options;
  }

  @Override
  public boolean sameValueAs(FormFieldRelation other)
  {
    return this.equals(other);
  }
}
