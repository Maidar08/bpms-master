/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Map;

import mn.erin.domain.bpm.model.account.UDField;

/**
 * @author Zorig
 */
public class CreateUDFieldsRequestBodyInput
{
  private final String productCode;
  private final Map<String, String> fields;
  private final Map<String, UDField> udFieldsMap;

  public CreateUDFieldsRequestBodyInput(String productCode, Map<String, String> fields,
      Map<String, UDField> udFieldsMap)
  {
    this.productCode = productCode;
    this.fields = fields;
    this.udFieldsMap = udFieldsMap;
  }

  public String getProductCode()
  {
    return productCode;
  }

  public Map<String, String> getFields()
  {
    return fields;
  }

  public Map<String, UDField> getUdFieldsMap()
  {
    return udFieldsMap;
  }
}
