/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpm.rest.model.RestCase;
import mn.erin.bpm.rest.model.RestCaseNode;
import mn.erin.bpm.rest.model.RestCollateral;
import mn.erin.bpm.rest.model.RestCompletedForm;
import mn.erin.bpm.rest.model.RestCompletedFormField;
import mn.erin.bpm.rest.model.RestCreatedRequest;
import mn.erin.bpm.rest.model.RestDocumentInfo;
import mn.erin.bpm.rest.model.RestExecution;
import mn.erin.bpm.rest.model.RestFieldProperty;
import mn.erin.bpm.rest.model.RestFormField;
import mn.erin.bpm.rest.model.RestFormFieldRelation;
import mn.erin.bpm.rest.model.RestProcessType;
import mn.erin.bpm.rest.model.RestSalaryInfo;
import mn.erin.bpm.rest.model.RestTask;
import mn.erin.bpm.rest.model.RestTaskForm;
import mn.erin.bpm.rest.model.RestTaskFormRelation;
import mn.erin.bpm.rest.model.RestVariable;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.model.cases.Execution;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.document.DocumentInfoId;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldRelation;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.form.TaskFormRelation;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;

import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.COLUMN_INDEX;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.CONFIGURATION;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.CONTEXT;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.DEFAULT_VALUE;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.FORM_FIELDS;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.FORM_FIELD_ID;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.FORM_FIELD_TYPE;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.FORM_FIELD_VALUE;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.IS_DISABLED;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.IS_REQUIRED;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.LABEL;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.NO_VALUE;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.OPTIONS;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.PROPERTY_ID;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.PROPERTY_VALUE;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.SPECIAL_FORMS;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.VALIDATIONS;
import static mn.erin.bpm.rest.constant.RestCompletedFormConstants.VALIDATION_NAME;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.FORM_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.FORM_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.JSON_TO_OBJECT_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.JSON_TO_OBJECT_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_COMPLETED_FORM_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REST_COMPLETED_FORM_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_AMOUNT_OF_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSPECTION_DATE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSPECTION_TYPE_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT_WITH_HOUR;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNERSHIP_TYPE_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_INSPTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_OWNERTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_DEFINITION_KEY;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.USER_TASK;

/**
 * Responsible utility for BPM REST.
 *
 * @author Tamir
 */
