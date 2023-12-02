/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.bpm.model.product.CollateralProduct;


/**
 * @author Zorig
 */
public class GetAllCollateralProductsOutput
{
  private final Collection<CollateralProduct> collateralProducts;

  public GetAllCollateralProductsOutput(Collection<CollateralProduct> collateralProducts)
  {
    this.collateralProducts = Objects.requireNonNull(collateralProducts, "Collateral Products collection must not be null!");
  }

  public Collection<CollateralProduct> getCollateralProducts()
  {
    return collateralProducts;
  }
}
