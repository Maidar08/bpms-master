/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
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
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;

/**
 * @author Zorig
 */
public class DeleteCollateralProductById extends AbstractUseCase<String, Boolean>
{
  private final CollateralProductRepository collateralProductRepository;

  public DeleteCollateralProductById(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product Repository is required!!");
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
      collateralProductRepository.deleteById(productId);

      CollateralProduct collateralProduct = collateralProductRepository.findById(ProductId.valueOf(productId));

      return collateralProduct == null;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
