package mn.erin.bpm.domain.ohs.xac.util;

import java.text.ParseException;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.model.property.PropertyInfo;

/**
 * @author Tamir
 */
public class PropertyUtilTest
{
  public static final String PROPERTY_RESPONSE_STR = "{\n"
      + "        \"@type\": \"ns2:PropertyData\",\n"
      + "        \"address\": \"Улаанбаатар Баянзүрх дүүрэг 6-р хороо 13-р хороолол /13373/ Энхтайваны өргөн чөлөө гудамж 8б байр, 40 тоот\",\n"
      + "        \"addressApartmentName\": \"8б\",\n"
      + "        \"addressDetail\": 40,\n"
      + "        \"addressRegionName\": \"13-р хороолол /13373/\",\n"
      + "        \"addressStreetName\": \"энхтайваны өргөн чөлөө\",\n"
      + "        \"aimagCityCode\": 11,\n"
      + "        \"aimagCityName\": \"Улаанбаатар\",\n"
      + "        \"bagKhorooCode\": 61,\n"
      + "        \"bagKhorooName\": \"6-р хороо\",\n"
      + "        \"intent\": \"Орон сууцны\",\n"
      + "        \"propertyNumber\": \"ү2204113645\",\n"
      + "        \"soumDistrictCode\": 10,\n"
      + "        \"soumDistrictName\": \"Баянзүрх\",\n"
      + "        \"square\": 73.64,\n"
      + "        \"processList\": [\n"
      + "            {\n"
      + "                \"date\": \"2021-03-16\",\n"
      + "                \"ownerDataLlist\": {\n"
      + "                    \"firstname\": \"отгондэлгэр\",\n"
      + "                    \"forename\": \"тайжнар\",\n"
      + "                    \"lastname\": \"доржготов\",\n"
      + "                    \"registerNumber\": \"аз85012904\"\n"
      + "                },\n"
      + "                \"serviceID\": 20210317143107,\n"
      + "                \"serviceName\": \"Шинээр баригдсан орон сууц өмчлөх эрхийг анх удаа бүртгэх\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"date\": \"2021-03-23\",\n"
      + "                \"ownerDataLlist\": {\n"
      + "                    \"firstname\": \"Хасбанк \"\n"
      + "                },\n"
      + "                \"serviceID\": 20210323142516,\n"
      + "                \"serviceName\": \"Барьцаалбартай барьцааны гэрээ\"\n"
      + "            }\n"
      + "        ]\n"
      + "    }";

  @Test
  public void verifyConvertToPropertyInfo() throws ParseException
  {
    JSONObject propertyInfoJson = new JSONObject(PROPERTY_RESPONSE_STR);

    PropertyInfo propertyInfo = PropertyUtil.toPropertyInfo(propertyInfoJson);

    Assert.assertNotNull(propertyInfo);

    Assert.assertEquals("ү2204113645", propertyInfo.getPropertyId().getId());
    Assert.assertEquals("Орон сууцны", propertyInfo.getIntent());

    Assert.assertEquals("73.64", propertyInfo.getSquaredMetersArea());
    Assert.assertEquals(2, propertyInfo.getPropertyProcessList().size());
  }
}
