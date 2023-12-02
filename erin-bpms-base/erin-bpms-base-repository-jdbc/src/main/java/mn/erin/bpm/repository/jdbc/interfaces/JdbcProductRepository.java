/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcProduct;

/**
 * @author Zorig
 */
public interface JdbcProductRepository extends CrudRepository<JdbcProduct, String>
{
  @Modifying
  @Query(value = "INSERT INTO ERIN_BPMS_PRODUCT(PRODUCT_ID, APPLICATION_CATEGORY, CATEGORY_DESCRIPTION, PRODUCT_DESCRIPTION, TYPE, LOAN_TO_VALUE_RATIO, HAS_COLLATERAL, HAS_INSURANCE) VALUES(:productId, :applicationCategory, :categoryDescription, :productDescription, :type, :loanToValueRatio, :hasCollateral, :hasInsurance)")
  int insert(@Param("productId") String productId, @Param("applicationCategory") String applicationCategory, @Param("categoryDescription") String categoryDescription, @Param("productDescription") String productDescription, @Param("type") String type, @Param("loanToValueRatio") String loanToValueRatio, @Param("hasCollateral") int hasCollateral, @Param("hasInsurance") int hasInsurance);

  @Query(value = "SELECT * FROM ERIN_BPMS_PRODUCT WHERE PRODUCT_ID = :productId AND APPLICATION_CATEGORY = :applicationCategory")
  JdbcProduct findByProductIdAndApplicationCategory(@Param("productId") String productId, @Param("applicationCategory") String applicationCategory);

  @Query("SELECT * FROM ERIN_BPMS_PRODUCT WHERE PRODUCT_ID = :productId")
  List<JdbcProduct> findAllByProductId(@Param("productId") String productId);

  @Query("SELECT * FROM ERIN_BPMS_PRODUCT WHERE APPLICATION_CATEGORY = :applicationCategory AND BORROWER_TYPE = :borrowerType")
  List<JdbcProduct> findByAppCategoryAndBorrowerType(@Param("applicationCategory") String applicationCategory, @Param("borrowerType") String borrowerType);

  @Modifying
  @Query("DELETE FROM ERIN_BPMS_PRODUCT WHERE PRODUCT_ID = :productId")
  int deleteProductsByProductId(@Param("productId") String productId);

  @Modifying
  @Query("DELETE FROM ERIN_BPMS_PRODUCT WHERE PRODUCT_ID = :productId AND APPLICATION_CATEGORY - :applicationCategory")
  int deleteProductByProductIdAndApplicationCategory(@Param("productId") String productId, @Param("applicationCategory") String applicationCategory);

  @Query(value = "SELECT * FROM ERIN_BPMS_PRODUCT WHERE APPLICATION_CATEGORY = :applicationCategory")
  List<JdbcProduct> findByApplicationCategory(@Param("applicationCategory") String applicationCategory);
}
