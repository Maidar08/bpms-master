package mn.erin.domain.bpm.usecase.process.collateral.updateCollateral;

import java.util.Collection;

import mn.erin.domain.bpm.model.form.CompletedFormField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Lkhagvadorj
 */
public class UpdateCollateralInput
{
  private String instanceId;
  private String collateralId;
  private ParameterEntityType entityType;
  private Collection<CompletedFormField> taskFormFields;

  public UpdateCollateralInput(String instanceId, String collateralId, ParameterEntityType entityType, Collection<CompletedFormField> taskFormFields)
  {
    this.instanceId = instanceId;
    this.collateralId = collateralId;
    this.entityType = entityType;
    this.taskFormFields = taskFormFields;
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public ParameterEntityType getEntityType()
  {
    return entityType;
  }

  public Collection<CompletedFormField> getTaskFormFields()
  {
    return taskFormFields;
  }
}
