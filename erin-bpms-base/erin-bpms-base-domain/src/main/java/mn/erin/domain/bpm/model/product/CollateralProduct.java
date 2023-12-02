/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.model.product;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;

/**
 * @author Zorig
 */
public class CollateralProduct implements Entity<CollateralProduct>
{
  private final ProductId id;
  private final String type;
  private final String subType;
  private final String description;
  private final String moreInformation;

  public CollateralProduct(ProductId id, String type, String subType, String description, String moreInformation)
  {
    this.id = Objects.requireNonNull(id, "Product id is required!");
    this.type = Objects.requireNonNull(type, "Type is required!");
    this.subType = Objects.requireNonNull(subType, "Sub Type is required!");
    this.description = Objects.requireNonNull(description, "Description is required!");
    this.moreInformation = Objects.requireNonNull(moreInformation, "More information is required!");
  }

  public ProductId getId()
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

  @Override
  public boolean sameIdentityAs(CollateralProduct other)
  {
    return false;
  }
}
