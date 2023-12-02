package mn.erin.domain.bpm.usecase.customer;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldId;

/**
 * @author Bilguunbor
 */
public class CreateUDFieldsRequestBodyTest
{
  private static final String PROD_CODE = "prodCode";
  private static final String UD_NUMBER_NAME_KEY = "FLD_NAME_NUM";
  private static final String UD_NUMBER_VALUE_KEY = "FLD_VAL_NUM";
  private static final String UD_TEXT_NAME_KEY = "FLD_NAME_CHAR";
  private static final String UD_TEXT_VALUE_KEY = "FLD_VAL_CHAR";
  private static final String UD_DATE_NAME_KEY = "FLD_NAME_DAT";
  private static final String UD_DATE_VALUE_KEY = "FLD_VAL_DAT";

  private CreateUDFieldsRequestBody useCase;
  private CreateUDFieldsRequestBodyInput input;

  @Before
  public void setUp()
  {
    useCase = new CreateUDFieldsRequestBody();
    input = new CreateUDFieldsRequestBodyInput(PROD_CODE, generateFieldMap(), generateUDFieldMap());
  }

  @Test
  public void when_ud_field_is_null()
  {
    JSONArray jsonArray = new JSONArray();
    JSONArray jsonArray1 = new JSONArray();
    JSONArray jsonArray2 = new JSONArray();

    CreateUDFieldsRequestBodyOutput output = useCase
        .execute(new CreateUDFieldsRequestBodyInput(PROD_CODE, generateFieldMap(), generateUDFieldMapWithRandomFieldNumber()));
    CreateUDFieldsRequestBodyOutput outputGenerated = new CreateUDFieldsRequestBodyOutput(jsonArray, jsonArray1, jsonArray2);

    Assert.assertTrue(output.getNumberUdFields().similar(outputGenerated.getNumberUdFields()));
    Assert.assertTrue(output.getTextUdFields().similar(outputGenerated.getTextUdFields()));
    Assert.assertTrue(output.getDateUdFields().similar(outputGenerated.getDateUdFields()));
  }

  @Test
  public void when_works_correctly()
  {
    CreateUDFieldsRequestBodyOutput output1 = generateOutput();
    CreateUDFieldsRequestBodyOutput output = useCase.execute(input);

    Assert.assertEquals(output1.getTextUdFields().get(0).toString(), output.getTextUdFields().get(0).toString());
    Assert.assertEquals(output1.getNumberUdFields().get(0).toString(), output.getNumberUdFields().get(0).toString());
    Assert.assertEquals(output1.getDateUdFields().get(0).toString(), output.getDateUdFields().get(0).toString());
  }

  @Test
  public void when_string_map_field_id_is_null()
  {
    CreateUDFieldsRequestBodyOutput output1 = generateDefaultValueOutput();
    CreateUDFieldsRequestBodyOutput output = useCase.execute(new CreateUDFieldsRequestBodyInput(PROD_CODE, generateFieldMapWithNoId(), generateUDFieldMap()));

    Assert.assertEquals(output1.getTextUdFields().get(0).toString(), output.getTextUdFields().get(0).toString());
    Assert.assertEquals(output1.getNumberUdFields().get(0).toString(), output.getNumberUdFields().get(0).toString());
    Assert.assertEquals(output1.getDateUdFields().get(0).toString(), output.getDateUdFields().get(0).toString());
  }

  @Test
  public void when_default_value_is_null()
  {
    CreateUDFieldsRequestBodyOutput output1 = generateNullOutput();
    CreateUDFieldsRequestBodyOutput output = useCase
        .execute(new CreateUDFieldsRequestBodyInput(PROD_CODE, generateFieldMapWithNoId(), generateUDFieldMapWithNoDefaultValue()));

    Assert.assertTrue(output1.getNumberUdFields().similar(output.getNumberUdFields()));
    Assert.assertTrue(output1.getTextUdFields().similar(output.getTextUdFields()));
    Assert.assertTrue(output1.getDateUdFields().similar(output.getDateUdFields()));
  }

  private Map<String, String> generateFieldMap()
  {
    Map<String, String> fieldMap = new HashMap<>();
    fieldMap.put("123", "123");

    return fieldMap;
  }

  private Map<String, String> generateFieldMapWithNoId()
  {
    Map<String, String> fieldMap = new HashMap<>();
    fieldMap.put(" ", " ");

    return fieldMap;
  }

  private Map<String, UDField> generateUDFieldMap()
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    UDField udField = new UDField(UDFieldId.valueOf("123"), "123", "T", "1", true, "123", "defaultValue", true, true);
    UDField udField1 = new UDField(UDFieldId.valueOf("456"), "123", "N", "1", true, "123", "defaultValue1", true, true);
    UDField udField2 = new UDField(UDFieldId.valueOf("789"), "123", "D", "1", true, "123", "defaultValue2", true, true);

