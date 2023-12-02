/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class DeleteProductsById extends AbstractUseCase<String, Boolean>
{
  private final ProductRepository productRepository;

  public DeleteProductsById(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product Repository service is required!");
  }

  @Override
  public Boolean execute(String productId) throws UseCaseException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new UseCaseException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    try
    {
      productRepository.deleteAllById(productId);

      return true;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
