/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class GetAllProducts extends AbstractUseCase<Void, GetAllProductsOutput>
{
  private final ProductRepository productRepository;

  public GetAllProducts(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public GetAllProductsOutput execute(Void input) throws UseCaseException
  {
    return new GetAllProductsOutput(productRepository.findAll());
  }
}
