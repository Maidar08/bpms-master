package mn.erin.domain.bpm.usecase.util;

import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static mn.erin.domain.bpm.util.process.BpmUtils.convertClobToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertJsonStringToMap;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertMapToJsonString;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertStringToDate;

/**
 * @author Bilguunbor
 */
public class BpmUtilsTest
{
  private final static String jsonString = "\n"
      + "{\n"
      + "\t\"state\":\"NEW\",\n"
      + "\t\"channel\":\"BPMS APP\",\n"
      + "\t\"userId\": \"EbanktoBpms\",\n"
      + "\t\n"
      + "\t\"registerNumber\":\"УУ95082714\",\n"
      + "\t\"cifNumber\": \"99887766\",\n"
      + "\t\n"
      + "\t\"phoneNumber\":\"99111518\",\n"
      + "\t\"email\": \"batbold@gmail.com\",\n"
      + "\t\n"
      + "\t\"branchNumber\": \"108\",\n"
      + "\t\"productCategory\":\"consumptionLoan\",\n"
      + "\t\n"
      + "\t\"incomeType\":\"Цалингын зээл\",\n"
      + "    \"incomeBeforeTax\": 150000,\n"
      + "    \n"
      + "    \"amount\":5000000,\n"
      + "\t\"term\": 30,\n"
      + "\t\n"
      + "\t\"monthlyRepayment\": 500000,\n"
      + "\t\"repaymentType\": \"test\",\n"
      + "\n"
      + "\t\"purpose\": \"test\",\n"
      + "\t\"firstPaymentDate\": \"1584325706828\",\n"
      + "\n"
      + "\t\"annualInterestRate\":0.8,\n"
      + "\t\"hasMortgage\": \"true\",\n"
      + "\t\"fullName\": \"Ebanknaas ilgeev 2020.06.30\"\n"
      + "}";

  @Test
  public void object_map_to_json_string() throws JsonProcessingException
  {
    String jsonString = convertMapToJsonString(generateMap());

    JSONObject testJson = new JSONObject(jsonString);
    Assert.assertEquals("Bilguun", testJson.getString("firstName"));
    Assert.assertEquals(21, testJson.getInt("age"));
  }

  @Test
  public void verify_string_to_map()
  {
    Map map = convertJsonStringToMap(jsonString);
    Assert.assertEquals(20, map.size());
  }

  @Test(expected = JsonSyntaxException.class)
  public void verify_string_map()
  {
    String randomString = "123123dsad";
    convertJsonStringToMap(randomString);
  }

  @Test(expected = JsonProcessingException.class)
  public void json_string_to_object_map() throws JsonProcessingException
  {
    convertMapToJsonString(generateInvalid());
  }

  @Test
  public void verify_clob_test() throws SQLException
  {
    String stringData = RandomStringUtils.random(64, true, false);
    Clob myClob = new javax.sql.rowset.serial.SerialClob(stringData.toCharArray());

    String stringClob = convertClobToString(myClob);

    Assert.assertNotNull(stringClob);
  }

  @Test
  public void convert_string_to_date() throws ParseException
  {
    String dateString = "2021-08-02 14:58:00.000000";
    Assert.assertNotNull(convertStringToDate("yyyy-MM-dd", dateString));
  }

  private Map<String, Object> generateMap()
  {
    Map<String, Object> objectMap = new HashMap<>();

    objectMap.put("firstName", "Bilguun");
    objectMap.put("age", 21);

    return objectMap;
  }

  private Map<String, Object> generateInvalid()
  {
    Map<String, Object> objectMap = new HashMap<>();

    Object object = new Object();
    objectMap.put("firstName", object);
    objectMap.put("age", object);

    return objectMap;
  }
}
