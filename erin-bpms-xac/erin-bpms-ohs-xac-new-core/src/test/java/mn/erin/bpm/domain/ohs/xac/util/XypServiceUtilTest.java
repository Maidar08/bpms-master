/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.citizen.PassportInfo;
import mn.erin.domain.bpm.model.salary.Organization;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;

import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.BIRTH_DATE_AS_TEXT;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.CITY_NAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.DISTRICT_NAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.FIRST_NAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.GENDER;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.IMAGE;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.LAST_NAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.PASSPORT_ADDRESS;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.PASSPORT_EXPIRE_DATE;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.PASSPORT_ISSUE_DATE;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.QUARTER_NAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.SURNAME;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getPassportInfoFrom;

/**
 * @author Tamir
 */
public class XypServiceUtilTest
{
  private static final String UB = "Улаанбаатар";
  private static final String KHAN_UUL_DISTRICT = "Хан-Уул дүүрэг";

  private static final String QUARTER_15 = "15-р хороо";
  private static final String RESIDENCE_WITH_GATE_NUMBER = "10-р байр 10 тоот";

  private static final String EXAMPLE_FULL_ADDRESS = "Дундговь Сайхан-Овоо 1-р баг, Онги," + RESIDENCE_WITH_GATE_NUMBER;
  private static final String EXAMPLE_FULL_ADDRESS_1 = "Улаанбаатар Хан-Уул дүүрэг 15-р хороо," + RESIDENCE_WITH_GATE_NUMBER;
  private static final String EMPTY = "";
  private static final String REG_NUM = "regnum";
  private static final String CUSTOMER_REG_NUMBER = "r1";
  private static final String EMP_REG_NUM = "r2";

  private static final String VEHICLE_INFO_JSON_STR = "{\n"
      + "  \"@type\": \"ns2:VehicleData\",\n"
      + "  \"archiveFirstNumber\": \"ШЭБЗД19063452\",\n"
      + "  \"archiveNumber\": \"ШЭБЗД19063452\",\n"
      + "  \"axleCount\": 0,\n"
      + "  \"buildYear\": 2011,\n"
      + "  \"cabinNumber\": \"ZVW301403204\",\n"
      + "  \"capacity\": 1797,\n"
      + "  \"certificateNumber\": \"ТЯ2231820\",\n"
      + "  \"className\": \"B\",\n"
      + "  \"colorName\": \"Мөнгөлөг\",\n"
      + "  \"countryName\": \"Япон\",\n"
      + "  \"fueltype\": \"Бензин - Цахилгаан\",\n"
      + "  \"height\": 1490,\n"
      + "  \"importDate\": \"2019-06-18T00:00:00+08:00\",\n"
      + "  \"length\": 4480,\n"
      + "  \"manCount\": 5,\n"
      + "  \"markName\": \"TOYOTA\",\n"
      + "  \"mass\": 1350,\n"
      + "  \"modelName\": \"Prius\",\n"
      + "  \"ownerAddress\": {\n"
      + "    \"apartment\": 408,\n"
      + "    \"door\": 3,\n"
      + "    \"soum\": \"БЗД\",\n"
      + "    \"state\": \"УБ\"\n"
      + "  },\n"
      + "  \"ownerCountry\": \"Монгол\",\n"
      + "  \"ownerFirstname\": \"БОЛДЭРДЭНЭ\",\n"
      + "  \"ownerHandphone\": 95061188,\n"
      + "  \"ownerLastname\": \"Жаргалсайхан\",\n"
      + "  \"ownerRegnum\": \"ДС92082911\",\n"
      + "  \"ownerType\": \"Хувь хүн\",\n"
      + "  \"plateNumber\": \"6927УНБ\",\n"
      + "  \"type\": \"Олон зориулалттай\",\n"
      + "  \"typeId\": 0,\n"
      + "  \"weight\": 0,\n"
      + "  \"wheelPosition\": \"Баруун\",\n"
      + "  \"width\": 1745\n"
      + "}";

