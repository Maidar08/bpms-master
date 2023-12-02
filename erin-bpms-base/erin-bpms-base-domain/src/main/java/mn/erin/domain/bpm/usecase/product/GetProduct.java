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
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class GetProduct extends AbstractUseCase<UniqueProductInput, Product>
{
  private final ProductRepository productRepository;

  public GetProduct(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public Product execute(UniqueProductInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      Product product = productRepository.findByProductIdAndApplicationCategory(input.getProductId(), input.getApplicationCategory());

      if (product == null)
      {
        throw new UseCaseException(BpmMessagesConstants.PRODUCT_DOESNT_EXIST_ERROR_CODE, BpmMessagesConstants.PRODUCT_DOESNT_EXIST_ERROR_MESSAGE);
      }

      return product;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }

  }
}
