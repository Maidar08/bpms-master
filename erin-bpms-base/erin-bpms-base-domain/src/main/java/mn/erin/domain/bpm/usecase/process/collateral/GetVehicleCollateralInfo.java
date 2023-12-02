package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
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

import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_INPUT_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_VEHICLE;
import static mn.erin.domain.bpm.BpmModuleConstants.MANUFACTURER_YEAR;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNERSHIP_TYPE_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_INSPTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_LEASING_SUPPLIER;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_OWNERTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_VEHICLE_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REMARKS;
import static mn.erin.domain.bpm.BpmModuleConstants.REVIEW_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.CHASSIS_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.CHASSIS_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.CIF;
import static mn.erin.domain.bpm.constants.CollateralConstants.CIF_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_CODE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.ENGINE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.ENGINE_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.FINANCIAL_LEASING_SUPPLIER;
import static mn.erin.domain.bpm.constants.CollateralConstants.FINANCIAL_LEASING_SUPPLIER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.FORM_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.FORM_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURE_YEAR_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNERSHIP_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_CIF_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.domain.bpm.constants.CollateralConstants.REMARKS_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.REVIEW_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_ADDRESS_1_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_ADDRESS_2_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_ADDRESS_3_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_MODEL;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_MODEL_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_REGISTER_NUMBER_MN;

public class GetVehicleCollateralInfo extends AbstractUseCase<String, TaskForm>
{
  private final NewCoreBankingService newCoreBankingService;
  private static final String STRING = "string";
  private static final String DATE = "date";
  private static final String BIG_DECIMAL = "BigDecimal";
  private static final String MAX_LENGTH = "maxlength";

