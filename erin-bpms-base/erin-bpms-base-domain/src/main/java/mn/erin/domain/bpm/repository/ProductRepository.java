/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.repository;

import java.math.BigDecimal;
import java.util.List;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.product.Product;

/**
 * @author Zorig
 */
public interface ProductRepository extends Repository<Product>
{
  /**
   * Creates and saves product from following parameters.
   *
   * @param productId Unique id of product.
   * @param applicationCategory application category.
   * @param categoryDescription category description
   * @param productDescription product description.
   * @param productType product type
   * @param loanToValueRatio loan to value ratio BigDecimal converted to String when saved.
   * @param hasCollateral boolean.
   * @throws BpmRepositoryException when there is a SQL insertion error or when parameters are null/blank.
   * @return created product.
   */
  Product create(String productId, String applicationCategory, String categoryDescription, String productDescription, String productType, BigDecimal loanToValueRatio, boolean hasCollateral, boolean hasInsurance)
      throws BpmRepositoryException;

  /**
   * Deletes product by product id.
   *
   * @param productId Unique id of product.
   * @throws BpmRepositoryException when there is a SQL deletion error or when parameter is null/blank.
   * @return integer - number of record updated.
   */
  int deleteAllById(String productId) throws BpmRepositoryException;

  /**
   * Updates product with given product id.
   * @param productId Unique id of product.
   * @param applicationCategory application category of product.
   * @param categoryDescription category description of product.
   * @param productDescription product description.
   * @param productType product type
   * @param loanToValueRatio loan to value ratio BigDecimal converted to String when saved.
   *
   * @return if successful updated returns true otherwise false.
   */
  boolean updateProductById(String productId, String applicationCategory, String categoryDescription, String productDescription, String productType, BigDecimal loanToValueRatio);

  /**
   * Retrieves list of products by product id.
   *
   * @param productId Id of product(s).
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   * @return List of Product
   */
  List<Product> findAllById(String productId) throws BpmRepositoryException;

  /**
   * Retrieves list of products by product id.
   *
   * @param applicationCategory application category.
   * @param borrowerType borrower type
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   * @return List of Product
   */
  List<Product> findByAppCategoryAndBorrowerType(String applicationCategory, String borrowerType) throws BpmRepositoryException;

  /**
   * Retrieves product by product id and loan to value ratio.
   *
   * @param productId Id of product. 1 of 2 keys(composite key)
   * @param applicationCategory application cateory field. 1 of 2 keys(composite key)
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   * @return Product if existent, null if non-existent.
   */
  Product findByProductIdAndApplicationCategory(String productId, String applicationCategory) throws BpmRepositoryException;

  /**
   * Retrieves product by product id and loan to value ratio.
   *
   * @param productId Id of product. 1 of 2 keys(composite key)
   * @param applicationCategory application category field. 1 of 2 keys(composite key)
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   * @return integer - number of records updated/deleted.
   */
  int deleteByProductIdAndApplicationCategory(String productId, String applicationCategory) throws BpmRepositoryException;

  /**
   * Retrieves list of products by application category.
   *
   * @param applicationCategory application category.
   * @throws BpmRepositoryException when there is a SQL error or when parameter is null/blank.
   * @return List of Product
   */
  List<Product> findByAppCategory(String applicationCategory) throws BpmRepositoryException;
}
