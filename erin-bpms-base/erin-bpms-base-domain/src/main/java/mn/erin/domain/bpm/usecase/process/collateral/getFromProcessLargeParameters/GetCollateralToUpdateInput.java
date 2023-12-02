package mn.erin.domain.bpm.usecase.process.collateral.getFromProcessLargeParameters;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Lkhagvadorj
 */
public class GetCollateralToUpdateInput
{
  private final String instanceId;
  private final String collateralId;
  private final ParameterEntityType parameterEntityType;

  public GetCollateralToUpdateInput(String instanceId, String collateralId, ParameterEntityType parameterEntityType)
  {
    this.instanceId = instanceId;
    this.collateralId = collateralId;
    this.parameterEntityType = parameterEntityType;
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }

  public String getInstanceId()
  {
    return instanceId;
  }
}