  public GetVehicleCollateralInfo(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public TaskForm execute(String input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INVALID_INPUT_CODE, INVALID_INPUT_MESSAGE);
    }
    try
    {
      Map<String, Object> vehicleCollateralInfo = newCoreBankingService.getVehicleCollInfo(input);
      return toTaskForm(vehicleCollateralInfo);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private TaskForm toTaskForm(Map<String, Object> vehicleCollateral) throws UseCaseException
  {
    Collection<TaskFormField> taskFormFields = new ArrayList<>();

    FieldValidation required = new FieldValidation("required", "");
    FieldValidation maxlength21 = new FieldValidation(MAX_LENGTH, "21");
    FieldValidation readonly = new FieldValidation("readonly", "");

    Map<String, List<String>> referenceCodeResponses = getReferenceCodes(Arrays
        .asList(REFERENCE_CODE_COLL_OWNERTYPE,
            REFERENCE_CODE_COLL_INSPTYPE,
            REFERENCE_CODE_COLL_LEASING_SUPPLIER,
            REFERENCE_CODE_COLL_VEHICLE_TYPE));

    Object collateralCodeValue = vehicleCollateral.get(COLLATERAL_CODE_FORM_FIELD_ID);
    TaskFormField collateralCode = new TaskFormField(FormFieldId.valueOf(COLLATERAL_CODE_FORM_FIELD_ID), new FormFieldValue(collateralCodeValue),
        COLLATERAL_CODE_MN, STRING);
    collateralCode.setFieldValidations(Arrays.asList(required, readonly));
    List<String> collateralCodes = getCollCodes(COLL_TYPE_VEHICLE);
    List<FieldProperty> collIdProperties = new ArrayList<>();

    for (String collCode : collateralCodes)
    {
      if (collCode.contains(String.valueOf(collateralCodeValue)))
      {
        collateralCode.getFormFieldValue().setDefaultValue(collCode);
      }
      collIdProperties.add(new FieldProperty(collCode, collCode));
    }
    collateralCode.setFieldProperties(collIdProperties);

    // vehicle collateral info

    Object vehicleRegNumValue = vehicleCollateral.get(VEHICLE_REGISTER_NUMBER);
    TaskFormField vehicleRegisterNumber = new TaskFormField(FormFieldId.valueOf(VEHICLE_REGISTER_NUMBER), new FormFieldValue(vehicleRegNumValue),
        VEHICLE_REGISTER_NUMBER_MN, STRING);
    vehicleRegisterNumber.setFieldValidations(Arrays.asList(required, maxlength21));

    Object formNumberValue = vehicleCollateral.get(FORM_NUMBER);
    TaskFormField formNumber = new TaskFormField(FormFieldId.valueOf(FORM_NUMBER), new FormFieldValue(formNumberValue),
        FORM_NUMBER_MN, STRING);
    formNumber.setFieldValidations(Collections.singletonList(maxlength21));

    Object chassisNumberValue = vehicleCollateral.get(CHASSIS_NUMBER);
    TaskFormField chassisNumber = new TaskFormField(FormFieldId.valueOf(CHASSIS_NUMBER), new FormFieldValue(chassisNumberValue),
        CHASSIS_NUMBER_MN, STRING);
    chassisNumber.setFieldValidations(Arrays.asList(required, maxlength21));

    Object engineNumberValue = vehicleCollateral.get(ENGINE_NUMBER);
    TaskFormField engineNumber = new TaskFormField(FormFieldId.valueOf(ENGINE_NUMBER), new FormFieldValue(engineNumberValue),
        ENGINE_NUMBER_MN, STRING);
    engineNumber.setFieldValidations(Arrays.asList(required, maxlength21));

    Object manufactureYearValue = vehicleCollateral.get(MANUFACTURER_YEAR);
    TaskFormField manufactureYear = new TaskFormField(FormFieldId.valueOf(MANUFACTURER_YEAR), new FormFieldValue(manufactureYearValue), MANUFACTURE_YEAR_MN,
        STRING);
    manufactureYear.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "7")));

    Object vehicleModelValue = vehicleCollateral.get(VEHICLE_MODEL);
    TaskFormField vehicleModel = new TaskFormField(FormFieldId.valueOf(VEHICLE_MODEL), new FormFieldValue(vehicleModelValue), VEHICLE_MODEL_MN,
        STRING);
    vehicleModel.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "16")));

    Object finLeasingSupplierValue = vehicleCollateral.get(FINANCIAL_LEASING_SUPPLIER);
    TaskFormField finLeasingSupplier = new TaskFormField(FormFieldId.valueOf(FINANCIAL_LEASING_SUPPLIER), new FormFieldValue(finLeasingSupplierValue),
        FINANCIAL_LEASING_SUPPLIER_MN,
        STRING);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_LEASING_SUPPLIER, finLeasingSupplier, referenceCodeResponses);

    Object purposeValue = vehicleCollateral.get(PURPOSE_OF_USAGE);
    TaskFormField purpose = new TaskFormField(FormFieldId.valueOf(PURPOSE_OF_USAGE), new FormFieldValue(purposeValue), PURPOSE_MN,
        STRING);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_VEHICLE_TYPE, purpose, referenceCodeResponses);

    Object customerCifValue = vehicleCollateral.get(CIF);
    TaskFormField customerCif = new TaskFormField(FormFieldId.valueOf(CIF), new FormFieldValue(customerCifValue), CIF_MN, STRING);
    customerCif.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "51")));

    Object remarksValue = vehicleCollateral.get(REMARKS);
    TaskFormField remarks = new TaskFormField(FormFieldId.valueOf(REMARKS), new FormFieldValue(remarksValue), REMARKS_MN, STRING);
    remarks.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "241")));

    Object reviewDateValue = vehicleCollateral.get(REVIEW_DATE);
    TaskFormField reviewDate = new TaskFormField(FormFieldId.valueOf(REVIEW_DATE), new FormFieldValue(reviewDateValue), REVIEW_DATE_MN, DATE);
    reviewDate.setFieldValidations(Collections.EMPTY_LIST);

    Object address1Value = vehicleCollateral.get(ADDRESS_1);
    TaskFormField address1 = new TaskFormField(FormFieldId.valueOf(ADDRESS_1), new FormFieldValue(address1Value), VEHICLE_ADDRESS_1_MN, STRING);
    address1.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "46")));

    Object address2Value = vehicleCollateral.get(ADDRESS_2);
    TaskFormField address2 = new TaskFormField(FormFieldId.valueOf(ADDRESS_2), new FormFieldValue(address2Value), VEHICLE_ADDRESS_2_MN, STRING);
    address2.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "46")));

    Object address3Value = vehicleCollateral.get(ADDRESS_3);
    TaskFormField address3 = new TaskFormField(FormFieldId.valueOf(ADDRESS_3), new FormFieldValue(address3Value), VEHICLE_ADDRESS_3_MN, STRING);
    address3.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "46")));

    // inspectionType
    Object inspectionTypeValue = vehicleCollateral.get(INSPECTION_TYPE);
    TaskFormField inspectionType = new TaskFormField(FormFieldId.valueOf(INSPECTION_TYPE),
        new FormFieldValue(inspectionTypeValue), INSPECTION_TYPE_MN, STRING);
    inspectionType.setFieldValidations(Collections.singletonList(required));
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_INSPTYPE, inspectionType, referenceCodeResponses);

    Object inspectionAmountValue = vehicleCollateral.get(INSPECTION_AMOUNT_VALUE);
    TaskFormField inspectionAmount = new TaskFormField(FormFieldId.valueOf(INSPECTION_AMOUNT_VALUE), new FormFieldValue(inspectionAmountValue),
        INSPECTION_AMOUNT_VALUE_MN, BIG_DECIMAL);
    inspectionAmount.setFieldValidations(Collections.singletonList(required));

    Object amountOfCollateralValue = vehicleCollateral.get(AMOUNT_OF_COLLATERAL);
    TaskFormField amountOfCollateral = new TaskFormField(FormFieldId.valueOf(AMOUNT_OF_COLLATERAL),
        new FormFieldValue(amountOfCollateralValue), AMOUNT_OF_COLLATERAL_MN, BIG_DECIMAL);
    amountOfCollateral.setFieldValidations(Collections.singletonList(required));

    Object deductionRateValue = vehicleCollateral.get(DEDUCTION_RATE);
    TaskFormField deductionRate = new TaskFormField(FormFieldId.valueOf(DEDUCTION_RATE), new FormFieldValue(deductionRateValue), DEDUCTION_RATE_MN, STRING);
    deductionRate.setFieldValidations(Collections.singletonList(readonly));

    Object collateralAssessmentValue = vehicleCollateral.get(COLLATERAL_ASSESSMENT);
    TaskFormField collateralAssessment = new TaskFormField(FormFieldId.valueOf(COLLATERAL_ASSESSMENT), new FormFieldValue(collateralAssessmentValue),
        COLLATERAL_ASSESSMENT_MN,
        BIG_DECIMAL);
    collateralAssessment.setFieldValidations(Collections.singletonList(readonly));

    Object inspectorIdValue = vehicleCollateral.get(INSPECTOR_ID);
    TaskFormField inspectorId = new TaskFormField(FormFieldId.valueOf(INSPECTOR_ID), new FormFieldValue(inspectorIdValue), INSPECTOR_ID_MN, STRING);
    inspectorId.setFieldValidations(Arrays.asList(required, new FieldValidation(MAX_LENGTH, "6")));

    Object inspectionDateValue = vehicleCollateral.get(INSPECTION_DATE);
    TaskFormField inspectionDate = new TaskFormField(FormFieldId.valueOf(INSPECTION_DATE), new FormFieldValue(inspectionDateValue), INSPECTION_DATE_MN,
        DATE);
    inspectionDate.setFieldValidations(Collections.EMPTY_LIST);

    // ownershipType
    Object ownershipTypeValue = vehicleCollateral.get(OWNERSHIP_TYPE_CODE_FORM_FIELD_ID);
    TaskFormField ownershipType = new TaskFormField(FormFieldId.valueOf(OWNERSHIP_TYPE_CODE_FORM_FIELD_ID), new FormFieldValue(ownershipTypeValue),
        OWNERSHIP_TYPE_MN, STRING);
    ownershipType.setFieldValidations(Collections.EMPTY_LIST);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_OWNERTYPE, ownershipType, referenceCodeResponses);

    Object ownerCifNumberValue = vehicleCollateral.get(OWNER_CIF_NUMBER);
    if (String.valueOf(ownerCifNumberValue).equals("null"))
    {
      ownerCifNumberValue = null;
    }
    TaskFormField ownerCifNumber = new TaskFormField(FormFieldId.valueOf(OWNER_CIF_NUMBER), new FormFieldValue(ownerCifNumberValue), OWNER_CIF_NUMBER_MN,
        STRING);
    ownerCifNumber.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "51")));

    Object ownerNameValue = vehicleCollateral.get(OWNER_NAME);
    TaskFormField ownerName = new TaskFormField(FormFieldId.valueOf(OWNER_NAME), new FormFieldValue(ownerNameValue), OWNER_NAME_MN, STRING);
    ownerName.setFieldValidations(Arrays.asList(required, new FieldValidation(MAX_LENGTH, "81")));

    Object serialNumberValue = vehicleCollateral.get(SERIAL_NUM);
    TaskFormField serialNumber = new TaskFormField(FormFieldId.valueOf(SERIAL_NUM), new FormFieldValue(serialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    Object inspSerialNumberValue = vehicleCollateral.get(INSPECTION_SERIAL_NUM);
    TaskFormField inspSerialNumber = new TaskFormField(FormFieldId.valueOf(INSPECTION_SERIAL_NUM), new FormFieldValue(inspSerialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    taskFormFields.add(collateralCode);
    taskFormFields.add(vehicleRegisterNumber);
    taskFormFields.add(chassisNumber);
    taskFormFields.add(engineNumber);
    taskFormFields.add(formNumber);
    taskFormFields.add(manufactureYear);
    taskFormFields.add(vehicleModel);
    taskFormFields.add(finLeasingSupplier);
    taskFormFields.add(purpose);
    taskFormFields.add(customerCif);
    taskFormFields.add(remarks);
    taskFormFields.add(address1);
    taskFormFields.add(address2);
    taskFormFields.add(address3);
    taskFormFields.add(reviewDate);
    taskFormFields.add(inspectionType);
    taskFormFields.add(inspectionAmount);
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

    for (TaskFormField taskFormField : taskFormFields)
    {
      if (null == taskFormField.getFieldProperties())
      {
        taskFormField.setFieldProperties(Collections.EMPTY_LIST);
      }
    }
    return new TaskForm(TaskFormId.valueOf("14. Барьцаа засварлах - Тээврийн хэрэгсэл"), TaskId.valueOf("user_task_new_core_update_vehicle_collateral"),
        "user_task_new_core_create_vehicle_collateral", taskFormFields);
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

  private Map<String, List<String>> getReferenceCodes(List<String> types) throws UseCaseException
  {
    GetCollReferenceCodes getCollReferenceCodes = new GetCollReferenceCodes(newCoreBankingService);
    GetCollReferenceCodesInput input = new GetCollReferenceCodesInput(types);

    GetCollReferenceCodesOutput output = getCollReferenceCodes.execute(input);

    return output.getReferenceCodes();
  }
}
