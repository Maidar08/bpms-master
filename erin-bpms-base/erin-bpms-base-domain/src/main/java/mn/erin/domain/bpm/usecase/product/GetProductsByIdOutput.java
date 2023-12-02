/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Collections;
import java.util.List;

import mn.erin.domain.bpm.model.product.Product;

/**
 * @author Zorig
 */
public class GetProductsByIdOutput
{
  private final List<Product> productsList;

  public GetProductsByIdOutput(List<Product> productsList)
  {
    this.productsList = productsList;
  }

  public List<Product> getProductsList()
  {
    return Collections.unmodifiableList(productsList);
  }
}
