/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class GetProductsById extends AbstractUseCase<String, GetProductsByIdOutput>
{
  private final ProductRepository productRepository;

  public GetProductsById(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public GetProductsByIdOutput execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      List<Product> products = productRepository.findAllById(input);

      return new GetProductsByIdOutput(products);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
