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
public class CreateProduct extends AbstractUseCase<CreateProductInput, Product>
{
  private final ProductRepository productRepository;

  public CreateProduct(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product Repository service is required!");
  }

  @Override
  public Product execute(CreateProductInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      Product product = productRepository.create(input.getProductId(), input.getApplicationCategory(), input.getCategoryDescription(), input.getProductDescription(), input.getType(), input.getLoanToValueRatio(), input.isHasCollateral(), input.isHasInsurance());

      if (product == null)
      {
        throw new UseCaseException(BpmMessagesConstants.PRODUCT_USECASE_CREATE_UNSUCCESSFUL_ERROR_CODE, BpmMessagesConstants.PRODUCT_USECASE_CREATE_UNSUCCESSFUL_MESSAGE);
      }

      return product;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
