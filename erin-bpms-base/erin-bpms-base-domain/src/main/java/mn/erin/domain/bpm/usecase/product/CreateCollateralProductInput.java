/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

/**
 * @author Zorig
 */
public class CreateCollateralProductInput
{
  private final String id;
  private final String type;
  private final String subType;
  private final String description;
  private final String moreInformation;

  public CreateCollateralProductInput(String id, String type, String subType, String description, String moreInformation)
  {
    this.id = id;
    this.type = type;
    this.subType = subType;
    this.description = description;
    this.moreInformation = moreInformation;
  }

  public String getId()
  {
    return id;
  }

  public String getType()
  {
    return type;
  }

  public String getSubType()
  {
    return subType;
  }

  public String getDescription()
  {
    return description;
  }

  public String getMoreInformation()
  {
    return moreInformation;
  }
}