  private static final String OWNER_LIST_JSON_STR = "[\n"
      + "  {\n"
      + "    \"@type\": \"ns2:VehicleData\",\n"
      + "    \"cabinNumber\": \"c1\",\n"
      + "    \"fromDate\": \"2019-06-18T00:00:00+08:00\",\n"
      + "    \"fullAddress\": \"F -Address 1\",\n"
      + "    \"ownerFirstname\": \"John\",\n"
      + "    \"ownerLastname\": \"Will\",\n"
      + "    \"ownerRegnum\": \"R123465\",\n"
      + "    \"phone\": 12346587,\n"
      + "    \"plateNumber\": \"\",\n"
      + "    \"toDate\": \"2019-06-18T00:00:00+08:00\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"@type\": \"ns2:VehicleData\",\n"
      + "    \"cabinNumber\": \"c2\",\n"
      + "    \"fromDate\": \"2019-06-18T00:00:00+08:00\",\n"
      + "    \"fullAddress\": \"F -Address 2\",\n"
      + "    \"ownerFirstname\": \"Bat\",\n"
      + "    \"ownerLastname\": \"Bold\",\n"
      + "    \"ownerRegnum\": \"R12346589\",\n"
      + "    \"phone\": \"88970850\",\n"
      + "    \"plateNumber\": \"\",\n"
      + "    \"toDate\": \"2019-06-18T00:00:00+08:00\"\n"
      + "  }\n"
      + "]";

  private static final String OWNER_JSON = "{\n"
      + "        \"@type\": \"ns2:VehicleData\",\n"
      + "        \"cabinNumber\": \"c1\",\n"
      + "        \"fromDate\": \"2019-06-18T00:00:00+08:00\",\n"
      + "        \"fullAddress\": \"F -Address 1\",\n"
      + "        \"ownerFirstname\": \"John\",\n"
      + "        \"ownerLastname\": \"Will\",\n"
      + "        \"ownerRegnum\": \"R123465\",\n"
      + "        \"phone\": 12346587,\n"
      + "        \"plateNumber\": 0,\n"
      + "        \"toDate\": \"2021-04-27T00:00:00+08:00\"\n"
      + "      }";

  @Test
  public void verify_mapping_vehicle_info()
  {
    JSONObject vehicleJson = new JSONObject(VEHICLE_INFO_JSON_STR);
    VehicleInfo vehicleInfo = XypServiceUtil.toVehicleInfo(vehicleJson);

    Assert.assertEquals("6927УНБ", vehicleInfo.getPlateNumber());
    Assert.assertEquals("ZVW301403204", vehicleInfo.getCabinNumber());

    Assert.assertEquals("ТЯ2231820", vehicleInfo.getCertificateNumber());
    Assert.assertEquals("2011", vehicleInfo.getYearOfMade().toString());

    Assert.assertEquals("2019-06-18", XypServiceUtil.toSimpleISODateStr(vehicleInfo.getImportedDate()));
    Assert.assertEquals("Бензин - Цахилгаан", vehicleInfo.getFuelType());

    Assert.assertEquals("TOYOTA", vehicleInfo.getMark());
    Assert.assertEquals("Prius", vehicleInfo.getModel());

    Assert.assertEquals("Мөнгөлөг", vehicleInfo.getColor());
    Assert.assertEquals("ДС92082911", vehicleInfo.getCurrentOwner().getId().getId());

    Assert.assertEquals("БОЛДЭРДЭНЭ", vehicleInfo.getCurrentOwner().getPersonInfo().getFirstName());
    Assert.assertEquals("Жаргалсайхан", vehicleInfo.getCurrentOwner().getPersonInfo().getLastName());
  }

