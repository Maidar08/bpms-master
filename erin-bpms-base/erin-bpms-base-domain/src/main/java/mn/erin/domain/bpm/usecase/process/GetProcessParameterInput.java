package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Tamir
 */
public class GetProcessParameterInput
{
  private final String instanceId;
  private final String parameterKey;
  private final ParameterEntityType parameterEntityType;

  public GetProcessParameterInput(String instanceId, String parameterKey, ParameterEntityType parameterEntityType)
  {
    this.instanceId = instanceId;
    this.parameterKey = parameterKey;
    this.parameterEntityType = parameterEntityType;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public String getParameterKey()
  {
    return parameterKey;
  }

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }
}
