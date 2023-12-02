package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class UpdateProcessParametersInput
{
  private final String processInstanceId;
  private final Map<ParameterEntityType, Map<String, Serializable>> parameters;

  public UpdateProcessParametersInput(String processInstanceId,
      Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {
    this.processInstanceId = processInstanceId;
    this.parameters = parameters;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public Map<ParameterEntityType, Map<String, Serializable>> getParameters()
  {
    return parameters;
  }
}
