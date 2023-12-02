package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Lkhagvadorj.A
 **/

public class DeleteProcessParameterByInstanceIdAndEntityInput
{
  private String processInstanceId;
  private ParameterEntityType parameterEntityType;

  public DeleteProcessParameterByInstanceIdAndEntityInput(String processInstanceId, ParameterEntityType parameterEntityType)
  {
    this.processInstanceId = processInstanceId;
    this.parameterEntityType = parameterEntityType;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }

  public void setParameterEntityType(ParameterEntityType parameterEntityType)
  {
    this.parameterEntityType = parameterEntityType;
  }
}
