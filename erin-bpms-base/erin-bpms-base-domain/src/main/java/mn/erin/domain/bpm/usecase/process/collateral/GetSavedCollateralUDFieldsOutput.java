/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process.collateral;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author Zorig
 */
public class GetSavedCollateralUDFieldsOutput
{
  private final Map<String, Serializable> udFields;

  public GetSavedCollateralUDFieldsOutput(Map<String, Serializable> udFields)
  {
    this.udFields = udFields;
  }

  public Map<String, Serializable> getUdFields()
  {
    return Collections.unmodifiableMap(udFields);
  }
}
