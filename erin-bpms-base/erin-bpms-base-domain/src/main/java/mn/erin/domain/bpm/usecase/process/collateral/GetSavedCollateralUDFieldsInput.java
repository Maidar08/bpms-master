/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.process.collateral;

/**
 * @author Zorig
 */
public class GetSavedCollateralUDFieldsInput
{
  private final String collateralId;

  public GetSavedCollateralUDFieldsInput(String collateralId)
  {
    this.collateralId = collateralId;
  }

  public String getCollateralId()
  {
    return collateralId;
  }
}
