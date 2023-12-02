/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

/**
 * @author Zorig
 */
public class UniqueProductInput
{
  private final String productId;
  private final String applicationCategory;
  private String borrowerType;

  public UniqueProductInput(String productId, String applicationCategory)
  {
    this.productId = productId;
    this.applicationCategory = applicationCategory;
  }

  public String getProductId()
  {
    return productId;
  }

  public String getApplicationCategory()
  {
    return applicationCategory;
  }

  public String getBorrowerType()
  {
    return borrowerType;
  }

  public void setBorrowerType(String borrowerType)
  {
    this.borrowerType = borrowerType;
  }
}
