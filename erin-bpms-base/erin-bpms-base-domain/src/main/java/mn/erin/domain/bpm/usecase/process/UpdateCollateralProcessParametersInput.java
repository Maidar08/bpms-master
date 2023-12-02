/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class UpdateCollateralProcessParametersInput
{
  private final String processInstanceId;
  private final String collateralId;
  private final Map<ParameterEntityType, Map<String, Serializable>> parameters;

  public UpdateCollateralProcessParametersInput(String processInstanceId, String collateralId,
      Map<ParameterEntityType, Map<String, Serializable>> parameters)
  {
    this.processInstanceId = processInstanceId;
    this.collateralId = collateralId;
    this.parameters = parameters;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public String getCollateralId()
  {
    return collateralId;
  }

  public Map<ParameterEntityType, Map<String, Serializable>> getParameters()
  {
    return parameters;
  }
}
