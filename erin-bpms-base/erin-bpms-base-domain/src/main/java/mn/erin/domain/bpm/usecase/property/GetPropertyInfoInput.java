/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.property;

import java.util.Collections;
import java.util.Map;

/**
 * @author Zorig
 */
public class GetPropertyInfoInput
{
  private final Map<String, String> operatorInfo;
  private final Map<String, String> citizenInfo;
  private final String propertyId;

  public GetPropertyInfoInput(Map<String, String> operatorInfo, Map<String, String> citizenInfo, String propertyId)
  {
    this.operatorInfo = operatorInfo;
    this.citizenInfo = citizenInfo;
    this.propertyId = propertyId;
  }

  public Map<String, String> getOperatorInfo()
  {
    return Collections.unmodifiableMap(operatorInfo);
  }

  public Map<String, String> getCitizenInfo()
  {
    return Collections.unmodifiableMap(citizenInfo);
  }

  public String getPropertyId()
  {
    return propertyId;
  }
}
