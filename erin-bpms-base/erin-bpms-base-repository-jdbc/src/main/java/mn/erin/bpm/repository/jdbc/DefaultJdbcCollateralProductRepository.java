/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcCollateralProductRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcCollateralProduct;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;

/**
 * @author Zorig
 */
@Repository
public class DefaultJdbcCollateralProductRepository implements CollateralProductRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcCollateralProductRepository.class);

  private final JdbcCollateralProductRepository jdbcCollateralProductRepository;

  @Inject
  public DefaultJdbcCollateralProductRepository(JdbcCollateralProductRepository jdbcCollateralProductRepository)
  {
    this.jdbcCollateralProductRepository = Objects.requireNonNull(jdbcCollateralProductRepository, "JDBC Collateral Product Repository is required!");
  }

  @Override
  public CollateralProduct create(String productId, String type, String subType, String productDescription, String moreInformation)
      throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(productDescription))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(moreInformation))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.COLLATERAL_PRODUCT_BLANK_MORE_INFORMATION_ERROR_CODE, BpmMessagesConstants.COLLATERAL_PRODUCT_BLANK_MORE_INFORMATION_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(type))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_TYPE_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_TYPE_ERROR_MESSAGE);
    }
    if (StringUtils.isBlank(subType))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.COLLATERAL_PRODUCT_BLANK_SUB_TYPE_ERROR_CODE, BpmMessagesConstants.COLLATERAL_PRODUCT_BLANK_SUB_TYPE_ERROR_MESSAGE);
    }

    try
    {
      int isInserted = jdbcCollateralProductRepository.insert(productId, type, subType, productDescription, moreInformation);

      if (isInserted == 0)
      {
        return null;
      }

      return new CollateralProduct(ProductId.valueOf(productId), type, subType, productDescription, moreInformation);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public void deleteById(String productId) throws BpmRepositoryException
  {
    if (StringUtils.isBlank(productId))
    {
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_CODE, BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    try
    {
      jdbcCollateralProductRepository.deleteById(productId);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.COLLATERAL_PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public CollateralProduct filterByTypeAndSubTypeAndDesc(String type, String subType, String description) throws BpmRepositoryException
  {
    try
    {
      final JdbcCollateralProduct jdbcCollateralProduct = jdbcCollateralProductRepository.filterByTypeAndSubTypeAndDesc(type, subType, description);
      if (null == jdbcCollateralProduct)
      {
        return null;
      }

      return toProduct(jdbcCollateralProduct);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      throw new BpmRepositoryException(BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_CODE, BpmMessagesConstants.PRODUCT_REPOSITORY_SQL_ERROR_MESSAGE);
    }
  }

  @Override
  public CollateralProduct findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Entity Id is required!");

    Optional<JdbcCollateralProduct> jdbcCollateralProductOptional = jdbcCollateralProductRepository.findById(entityId.getId());

    return jdbcCollateralProductOptional.map(this::toProduct).orElse(null);
  }

  @Override
  public Collection<CollateralProduct> findAll()
  {
    Iterator<JdbcCollateralProduct> jdbcCollateralProductIterator = jdbcCollateralProductRepository.findAll().iterator();

    Collection<CollateralProduct> products = new ArrayList<>();

    while (jdbcCollateralProductIterator.hasNext())
    {
      products.add(toProduct(jdbcCollateralProductIterator.next()));
    }

    return products;
  }

  private CollateralProduct toProduct(JdbcCollateralProduct jdbcCollateralProduct)
  {
    ProductId productId = ProductId.valueOf(jdbcCollateralProduct.getId());
    String type = jdbcCollateralProduct.getType();
    String subType = jdbcCollateralProduct.getSubType();
    String description = jdbcCollateralProduct.getDescription();
    String moreInformation = jdbcCollateralProduct.getMoreInformation();


    return new CollateralProduct(productId, type, subType, description, moreInformation);
  }
}
