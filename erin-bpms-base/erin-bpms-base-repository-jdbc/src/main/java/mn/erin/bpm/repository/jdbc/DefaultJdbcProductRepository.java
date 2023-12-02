/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProductRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProduct;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcProductRepository implements ProductRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcProductRepository.class);

  private final JdbcProductRepository jdbcProductRepository;

  @Inject
  public DefaultJdbcProductRepository(JdbcProductRepository jdbcProductRepository)
  {
    this.jdbcProductRepository = Objects.requireNonNull(jdbcProductRepository, "Product Repository is required!");
  }

  @Override
  public Product findById(EntityId entityId)
  {
    throw new UnsupportedOperationException("Unsupported operation: Finding by Id returns multiple entities.");
  }

  @Override
  public Collection<Product> findAll()
  {
    Iterator<JdbcProduct> jdbcProductIterator = jdbcProductRepository.findAll().iterator();

    Collection<Product> products = new ArrayList<>();

    while (jdbcProductIterator.hasNext())
    {
      products.add(toProduct(jdbcProductIterator.next()));
    }

    return products;
  }

  @Override
  public Product create(String productId, String applicationCategory, String categoryDescription, String productDescription, String productType,
      BigDecimal loanToValueRatio, boolean hasCollateral, boolean hasInsurance) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(applicationCategory))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(productDescription))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(productType))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_TYPE_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_TYPE_ERROR_MESSAGE);
    }
    if (loanToValueRatio == null)
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_LOAN_TO_VALUE_RATIO_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_LOAN_TO_VALUE_RATIO_ERROR_MESSAGE);
    }

    try
    {
      int isInserted = jdbcProductRepository
          .insert(productId, applicationCategory, categoryDescription, productDescription, productType, loanToValueRatio.toPlainString(), hasCollateral ? 1 : 0,
              hasInsurance ? 1 : 0);

      if (isInserted == 0)
      {
        return null;
      }

      return new Product(ProductId.valueOf(productId), applicationCategory, categoryDescription, productDescription, productType, loanToValueRatio,
          hasCollateral, hasInsurance);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public int deleteAllById(String productId) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    try
    {
      return jdbcProductRepository.deleteProductsByProductId(productId);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public boolean updateProductById(String productId, String applicationCategory, String categoryDescription, String productDescription, String productType,
      BigDecimal loanToValueRatio)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllById(String productId) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    try
    {
      List<Product> productsToReturn = new ArrayList<>();

      for (JdbcProduct jdbcProduct : jdbcProductRepository.findAllByProductId(productId))
      {
        productsToReturn.add(toProduct(jdbcProduct));
      }

      return productsToReturn;
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public List<Product> findByAppCategoryAndBorrowerType(String applicationCategory, String borrowerType) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(applicationCategory))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE);
    }

    return jdbcProductRepository.findByAppCategoryAndBorrowerType(applicationCategory, borrowerType)
        .stream()
        .map(this::toProduct)
        .collect(Collectors.toList());
  }

  @Override
  public Product findByProductIdAndApplicationCategory(String productId, String applicationCategory) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(applicationCategory))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE);
    }

    try
    {
      JdbcProduct jdbcProduct = jdbcProductRepository.findByProductIdAndApplicationCategory(productId, applicationCategory);

      if (jdbcProduct != null)
      {
        return toProduct(jdbcProduct);
      }
      return null;
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public int deleteByProductIdAndApplicationCategory(String productId, String applicationCategory) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(applicationCategory))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE);
    }

    try
    {
      return jdbcProductRepository.deleteProductByProductIdAndApplicationCategory(productId, applicationCategory);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public List<Product> findByAppCategory(String applicationCategory) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(applicationCategory))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_CODE,
          BpmMessagesConstants.PRODUCT_BLANK_APPLICATION_CATEGORY_ERROR_MESSAGE);
    }

    return jdbcProductRepository.findByApplicationCategory(applicationCategory)
        .stream()
        .map(this::toProduct)
        .collect(Collectors.toList());
  }

  private Product toProduct(JdbcProduct jdbcProduct)
  {
    ProductId productId = ProductId.valueOf(jdbcProduct.getProductID());
    String applicationCategory = jdbcProduct.getApplicationCategory();
    String categoryDescription = jdbcProduct.getCategoryDescription();
    String productDescription = jdbcProduct.getProductDescription();
    String type = jdbcProduct.getType();
    BigDecimal loanToValueRatio = new BigDecimal(jdbcProduct.getLoanToValueRatio());
    boolean hasCollateral = jdbcProduct.getHasCollateral() == 1;
    boolean hasInsurance = jdbcProduct.getHasInsurance() == 1;
    String rate = String.valueOf(jdbcProduct.getRate());
    String frequency = String.valueOf(jdbcProduct.getFrequency());
    Product product = new Product(productId, applicationCategory, categoryDescription, productDescription, type, loanToValueRatio, hasCollateral, hasInsurance);
    product.setRate(rate);
    product.setFrequency(frequency);
    return product;
  }
}