public final class BpmRestUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BpmRestUtils.class);

  private BpmRestUtils()
  {
  }

  public static RestCase toRestCase(Case caseEntity)
  {
    String id = caseEntity.getId().getId();
    String name = caseEntity.getName();

    String definitionKey = caseEntity.getDefinitionKey();

    return new RestCase(id, name, definitionKey);
  }

  @SuppressWarnings("unchecked")
  public static List<RestCollateral> getSelectedRestCollaterals(String selectedKey, List<Object> collaterals)
  {
    List<RestCollateral> restCollaterals = new ArrayList<>();

    for (Object collateral : collaterals)
    {
      Map<String, Object> collateralMap = (Map<String, Object>) collateral;

      if (collateralMap.containsKey(selectedKey) && collateralMap.get(selectedKey).equals(true))
      {
        RestCollateral restCollateral = new RestCollateral();

        restCollateral.setCollateralId((String) collateralMap.get(BpmModuleConstants.COLLATERAL_ID));
        setRestCollateralDigitValue(COLLATERAL_AMOUNT_OF_ASSESSMENT, restCollateral, collateralMap);

        restCollaterals.add(restCollateral);
      }
    }

    return restCollaterals;
  }

  public static void setRestCollateralDigitValue(String key, RestCollateral restCollateral, Map<String, Object> collateralMap)
  {
    if (collateralMap.get(key) instanceof Integer)
    {
      restCollateral.setAmountOfAssessment(BigDecimal.valueOf((Integer) collateralMap.get(key)));
    }
    else if (collateralMap.get(key) instanceof Double)
    {
      restCollateral.setAmountOfAssessment(BigDecimal.valueOf((Double) collateralMap.get(key)));
    }
    else if (collateralMap.get(key) instanceof Long)
    {
      restCollateral.setAmountOfAssessment(BigDecimal.valueOf((Long) collateralMap.get(key)));
    }
  }

  public static RestTaskFormRelation toRestTaskFormRelation(TaskFormRelation taskFormRelation)
  {
    if (null == taskFormRelation)
    {
      return new RestTaskFormRelation();
    }

    String taskDefId = taskFormRelation.getTaskDefinitionId().getId();

    Map<String, Collection<FormFieldRelation>> fieldRelationMap = taskFormRelation.getFieldRelations();
    Set<Map.Entry<String, Collection<FormFieldRelation>>> fieldEntities = fieldRelationMap.entrySet();

    Map<String, List<RestFormFieldRelation>> restFormFieldMap = new HashMap<>();

    for (Map.Entry<String, Collection<FormFieldRelation>> fieldEntity : fieldEntities)
    {
      String fieldId = fieldEntity.getKey();
      List<FormFieldRelation> relations = (List<FormFieldRelation>) fieldEntity.getValue();

      Collection<RestFormFieldRelation> restFormFieldRelations = toRestFormFieldRelations(relations);
      restFormFieldMap.put(fieldId, (List<RestFormFieldRelation>) restFormFieldRelations);
    }

    return new RestTaskFormRelation(taskDefId, restFormFieldMap);
  }

  public static Collection<RestFormFieldRelation> toRestFormFieldRelations(Collection<FormFieldRelation> formFieldRelations)
  {
    Collection<RestFormFieldRelation> restFormFieldRelations = new ArrayList<>();

    for (FormFieldRelation formFieldRelation : formFieldRelations)
    {
      String updatedFieldID = formFieldRelation.getUpdatedFieldID();
      String operationType = formFieldRelation.getOperationType();

      Collection<String> operations = formFieldRelation.getOperations();
      Collection<String> references = formFieldRelation.getReferences();

      Map<String, Collection<FieldProperty>> options = formFieldRelation.getOptions();

      RestFormFieldRelation restFormFieldRelation = new RestFormFieldRelation();

      restFormFieldRelation.setUpdatedFieldID(updatedFieldID);
      restFormFieldRelation.setOperationType(operationType);

      restFormFieldRelation.setOperations(operations);
      restFormFieldRelation.setReferences(references);

      Map<String, List<RestFieldProperty>> restOptions = new HashMap<>();

      Set<Map.Entry<String, Collection<FieldProperty>>> optionEntries = options.entrySet();

      for (Map.Entry<String, Collection<FieldProperty>> optionEntry : optionEntries)
      {
        String key = optionEntry.getKey();
        Collection<FieldProperty> value = optionEntry.getValue();
        Collection<RestFieldProperty> restFieldProperties = toRestFieldProperty(value);

        restOptions.put(key, (List<RestFieldProperty>) restFieldProperties);
      }

      restFormFieldRelation.setOptions(restOptions);
      restFormFieldRelations.add(restFormFieldRelation);
    }

    return restFormFieldRelations;
  }

  public static Collection<RestFieldProperty> toRestFieldProperty(Collection<FieldProperty> fieldProperties)
  {
    Collection<RestFieldProperty> restFieldProperties = new ArrayList<>();

    for (FieldProperty fieldProperty : fieldProperties)
    {
      String id = fieldProperty.getId();
      String value = fieldProperty.getValue();

      restFieldProperties.add(new RestFieldProperty(id, value));
    }
    return restFieldProperties;
  }

  public static List<RestCollateral> toRestCollaterals(List<Collateral> collaterals, boolean isCreatedOnBpms)
  {
    List<RestCollateral> restCollaterals = new ArrayList<>();

    for (Collateral collateral : collaterals)
    {
      RestCollateral restCollateral = toRestCollateral(collateral, isCreatedOnBpms);
      restCollaterals.add(restCollateral);
    }
    return restCollaterals;
  }

  public static RestCollateral toRestCollateral(Collateral collateral, boolean isCreatedOnBpms)
  {
    RestCollateral restCollateral = new RestCollateral();

    String collateralId = collateral.getId().getId();
    AccountId accountIdEntity = collateral.getAccountId();
    String accountId = null == accountIdEntity ? null : accountIdEntity.getId();

    String name = collateral.getName();
    String currencyType = null == collateral.getCurrencyType() ? null : collateral.getCurrencyType().getValue();

    LocalDate startDate = collateral.getStartDate();
    LocalDate endDate = collateral.getEndDate();

    List<String> ownerNames = collateral.getOwnerNames();
    BigDecimal amountOfAssessment = collateral.getAmountOfAssessment();

    CollateralInfo collateralInfo = collateral.getCollateralInfo();

    String customerName = collateralInfo.getCustomerName();
    String liableNumber = collateralInfo.getLiableNumber();

    BigDecimal hairCut = collateralInfo.getHairCut();
    BigDecimal availableAmount = collateralInfo.getAvailableAmount();

    String description = collateralInfo.getDescription();
    String linkedReferenceNumber = collateralInfo.getLinkedReferenceNumber();
    LocalDate revalueDate = collateralInfo.getRevalueDate();

    String type = collateral.getType();

    restCollateral.setType(type);

    restCollateral.setCollateralId(collateralId);
    restCollateral.setAccountId(accountId);

    restCollateral.setName(name);
    restCollateral.setCurrencyType(currencyType);

    restCollateral.setStartDate(toDateFromLocalDate(startDate));
    restCollateral.setEndDate(toDateFromLocalDate(endDate));

    restCollateral.setOwnerNames(ownerNames);
    restCollateral.setAmountOfAssessment(amountOfAssessment);

    restCollateral.setCustomerName(customerName);
    restCollateral.setLiableNumber(liableNumber);

    restCollateral.setHairCut(hairCut);
    restCollateral.setAvailableAmount(availableAmount);

    restCollateral.setDescription(description);
    restCollateral.setLinkedReferenceNumber(linkedReferenceNumber);

    restCollateral.setRevalueDate(toDateFromLocalDate(revalueDate));
    restCollateral.setCreatedOnBpms(isCreatedOnBpms);

    return restCollateral;
  }

  public static JSONObject toCompletedFormJson(RestCompletedForm restCompletedForm) throws BpmInvalidArgumentException
  {
    if (null == restCompletedForm)
    {
      throw new BpmInvalidArgumentException(REST_COMPLETED_FORM_NULL_CODE, REST_COMPLETED_FORM_NULL_MESSAGE);
    }

    String taskId = restCompletedForm.getTaskId();
    String caseInstanceId = restCompletedForm.getCaseInstanceId();
    String formId = restCompletedForm.getFormId();
    String taskDefKey = restCompletedForm.getTaskDefinitionKey();

    Collection<RestCompletedFormField> formFields = restCompletedForm.getFormFields();
    Map<String, Object> specialForms = restCompletedForm.getSpecialForms();

    JSONArray formFieldsArray = toFormFieldsJson(formFields);
    JSONObject completedFormJson = new JSONObject();

    completedFormJson.put(TASK_ID, taskId);
    completedFormJson.put(CASE_INSTANCE_ID, caseInstanceId);
    completedFormJson.put(FORM_ID, formId);
    completedFormJson.put(TASK_DEFINITION_KEY, taskDefKey);

    completedFormJson.put(FORM_FIELDS, formFieldsArray);

    if (null == specialForms)
    {
      completedFormJson.put(SPECIAL_FORMS, new JSONObject());
    }
    else
    {
      Set<Map.Entry<String, Object>> specialFormEntries = specialForms.entrySet();

      JSONObject specialFormJson = new JSONObject();
      for (Map.Entry<String, Object> specialFormEntry : specialFormEntries)
      {

        String key = specialFormEntry.getKey();
        Object value = specialFormEntry.getValue();

        if (!StringUtils.isBlank(key) && null != value)
        {
          specialFormJson.put(key, value);
        }
      }

      completedFormJson.put(SPECIAL_FORMS, specialFormJson);
    }
    return completedFormJson;
  }

  public static <T> T jsonToObject(String jsonAsString, Class<T> valueType) throws BpmInvalidArgumentException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    try
    {
      return objectMapper.readValue(jsonAsString, valueType);
    }
    catch (JsonProcessingException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new BpmInvalidArgumentException(JSON_TO_OBJECT_ERROR_CODE, JSON_TO_OBJECT_ERROR_MESSAGE);
    }
  }

  public static JSONArray toFormFieldsJson(Collection<RestCompletedFormField> formFields)
  {
    if (formFields.isEmpty())
    {
      return new JSONArray();
    }

    JSONArray formFieldsArray = new JSONArray();

    for (RestCompletedFormField formField : formFields)
    {
      JSONObject fieldJson = new JSONObject();

      String formFieldId = formField.getId();
      String label = formField.getLabel();

      String type = formField.getType();
      String context = formField.getContext();
      Integer columnIndex = formField.getColumnIndex();

      boolean isDisabled = formField.getDisabled();
      boolean isRequired = formField.getRequired();

      JSONObject valueJson = toFieldValueJson(formField.getFormFieldValue());
      JSONArray validationArray = toFieldValidationJsonArray(formField.getValidations());
      JSONArray optionArray = toFieldPropertyJsonArray(formField.getOptions());

      fieldJson.put(FORM_FIELD_ID, formFieldId);
      fieldJson.put(LABEL, label);
      fieldJson.put(FORM_FIELD_TYPE, type);

      fieldJson.put(IS_DISABLED, isDisabled);
      fieldJson.put(IS_REQUIRED, isRequired);

      if (null == columnIndex)
      {
        fieldJson.put(COLUMN_INDEX, 0);
      }
      else
      {
        fieldJson.put(COLUMN_INDEX, columnIndex);
      }

      fieldJson.put(CONTEXT, context);
      fieldJson.put(FORM_FIELD_VALUE, valueJson);
      fieldJson.put(VALIDATIONS, validationArray);
      fieldJson.put(OPTIONS, optionArray);

      formFieldsArray.put(fieldJson);
    }

    return formFieldsArray;
  }

  public static String decodeStringToUtf8(String text) throws UnsupportedEncodingException
  {
    if (isValidISOString(text))
    {
      return new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
    else
    {
      return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
    }
  }

  private static boolean isValidISOString(String input)
  {
    byte[] bytes = input.getBytes(StandardCharsets.ISO_8859_1);

    if (bytes.length != 0)
    {
      String valueFromBytes = new String(bytes, StandardCharsets.ISO_8859_1);

      return input.equalsIgnoreCase(valueFromBytes);
    }
    return false;
  }

  private static JSONObject toFieldValueJson(FormFieldValue formFieldValue)
  {
    if (null == formFieldValue)
    {
      JSONObject emptyValueJson = new JSONObject();

      emptyValueJson.put(DEFAULT_VALUE, NO_VALUE);

      return emptyValueJson;
    }

    JSONObject fieldValueJson = new JSONObject();

    Object defaultValue = formFieldValue.getDefaultValue();
    fieldValueJson.put(DEFAULT_VALUE, defaultValue);
    return fieldValueJson;
  }

  private static Date toDateFromLocalDate(LocalDate localDate)
  {
    if (null == localDate)
    {
      return null;
    }

    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private static JSONArray toFieldValidationJsonArray(Collection<FieldValidation> fieldValidations)
  {
    if (fieldValidations.isEmpty())
    {
      return new JSONArray();
    }

    JSONArray validationsJson = new JSONArray();

    for (FieldValidation fieldValidation : fieldValidations)
    {
      if (null == fieldValidation)
      {
        continue;
      }

      String name = fieldValidation.getName();
      String configuration = fieldValidation.getConfiguration();

      if (!StringUtils.isBlank(name) && !StringUtils.isBlank(configuration))
      {
        JSONObject validationJson = new JSONObject();

        validationJson.put(VALIDATION_NAME, name);
        validationJson.put(CONFIGURATION, configuration);

        validationsJson.put(validationJson);
      }
    }

    return validationsJson;
  }

  private static JSONArray toFieldPropertyJsonArray(Collection<FieldProperty> fieldProperties)
  {
    if (fieldProperties.isEmpty())
    {
      return new JSONArray();
    }

    JSONArray propertyJsonArray = new JSONArray();

    for (FieldProperty fieldProperty : fieldProperties)
    {
      if (null == fieldProperty)
      {
        continue;
      }

      String propertyId = fieldProperty.getId();
      String value = fieldProperty.getValue();

      if (!StringUtils.isBlank(propertyId) && null != value)
      {
        JSONObject propertyJson = new JSONObject();

        propertyJson.put(PROPERTY_ID, propertyId);
        propertyJson.put(PROPERTY_VALUE, value);

        propertyJsonArray.put(propertyJson);
      }
    }
    return propertyJsonArray;
  }

  public static void validateCompletedFormInput(String instanceId, String taskId, String formId)
      throws BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(taskId))
    {
      throw new BpmInvalidArgumentException(TASK_ID_NULL_CODE, TASK_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(formId))
    {
      throw new BpmInvalidArgumentException(FORM_ID_NULL_CODE, FORM_ID_NULL_MESSAGE);
    }
  }

  public static Map<String, Object> mapLoanContractForm(Map<String, Object> parameters)
  {
    for (int i = 1; i < 5; i++)
    {
      if (!parameters.containsKey("currentAccount" + i))
      {
        parameters.put("currentAccount" + i, "empty");
      }
      if (!parameters.containsKey("otherCurrentAccount" + i))
      {
        parameters.put("otherCurrentAccount" + i, "empty");
      }
    }
    return parameters;
  }

  public static Map<String, Serializable> objectMapToSerializableMap(Map<String, Object> objectMap)
  {
    Map<String, Serializable> serializableMap = new HashMap<>();

    for (Map.Entry<String, Object> parameter : objectMap.entrySet())
    {
      serializableMap.put(parameter.getKey(), (Serializable) parameter.getValue());
    }

    return serializableMap;
  }

  public static Collection<RestDocumentInfo> toRestDocumentInfos(Collection<DocumentInfo> documentInfos)
  {
    Collection<RestDocumentInfo> restDocumentInfos = new ArrayList<>();

    for (DocumentInfo documentInfo : documentInfos)
    {
      if (null != documentInfo)
      {
        DocumentInfoId documentInfoId = documentInfo.getId();
        String id = documentInfoId.getId();

        String name = documentInfo.getName();
        String parentId = documentInfo.getParentId();

        String type = documentInfo.getType();

        RestDocumentInfo restDocumentInfo = new RestDocumentInfo();

        restDocumentInfo.setId(id);
        restDocumentInfo.setParentId(parentId);

        restDocumentInfo.setName(name);
        restDocumentInfo.setType(type);

        restDocumentInfos.add(restDocumentInfo);
      }
    }
    return restDocumentInfos;
  }

  public static List<RestSalaryInfo> toRestSalaryInfo(List<SalaryInfo> salaryInfos)
  {
    List<RestSalaryInfo> restSalaryInfos = new ArrayList<>();

    for (SalaryInfo salaryInfo : salaryInfos)
    {
      Integer month = salaryInfo.getMonth();
      Integer year = salaryInfo.getYear();

      BigDecimal amount = salaryInfo.getAmount();
      BigDecimal salaryFee = salaryInfo.getSalaryFee();

      boolean isPaidSocialInsurance = salaryInfo.isPaidSocialInsurance();

      RestSalaryInfo restSalaryInfo = new RestSalaryInfo(month, year, amount, salaryFee, isPaidSocialInsurance);

      restSalaryInfos.add(restSalaryInfo);
    }
    return restSalaryInfos;
  }

  public static List<RestTask> toRestTasks(List<Task> tasks)
  {
    List<RestTask> restTasks = new ArrayList<>();

    for (Task task : tasks)
    {
      if (null != task)
      {
        String id = task.getId().getId();
        String name = task.getName();
        String status = task.getStatus();

        String executionId = task.getExecutionId().getId();
        String instanceId = task.getCaseInstanceId().getId();
        String type = task.getType();
        String parentTaskId = task.getParentTaskId();
        String definitionKey = task.getDefinitionKey();

        restTasks.add(new RestTask(id, executionId, instanceId, name, status, type, parentTaskId, definitionKey));
      }
    }
    return restTasks;
  }

  public static List<RestVariable> toRestVariables(Collection<Variable> variables)
  {
    List<RestVariable> restVariables = new ArrayList<>();

    for (Variable variable : variables)
    {
      String id = variable.getId().getId();
      Serializable value = variable.getValue();

      String type = variable.getType();
      String label = variable.getLabel();

      String context = variable.getContext();
      boolean islocalVariable = variable.isLocalVariable();

      RestVariable restVariable = new RestVariable(id, value, type, label, context, islocalVariable);
      restVariables.add(restVariable);
    }
    return restVariables;
  }

  public static List<Variable> toVariables(List<RestVariable> restVariables)
  {
    List<Variable> variables = new ArrayList<>();

    for (RestVariable restVariable : restVariables)
    {
      String id = restVariable.getId();
      Serializable value = restVariable.getValue();

      String label = restVariable.getLabel();
      String type = restVariable.getType();
      boolean islocalVariable = restVariable.isLocalVariable();
      String context = restVariable.getContext();

      variables.add(new Variable(VariableId.valueOf(id), value, type, label, context, islocalVariable));
    }
    return variables;
  }

  public static List<RestCaseNode> toRestCaseNode(Map<String, List<Execution>> groupedExecution)
  {
    Set<String> keys = groupedExecution.keySet();
    List<RestCaseNode> tempCaseNodes = new ArrayList<>();
    List<RestCaseNode> restCaseNodes = new ArrayList<>();
    for (String key : keys)
    {
      List<Execution> executions = groupedExecution.get(key);
      Execution execution = executions.get(0);
      String parentId = execution.getParentId();
      if (key.equals("ungrouped"))
      {
        executions.stream().forEach(exec -> tempCaseNodes.add(new RestCaseNode(exec.getId().getId(), exec.getActivityName(), null)));
      }
      else
      {
        tempCaseNodes.add(new RestCaseNode(parentId, key, toChildNode(executions)));
      }
    }

    cloneCaseNodeList(restCaseNodes, tempCaseNodes);
    restCaseNodes.removeIf(node -> checkNode(node, tempCaseNodes));
    return restCaseNodes;
  }

  public static Collection<RestExecution> toRestExecutions(Collection<Execution> executions)
  {
    Collection<RestExecution> restExecutions = new ArrayList<>();

    for (Execution execution : executions)
    {
      String executionId = execution.getId().getId();
      String instanceId = execution.getInstanceId().getId();

      String activityId = execution.getActivityId().getId();
      String taskId = execution.getTaskId().getId();

      String executionType = execution.getExecutionType().getValue();
      String activityType = execution.getActivityType();

      String activityName = execution.getActivityName();

      restExecutions.add(new RestExecution(executionId, instanceId, activityId, taskId, executionType, activityType, activityName));
    }
    return restExecutions;
  }

  public static Collection<RestExecution> toRestExecutionsFromTasks(Collection<Task> tasks)
  {
    Collection<RestExecution> restExecutions = new ArrayList<>();

    for (Task task : tasks)
    {
      String executionId = task.getExecutionId().getId();
      String caseInstanceId = task.getCaseInstanceId().getId();

      String taskId = task.getId().getId();
      String taskName = task.getName();

      RestExecution restExecution = new RestExecution();

      restExecution.setExecutionId(executionId);
      restExecution.setInstanceId(caseInstanceId);

      restExecution.setTaskId(taskId);
      restExecution.setExecutionType(USER_TASK);
      restExecution.setActivityName(taskName);

      restExecutions.add(restExecution);
    }
    return restExecutions;
  }

  public static Collection<RestProcessType> toRestProcessTypes(Collection<ProcessType> processTypes)
  {
    Collection<RestProcessType> restProcessTypes = new ArrayList<>();

    for (ProcessType processType : processTypes)
    {
      restProcessTypes.add(toRestProcessType(processType));
    }
    return restProcessTypes;
  }

  public static RestProcessType toRestProcessType(ProcessType processType)
  {
    String id = processType.getId().getId();
    String definitionKey = processType.getDefinitionKey();

    String version = processType.getVersion();
    ProcessDefinitionType definitionType = processType.getProcessDefinitionType();
    String name = processType.getName();
    String processTypeCategory = processType.getProcessTypeCategory();

    return new RestProcessType(id, definitionKey,
        name, version, definitionType.toString(), processTypeCategory);
  }

  public static List<RestTaskForm> toRestTaskForms(List<TaskForm> taskForms)
  {
    List<RestTaskForm> restTaskForms = new ArrayList<>();

    for (TaskForm taskForm : taskForms)
    {
      restTaskForms.add(toRestTaskForm(taskForm));
    }
    return restTaskForms;
  }

  public static RestTaskForm toRestTaskForm(TaskForm taskForm)
  {
    String taskId = taskForm.getTaskId().getId();
    String formId = taskForm.getTaskFormId().getId();

    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

    List<RestFormField> restFormFields = toRestFormFields(taskFormFields);

    return new RestTaskForm(formId, taskId, restFormFields);
  }

  public static RestTaskForm toRestTaskForm(TaskForm taskForm, Map<String, Object> tableData)
  {
    String taskId = taskForm.getTaskId().getId();
    String formId = taskForm.getTaskFormId().getId();

    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

    List<RestFormField> restFormFields = toRestFormFields(taskFormFields);

    final RestTaskForm restTaskForm = new RestTaskForm(formId, taskId, restFormFields);
    restTaskForm.setTableData(tableData);
    return restTaskForm;
  }

  public static List<RestCase> toRestCases(List<Case> caseList)
  {
    List<RestCase> restCases = new ArrayList<>();

    for (Case caseEntity : caseList)
    {
      String caseId = caseEntity.getId().getId();
      String name = caseEntity.getName();
      String definitionKey = caseEntity.getDefinitionKey();

      RestCase restCase = new RestCase(caseId, name, definitionKey);
      restCases.add(restCase);
    }

    return restCases;
  }

  public static List<RestFormField> toRestFormFields(Collection<TaskFormField> taskFormFields)
  {
    List<RestFormField> restFormFields = new ArrayList<>();

    for (TaskFormField taskFormField : taskFormFields)
    {
      String id = taskFormField.getId().getId();

      Collection<FieldProperty> fieldProperties = taskFormField.getFieldProperties();
      Collection<FieldValidation> fieldValidations = taskFormField.getFieldValidations();

      String label = taskFormField.getLabel();
      String type = taskFormField.getType();
      FormFieldValue formFieldValue = taskFormField.getFormFieldValue();

      restFormFields.add(new RestFormField(id, formFieldValue, label, type, fieldValidations, fieldProperties));
    }

    return restFormFields;
  }

  public static RestCreatedRequest toRestCreatedRequest(ProcessRequest processRequest, String processTypeName)
  {

    RestCreatedRequest createdRequest = new RestCreatedRequest();

    String id = processRequest.getId().getId();
    String processTypeId = processRequest.getProcessTypeId().getId();

    String processInstanceId = processRequest.getProcessInstanceId();
    LocalDateTime createdDateTime = processRequest.getCreatedTime();

    UserId assignedUserId = processRequest.getAssignedUserId();
    String requestedUserId = processRequest.getRequestedUserId();

    ProcessRequestState state = processRequest.getState();
    Map<String, Serializable> parameters = processRequest.getParameters();

    createdRequest.setId(id);
    createdRequest.setProcessTypeId(processTypeId);

    createdRequest.setProcessInstanceId(processInstanceId);
    createdRequest.setCreatedDate(toDateString(createdDateTime));

    createdRequest.setState(state.toString());
    createdRequest.setParameters(parameters);

    if (null != assignedUserId)
    {
      createdRequest.setAssignedUserId(assignedUserId.getId());
    }

    createdRequest.setProcessTypeName(processTypeName);
    createdRequest.setCreatedUserId(requestedUserId);

    return createdRequest;
  }

  public static String toDateString(LocalDateTime createdTime)
  {
    Date date = new GregorianCalendar(createdTime.getYear(), createdTime.getMonth().getValue() - 1, createdTime.getDayOfMonth()).getTime();
    SimpleDateFormat format = new SimpleDateFormat(ISO_DATE_FORMAT);

    return format.format(date);
  }

  public static String toDateStringWithHour(LocalDateTime createdTime)
  {
    Date date = new GregorianCalendar(createdTime.getYear(), createdTime.getMonth().getValue() - 1, createdTime.getDayOfMonth(), createdTime.getHour(),
        createdTime.getMinute()).getTime();
    SimpleDateFormat format = new SimpleDateFormat(ISO_DATE_FORMAT_WITH_HOUR);

    return format.format(date);
  }

  private static List<RestCaseNode> toChildNode(List<Execution> executions)
  {
    List<RestCaseNode> restCaseNodes = new ArrayList<>();
    for (Execution execution : executions)
    {
      String executionId = execution.getId().getId();
      String activityName = execution.getActivityName();
      restCaseNodes.add(new RestCaseNode(executionId, activityName, null));
    }
    return restCaseNodes;
  }

  private static void cloneCaseNodeList(List<RestCaseNode> destination, List<RestCaseNode> source)
  {
    for (RestCaseNode caseNode : source)
    {
      List<RestCaseNode> children = caseNode.getChildren();
      String processId = caseNode.getProcessId();
      String label = caseNode.getLabel();
      destination.add(new RestCaseNode(processId, label, children));
    }
  }

  private static boolean checkNode(RestCaseNode caseNode, List<RestCaseNode> restCaseNodes)
  {
    String processId = caseNode.getProcessId();
    for (RestCaseNode node : restCaseNodes)
    {
      if (!node.getProcessId().equals(processId) && checkChildNode(caseNode, node.getChildren()))
      {
        return true;
      }
    }
    return false;
  }

  private static boolean checkChildNode(RestCaseNode caseNode, List<RestCaseNode> restCaseNodes)
  {
    if (null == restCaseNodes || restCaseNodes.isEmpty())
    {
      return false;
    }

    for (RestCaseNode restCaseNode : restCaseNodes)
    {
      if (restCaseNode.getProcessId().equals(caseNode.getProcessId()))
      {
        restCaseNode.setChildren(caseNode.getChildren());
        return true;
      }

      if (checkChildNode(caseNode, restCaseNode.getChildren()))
      {
        return true;
      }
    }

    return false;
  }


  public static void setCollateralCode(TaskFormField taskFormField, List<String> collCodes) throws UseCaseException
  {

    List<FieldProperty> collIdProperties = new ArrayList<>();

    for (String collCode : collCodes)
    {
      collIdProperties.add(new FieldProperty(collCode, collCode));
    }

    taskFormField.setFieldProperties(collIdProperties);
  }

  public static void setCreateCollateralFieldValue(TaskFormField taskFormField, Map<String, List<String>> referenceCodeResponses, String fieldId)
  {
    if (fieldId.equalsIgnoreCase(INSPECTION_TYPE_CODE_FORM_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_INSPTYPE, taskFormField, referenceCodeResponses);
    }
    else if (fieldId.equalsIgnoreCase(OWNERSHIP_TYPE_CODE_FORM_FIELD_ID))
    {
      setFieldByReferenceCodes(REFERENCE_CODE_COLL_OWNERTYPE, taskFormField, referenceCodeResponses);
    }
    else if (fieldId.equalsIgnoreCase(INSPECTION_DATE_FORM_FIELD_ID))
    {
      taskFormField.setFormFieldValue(new FormFieldValue(new Date()));
    }
  }

  public static void setFieldByReferenceCodes(String reference, TaskFormField taskFormField, Map<String, List<String>> referenceCodeResponses)
  {
    List<FieldProperty> properties = new ArrayList<>();

    List<String> codeList = referenceCodeResponses.get(reference);

    for (String code : codeList)
    {
      properties.add(new FieldProperty(code, code));
    }

    taskFormField.setFieldProperties(properties);
  }
}
