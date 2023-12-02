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
import mn.erin.domain.bpm.repository.CollateralProductRepository;

/**
 * @author Zorig
 */
public class GetAllCollateralProducts extends AbstractUseCase<Void, GetAllCollateralProductsOutput>
{
  private final CollateralProductRepository collateralProductRepository;

  public GetAllCollateralProducts(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product repository is required!");
  }

  @Override
  public GetAllCollateralProductsOutput execute(Void input) throws UseCaseException
  {
    return new GetAllCollateralProductsOutput(collateralProductRepository.findAll());
  }
}
