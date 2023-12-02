/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("ERIN_BPMS_PRODUCT")
public class JdbcProduct
{
  @Id
  String productID;
  String applicationCategory;
  String categoryDescription;
  String productDescription;
  String type;
  String loanToValueRatio;
  int hasCollateral;
  int hasInsurance;
  String rate;
  String frequency;

  public int getHasInsurance()
  {
    return hasInsurance;
  }

  public void setHasInsurance(int hasInsurance)
  {
    this.hasInsurance = hasInsurance;
  }

  public int getHasCollateral()
  {
    return hasCollateral;
  }

  public void setHasCollateral(int hasCollateral)
  {
    this.hasCollateral = hasCollateral;
  }

  public String getProductID()
  {
    return productID;
  }

  public void setProductID(String productID)
  {
    this.productID = productID;
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

  public String getLoanToValueRatio()
  {
    return loanToValueRatio;
  }

  public void setLoanToValueRatio(String loanToValueRatio)
  {
    this.loanToValueRatio = loanToValueRatio;
  }

  public String getRate()
  {
    return rate;
  }

  public void setRate(String rate)
  {
    this.rate = rate;
  }

  public String getFrequency()
  {
    return frequency;
  }

  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }
}
