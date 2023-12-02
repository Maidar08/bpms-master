package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.constants.CollateralConstants;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.FieldValidation;
import mn.erin.domain.bpm.model.form.FormFieldId;
import mn.erin.domain.bpm.model.form.FormFieldValue;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodes;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodesInput;
import mn.erin.domain.bpm.usecase.collateral.GetCollReferenceCodesOutput;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralCodes;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralCodesOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_IMMOVABLE;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.READONLY;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_CITY;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_INSPTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_OWNERTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_PURPOSE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILDER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILDER_NAME_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILT_AREA;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILT_AREA_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.CITY;
import static mn.erin.domain.bpm.constants.CollateralConstants.CITY_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_CODE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.CONDITION_OF_CONTRACT;
import static mn.erin.domain.bpm.constants.CollateralConstants.CONDITION_OF_CONTRACT_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.DUE_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.DUE_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.HOUSE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.HOUSE_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.IMMOVABLE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.IMMOVABLE_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.LAND_AREA;
import static mn.erin.domain.bpm.constants.CollateralConstants.LAND_AREA_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNERSHIP_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_CIF_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_DOC_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_DOC_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_ID_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_OF_USAGE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.REMARKS;
import static mn.erin.domain.bpm.constants.CollateralConstants.REVIEW_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.REVIEW_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NAME_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.YEAR_OF_CONSTRUCTION;
import static mn.erin.domain.bpm.constants.CollateralConstants.YEAR_OF_CONSTRUCTION_MN;

/**
 * @author Lkhagvadorj.A
 **/

public class GetImmobavlePropertyCollateralInfo extends AbstractUseCase<String, TaskForm>
{
  private final NewCoreBankingService newCoreBankingService;
  private static final String STRING = "string";
  private static final String DATE = "date";
  private static final String BIG_DECIMAL = "BigDecimal";
  private static final String MAX_LENGTH = "maxlength";
  private static final String REQUIRED = "required";

  public GetImmobavlePropertyCollateralInfo(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core Banking Service is required!");
  }

