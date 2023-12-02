/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcCollateralProduct;

/**
 * @author Zorig
 */
public interface JdbcCollateralProductRepository extends CrudRepository<JdbcCollateralProduct, String>
{
  @Modifying
  @Query(value = "INSERT INTO ERIN_BPMS_COLLATERAL_PRODUCT(ID, TYPE, SUB_TYPE, DESCRIPTION, MORE_INFORMATION) VALUES(:productId, :type, :subType, :productDescription, :moreInformation)")
  int insert(@Param("productId") String productId, @Param("type") String type, @Param("subType") String subType, @Param("productDescription") String productDescription, @Param("moreInformation") String moreInformation);


  // TODO : deprecated query, remove after new query tested.
//  @Query("SELECT * FROM ERIN_BPMS_COLLATERAL_PRODUCT WHERE TYPE = :type AND SUB_TYPE = :subType AND DESCRIPTION = :description")

  @Query("SELECT * FROM ERIN_BPMS_COLLATERAL_PRODUCT WHERE REPLACE(TYPE, ' ', '') = REPLACE(:type, ' ', '') AND REPLACE(SUB_TYPE, ' ', '') = REPLACE(:subType, ' ', '') AND REPLACE(DESCRIPTION, ' ', '') = REPLACE(:description, ' ', '')")
  JdbcCollateralProduct filterByTypeAndSubTypeAndDesc(@Param("type") String type, @Param("subType") String subType, @Param("description") String description);
}