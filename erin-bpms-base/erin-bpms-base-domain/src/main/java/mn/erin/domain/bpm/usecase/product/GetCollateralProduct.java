/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.CollateralProductRepository;

/**
 * @author Zorig
 */
public class GetCollateralProduct extends AbstractUseCase<String, CollateralProduct>
{
  private final CollateralProductRepository collateralProductRepository;

  public GetCollateralProduct(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product Repository is required!!");
  }

  @Override
  public CollateralProduct execute(String productId) throws UseCaseException
  {
    try
    {
      CollateralProduct product = collateralProductRepository.findById(ProductId.valueOf(productId));

      if (product == null)
      {
        throw new UseCaseException(BpmMessagesConstants.PRODUCT_DOESNT_EXIST_ERROR_CODE, BpmMessagesConstants.PRODUCT_DOESNT_EXIST_ERROR_MESSAGE);
      }

      return product;
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

  }
}