  @Override
  public TaskForm execute(String input) throws UseCaseException
  {
    if (StringUtils.isBlank(input))
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    try
    {
      Map<String, Object> immovablePropertyCollateral = newCoreBankingService.getImmovablePropertyCollateral(input);
      return toTaskForm(immovablePropertyCollateral);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private TaskForm toTaskForm(Map<String, Object> immovablePropertyCollateral) throws UseCaseException
  {
    Collection<TaskFormField> taskFormFields = new ArrayList<>();

    FieldValidation required = new FieldValidation(REQUIRED, "");
    FieldValidation maxlength21 = new FieldValidation(MAX_LENGTH, "21");
    FieldValidation readonly = new FieldValidation(READONLY, "");

    Map<String, List<String>> referenceCodeResponses = getReferenceCodes(Arrays
        .asList(REFERENCE_CODE_COLL_OWNERTYPE,
            REFERENCE_CODE_COLL_INSPTYPE,
            REFERENCE_CODE_COLL_CITY,
            REFERENCE_CODE_COLL_PURPOSE));

    // collateralCode
    Object collateralCodeValue = immovablePropertyCollateral.get(COLLATERAL_CODE_FORM_FIELD_ID);
    TaskFormField collateralCode = new TaskFormField(FormFieldId.valueOf(COLLATERAL_CODE_FORM_FIELD_ID), new FormFieldValue(collateralCodeValue),
        COLLATERAL_CODE_MN, STRING);
    collateralCode.setFieldValidations(Arrays.asList(required, readonly));
    List<String> collCodes = getCollCodes(COLL_TYPE_IMMOVABLE);
    List<FieldProperty> collIdProperties = new ArrayList<>();

    for (String collCode : collCodes)
    {
      if (collCode.contains(String.valueOf(collateralCodeValue)))
      {
        collateralCode.getFormFieldValue().setDefaultValue(collCode);
      }
      collIdProperties.add(new FieldProperty(collCode, collCode));
    }
    collateralCode.setFieldProperties(collIdProperties);

    Object reviewDateValue = immovablePropertyCollateral.get(REVIEW_DATE);
    TaskFormField reviewDate = createTaskFormField(REVIEW_DATE, reviewDateValue, REVIEW_DATE_MN, DATE);

    Object yearOfConstructionValue = immovablePropertyCollateral.get(YEAR_OF_CONSTRUCTION);
    TaskFormField yearOfConstruction = createTaskFormField(YEAR_OF_CONSTRUCTION, yearOfConstructionValue, YEAR_OF_CONSTRUCTION_MN, STRING);
    addValidation(yearOfConstruction, MAX_LENGTH, "7");

    Object conditionOfContractValue = immovablePropertyCollateral.get(CONDITION_OF_CONTRACT);
    TaskFormField conditionOfContract = createTaskFormField(CONDITION_OF_CONTRACT, conditionOfContractValue, CONDITION_OF_CONTRACT_MN, BIG_DECIMAL);

    Object purposeOfUsageValue = immovablePropertyCollateral.get(PURPOSE_OF_USAGE);
    TaskFormField purposeOfUsage = createTaskFormField(PURPOSE_OF_USAGE, purposeOfUsageValue, PURPOSE_OF_USAGE_MN, STRING);
    List<FieldProperty> purposeOfUsageOptions = new ArrayList<>();
    purposeOfUsageOptions.add(new FieldProperty("Property_16ch1q2", "Амьдран суух"));
    purposeOfUsageOptions.add(new FieldProperty("Property_0bpcua6", "Үйлчилгээ"));
    purposeOfUsageOptions.add(new FieldProperty("Property_33b8p7b", "Газар өмчлөл"));
    purposeOfUsage.setFieldProperties(purposeOfUsageOptions);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_PURPOSE, purposeOfUsage, referenceCodeResponses);

    Object formNumValue = immovablePropertyCollateral.get(CollateralConstants.FORM_NUMBER);
    TaskFormField formNum = createTaskFormField(CollateralConstants.FORM_NUMBER, formNumValue, "Барьцаалбарын дугаар", STRING);
    formNum.setFieldValidations(Collections.singletonList(maxlength21));

    Object propertyIdValue = immovablePropertyCollateral.get(PROPERTY_ID);
    TaskFormField propertyId = createTaskFormField(PROPERTY_ID, propertyIdValue, PROPERTY_ID_MN, STRING);
    propertyId.setFieldValidations(Collections.singletonList(maxlength21));

    Object propertyDocNumberValue = immovablePropertyCollateral.get(PROPERTY_DOC_NUMBER);
    TaskFormField propertyDocNumber = createTaskFormField(PROPERTY_DOC_NUMBER, propertyDocNumberValue, PROPERTY_DOC_NUMBER_MN, STRING);
    addValidation(propertyDocNumber, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "14"));

    Object builtAreaValue = immovablePropertyCollateral.get(BUILT_AREA);
    TaskFormField builtArea = createTaskFormField(BUILT_AREA, builtAreaValue, BUILT_AREA_MN, STRING);
    addValidation(builtArea, MAX_LENGTH, "16");

    Object dueDateValue = immovablePropertyCollateral.get(DUE_DATE);
    TaskFormField dueDate = createTaskFormField(DUE_DATE, dueDateValue, DUE_DATE_MN, DATE);

    Object landAreaValue = immovablePropertyCollateral.get(LAND_AREA);
    int landAreaIntValue = String.valueOf(landAreaValue).equals("") ? 0 : new Double(Double.valueOf( String.valueOf(landAreaValue) )).intValue();
    TaskFormField landArea = createTaskFormField(LAND_AREA, landAreaIntValue, LAND_AREA_MN, BIG_DECIMAL);
    List<FieldProperty> landAreaOptions = new ArrayList<>();
    for (int i = 1; i < 11; i++)
    {
      landAreaOptions.add(new FieldProperty(String.valueOf(i), String.valueOf(i)));
    }
    landArea.setFieldProperties(landAreaOptions);

    Object cityValue = immovablePropertyCollateral.get(CITY);
    TaskFormField city = createTaskFormField(CITY, cityValue, CITY_MN, STRING);
    setCityOptions(city);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_CITY, city, referenceCodeResponses);

