/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("ERIN_BPMS_COLLATERAL_PRODUCT")
public class JdbcCollateralProduct
{
  @Id
  String id;
  String type;
  String subType;
  String description;
  String moreInformation;

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

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getMoreInformation()
  {
    return moreInformation;
  }

  public void setMoreInformation(String moreInformation)
  {
    this.moreInformation = moreInformation;
  }
}