  @Test
  public void verify_mapping_owner_list()
  {
    JSONArray vehicleJsonArray = new JSONArray(OWNER_LIST_JSON_STR);
    List<VehicleOwner> owners = XypServiceUtil.toVehicleOwners(vehicleJsonArray);

    Assert.assertEquals(2, owners.size());
    Assert.assertEquals("R123465", owners.get(0).getId().getId());
  }

  @Test
  public void verify_mapping_owner()
  {
    JSONObject vehicleJson = new JSONObject(OWNER_JSON);
    VehicleOwner vehicleOwner = XypServiceUtil.toVehicleOwner(vehicleJson);

    Assert.assertEquals("R123465", vehicleOwner.getId().getId());
    Assert.assertEquals("2019-06-18", XypServiceUtil.toSimpleISODateStr(vehicleOwner.getStartOwnerDate()));
    Assert.assertEquals("2021-04-27", XypServiceUtil.toSimpleISODateStr(vehicleOwner.getEndOwnerDate()));

    Assert.assertEquals("John", vehicleOwner.getPersonInfo().getFirstName());
    Assert.assertEquals("Will", vehicleOwner.getPersonInfo().getLastName());

    Assert.assertEquals("12346587", vehicleOwner.getContactInfoList().get(0).getPhone().toString());
    Assert.assertEquals("F -Address 1", vehicleOwner.getAddressInfoList().get(0).getFullAddress());
  }

  @Test
  public void verify_no_salary_info()
  {
    JSONObject responseJson = new JSONObject();

    responseJson.put("listData", new JSONArray());

    Assert.assertEquals(0, XypServiceUtil.getSalaryInfosFromJson(responseJson).size());
  }

  @Test
  public void verify_salary_request_body()
  {
    JSONObject requestBody = XypServiceUtil.createRequestBodySalaryInfo(CUSTOMER_REG_NUMBER, EMP_REG_NUM, 12);

    Assert.assertEquals(CUSTOMER_REG_NUMBER, requestBody.getString(REG_NUM));

    JSONObject authJson = requestBody.getJSONObject("auth");

    JSONObject citizen = authJson.getJSONObject("citizen");
    JSONObject operator = authJson.getJSONObject("operator");

    Assert.assertEquals(CUSTOMER_REG_NUMBER, citizen.getString(REG_NUM));
    Assert.assertEquals(EMP_REG_NUM, operator.getString(REG_NUM));
  }

  @Test
  public void verify_get_passport_info()
  {
    JSONObject passportInfoJson = getPassportInfo();

    PassportInfo passportInfo = getPassportInfoFrom(passportInfoJson);

    Assert.assertNotNull(passportInfo);

    Assert.assertEquals(2019, passportInfo.getIssueDate().getYear());
    Assert.assertEquals(2022, passportInfo.getExpireDate().getYear());
  }

  @Test
  public void verify_get_person_info()
  {
    JSONObject personInfoJson = getPersonInfo("Бат", "Болд");

    PersonInfo personInfo = XypServiceUtil.gerPersonInfoFrom(personInfoJson);
    Assert.assertNotNull(personInfo);

    LocalDate birthDate = personInfo.getBirthDate();
    Assert.assertNotNull(birthDate);

    Assert.assertEquals(1995, birthDate.getYear());
    Assert.assertEquals(9, birthDate.getMonth().getValue());
    Assert.assertEquals(11, birthDate.getDayOfMonth());
  }

  @Test
  public void verify_salary_infos()
  {
    JSONArray salaryInfos = new JSONArray();

    salaryInfos.put(getSalaryInfo(2020, 1));
    salaryInfos.put(getSalaryInfo(2020, 1));

    JSONObject responseBody = new JSONObject();
    responseBody.put("listData", salaryInfos);

    List<SalaryInfo> customerSalaryInfos = XypServiceUtil.getSalaryInfosFromJson(responseBody);

    Assert.assertEquals(2, customerSalaryInfos.size());

    for (SalaryInfo salaryInfo : customerSalaryInfos)
    {
      Organization organization = salaryInfo.getOrganization();
      Assert.assertNotNull(organization);

      Assert.assertEquals("Bank", organization.getName());
      Assert.assertEquals("260020903", organization.getOrganizationId().getId());
      Assert.assertEquals("Сүхбаатар дүүрэг", organization.getRegionName());

      Assert.assertEquals(BigDecimal.valueOf(2100000.0), salaryInfo.getAmount());
      Assert.assertEquals(BigDecimal.valueOf(504000.0), salaryInfo.getSalaryFee());
      Assert.assertTrue(salaryInfo.isPaidSocialInsurance());
    }
  }

