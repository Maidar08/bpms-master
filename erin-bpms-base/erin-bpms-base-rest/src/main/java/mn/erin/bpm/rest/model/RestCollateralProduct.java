/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.rest.model;

/**
 * @author Zorig
 */
public class RestCollateralProduct
{
  private String id;
  private String type;
  private String subType;
  private String moreInformation;
  private String description;

  public RestCollateralProduct()
  {
    // no needed to do something
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getSubType()
  {
    return subType;
  }

  public void setSubType(String subType)
  {
    this.subType = subType;
  }

  public String getMoreInformation()
  {
    return moreInformation;
  }

  public void setMoreInformation(String moreInformation)
  {
    this.moreInformation = moreInformation;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
