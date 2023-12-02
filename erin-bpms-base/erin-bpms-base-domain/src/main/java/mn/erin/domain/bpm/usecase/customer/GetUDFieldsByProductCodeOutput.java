/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.Collections;
import java.util.Map;

import mn.erin.domain.bpm.model.account.UDField;

/**
 * @author Zorig
 */
public class GetUDFieldsByProductCodeOutput
{
  private final Map<String, UDField> udFieldsMap;
  private String subTypeName;

  public GetUDFieldsByProductCodeOutput(Map<String, UDField> udFieldsMap)
  {
    this.udFieldsMap = udFieldsMap;
  }

  public GetUDFieldsByProductCodeOutput(Map<String, UDField> udFieldsMap, String subTypeName)
  {
    this.udFieldsMap = udFieldsMap;
    this.subTypeName = subTypeName;
  }

  public Map<String, UDField> getUdFieldsMap()
  {
    return Collections.unmodifiableMap(udFieldsMap);
  }

  public String getSubTypeName()
  {
    return subTypeName;
  }

  public void setSubTypeName(String subTypeName)
  {
    this.subTypeName = subTypeName;
  }
}
