/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.domain.ohs.xac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.property.PropertyId;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.model.property.PropertyProcess;
import mn.erin.domain.bpm.model.property.PropertyServiceId;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.PROPERTY_INFO_PROCESS_LIST;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_CERT_FIELD_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_CERT_FIELD_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.STRING_AS_EMPTY;

/**
 * @author Zorig
 */
public final class PropertyUtil
{
  private static final Logger LOG = LoggerFactory.getLogger(PropertyUtil.class);
  public static final String PROPERTY_INFO_OWNER_DATA_LIST = "ownerDataLlist";

  private PropertyUtil()
  {
  }

  public static JSONObject createRequestBodyForPropertyInfoService(Map<String, String> operatorInfo, Map<String, String> citizenInfo, String propertyID)
  {
    JSONObject requestBody = new JSONObject();

    JSONObject auth = new JSONObject();

    JSONObject citizen = new JSONObject();
    citizen.put("cert", citizenInfo.get("cert"));
    citizen.put("certFingerprint", citizenInfo.get("certFingerprint"));
    citizen.put("fingerprint", citizenInfo.get("fingerprint"));
    citizen.put("regnum", citizenInfo.get("regnum"));

    JSONObject operator = new JSONObject();
    operator.put("cert", operatorInfo.get("cert"));
    operator.put("certFingerprint", operatorInfo.get("certFingerprint"));
    operator.put("fingerprint", operatorInfo.get("fingerprint"));
    operator.put("regnum", operatorInfo.get("regnum"));

    auth.put("citizen", citizen);
    auth.put("operator", operator);

    requestBody.put("auth", auth);
    requestBody.put("propertyNumber", propertyID);
    requestBody.put("regnum", citizenInfo.get("regnum"));

    return requestBody;
  }

  public static PropertyInfo toPropertyInfo(JSONObject jsonResponse)
  {
    PropertyId propertyId = PropertyId.valueOf(getPropertyFieldOrBlank("propertyNumber", jsonResponse));
    String type = getPropertyFieldOrBlank("@type", jsonResponse);
    String address = getPropertyFieldOrBlank("address", jsonResponse);
    String addressApartmentName = getPropertyFieldOrBlank("addressApartmentName", jsonResponse);
    String addressDetail = getPropertyFieldOrBlank("addressDetail", jsonResponse);
    String addressRegionName = getPropertyFieldOrBlank("addressRegionName", jsonResponse);
    String addressStreetName = getPropertyFieldOrBlank("addressRegionName", jsonResponse);
    String aimagCityCode = getPropertyFieldOrBlank("aimagCityCode", jsonResponse);
    String aimagCityName = getPropertyFieldOrBlank("aimagCityName", jsonResponse);
    String bagKhorooCode = getPropertyFieldOrBlank("bagKhorooCode", jsonResponse);
    String bagKhorooName = getPropertyFieldOrBlank("bagKhorooName", jsonResponse);
    String intent = getPropertyFieldOrBlank("intent", jsonResponse);
    String soumDistrictCode = getPropertyFieldOrBlank("soumDistrictCode", jsonResponse);
    String soumDistrictName = getPropertyFieldOrBlank("soumDistrictName", jsonResponse);
    String squaredMetersArea = getPropertyFieldOrBlank("square", jsonResponse);

    List<PropertyProcess> propProcessList = new ArrayList<>();

    Object processListObj = jsonResponse.get(PROPERTY_INFO_PROCESS_LIST);

    if (null == processListObj)
    {
      return new PropertyInfo(propertyId, type, address, addressApartmentName, addressDetail, addressRegionName, addressStreetName, aimagCityCode,
          aimagCityName, bagKhorooCode, bagKhorooName, intent, soumDistrictCode, soumDistrictName, squaredMetersArea, propProcessList);
    }
    else if (processListObj instanceof JSONObject)
    {
      addPropertyProcess(propProcessList, (JSONObject) processListObj);

      return new PropertyInfo(propertyId, type, address, addressApartmentName, addressDetail, addressRegionName, addressStreetName, aimagCityCode,
          aimagCityName, bagKhorooCode, bagKhorooName, intent, soumDistrictCode, soumDistrictName, squaredMetersArea, propProcessList);
    }

    JSONArray processList = jsonResponse.getJSONArray(PROPERTY_INFO_PROCESS_LIST);

    for (int index = 0; index < processList.length(); index++)
    {
      JSONObject processJSON = (JSONObject) processList.get(index);
      addPropertyProcess(propProcessList, processJSON);
    }

    return new PropertyInfo(propertyId, type, address, addressApartmentName, addressDetail, addressRegionName, addressStreetName, aimagCityCode,
        aimagCityName, bagKhorooCode, bagKhorooName, intent, soumDistrictCode, soumDistrictName, squaredMetersArea, propProcessList);
  }

