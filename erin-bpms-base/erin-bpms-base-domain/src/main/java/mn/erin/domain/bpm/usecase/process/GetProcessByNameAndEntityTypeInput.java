/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process;

import mn.erin.domain.bpm.model.process.ParameterEntityType;

/**
 * @author Zorig
 */
public class GetProcessByNameAndEntityTypeInput
{
  private final String parameterName;
  private final ParameterEntityType parameterEntityType;

  public GetProcessByNameAndEntityTypeInput(String parameterName, ParameterEntityType parameterEntityType)
  {
    this.parameterName = parameterName;
    this.parameterEntityType = parameterEntityType;
  }

  public String getParameterName()
  {
    return parameterName;
  }

  public ParameterEntityType getParameterEntityType()
  {
    return parameterEntityType;
  }
}
