/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.rest.model;

import java.math.BigDecimal;

/**
 * @author Zorig
 */
public class RestProduct
{
  private String id;
  private String applicationCategory;
  private String categoryDescription;
  private String productDescription;
  private String type;
  private BigDecimal loanToValueRatio;
  private boolean hasCollateral;
  private boolean hasInsurance;

  public RestProduct()
  {
    // no needed to do something
  }

  public boolean isHasInsurance()
  {
    return hasInsurance;
  }

  public void setHasInsurance(boolean hasInsurance)
  {
    this.hasInsurance = hasInsurance;
  }

  public boolean isHasCollateral()
  {
    return hasCollateral;
  }

  public void setHasCollateral(boolean hasCollateral)
  {
    this.hasCollateral = hasCollateral;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getApplicationCategory()
  {
    return applicationCategory;
  }

  public void setApplicationCategory(String applicationCategory)
  {
    this.applicationCategory = applicationCategory;
  }

  public String getCategoryDescription()
  {
    return categoryDescription;
  }

  public void setCategoryDescription(String categoryDescription)
  {
    this.categoryDescription = categoryDescription;
  }

  public String getProductDescription()
  {
    return productDescription;
  }

  public void setProductDescription(String productDescription)
  {
    this.productDescription = productDescription;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public BigDecimal getLoanToValueRatio()
  {
    return loanToValueRatio;
  }

  public void setLoanToValueRatio(BigDecimal loanToValueRatio)
  {
    this.loanToValueRatio = loanToValueRatio;
  }
}
