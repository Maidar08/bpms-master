/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.repository;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.product.CollateralProduct;

/**
 * @author Zorig
 */
public interface CollateralProductRepository extends Repository<CollateralProduct>
{
  /**
   * Creates and saves collateral product from following parameters.
   *
   * @param productId Unique id of product.
   * @param type application category.
   * @param subType category description
   * @param productDescription product description.
   * @param moreInformation product type
   * @throws BpmRepositoryException when there is a SQL insertion error or when parameters are null/blank.
   * @return created collateral product.
   */
  CollateralProduct create(String productId, String type, String subType, String productDescription, String moreInformation)
      throws BpmRepositoryException;

  /**
   * Deletes product by product id.
   *
   * @param productId Unique id of product.
   * @throws BpmRepositoryException when there is a SQL deletion error or when parameter is null/blank.
   */
  void deleteById(String productId) throws BpmRepositoryException;

  /**
   * Get collateral product by type, subtype and description
   * @param type collateral product type
   * @param subType collateral product sub type
   * @param description collateral product description
   * @return Product if existent, null if non-existent.
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   */
  CollateralProduct filterByTypeAndSubTypeAndDesc(String type, String subType, String description) throws BpmRepositoryException;
}
