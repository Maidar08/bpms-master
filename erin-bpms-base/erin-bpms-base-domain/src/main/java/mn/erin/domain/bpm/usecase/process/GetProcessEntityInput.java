package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class GetProcessEntityInput
{
  private final String processInstanceId;
  private final ParameterEntityType parameterEntityType;

  public GetProcessEntityInput(String processInstanceId, ParameterEntityType parameterEntityType)
  {
    this.processInstanceId = processInstanceId;
    this.parameterEntityType = parameterEntityType;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }
}