  private static List<PropertyProcess> addPropertyProcess(List<PropertyProcess> propProcessList, JSONObject propProcessJSON)
  {
    PropertyServiceId propertyServiceId = PropertyServiceId.valueOf(getPropertyFieldOrBlank("serviceID", propProcessJSON));
    String propertyServiceName = getPropertyFieldOrBlank("serviceName", propProcessJSON);

    String dateString = getPropertyFieldOrBlank("date", propProcessJSON);

    Date processDate = null;
    if (!StringUtils.isBlank(dateString))
    {
      try
      {
        processDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
      }
      catch (ParseException e)
      {
        LOG.error(e.getMessage());
      }
    }

    List<Person> ownerList = new ArrayList<>();

    Object propertyOwners =  propProcessJSON.has(PROPERTY_INFO_OWNER_DATA_LIST) ?  propProcessJSON.get(PROPERTY_INFO_OWNER_DATA_LIST) : null;

    if (null == propertyOwners)
    {
      PropertyProcess property = new PropertyProcess(propertyServiceId, propertyServiceName, processDate, ownerList);
      propProcessList.add(property);

      return propProcessList;
    }
    else if (propertyOwners instanceof JSONObject)
    {
      addOwnerList((JSONObject) propertyOwners, ownerList);
    }
    else
    {
      JSONArray ownerDataList = propProcessJSON.getJSONArray(PROPERTY_INFO_OWNER_DATA_LIST);

      for (int index = 0; index < ownerDataList.length(); index++)
      {
        JSONObject ownerJSON = (JSONObject) ownerDataList.get(index);

        addOwnerList(ownerJSON, ownerList);
      }
    }

    PropertyProcess property = new PropertyProcess(propertyServiceId, propertyServiceName, processDate, ownerList);
    propProcessList.add(property);

    return propProcessList;
  }

  public static void addOwnerList(JSONObject ownerJSON, List<Person> ownerList)
  {
    String firstName = getPropertyFieldOrBlank("firstname", ownerJSON);
    String lastName = getPropertyFieldOrBlank("lastname", ownerJSON);
    String registerNumber = getPropertyFieldOrBlank("registerNumber", ownerJSON);

    if (StringUtils.isBlank(registerNumber))
    {
      Person owner = new Person(PersonId.valueOf(STRING_AS_EMPTY));
      PersonInfo personInfo = new PersonInfo(firstName, lastName);
      owner.setPersonInfo(personInfo);

      ownerList.add(owner);
    }
    else
    {
      Person owner = new Person(PersonId.valueOf(registerNumber));
      PersonInfo personInfo = new PersonInfo(firstName, lastName);
      owner.setPersonInfo(personInfo);

      ownerList.add(owner);
    }
  }

  public static String getPropertyFieldOrBlank(String fieldName, JSONObject jsonResponse)
  {
    if (!jsonResponse.has(fieldName))
    {
      return BLANK;
    }
    else if (null != jsonResponse.get(fieldName) || jsonResponse.get(fieldName) != JSONObject.NULL)
    {
      return String.valueOf(jsonResponse.get(fieldName));
    }
    return BLANK;
  }

  public static void validatePropertyPersonInfo(Map<String, String> personInfo) throws BpmServiceException
  {
    if (personInfo.get("cert") == null)
    {
      throw new BpmServiceException(PROPERTY_INFO_CERT_FIELD_NULL_CODE, PROPERTY_INFO_CERT_FIELD_NULL_MESSAGE);
    }
    else if (personInfo.get("certFingerprint") == null)
    {
      throw new BpmServiceException(PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_CODE, PROPERTY_INFO_CERT_FINGERPRINT_FIELD_NULL_MESSAGE);
    }
    else if (StringUtils.isBlank(personInfo.get("regnum")))
    {
      throw new BpmServiceException(REGISTER_NUMBER_BLANK_CODE, REGISTER_NUMBER_BLANK_MESSAGE);
    }
  }
}
