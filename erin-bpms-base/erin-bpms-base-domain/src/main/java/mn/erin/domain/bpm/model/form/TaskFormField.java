/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.model.ValueObject;

/**
 * Represents a task form field entity.
 *
 * @author Tamir
 */
public class TaskFormField implements ValueObject<TaskFormField>
{
  private FormFieldId id;
  private FormFieldValue formFieldValue;

  private String label;
  private String type;

  private List<FieldValidation> fieldValidations;
  private List<FieldProperty> fieldProperties;

  public TaskFormField()
  {

  }

  public TaskFormField(FormFieldId id, FormFieldValue formFieldValue, String label, String type)
  {
    this.id = Objects.requireNonNull(id, "Form field id is required!");
    this.formFieldValue = Objects.requireNonNull(formFieldValue, "Form field value is required!");

    this.label = Objects.requireNonNull(label, "Form field label is required!");
    this.type = Objects.requireNonNull(type, "Form field type is required!");
  }

  public void setId(FormFieldId id)
  {
    this.id = id;
  }

  public FormFieldId getId()
  {
    return id;
  }

  public FormFieldValue getFormFieldValue()
  {
    return formFieldValue;
  }

  public void setFormFieldValue(FormFieldValue formFieldValue)
  {
    this.formFieldValue = formFieldValue;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public Collection<FieldValidation> getFieldValidations()
  {
    return fieldValidations;
  }

  public void setFieldValidations(List<FieldValidation> fieldValidations)
  {
    this.fieldValidations = fieldValidations;
  }

  public Collection<FieldProperty> getFieldProperties()
  {
    return fieldProperties;
  }

  public void setFieldProperties(List<FieldProperty> fieldProperties)
  {
    this.fieldProperties = fieldProperties;
  }

  @Override
  public boolean sameValueAs(TaskFormField other)
  {
    return this.id.equals(other.id) && this.label.equals(other.label) && this.type.equals(other.type);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof TaskFormField)
    {
      return sameValueAs((TaskFormField) obj);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id, label, type);
  }

  public static class Builder implements Serializable
  {
    private static final long serialVersionUID = -2161760794718785307L;

    private String id;
    private FormFieldValue formFieldValue;

    private String label;
    private String type;

    private List<FieldValidation> fieldValidations;
    private List<FieldProperty> fieldProperties;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withValue(FormFieldValue formFieldValue)
    {
      this.formFieldValue = formFieldValue;
      return this;
    }

    public Builder withLabel(String label)
    {
      this.label = label;
      return this;
    }

    public Builder withType(String type)
    {
      this.type = type;
      return this;
    }

    public Builder withValidations(List<FieldValidation> fieldValidations)
    {
      this.fieldValidations = fieldValidations;
      return this;
    }

    public Builder withProperties(List<FieldProperty> fieldProperties)
    {
      this.fieldProperties = fieldProperties;
      return this;
    }

    public TaskFormField build()
    {
      TaskFormField taskFormField = new TaskFormField();

      taskFormField.id = new FormFieldId(this.id);
      taskFormField.formFieldValue = this.formFieldValue;

      taskFormField.label = this.label;
      taskFormField.type = this.type;

      taskFormField.fieldProperties = this.fieldProperties;
      taskFormField.fieldValidations = this.fieldValidations;

      return taskFormField;
    }
  }
}
