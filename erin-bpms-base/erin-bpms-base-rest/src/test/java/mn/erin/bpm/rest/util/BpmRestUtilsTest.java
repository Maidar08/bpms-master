package mn.erin.bpm.rest.util;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.bpm.rest.model.RestCompletedForm;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;

import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.FORM_FIELDS;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.SPECIAL_FORMS;

/**
 * @author Tamir
 */
public class BpmRestUtilsTest
{
  private static final String SALARY_TABLE_SPECIAL_FORM_KEY = "SALARY_TABLE";
  private static final String COLLATERAL_TABLE_FORM_KEY = "COLLATERAL_TABLE";

  private static final String CASE_INSTANCE_ID = "2bb10c6b-02c8-11eb-852f-225bad546c87";
  private static final String TASK_ID = "task1";

  private static final String SALARY_JSON_STRING_VALUE = "SALARY_JSON_STRING";
  private static final String COLLATERAL_JSON_STRING_VALUE = "COLLATERAL_JSON_STRING";

  private String restCompletedFormString;

  @Before
  public void setUp()
  {
    restCompletedFormString = getRestCompletedFormString();
  }

  @Test
  public void verifyJsonToPojoObject() throws BpmInvalidArgumentException
  {
    RestCompletedForm restCompletedForm = BpmRestUtils.jsonToObject(restCompletedFormString, RestCompletedForm.class);

    Assert.assertEquals(TASK_ID, restCompletedForm.getTaskId());
    Assert.assertEquals(CASE_INSTANCE_ID, restCompletedForm.getCaseInstanceId());

    Map<String, Object> specialForms = restCompletedForm.getSpecialForms();

    Assert.assertEquals(2, restCompletedForm.getFormFields().size());
    Assert.assertEquals(2, specialForms.size());

    String salaryJsonString = (String) specialForms.get(SALARY_TABLE_SPECIAL_FORM_KEY);
    String collateralJsonString = (String) specialForms.get(COLLATERAL_TABLE_FORM_KEY);


    Assert.assertEquals(SALARY_JSON_STRING_VALUE, salaryJsonString);
    Assert.assertEquals(COLLATERAL_JSON_STRING_VALUE, collateralJsonString);

  }

  @Test
  public void verifyToCompletedFormJson() throws BpmInvalidArgumentException
  {
    RestCompletedForm completedForm = BpmRestUtils.jsonToObject(restCompletedFormString, RestCompletedForm.class);

    JSONObject jsonCompletedForm = BpmRestUtils.toCompletedFormJson(completedForm);

    Assert.assertEquals(TASK_ID, jsonCompletedForm.get(BpmModuleConstants.TASK_ID));
    Assert.assertEquals(CASE_INSTANCE_ID, jsonCompletedForm.get(BpmModuleConstants.CASE_INSTANCE_ID));

    JSONArray formFields = (JSONArray) jsonCompletedForm.get(FORM_FIELDS);
    JSONObject specialForms = (JSONObject) jsonCompletedForm.get(SPECIAL_FORMS);

    Assert.assertEquals(2, formFields.length());
    Assert.assertEquals(2, specialForms.length());

    String salaryJsonString = (String) specialForms.get(SALARY_TABLE_SPECIAL_FORM_KEY);
    String collateralJsonString = (String) specialForms.get(COLLATERAL_TABLE_FORM_KEY);

    Assert.assertEquals(SALARY_JSON_STRING_VALUE, salaryJsonString);
    Assert.assertEquals(COLLATERAL_JSON_STRING_VALUE, collateralJsonString);
  }

  private String getRestCompletedFormString()
  {
    return "{\n"
        + "    \"taskId\" : \"task1\",\n"
        + "    \"caseInstanceId\": \"2bb10c6b-02c8-11eb-852f-225bad546c87\",\n"
        + "    \"formFields\" : [\n"
        + "        {\n"
        + "          \"id\": \"firstName\",\n"
        + "          \"formFieldValue\": {\n"
        + "              \"defaultValue\": \"Hello\",\n"
        + "              \"valueInfo\": null\n"
        + "          },\n"
        + "          \"label\" : \"Өөрчлөлт\",\n"
        + "          \"type\" : \"String\",\n"
        + "          \"context\" : \"USER_INFO\",\n"
        + "          \"disabled\" : false,\n"
        + "          \"required\" : true,\n"
        + "          \"columnIndex\" : 1,\n"
        + "          \"validations\" : [\n"
        + "              {\n"
        + "                  \"name\": \"required\",\n"
        + "                  \"configuration\": \"yes\"\n"
        + "              }\n"
        + "          ],\n"
        + "          \"options\" : [\n"
        + "              {\n"
        + "                  \"id\": \"firstValue\",\n"
        + "                  \"value\": \"First\"\n"
        + "              },\n"
        + "                {\n"
        + "                  \"id\": \"secondValue\",\n"
        + "                  \"value\": \"Second\"\n"
        + "              }\n"
        + "          ]\n"
        + "        },\n"
        + "        {\n"
        + "        \"id\": \"lastName\",\n"
        + "        \"formFieldValue\": {\n"
        + "              \"defaultValue\": \"World\",\n"
        + "              \"valueInfo\": null\n"
        + "          },\n"
        + "           \"label\" : \"Овог\",\n"
        + "          \"type\" : \"String\",\n"
        + "          \"context\" : \"USER_INFO\",\n"
        + "          \"disabled\" : false,\n"
        + "          \"required\" : true,\n"
        + "          \"columnIndex\" : 2,\n"
        + "          \"validations\" : [\n"
        + "              {\n"
        + "                  \"name\": \"required\",\n"
        + "                  \"configuration\": \"yes\"\n"
        + "              }\n"
        + "          ],\n"
        + "         \"options\" : [\n"
        + "              {\n"
        + "                  \"id\": \"thirdValue\",\n"
        + "                  \"value\": \"Third\"\n"
        + "              },\n"
        + "                {\n"
        + "                  \"id\": \"fourthValue\",\n"
        + "                  \"value\": \"Four\"\n"
        + "              }\n"
        + "          ]\n"
        + "        }\n"
        + "    ],\n"
        + "    \"specialForms\": {\n"
        + "        \"SALARY_TABLE\": \"SALARY_JSON_STRING\",\n"
        + "        \"COLLATERAL_TABLE\": \"COLLATERAL_JSON_STRING\"\n"
        + "    }\n"
        + "}";
  }
}
