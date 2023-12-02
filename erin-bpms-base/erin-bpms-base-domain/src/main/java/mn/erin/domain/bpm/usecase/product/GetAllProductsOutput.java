/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.bpm.model.product.Product;

/**
 * @author Zorig
 */
public class GetAllProductsOutput
{
  private final Collection<Product> products;

  public GetAllProductsOutput(Collection<Product> products)
  {
    this.products = Objects.requireNonNull(products, "Products collection must not be null!");
  }

  public Collection<Product> getProducts()
  {
    return products;
  }
}
