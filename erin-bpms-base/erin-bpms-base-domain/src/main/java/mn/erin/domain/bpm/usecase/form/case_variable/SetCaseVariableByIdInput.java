package mn.erin.domain.bpm.usecase.form.case_variable;

import java.util.Objects;

/**
 * @author Tamir
 */
public class SetCaseVariableByIdInput
{
  private String caseInstanceId;
  private String variableId;
  private Object variableValue;

  public SetCaseVariableByIdInput(String caseInstanceId, String variableId, Object variableValue)
  {
    this.caseInstanceId = Objects.requireNonNull(caseInstanceId, "Case instance id is required!");
    this.variableId = Objects.requireNonNull(variableId, "Variable id is required!");
    this.variableValue = Objects.requireNonNull(variableValue, "Variable values is required!");
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getVariableId()
  {
    return variableId;
  }

  public void setVariableId(String variableId)
  {
    this.variableId = variableId;
  }

  public Object getVariableValue()
  {
    return variableValue;
  }

  public void setVariableValue(Object variableValue)
  {
    this.variableValue = variableValue;
  }
}
