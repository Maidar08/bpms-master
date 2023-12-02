package mn.erin.bpms.branch.banking.webapp.model;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldValue;

/**
 * @author Lkhagvadorj.A
 **/

public class RestCompletedFormField
{
    private static final long serialVersionUID = 4207684919482643290L;

    private String id;
    private FormFieldValue formFieldValue;
    private String label;
    private String type;

    private List<FieldValidation> validations = new ArrayList<>();
    private List<FieldProperty> options = new ArrayList<>();

    private String context;
    private boolean disabled;
    private boolean required;
    private Integer columnIndex;

    public RestCompletedFormField()
    {
    }

    public RestCompletedFormField(String id, FormFieldValue formFieldValue, String label, String type,
        List<FieldValidation> validations, List<FieldProperty> properties, String context, boolean disabled, boolean required, Integer columnIndex)
    {
        this.id = id;
        this.formFieldValue = formFieldValue;
        this.label = label;
        this.type = type;
        this.validations = validations;
        this.options = properties;
        this.context = context;
        this.disabled = disabled;
        this.required = required;
        this.columnIndex = columnIndex;
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

    public List<FieldValidation> getValidations()
    {
        return validations;
    }

    public void setValidations(List<FieldValidation> validations)
    {
        this.validations = validations;
    }

    public List<FieldProperty> getOptions()
    {
        return options;
    }

    public void setOptions(List<FieldProperty> options)
    {
        this.options = options;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public boolean getDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        this.disabled = disabled;
    }

    public boolean getRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public Integer getColumnIndex()
    {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex)
    {
        this.columnIndex = columnIndex;
    }
}
