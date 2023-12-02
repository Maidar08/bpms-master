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
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;

/**
 * @author Zorig
 */
public class CreateCollateralProduct extends AbstractUseCase<CreateCollateralProductInput, CollateralProduct>
{
  private final CollateralProductRepository collateralProductRepository;

  public CreateCollateralProduct(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product Repository is required!!");
  }

  @Override
  public CollateralProduct execute(CreateCollateralProductInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    try
    {
      CollateralProduct collateralProduct = collateralProductRepository.create(input.getId(), input.getType(), input.getSubType(), input.getDescription(), input.getMoreInformation());

      if (collateralProduct == null)
      {
        throw new UseCaseException(BpmMessagesConstants.PRODUCT_USECASE_CREATE_UNSUCCESSFUL_ERROR_CODE, BpmMessagesConstants.PRODUCT_USECASE_CREATE_UNSUCCESSFUL_MESSAGE);
      }

      return collateralProduct;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