    udFieldMap.put("1", udField);
    udFieldMap.put("3", udField1);
    udFieldMap.put("6", udField2);
    return udFieldMap;
  }

  private Map<String, UDField> generateUDFieldMapWithRandomFieldNumber()
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    UDField udField = new UDField(UDFieldId.valueOf("123"), "123", "T", "4", true, "123", "defaultValue", true, true);
    UDField udField1 = new UDField(UDFieldId.valueOf("456"), "123", "N", "5", true, "123", "defaultValue1", true, true);
    UDField udField2 = new UDField(UDFieldId.valueOf("789"), "123", "D", "6", true, "123", "defaultValue2", true, true);

    udFieldMap.put("1", udField);
    udFieldMap.put("3", udField1);
    udFieldMap.put("6", udField2);
    return udFieldMap;
  }

  private Map<String, UDField> generateUDFieldMapWithNoDefaultValue()
  {
    Map<String, UDField> udFieldMap = new HashMap<>();
    UDField udField = new UDField(UDFieldId.valueOf("123"), "123", "T", "1", true, "123", " ", true, true);
    UDField udField1 = new UDField(UDFieldId.valueOf("456"), "123", "N", "1", true, "123", " ", true, true);
    UDField udField2 = new UDField(UDFieldId.valueOf("789"), "123", "D", "1", true, "123", " ", true, true);

    udFieldMap.put("1", udField);
    udFieldMap.put("3", udField1);
    udFieldMap.put("6", udField2);
    return udFieldMap;
  }

  private CreateUDFieldsRequestBodyOutput generateOutput()
  {
    JSONObject object = new JSONObject();
    JSONObject object1 = new JSONObject();
    JSONObject object2 = new JSONObject();

    JSONArray udFieldsArray = new JSONArray();
    JSONArray udFieldsArray1 = new JSONArray();
    JSONArray udFieldsArray2 = new JSONArray();

    object.put(UD_NUMBER_NAME_KEY, "456");
    object.put(UD_NUMBER_VALUE_KEY, "defaultValue1");
    udFieldsArray.put(object);

    object1.put(UD_TEXT_VALUE_KEY, "123");
    object1.put(UD_TEXT_NAME_KEY, "123");
    udFieldsArray1.put(object1);

    object2.put(UD_DATE_VALUE_KEY, "defaultValue2");
    object2.put(UD_DATE_NAME_KEY, "789");
    udFieldsArray2.put(object2);

    return new CreateUDFieldsRequestBodyOutput(udFieldsArray, udFieldsArray1, udFieldsArray2);
  }

  private CreateUDFieldsRequestBodyOutput generateNullOutput()
  {
    JSONObject object = new JSONObject();
    JSONObject object1 = new JSONObject();
    JSONObject object2 = new JSONObject();

    JSONArray jsonArray = new JSONArray();
    JSONArray jsonArray1 = new JSONArray();
    JSONArray jsonArray2 = new JSONArray();

    object.put(UD_NUMBER_VALUE_KEY, JSONObject.NULL);
    object.put(UD_NUMBER_NAME_KEY, "NULL");
    jsonArray.put(object);

    object1.put(UD_TEXT_VALUE_KEY, JSONObject.NULL);
    object1.put(UD_TEXT_NAME_KEY, "NULL");
    jsonArray1.put(object1);

    object2.put(UD_DATE_VALUE_KEY, JSONObject.NULL);
    object2.put(UD_DATE_NAME_KEY, "NULL");
    jsonArray2.put(object2);

    return new CreateUDFieldsRequestBodyOutput(jsonArray, jsonArray1, jsonArray2);
  }

  private CreateUDFieldsRequestBodyOutput generateDefaultValueOutput()
  {
    JSONObject object = new JSONObject();
    JSONObject object1 = new JSONObject();
    JSONObject object2 = new JSONObject();

    JSONArray jsonArray = new JSONArray();
    JSONArray jsonArray1 = new JSONArray();
    JSONArray jsonArray2 = new JSONArray();

    object.put(UD_NUMBER_VALUE_KEY, "defaultValue1");
    object.put(UD_NUMBER_NAME_KEY, "456");
    jsonArray.put(object);

    object1.put(UD_TEXT_VALUE_KEY, "defaultValue");
    object1.put(UD_TEXT_NAME_KEY, "123");
    jsonArray1.put(object1);

    object2.put(UD_DATE_VALUE_KEY, "defaultValue2");
    object2.put(UD_DATE_NAME_KEY, "789");
    jsonArray2.put(object2);

    return new CreateUDFieldsRequestBodyOutput(jsonArray, jsonArray1, jsonArray2);
  }
}