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
public class GetUDFieldsByFnOutput
{
  private final Map<String, UDField> udFieldMap;

  public GetUDFieldsByFnOutput(Map<String, UDField> udFieldMap)
  {
    this.udFieldMap = udFieldMap;
  }

  public Map<String, UDField> getUdFieldMap()
  {
    return Collections.unmodifiableMap(udFieldMap);
  }
}