  @Test
  public void verify_get_address_info()
  {
    JSONObject addressJson = getAddressJson();
    AddressInfo addressInfo = XypServiceUtil.getAddressInfoFromJson(addressJson);

    Assert.assertNotNull(addressInfo);

    Assert.assertEquals(UB, addressInfo.getCity());
    Assert.assertEquals(KHAN_UUL_DISTRICT, addressInfo.getDistrict());

    Assert.assertEquals(QUARTER_15, addressInfo.getQuarter());
    Assert.assertEquals(RESIDENCE_WITH_GATE_NUMBER, addressInfo.getResidence());
  }

  @Test
  public void verify_get_residence_with_gate_number()
  {
    String residence = XypServiceUtil.getResidenceWithGateNumber(EXAMPLE_FULL_ADDRESS);
    Assert.assertEquals(RESIDENCE_WITH_GATE_NUMBER, residence);
  }

  @Test
  public void verify_empty_residence()
  {
    String fullAddress = "Дундговь Сайхан-Овоо 1-р баг, Онги, ";
    String residence = XypServiceUtil.getResidenceWithGateNumber(fullAddress);

    Assert.assertEquals(EMPTY, residence);
  }

  private JSONObject getAddressJson()
  {
    JSONObject addressJson = new JSONObject();

    addressJson.put(CITY_NAME, UB);
    addressJson.put(DISTRICT_NAME, KHAN_UUL_DISTRICT);
    addressJson.put(QUARTER_NAME, QUARTER_15);
    addressJson.put(XypServiceUtil.FULL_ADDRESS, EXAMPLE_FULL_ADDRESS_1);

    return addressJson;
  }

  private JSONObject getSalaryInfo(Integer year, Integer month)
  {
    JSONObject salaryInfo = new JSONObject();

    salaryInfo.put("year", year);
    salaryInfo.put("month", month);

    salaryInfo.put("domName", "Сүхбаатар дүүрэг");
    salaryInfo.put("orgName", "Bank");
    salaryInfo.put("orgSiID", 260020903);
    salaryInfo.put("paid", true);
    salaryInfo.put("salaryAmount", 2100000);
    salaryInfo.put("salaryFee", 504000);

    return salaryInfo;
  }

  private JSONObject getPersonInfo(String firstName, String lastName)
  {
    JSONObject personInfoJson = new JSONObject();

    personInfoJson.put(CITY_NAME, "Дундговь");
    personInfoJson.put(DISTRICT_NAME, "Сайхан-Овоо");

    personInfoJson.put(FIRST_NAME, firstName);
    personInfoJson.put(LAST_NAME, lastName);

    personInfoJson.put(SURNAME, "Боржгон");
    personInfoJson.put(BIRTH_DATE_AS_TEXT, "1995-09-11 00:00:00.0");
    personInfoJson.put(GENDER, "Эрэгтэй");

    return personInfoJson;
  }

  private JSONObject getPassportInfo()
  {
    JSONObject passportInfoJson = new JSONObject();

    passportInfoJson.put(PASSPORT_ADDRESS, "Full address");
    passportInfoJson.put(IMAGE, "base64String");

    passportInfoJson.put(PASSPORT_EXPIRE_DATE, "2022/09/11");
    passportInfoJson.put(PASSPORT_ISSUE_DATE, "2019-09-11 00:00:00.0");

    return passportInfoJson;
  }
}
