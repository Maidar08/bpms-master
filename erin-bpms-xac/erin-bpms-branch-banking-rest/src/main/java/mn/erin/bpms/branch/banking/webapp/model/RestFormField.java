package mn.erin.bpms.branch.banking.webapp.model;

import java.util.Collection;

import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldValue;

/**
 * @author Lkhagvadorj.A
 **/

public class RestFormField
{
    private String id;
    private FormFieldValue formFieldValue;

    private String label;
    private String type;

    private Collection<FieldValidation> validations;
    private Collection<FieldProperty> options;

    public RestFormField(String id, FormFieldValue formFieldValue, String label, String type,
        Collection<FieldValidation> validations, Collection<FieldProperty> options)
    {
        this.id = id;
        this.formFieldValue = formFieldValue;
        this.label = label;
        this.type = type;
        this.validations = validations;
        this.options = options;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public Collection<FieldValidation> getValidations()
    {
        return validations;
    }

    public void setValidations(Collection<FieldValidation> validations)
    {
        this.validations = validations;
    }

    public Collection<FieldProperty> getOptions()
    {
        return options;
    }

    public void setOptions(Collection<FieldProperty> fieldProperties)
    {
        this.options = fieldProperties;
    }
}
