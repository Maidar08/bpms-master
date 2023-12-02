package mn.erin.bpm.domain.ohs.xac.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.citizen.PassportInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.salary.Organization;
import mn.erin.domain.bpm.model.salary.OrganizationId;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;

import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.BUILD_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CERTIFICATE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLOR_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.FUEL_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MARK_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MODEL_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_FIRSTNAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_FROM_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_FULL_ADDRESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_LASTNAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_PHONE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_REGNUM;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.OWNER_TO_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.VEHICLE_CABIN_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.VEHICLE_IMPORT_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.VEHICLE_PLATE_NUMBER;
import static mn.erin.common.datetime.DateTimeUtils.SHORT_ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.STRING_AS_EMPTY;

/**
 * @author Tamir
 */
public final class XypServiceUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XypServiceUtil.class);

  private XypServiceUtil()
  {

  }

  public static final String EMPTY_STR = "";
  public static final String CERT_FINGERPRINT = "certFingerprint";
  public static final String CERT = "cert";

  public static final String FINGERPRINT = "fingerprint";
  public static final String REG_NUM = "regnum";

  public static final String CITIZEN = "citizen";
  public static final String OPERATOR = "operator";

  public static final String CIVIL_ID = "civilId";
  public static final String AUTH = "auth";

  public static final String START_YEAR = "syear";
  public static final String END_YEAR = "eyear";

  public static final String NATIONALITY = "nationality";
  public static final String PASSPORT_ADDRESS = "passportAddress";

  public static final String IMAGE = "image";
  public static final String PASSPORT_EXPIRE_DATE = "passportExpireDate";
  public static final String PASSPORT_ISSUE_DATE = "passportIssueDate";

  public static final String SALARY_DATA_LIST = "listData";
  public static final String IS_PAID = "paid";

  public static final String SALARY_AMOUNT = "salaryAmount";
  public static final String SALARY_FEE = "salaryFee";

  public static final String ORG_ID = "orgSiID";
  public static final String ORG_NAME = "orgName";
  public static final String DOM_NAME = "domName";

  public static final String YEAR = "year";
  public static final String MONTH = "month";

  public static final String CITY_NAME = "aimagCityName";
  public static final String DISTRICT_NAME = "soumDistrictName";

  public static final String QUARTER_NAME = "bagKhorooName";
  public static final String FULL_ADDRESS = "fullAddress";
  public static final String STREET_NAME = "addressStreetName";

  public static final String COMMA = ",";
  public static final String FIRST_NAME = "firstname";
  public static final String LAST_NAME = "lastname";

  public static final String SURNAME = "surname";
  public static final String BIRTH_DATE_AS_TEXT = "birthDateAsText";
  public static final String GENDER = "gender";

  public static AddressInfo getAddressInfoFromJson(JSONObject responseBody)
  {
    String city = responseBody.getString(CITY_NAME);

    String district = responseBody.getString(DISTRICT_NAME);
    String quarter = responseBody.getString(QUARTER_NAME);

    String fullAddress = responseBody.getString(FULL_ADDRESS);
    String residence = getResidenceWithGateNumber(fullAddress);

    AddressInfo addressInfo = new AddressInfo();

    addressInfo.setCity(city);
    addressInfo.setDistrict(district);

    addressInfo.setQuarter(quarter);
    addressInfo.setResidence(residence);

    return addressInfo;
  }

  public static String getResidenceWithGateNumber(String fullAddress)
  {
    String[] addressSeparated = fullAddress.split(COMMA);

    String residence = addressSeparated[addressSeparated.length - 1];

    if (!StringUtils.isBlank(residence))
    {
      return residence;
    }
    return "";
  }

  public static Customer getCustomerIDCardFromJson(JSONObject jsonResponse)
  {
    PersonInfo personInfo = gerPersonInfoFrom(jsonResponse);
    AddressInfo addressInfo = getAddressInfoFrom(jsonResponse);
    PassportInfo passportInfo = getPassportInfoFrom(jsonResponse);

    String nationality = getNotNullString(jsonResponse, NATIONALITY);

    String registerNumber = jsonResponse.getString(REG_NUM);

    Customer customer = new Customer(PersonId.valueOf(registerNumber));

    customer.setPersonInfo(personInfo);
    customer.setNationality(nationality);

    customer.setPassportInfo(passportInfo);
    customer.setAddressInfoList(Arrays.asList(addressInfo));

    return customer;
  }

  public static PassportInfo getPassportInfoFrom(JSONObject jsonResponse)
  {
    String passportAddress = getNotNullString(jsonResponse, PASSPORT_ADDRESS);
    String image = getNotNullString(jsonResponse, IMAGE);

    String passportExpireDate = getNotNullString(jsonResponse, PASSPORT_EXPIRE_DATE);
    String passportIssueDate = getNotNullString(jsonResponse, PASSPORT_ISSUE_DATE);

    LocalDate issueDate = toLocalDateFromText(passportIssueDate, "yyyy-MM-dd HH:mm:ss");
    LocalDate expiryDate = toLocalDateFromText(passportExpireDate, "yyyy/MM/dd");

    return new PassportInfo(passportAddress, image, issueDate, expiryDate);
  }

  public static AddressInfo getAddressInfoFrom(JSONObject jsonResponse)
  {
    String passportAddress = "";

    if (jsonResponse.has(PASSPORT_ADDRESS))
    {
      passportAddress = getNotNullString(jsonResponse, PASSPORT_ADDRESS);
    }

    AddressInfo addressInfo = new AddressInfo();

    addressInfo.setFullAddress(passportAddress);

    return addressInfo;
  }

  public static PersonInfo gerPersonInfoFrom(JSONObject jsonResponse)
  {

    String firstName = getNotNullString(jsonResponse, FIRST_NAME);
    String lastName = getNotNullString(jsonResponse, LAST_NAME);

    String familyName = getNotNullString(jsonResponse, SURNAME);
    String birthDateAsText = getNotNullString(jsonResponse, BIRTH_DATE_AS_TEXT);

    LocalDate birthDate = toLocalDateFromText(birthDateAsText, "yyyy-MM-dd HH:mm:ss");
    String gender = getNotNullString(jsonResponse, GENDER);

    PersonInfo personInfo = new PersonInfo();

    personInfo.setFirstName(firstName);
    personInfo.setLastName(lastName);
    personInfo.setGender(gender);

    personInfo.setFamilyName(familyName);
    personInfo.setBirthDate(birthDate);

    return personInfo;
  }

  public static VehicleInfo toVehicleInfo(JSONObject responseJSON)
  {
    if (null == responseJSON)
    {
      return null;
    }

    if (!responseJSON.has(VEHICLE_PLATE_NUMBER))
    {
      return null;
    }

    String plateNumber = getNotNullString(responseJSON, VEHICLE_PLATE_NUMBER);
    String markName = getNotNullString(responseJSON, MARK_NAME);

    String modelName = getNotNullString(responseJSON, MODEL_NAME);
    String cabinNumber = getNotNullString(responseJSON, VEHICLE_CABIN_NUMBER);
    String colorName = getNotNullString(responseJSON, COLOR_NAME);

    String certificateNumber = getNotNullString(responseJSON, CERTIFICATE_NUMBER);
    String fuelType = getNotNullString(responseJSON, FUEL_TYPE);

    String ownerLastName = getNotNullString(responseJSON, OWNER_LASTNAME);
    String ownerFirstname = getNotNullString(responseJSON, OWNER_FIRSTNAME);
    String ownerRegNumber = getNotNullString(responseJSON, OWNER_REGNUM);

    Person currentOwner = new Person(PersonId.valueOf(ownerRegNumber));
    currentOwner.setPersonInfo(new PersonInfo(ownerFirstname, ownerLastName));

    VehicleInfo vehicleInfo = new VehicleInfo();

    vehicleInfo.setPlateNumber(plateNumber);
    vehicleInfo.setCabinNumber(cabinNumber);

    vehicleInfo.setCertificateNumber(certificateNumber);
    vehicleInfo.setFuelType(fuelType);

    vehicleInfo.setMark(markName);
    vehicleInfo.setModel(modelName);
    vehicleInfo.setColor(colorName);

    vehicleInfo.setCurrentOwner(currentOwner);

    if (responseJSON.get(BUILD_YEAR) instanceof Integer)
    {
      int buildYear = responseJSON.getInt(BUILD_YEAR);
      vehicleInfo.setYearOfMade(buildYear);
    }
    else if (responseJSON.get(BUILD_YEAR) instanceof String)
    {
      String buildYearStr = responseJSON.getString(BUILD_YEAR);
      if (StringUtils.isBlank(buildYearStr))
      {
        buildYearStr = BLANK;
      }

      vehicleInfo.setYearOfMade(Integer.valueOf(buildYearStr));
    }

    if (responseJSON.get(VEHICLE_IMPORT_DATE) instanceof String)
    {
      String importDateStr = responseJSON.getString(VEHICLE_IMPORT_DATE);

      Date date = toSimpleISODate(importDateStr);

      if (null != date)
      {
        vehicleInfo.setImportedDate(date);
      }
    }
    else if (responseJSON.get(VEHICLE_IMPORT_DATE) instanceof Date)
    {
      Date importDate = (Date) responseJSON.get(VEHICLE_IMPORT_DATE);

      vehicleInfo.setImportedDate(importDate);
    }

    return vehicleInfo;
  }

  public static String getNotNullString(JSONObject responseJSON, String key)
  {
    if (responseJSON.has(key) && !responseJSON.isNull(key) && null != responseJSON.get(key))
    {
      return String.valueOf(responseJSON.get(key));
    }

    return BLANK;
  }

  public static List<VehicleOwner> toVehicleOwners(Object responseBody)
  {
    if (null == responseBody)
    {
      return Collections.emptyList();
    }

    if (responseBody instanceof JSONArray)
    {
      JSONArray responseArray = (JSONArray) responseBody;
      LOGGER.info("######## VEHICLE OWNERS JSON ARRAY = [{}]", responseArray.toString());
      return toVehicleOwnerList(responseArray);
    }

    JSONObject responseJSON = (JSONObject) responseBody;
    LOGGER.info("######## VEHICLE OWNERS JSON OBJECT = [{}]", responseJSON.toString());

    VehicleOwner vehicleOwner = toVehicleOwner(responseJSON);

    return Collections.singletonList(vehicleOwner);
  }

  public static List<VehicleOwner> toVehicleOwnerList(JSONArray responseArray)
  {
    if (null == responseArray || responseArray.length() == 0)
    {
      return Collections.emptyList();
    }

    List<VehicleOwner> owners = new ArrayList<>();

    for (int index = 0; index < responseArray.length(); index++)
    {
      JSONObject responseJSON = (JSONObject) responseArray.get(index);

      owners.add(toVehicleOwner(responseJSON));
    }

    return owners;
  }

  public static VehicleOwner toVehicleOwner(JSONObject responseJSON)
  {
    String ownerRegNumber = BLANK;
    String ownerFirstName = BLANK;
    String ownerLastName = BLANK;
    String fullAddress = BLANK;

    if (responseJSON.has(OWNER_REGNUM))
    {
      ownerRegNumber = String.valueOf(responseJSON.get(OWNER_REGNUM));
    }

    if (responseJSON.has(OWNER_FIRSTNAME))
    {
      ownerFirstName = String.valueOf(responseJSON.get(OWNER_FIRSTNAME));
    }

    if (responseJSON.has(OWNER_LASTNAME))
    {
      ownerLastName = String.valueOf(responseJSON.get(OWNER_LASTNAME));
    }

    if (responseJSON.has(OWNER_FULL_ADDRESS))
    {
      fullAddress = String.valueOf(responseJSON.get(OWNER_FULL_ADDRESS));
    }

    if (StringUtils.isBlank(ownerRegNumber))
    {
      ownerRegNumber = STRING_AS_EMPTY;
    }

    VehicleOwner vehicleOwner = new VehicleOwner(PersonId.valueOf(ownerRegNumber));
    PersonInfo personInfo = new PersonInfo(ownerFirstName, ownerLastName);
    AddressInfo addressInfo = new AddressInfo();

    addressInfo.setFullAddress(fullAddress);

    vehicleOwner.setPersonInfo(personInfo);
    vehicleOwner.setAddressInfoList(Collections.singletonList(addressInfo));

    if (responseJSON.has(OWNER_PHONE))
    {
      if (responseJSON.get(OWNER_PHONE) instanceof Integer)
      {
        int phoneNumber = responseJSON.getInt(OWNER_PHONE);
        ContactInfo contactInfo = new ContactInfo(String.valueOf(phoneNumber), BLANK);
        vehicleOwner.setContactInfoList(Collections.singletonList(contactInfo));
      }
      else if (responseJSON.get(OWNER_PHONE) instanceof String)
      {
        String phoneNumber = responseJSON.getString(OWNER_PHONE);
        ContactInfo contactInfo = new ContactInfo(String.valueOf(phoneNumber), BLANK);
        vehicleOwner.setContactInfoList(Collections.singletonList(contactInfo));
      }
      else
      {
        if (null != responseJSON.get(OWNER_PHONE))
        {
          ContactInfo contactInfo = new ContactInfo(String.valueOf(responseJSON.get(OWNER_PHONE)), BLANK);
          vehicleOwner.setContactInfoList(Collections.singletonList(contactInfo));
        }
      }
    }

    if (responseJSON.has(OWNER_FROM_DATE))
    {
      if (responseJSON.get(OWNER_FROM_DATE) instanceof Date)
      {
        Date fromDate = (Date) responseJSON.get(OWNER_FROM_DATE);

        vehicleOwner.setStartOwnerDate(fromDate);
      }
      else
      {
        String fromDateStr = String.valueOf(responseJSON.get(OWNER_FROM_DATE));
        Date fromDate = toSimpleISODate(fromDateStr);

        vehicleOwner.setStartOwnerDate(fromDate);
      }
    }

    if (responseJSON.has(OWNER_TO_DATE))
    {

      if (responseJSON.get(OWNER_TO_DATE) instanceof Date)
      {
        Date endOwnerDate = (Date) responseJSON.get(OWNER_TO_DATE);

        vehicleOwner.setEndOwnerDate(endOwnerDate);
      }
      else
      {
        String endOwnerDateStr = String.valueOf(responseJSON.get(OWNER_TO_DATE));
        Date endOwnerDate = toSimpleISODate(endOwnerDateStr);

        vehicleOwner.setEndOwnerDate(endOwnerDate);
      }
    }

    return vehicleOwner;
  }

  public static String toSimpleISODateStr(Date date)
  {
    DateFormat sdf = new SimpleDateFormat(SHORT_ISO_DATE_FORMAT);

    return sdf.format(date);
  }

  public static Date toSimpleISODate(String dateStr)
  {
    DateFormat sdf = new SimpleDateFormat(SHORT_ISO_DATE_FORMAT);

    try
    {
      return sdf.parse(dateStr);
    }
    catch (ParseException e)
    {
      LOGGER.error(e.getMessage());
      return null;
    }
  }

  public static List<SalaryInfo> getSalaryInfosFromJson(JSONObject responseBody)
  {
    List<SalaryInfo> salaryInfoList = new ArrayList<>();

    JSONArray salaryArray = (JSONArray) responseBody.get(SALARY_DATA_LIST);

    for (int index = 0; index < salaryArray.length(); index++)
    {
      JSONObject salaryJson = (JSONObject) salaryArray.get(index);

      double amount = salaryJson.getDouble(SALARY_AMOUNT);
      double salaryFee = salaryJson.getDouble(SALARY_FEE);

      Integer organizationId = salaryJson.getInt(ORG_ID);
      String organizationIdString = String.valueOf(organizationId);

      String organizationName = salaryJson.getString(ORG_NAME);
      String regionName = salaryJson.getString(DOM_NAME);

      Organization organization = new Organization(OrganizationId.valueOf(organizationIdString), organizationName, regionName);
      boolean isPaidSocialInsurance = salaryJson.getBoolean(IS_PAID);

      int year = salaryJson.getInt(YEAR);
      int month = salaryJson.getInt(MONTH);

      SalaryInfo salaryInfo = new SalaryInfo(BigDecimal.valueOf(amount), BigDecimal.valueOf(salaryFee),
          organization, isPaidSocialInsurance, year, month);
      salaryInfoList.add(salaryInfo);
    }
    return salaryInfoList;
  }

  public static JSONObject createRequestBodySalaryInfo(String regNumber, String employeeRegNumber,
      Integer month)
  {
    JSONObject responseBody = new JSONObject();
    LocalDate now = LocalDate.now();

    int currentYear = now.getYear();
    Integer starYear = getStartYear(currentYear, month);

    responseBody.put(REG_NUM, regNumber);
    responseBody.put(START_YEAR, starYear);
    responseBody.put(END_YEAR, currentYear);

    JSONObject authJson = new JSONObject();

    JSONObject citizenJson = getCitizen(regNumber);
    JSONObject operatorJson = getOperator(employeeRegNumber);

    authJson.put(CITIZEN, citizenJson);
    authJson.put(OPERATOR, operatorJson);

    responseBody.put(AUTH, authJson);

    return responseBody;
  }

  public static JSONObject createVehicleInfoRequest(String regNumber, String employeeRegNumber,
      String plateNumber)
  {
    JSONObject responseBody = new JSONObject();

    responseBody.put(REG_NUM, regNumber);
    responseBody.put(VEHICLE_PLATE_NUMBER, plateNumber);
    responseBody.put(VEHICLE_CABIN_NUMBER, EMPTY_STR);

    responseBody.put(AUTH, createAuthJson(regNumber, employeeRegNumber));

    return responseBody;
  }

  public static JSONObject createVehicleOwnerRequest(String regNumber, String employeeRegNumber,
      String plateNumber)
  {
    JSONObject responseBody = new JSONObject();

    responseBody.put(REG_NUM, regNumber);
    responseBody.put(VEHICLE_PLATE_NUMBER, plateNumber);
    responseBody.put(VEHICLE_CABIN_NUMBER, EMPTY_STR);

    responseBody.put(AUTH, createAuthJson(regNumber, employeeRegNumber));

    return responseBody;
  }

  private static JSONObject createAuthJson(String regNumber, String employeeRegNumber)
  {
    JSONObject authJson = new JSONObject();

    JSONObject citizenJson = getCitizen(regNumber);
    JSONObject operatorJson = getOperator(employeeRegNumber);

    authJson.put(CITIZEN, citizenJson);
    authJson.put(OPERATOR, operatorJson);

    return authJson;
  }

  public static JSONObject createRequestBody(String regNumber, String employeeRegNumber)
  {
    JSONObject responseBody = new JSONObject();

    responseBody.put(CIVIL_ID, "");
    responseBody.put(REG_NUM, regNumber);

    responseBody.put(AUTH, getAuth(regNumber, employeeRegNumber));

    return responseBody;
  }

  public static JSONObject getAuth(String regNumber, String employeeRegNumber)
  {
    JSONObject auth = new JSONObject();

    auth.put(CITIZEN, getCitizen(regNumber));
    auth.put(OPERATOR, getOperator(employeeRegNumber));

    return auth;
  }

  public static JSONObject getCitizen(String regNumber)
  {
    JSONObject citizen = new JSONObject();

    citizen.put(CERT, "");
    citizen.put(CERT_FINGERPRINT, "");

    citizen.put(REG_NUM, regNumber);
    citizen.put(FINGERPRINT, "");

    return citizen;
  }

  public static JSONObject getOperator(String employeeRegNumber)
  {
    JSONObject citizen = new JSONObject();

    citizen.put(CERT, "");
    citizen.put(CERT_FINGERPRINT, "");

    citizen.put(REG_NUM, employeeRegNumber);
    citizen.put(FINGERPRINT, "");

    return citizen;
  }

  private static Integer getStartYear(Integer currentYear, Integer month)
  {
    switch (month)
    {
    case 12:
      return currentYear - 1;
    case 24:
      return currentYear - 2;
    case 36:
      return currentYear - 3;
    default:
      return currentYear;
    }
  }

  private static LocalDate toLocalDateFromText(String asTextDate, String formatString)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(formatString);
    LocalDate localDate = null;
    try
    {
      Date date = formatter.parse(asTextDate);
      localDate = date.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
    catch (ParseException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
    return localDate;
  }
}
