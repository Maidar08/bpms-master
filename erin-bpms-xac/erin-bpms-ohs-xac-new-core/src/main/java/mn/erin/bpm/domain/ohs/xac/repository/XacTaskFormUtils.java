/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskFormField;

/**
 * Responsible utils for XAC task form.
 *
 * @author Tamir
 */
public final class XacTaskFormUtils
{
  private XacTaskFormUtils()
  {

  }

  public static Collection<TaskFormField> toTaskFormFields(JSONArray fieldArray)
  {
    Collection<TaskFormField> taskFormFields = new ArrayList<>();

    for (int index = 0; index < fieldArray.length(); index++)
    {
      JSONObject fieldJson = (JSONObject) fieldArray.get(index);
      taskFormFields.add(toTaskFormField(fieldJson));
    }
    return taskFormFields;
  }

  public static TaskFormField toTaskFormField(JSONObject fieldJson)
  {
    String fieldId = fieldJson.getString("id");
    String label = fieldJson.getString("label");
    String type = fieldJson.getString("type");

    List<FieldValidation> validations = getValidations(fieldJson);
    List<FieldProperty> properties = getProperties(fieldJson);

    TaskFormField formField = new TaskFormField(FormFieldId.valueOf(fieldId), new FormFieldValue(), label, type);

    formField.setFieldValidations(validations);
    formField.setFieldProperties(properties);

    return formField;
  }

  private static List<FieldValidation> getValidations(JSONObject formField)
  {
    List<FieldValidation> validations = new ArrayList<>();

    JSONArray validationArray = (JSONArray) formField.get("fieldValidations");
    validationArray.forEach(validation -> {
      JSONObject validationJson = (JSONObject) validation;

      validations.add(parseFieldValidation(validationJson));
    });

    return validations;
  }

  private static List<FieldProperty> getProperties(JSONObject formField)
  {
    List<FieldProperty> properties = new ArrayList<>();

    JSONArray fieldPropertiesArray = (JSONArray) formField.get("fieldProperties");

    fieldPropertiesArray.forEach(property -> {
      JSONObject jsonProperty = (JSONObject) property;
      properties.add(parseFieldProperty(jsonProperty));
    });

    return properties;
  }

  private static FieldProperty parseFieldProperty(JSONObject fieldPropertyJson)
  {
    String id = fieldPropertyJson.getString("id");
    String value = fieldPropertyJson.getString("value");

    return new FieldProperty(id, value);
  }

  private static FieldValidation parseFieldValidation(JSONObject validationJson)
  {
    String name = validationJson.getString("name");
    String configuration = (String) validationJson.get("configuration");

    return new FieldValidation(name, configuration);
  }
}
