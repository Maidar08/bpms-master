/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.model.property;

import java.util.Collections;
import java.util.List;

/**
 * @author Zorig
 */
public class PropertyInfo
{
  private final PropertyId propertyId;
  private final String type;
  private final String address;
  private final String addressApartmentName;
  private final String addressDetail;
  private final String addressRegionName;
  private final String addressStreetName;
  private final String aimagCityCode;
  private final String aimagCityName;
  private final String bagKhorooCode;
  private final String bagKhorooName;
  private final String intent;
  private final String soumDistrictCode;
  private final String soumDistrictName;
  private final String squaredMetersArea;

  private final List<PropertyProcess> propertyProcessList;

  public PropertyInfo(PropertyId propertyId, String type, String address, String addressApartmentName, String addressDetail, String addressRegionName,
      String addressStreetName, String aimagCityCode, String aimagCityName, String bagKhorooCode, String bagKhorooName, String intent,
      String soumDistrictCode, String soumDistrictName, String squaredMetersArea,
      List<PropertyProcess> propertyProcessList)
  {
    this.propertyId = propertyId;
    this.type = type;
    this.address = address;
    this.addressApartmentName = addressApartmentName;
    this.addressDetail = addressDetail;
    this.addressRegionName = addressRegionName;
    this.addressStreetName = addressStreetName;
    this.aimagCityCode = aimagCityCode;
    this.aimagCityName = aimagCityName;
    this.bagKhorooCode = bagKhorooCode;
    this.bagKhorooName = bagKhorooName;
    this.intent = intent;
    this.soumDistrictCode = soumDistrictCode;
    this.soumDistrictName = soumDistrictName;
    this.squaredMetersArea = squaredMetersArea;
    this.propertyProcessList = propertyProcessList;
  }

  public PropertyId getPropertyId()
  {
    return propertyId;
  }

  public String getType()
  {
    return type;
  }

  public String getAddress()
  {
    return address;
  }

  public String getAddressApartmentName()
  {
    return addressApartmentName;
  }

  public String getAddressDetail()
  {
    return addressDetail;
  }

  public String getAddressRegionName()
  {
    return addressRegionName;
  }

  public String getAddressStreetName()
  {
    return addressStreetName;
  }

  public String getAimagCityCode()
  {
    return aimagCityCode;
  }

  public String getAimagCityName()
  {
    return aimagCityName;
  }

  public String getBagKhorooCode()
  {
    return bagKhorooCode;
  }

  public String getBagKhorooName()
  {
    return bagKhorooName;
  }

  public String getIntent()
  {
    return intent;
  }

  public String getSoumDistrictCode()
  {
    return soumDistrictCode;
  }

  public String getSoumDistrictName()
  {
    return soumDistrictName;
  }

  public String getSquaredMetersArea()
  {
    return squaredMetersArea;
  }

  public List<PropertyProcess> getPropertyProcessList()
  {
    return Collections.unmodifiableList(propertyProcessList);
  }
}
