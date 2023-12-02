/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.bpm.model.account.UDField;

/**
 * @author Zorig
 */
public class CreateUDFieldsRequestBody extends AbstractUseCase<CreateUDFieldsRequestBodyInput, CreateUDFieldsRequestBodyOutput>
{
  private static final String UD_NUMBER_NAME_KEY = "FLD_NAME_NUM";
  private static final String UD_NUMBER_VALUE_KEY = "FLD_VAL_NUM";
  private static final String UD_TEXT_NAME_KEY = "FLD_NAME_CHAR";
  private static final String UD_TEXT_VALUE_KEY = "FLD_VAL_CHAR";
  private static final String UD_DATE_NAME_KEY = "FLD_NAME_DAT";
  private static final String UD_DATE_VALUE_KEY = "FLD_VAL_DAT";

  @Override
  public CreateUDFieldsRequestBodyOutput execute(CreateUDFieldsRequestBodyInput input)
  {
    Map<String, String> fields = input.getFields();
    Map<String, UDField> udFieldsMap = input.getUdFieldsMap();

    Map<String, UDField> organizedTextUDFields = organizeUDFieldsByFieldTypeAndOrder(udFieldsMap, "T");
    Map<String, UDField> organizedNumberUDFields = organizeUDFieldsByFieldTypeAndOrder(udFieldsMap, "N");
    Map<String, UDField> organizedDateUDFields = organizeUDFieldsByFieldTypeAndOrder(udFieldsMap, "D");

    JSONArray textUDFieldsJson = getUDFieldsRequestBody(UD_TEXT_NAME_KEY, UD_TEXT_VALUE_KEY, fields, organizedTextUDFields);

    JSONArray numberUDFieldsJson = getUDFieldsRequestBody(UD_NUMBER_NAME_KEY, UD_NUMBER_VALUE_KEY, fields, organizedNumberUDFields);

    JSONArray dateUDFieldsJson = getUDFieldsRequestBody(UD_DATE_NAME_KEY, UD_DATE_VALUE_KEY, fields, organizedDateUDFields);

    return new CreateUDFieldsRequestBodyOutput(numberUDFieldsJson, textUDFieldsJson, dateUDFieldsJson);
  }

  private JSONArray getUDFieldsRequestBody(String udFieldNameKey, String udFieldValueKey, Map<String, String> fields,
      Map<String, UDField> organizedUdFields)
  {
    JSONArray udFieldsArray = new JSONArray();

    JSONObject nullUDField = new JSONObject();
    nullUDField.put(udFieldValueKey, JSONObject.NULL);
    nullUDField.put(udFieldNameKey, "NULL");

    boolean flag = true;
    int counter = 1;

    while (flag)
    {
      UDField udField = organizedUdFields.get(String.valueOf(counter));

      if (udField == null)
      {
        flag = false;
        continue;
      }

      JSONObject currentUDFieldToInsert = new JSONObject();
      String fieldId = udField.getId().getId();

      //if there is data with key fieldId, create UDField with that info, then insert in UD field json array.
      if (fields.get(fieldId) != null && !StringUtils.isBlank(fields.get(fieldId)))
      {
        currentUDFieldToInsert.put(udFieldValueKey, fields.get(fieldId));
        currentUDFieldToInsert.put(udFieldNameKey, fieldId);
        udFieldsArray.put(currentUDFieldToInsert);
        counter++;
      }
      else
      {
        //if there is no data with key field id,
        if (!StringUtils.isBlank(udField.getDefaultValue()))
        {
          //if default value for udf field exists, insert default value.
          currentUDFieldToInsert.put(udFieldValueKey, udField.getDefaultValue());
          currentUDFieldToInsert.put(udFieldNameKey, fieldId);
          udFieldsArray.put(currentUDFieldToInsert);
        }
        else
        {
          //if no default value for udf field
          //insert placeholder into udFieldsArray
          udFieldsArray.put(nullUDField);
        }
        counter++;
      }
    }
    return udFieldsArray;
  }


  private Map<String, UDField> organizeUDFieldsByFieldTypeAndOrder(Map<String, UDField> udFieldsMap, String fieldTypeFilter)
  {
    Map<String, UDField> organizedUDFieldsByType = new HashMap<>();

    for (Map.Entry<String, UDField> udFieldEntry : udFieldsMap.entrySet())
    {
      UDField udField = udFieldEntry.getValue();

      String fieldType = udField.getFieldType();
      String orderNumber = udField.getFieldNumber();

      if (fieldType.equals(fieldTypeFilter))
      {
        organizedUDFieldsByType.put(orderNumber, udField);
      }
    }

    return organizedUDFieldsByType;
  }
}
