package mn.erin.domain.bpm.usecase.process.process_parameter;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

public class ProcessParameterByNameAndEntityInput {
    private String parameterName;
    private String parameterNameValue;
    private ParameterEntityType entityType;

    public ProcessParameterByNameAndEntityInput(String parameterName, String parameterNameValue, ParameterEntityType entityType)
    {
        this.parameterName = parameterName;
        this.entityType = entityType;
        this.parameterNameValue = parameterNameValue;
    }

    public ParameterEntityType getEntityType() {
        return entityType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setEntityType(ParameterEntityType entityType) {
        this.entityType = entityType;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterNameValue() {
        return parameterNameValue;
    }

    public void setParameterNameValue(String parameterNameValue) {
        this.parameterNameValue = parameterNameValue;
    }
}
