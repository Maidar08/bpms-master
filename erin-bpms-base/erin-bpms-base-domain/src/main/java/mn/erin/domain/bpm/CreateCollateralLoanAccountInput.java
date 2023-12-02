/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm;

import java.util.List;
import java.util.Map;

/**
 * @author Zorig
 */
public class CreateCollateralLoanAccountInput
{
  private final String productCode;
  private final Map<String, Object> accountCreationInformation;
  private final List<Map<String, String>> coBorrowers;
  private final Map<String, Map<String, Object>> collaterals;

  public CreateCollateralLoanAccountInput(String productCode, Map<String, Object> accountCreationInformation,
      List<Map<String, String>> coBorrowers, Map<String, Map<String, Object>> collaterals)
  {
    this.productCode = productCode;
    this.accountCreationInformation = accountCreationInformation;
    this.coBorrowers = coBorrowers;
    this.collaterals = collaterals;
  }

  public String getProductCode()
  {
    return productCode;
  }

  public Map<String, Object> getAccountCreationInformation()
  {
    return accountCreationInformation;
  }

  public List<Map<String, String>> getCoBorrowers()
  {
    return coBorrowers;
  }

  public Map<String, Map<String, Object>> getCollaterals()
  {
    return collaterals;
  }
}
