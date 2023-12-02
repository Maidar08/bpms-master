/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Zorig
 */
public class CreateProductInput
{
  private final String productId;
  private final String applicationCategory;
  private final String categoryDescription;
  private final String productDescription;
  private final String type;
  private final BigDecimal loanToValueRatio;
  private final boolean hasCollateral;
  private final boolean hasInsurance;

  public CreateProductInput(String productId, String applicationCategory, String categoryDescription, String productDescription, String type,
      BigDecimal loanToValueRatio, boolean hasCollateral, boolean hasInsurance)
  {
    this.productId = Objects.requireNonNull(productId, "Product id is required!");
    this.applicationCategory = Objects.requireNonNull(applicationCategory, "Application category is required!");
    this.categoryDescription = Objects.requireNonNull(categoryDescription, "Category description is required!");
    this.productDescription = Objects.requireNonNull(productDescription, "Product description is required!");
    this.type = Objects.requireNonNull(type, "Product type is required!");
    this.loanToValueRatio = Objects.requireNonNull(loanToValueRatio, "Loan To Value Ratio is required!");
    this.hasCollateral = Objects.requireNonNull(hasCollateral, "Has Collateral is required!");
    this.hasInsurance = Objects.requireNonNull(hasInsurance, "Has Insurance is required!");
  }

  public boolean isHasInsurance()
  {
    return hasInsurance;
  }

  public boolean isHasCollateral()
  {
    return hasCollateral;
  }

  public String getProductId()
  {
    return productId;
  }

  public String getApplicationCategory()
  {
    return applicationCategory;
  }

  public String getCategoryDescription()
  {
    return categoryDescription;
  }

  public String getProductDescription()
  {
    return productDescription;
  }

  public String getType()
  {
    return type;
  }

  public BigDecimal getLoanToValueRatio()
  {
    return loanToValueRatio;
  }
}