    Object address1Value = immovablePropertyCollateral.get(ADDRESS_1);
    TaskFormField address1 = createTaskFormField(ADDRESS_1, address1Value, ADDRESS_1_MN, STRING);
    addValidation(address1, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "46"));

    Object address2Value = immovablePropertyCollateral.get(ADDRESS_2);
    TaskFormField address2 = createTaskFormField(ADDRESS_2, address2Value, ADDRESS_2_MN, STRING);
    addValidation(address2, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "46"));

    Object address3Value = immovablePropertyCollateral.get(ADDRESS_3);
    TaskFormField address3 = createTaskFormField(ADDRESS_3, address3Value, ADDRESS_3_MN, STRING);
    addValidation(address3, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "46"));

    Object builderNameValue = immovablePropertyCollateral.get(BUILDER_NAME);
    TaskFormField builderName = createTaskFormField(BUILDER_NAME, builderNameValue, BUILDER_NAME_MN, STRING);
    addValidation(builderName, MAX_LENGTH, "46");

    Object houseNumberValue = immovablePropertyCollateral.get(HOUSE_NUMBER);
    TaskFormField houseNumber = createTaskFormField(HOUSE_NUMBER, houseNumberValue, HOUSE_NUMBER_MN, STRING);
    addValidation(houseNumber, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "11"));

    Object streetNameValue = immovablePropertyCollateral.get(STREET_NAME);
    TaskFormField streetName = createTaskFormField(STREET_NAME, streetNameValue, STREET_NAME_MN, STRING);
    addValidation(streetName, MAX_LENGTH, "51");

    Object streetNumberValue = immovablePropertyCollateral.get(STREET_NUMBER);
    TaskFormField streetNumber = createTaskFormField(STREET_NUMBER, streetNumberValue, STREET_NUMBER_MN, STRING);
    addValidation(streetNumber, MAX_LENGTH, "51");

    Object remarksValue = immovablePropertyCollateral.get(REMARKS);
    TaskFormField remarks = createTaskFormField(REMARKS, remarksValue, "Дэлгэрэнгүй", STRING);
    addValidation(remarks, MAX_LENGTH, "241");

    Object inspectionTypeValue = immovablePropertyCollateral.get(INSPECTION_TYPE);
    TaskFormField inspectionType = createTaskFormField(INSPECTION_TYPE, inspectionTypeValue, INSPECTION_TYPE_MN, STRING);
    addValidation(inspectionType, REQUIRED, "");
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_INSPTYPE, inspectionType, referenceCodeResponses);

    Object inspectionAmountValueObj = immovablePropertyCollateral.get(INSPECTION_AMOUNT_VALUE);
    TaskFormField inspectionAmountValue = createTaskFormField(INSPECTION_AMOUNT_VALUE, inspectionAmountValueObj, INSPECTION_AMOUNT_VALUE_MN, BIG_DECIMAL);
    addValidation(inspectionAmountValue, REQUIRED, "");

    Object amountOfCollateralValue = immovablePropertyCollateral.get(AMOUNT_OF_COLLATERAL);
    TaskFormField amountOfCollateral = createTaskFormField(AMOUNT_OF_COLLATERAL, amountOfCollateralValue, AMOUNT_OF_COLLATERAL_MN, BIG_DECIMAL);
    addValidation(amountOfCollateral, REQUIRED, "");

    Object deductionRateValue = immovablePropertyCollateral.get(DEDUCTION_RATE);
    TaskFormField deductionRate = createTaskFormField(DEDUCTION_RATE, deductionRateValue, DEDUCTION_RATE_MN, STRING);
    addValidation(deductionRate, READONLY, "");

    Object collateralAssessmentValue = immovablePropertyCollateral.get(COLLATERAL_ASSESSMENT);
    TaskFormField collateralAssessment = createTaskFormField(COLLATERAL_ASSESSMENT, collateralAssessmentValue, COLLATERAL_ASSESSMENT_MN, BIG_DECIMAL);
    addValidation(collateralAssessment, "readonly", "");

    Object inspectorIdValue = immovablePropertyCollateral.get(INSPECTOR_ID);
    TaskFormField inspectorId = createTaskFormField(INSPECTOR_ID, inspectorIdValue, INSPECTOR_ID_MN, STRING);
    addValidation(inspectorId, MAX_LENGTH, "6");

    Object inspectionDateValue = immovablePropertyCollateral.get(INSPECTION_DATE);
    TaskFormField inspectionDate = createTaskFormField(INSPECTION_DATE, inspectionDateValue, INSPECTION_DATE_MN, DATE);

    Object ownershipTypeValue = immovablePropertyCollateral.get(OWNERSHIP_TYPE);
    TaskFormField ownershipType = createTaskFormField(OWNERSHIP_TYPE, ownershipTypeValue, OWNERSHIP_TYPE_MN, STRING);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_OWNERTYPE, ownershipType, referenceCodeResponses);

    Object ownerCifNumberValue = immovablePropertyCollateral.get(OWNER_CIF_NUMBER);
    TaskFormField ownerCifNumber = createTaskFormField(OWNER_CIF_NUMBER, ownerCifNumberValue, OWNER_CIF_NUMBER_MN, STRING);
    addValidation(ownerCifNumber, MAX_LENGTH, "51");

    Object ownerNameValue = immovablePropertyCollateral.get(OWNER_NAME);
    TaskFormField ownerName = createTaskFormField(OWNER_NAME, ownerNameValue, OWNER_NAME_MN, STRING);
    addValidation(ownerName, Arrays.asList(REQUIRED, MAX_LENGTH), Arrays.asList("", "80"));

    Object serialNumberValue = immovablePropertyCollateral.get(SERIAL_NUM);
    TaskFormField serialNumber = new TaskFormField(FormFieldId.valueOf(SERIAL_NUM), new FormFieldValue(serialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    Object inspSerialNumberValue = immovablePropertyCollateral.get(INSPECTION_SERIAL_NUM);
    TaskFormField inspSerialNumber = new TaskFormField(FormFieldId.valueOf(INSPECTION_SERIAL_NUM), new FormFieldValue(inspSerialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    Object immovableNumberValue = immovablePropertyCollateral.get(IMMOVABLE_NUMBER);
    TaskFormField immovableNumber = createTaskFormField(IMMOVABLE_NUMBER, immovableNumberValue, IMMOVABLE_NUMBER_MN, STRING);
    addValidation(immovableNumber, MAX_LENGTH, "21");

    taskFormFields.add(collateralCode);
    taskFormFields.add(reviewDate);
    taskFormFields.add(yearOfConstruction);
    taskFormFields.add(conditionOfContract);
    taskFormFields.add(purposeOfUsage);
    taskFormFields.add(formNum);
    taskFormFields.add(propertyId);
    taskFormFields.add(propertyDocNumber);
    taskFormFields.add(builtArea);
    taskFormFields.add(dueDate);
    taskFormFields.add(landArea);
    taskFormFields.add(city);
    taskFormFields.add(address1);
    taskFormFields.add(address2);
    taskFormFields.add(address3);
    taskFormFields.add(builderName);
    taskFormFields.add(houseNumber);
    taskFormFields.add(streetName);
    taskFormFields.add(streetNumber);
    taskFormFields.add(remarks);
    taskFormFields.add(inspectionType);
    taskFormFields.add(inspectionAmountValue);
    taskFormFields.add(amountOfCollateral);
    taskFormFields.add(deductionRate);
    taskFormFields.add(collateralAssessment);
    taskFormFields.add(inspectorId);
    taskFormFields.add(inspectionDate);
    taskFormFields.add(ownershipType);
    taskFormFields.add(ownerCifNumber);
    taskFormFields.add(ownerName);
    taskFormFields.add(serialNumber);
    taskFormFields.add(inspSerialNumber);
    taskFormFields.add(immovableNumber);

    for (TaskFormField taskFormField : taskFormFields)
    {
      if (null == taskFormField.getFieldProperties())
      {
        taskFormField.setFieldProperties(Collections.EMPTY_LIST);
      }
    }
    return new TaskForm(TaskFormId.valueOf("14. Барьцаа засварлах - Үл хөдлөх хөрөнгө"), TaskId.valueOf("user_task_new_core_update_immovable_collateral"),
        "user_task_new_core_create_immovable_collateral", taskFormFields);
  }

  private TaskFormField createTaskFormField(String fieldId, Object value, String label, String type)
  {
    return new TaskFormField(FormFieldId.valueOf(fieldId), new FormFieldValue(value), label, type);
  }

  private void addValidation(TaskFormField taskFormField, String name, String config)
  {
    taskFormField.setFieldValidations(Collections.singletonList(new FieldValidation(name, config)));
  }

  private void addValidation(TaskFormField taskFormField, List<String> names, List<String> configs)
  {
    List<FieldValidation> validationList = new ArrayList<>();
    for (int i = 0; i < names.size(); i++)
    {
      validationList.add(new FieldValidation(names.get(i), configs.get(i)));
    }
    taskFormField.setFieldValidations(validationList);
  }

  private Map<String, List<String>> getReferenceCodes(List<String> types) throws UseCaseException
  {
    GetCollReferenceCodes getCollReferenceCodes = new GetCollReferenceCodes(newCoreBankingService);
    GetCollReferenceCodesInput input = new GetCollReferenceCodesInput(types);

    GetCollReferenceCodesOutput output = getCollReferenceCodes.execute(input);

    return output.getReferenceCodes();
  }

  private List<String> getCollCodes(String collType) throws UseCaseException
  {
    GetCollateralCodes getCollateralCodes = new GetCollateralCodes(newCoreBankingService);
    GetCollateralCodesOutput codesOutput = getCollateralCodes.execute(collType);
    return codesOutput.getCollateralCodes();
  }

  private void setFieldByReferenceCodes(String reference, TaskFormField taskFormField, Map<String, List<String>> referenceCodeResponses)
  {
    List<FieldProperty> properties = new ArrayList<>();
    String defaultValue = String.valueOf(taskFormField.getFormFieldValue().getDefaultValue());

    List<String> codeList = referenceCodeResponses.get(reference);

    for (String code : codeList)
    {
      if (code.contains(defaultValue))
      {
        taskFormField.getFormFieldValue().setDefaultValue(code);
      }
      properties.add(new FieldProperty(code, code));
    }

    taskFormField.setFieldProperties(properties);
  }

  private void setCityOptions(TaskFormField taskFormField)
  {
    List<FieldProperty> cityOptions = new ArrayList<>();
    cityOptions.add(new FieldProperty("1", "Улаанбаатар хот"));
    cityOptions.add(new FieldProperty("2", "Архангай аймаг"));
    cityOptions.add(new FieldProperty("3", "Баян-өлгий аймаг"));
    cityOptions.add(new FieldProperty("4", "Булган аймаг"));
    cityOptions.add(new FieldProperty("5", "Дархан-уул аймаг"));
    cityOptions.add(new FieldProperty("6", "Дорнод аймаг"));
    cityOptions.add(new FieldProperty("7", "Дорноговь аймаг"));
    cityOptions.add(new FieldProperty("8", "Говь-алтай аймаг"));
    cityOptions.add(new FieldProperty("9", "Говьсүмбэр аймаг"));
    cityOptions.add(new FieldProperty("10", "Хэнтий аймаг"));
    cityOptions.add(new FieldProperty("11", "Ховд аймаг"));
    cityOptions.add(new FieldProperty("12", "Хөвсгөл аймаг"));
    cityOptions.add(new FieldProperty("13", "Орхон аймаг"));
    cityOptions.add(new FieldProperty("14", "Сэлэнгэ аймаг"));
    cityOptions.add(new FieldProperty("15", "Сүхбаатар аймаг"));
    cityOptions.add(new FieldProperty("16", "Төв аймаг"));
    cityOptions.add(new FieldProperty("17", "Өмнөговь аймаг"));
    cityOptions.add(new FieldProperty("18", "Увс аймаг"));
    cityOptions.add(new FieldProperty("19", "Өвөрхангай аймаг"));
    cityOptions.add(new FieldProperty("20", "Завхан аймаг"));

    taskFormField.setFieldProperties(cityOptions);
  }
}
