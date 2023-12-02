package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
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

import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLL_TYPE_MACHINERY;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSPECTION_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSPECTOR_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.MACHINE_MODEL;
import static mn.erin.domain.bpm.BpmModuleConstants.MACHINE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MANUFACTURER;
import static mn.erin.domain.bpm.BpmModuleConstants.MANUFACTURER_YEAR;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNERSHIP_TYPE_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.OWNER_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_CITY;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_INSPTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_OWNERTYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REFERENCE_CODE_COLL_PURPOSE;
import static mn.erin.domain.bpm.BpmModuleConstants.REMARKS;
import static mn.erin.domain.bpm.BpmModuleConstants.REVIEW_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.CIF;
import static mn.erin.domain.bpm.constants.CollateralConstants.CIF_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_CODE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.FORM_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.MACHINE_MODEL_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.MACHINE_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURER_YEAR_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNERSHIP_TYPE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_CIF_NUMBER_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.REMARKS_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.REVIEW_DATE_MN;
import static mn.erin.domain.bpm.constants.CollateralConstants.SERIAL_NUM;

/**
 * @author Lkhagvadorj.A
 **/

public class GetMachineryCollateralInfo extends AbstractUseCase<String, TaskForm>
{
  private final NewCoreBankingService newCoreBankingService;
  private static final String STRING = "string";
  private static final String DATE = "date";
  private static final String BIG_DECIMAL = "BigDecimal";
  private static final String MAX_LENGTH = "maxlength";

  public GetMachineryCollateralInfo(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core Banking Service is required!");
  }

  @Override
  public TaskForm execute(String input) throws UseCaseException
  {
    try
    {
      Map<String, Object> machineryCollateral = newCoreBankingService.getMachineryCollateral(input);
      return toTaskForm(machineryCollateral);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private TaskForm toTaskForm(Map<String, Object> machineryCollateral) throws UseCaseException
  {
    Collection<TaskFormField> taskFormFields = new ArrayList<>();

    FieldValidation required = new FieldValidation("required", "");
    FieldValidation maxlength21 = new FieldValidation(MAX_LENGTH, "21");
    FieldValidation readonly = new FieldValidation("readonly", "");

    Map<String, List<String>> referenceCodeResponses = getReferenceCodes(Arrays
        .asList(REFERENCE_CODE_COLL_OWNERTYPE,
            REFERENCE_CODE_COLL_INSPTYPE,
            REFERENCE_CODE_COLL_CITY,
            REFERENCE_CODE_COLL_PURPOSE));

    // collateralCode
    Object collateralCodeValue = machineryCollateral.get(COLLATERAL_CODE_FORM_FIELD_ID);
    TaskFormField collateralCode = new TaskFormField(FormFieldId.valueOf(COLLATERAL_CODE_FORM_FIELD_ID), new FormFieldValue(collateralCodeValue),
        COLLATERAL_CODE_MN, STRING);
    collateralCode.setFieldValidations(Arrays.asList(required, readonly));
    List<String> collCodes = getCollCodes(COLL_TYPE_MACHINERY);
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

    // formNum
    Object formNumValue = machineryCollateral.get(FORM_NUMBER);
    TaskFormField formNum = new TaskFormField(FormFieldId.valueOf(FORM_NUMBER), new FormFieldValue(formNumValue), FORM_NUMBER_MN, STRING);
    formNum.setFieldValidations(Collections.singletonList(maxlength21));

    Object manufacturerValue = machineryCollateral.get(MANUFACTURER);
    TaskFormField manufacturer = new TaskFormField(FormFieldId.valueOf(MANUFACTURER), new FormFieldValue(manufacturerValue), MANUFACTURER_MN, STRING);
    manufacturer.setFieldValidations(Collections.singletonList(maxlength21));

    Object machineModelValue = machineryCollateral.get(MACHINE_MODEL);
    TaskFormField machineModel = new TaskFormField(FormFieldId.valueOf(MACHINE_MODEL), new FormFieldValue(machineModelValue), MACHINE_MODEL_MN,
        STRING);
    machineModel.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "16")));

    Object machineNumValue = machineryCollateral.get(MACHINE_NUMBER);
    TaskFormField machineNum = new TaskFormField(FormFieldId.valueOf(MACHINE_NUMBER), new FormFieldValue(machineNumValue), MACHINE_NUMBER_MN, STRING);
    machineNum.setFieldValidations(Arrays.asList(required, maxlength21));

    Object manufactureYearValue = machineryCollateral.get(MANUFACTURER_YEAR);
    TaskFormField manufactureYear = new TaskFormField(FormFieldId.valueOf(MANUFACTURER_YEAR), new FormFieldValue(manufactureYearValue), MANUFACTURER_YEAR_MN,
        STRING);
    manufactureYear.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "7")));

    Object custIdValue = machineryCollateral.get(CIF);
    TaskFormField custId = new TaskFormField(FormFieldId.valueOf(CIF), new FormFieldValue(custIdValue), CIF_MN, STRING);
    custId.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "51")));

    Object remarksValue = machineryCollateral.get(REMARKS);
    TaskFormField remarks = new TaskFormField(FormFieldId.valueOf(REMARKS), new FormFieldValue(remarksValue), REMARKS_MN, STRING);
    remarks.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "241")));

    Object reviewDateValue = machineryCollateral.get(REVIEW_DATE);
    TaskFormField reviewDate = new TaskFormField(FormFieldId.valueOf(REVIEW_DATE), new FormFieldValue(reviewDateValue), REVIEW_DATE_MN, DATE);
    reviewDate.setFieldValidations(Collections.EMPTY_LIST);

    // inspectionType
    Object inspectionTypeValue = machineryCollateral.get(BpmModuleConstants.INSPECTION_TYPE_CODE_FORM_FIELD_ID);
    TaskFormField inspectionType = new TaskFormField(FormFieldId.valueOf(BpmModuleConstants.INSPECTION_TYPE_CODE_FORM_FIELD_ID),
        new FormFieldValue(inspectionTypeValue), INSPECTION_TYPE_MN, STRING);
    inspectionType.setFieldValidations(Collections.singletonList(required));
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_INSPTYPE, inspectionType, referenceCodeResponses);

    Object inspectionAmountValue = machineryCollateral.get(INSPECTION_AMOUNT_VALUE);
    TaskFormField inspectionAmount = new TaskFormField(FormFieldId.valueOf(INSPECTION_AMOUNT_VALUE), new FormFieldValue(inspectionAmountValue),
        INSPECTION_AMOUNT_VALUE_MN, BIG_DECIMAL);
    inspectionAmount.setFieldValidations(Collections.singletonList(required));

    Object amountOfCollateralValue = machineryCollateral.get(AMOUNT_OF_COLLATERAL);
    TaskFormField amountOfCollateral = new TaskFormField(FormFieldId.valueOf(AMOUNT_OF_COLLATERAL),
        new FormFieldValue(amountOfCollateralValue), AMOUNT_OF_COLLATERAL_MN, BIG_DECIMAL);
    amountOfCollateral.setFieldValidations(Collections.singletonList(required));

    Object deductionRateValue = machineryCollateral.get(DEDUCTION_RATE);
    TaskFormField deductionRate = new TaskFormField(FormFieldId.valueOf(DEDUCTION_RATE), new FormFieldValue(deductionRateValue), DEDUCTION_RATE_MN, STRING);
    deductionRate.setFieldValidations(Collections.singletonList(readonly));

    Object collateralAssessmentValue = machineryCollateral.get(COLLATERAL_ASSESSMENT);
    TaskFormField collateralAssessment = new TaskFormField(FormFieldId.valueOf(COLLATERAL_ASSESSMENT), new FormFieldValue(collateralAssessmentValue),
        COLLATERAL_ASSESSMENT_MN,
        BIG_DECIMAL);
    collateralAssessment.setFieldValidations(Collections.singletonList(readonly));

    Object inspectorIdValue = machineryCollateral.get(INSPECTOR_ID);
    TaskFormField inspectorId = new TaskFormField(FormFieldId.valueOf(INSPECTOR_ID), new FormFieldValue(inspectorIdValue), INSPECTOR_ID_MN, STRING);
    inspectorId.setFieldValidations(Arrays.asList(required, new FieldValidation(MAX_LENGTH, "6")));

    Object inspectionDateValue = machineryCollateral.get(INSPECTION_DATE);
    TaskFormField inspectionDate = new TaskFormField(FormFieldId.valueOf(INSPECTION_DATE), new FormFieldValue(inspectionDateValue), INSPECTION_DATE_MN,
        DATE);
    inspectionDate.setFieldValidations(Collections.EMPTY_LIST);

    // ownershipType
    Object ownershipTypeValue = machineryCollateral.get(OWNERSHIP_TYPE_CODE_FORM_FIELD_ID);
    TaskFormField ownershipType = new TaskFormField(FormFieldId.valueOf(OWNERSHIP_TYPE_CODE_FORM_FIELD_ID), new FormFieldValue(ownershipTypeValue),
        OWNERSHIP_TYPE_MN, STRING);
    ownershipType.setFieldValidations(Collections.EMPTY_LIST);
    setFieldByReferenceCodes(REFERENCE_CODE_COLL_OWNERTYPE, ownershipType, referenceCodeResponses);

    Object ownerCifNumberValue = machineryCollateral.get(OWNER_CIF_NUMBER);
    if (String.valueOf(ownerCifNumberValue).equals("null"))
    {
      ownerCifNumberValue = null;
    }
    TaskFormField ownerCifNumber = new TaskFormField(FormFieldId.valueOf(OWNER_CIF_NUMBER), new FormFieldValue(ownerCifNumberValue), OWNER_CIF_NUMBER_MN,
        STRING);
    ownerCifNumber.setFieldValidations(Collections.singletonList(new FieldValidation(MAX_LENGTH, "51")));

    Object ownerNameValue = machineryCollateral.get(OWNER_NAME);
    TaskFormField ownerName = new TaskFormField(FormFieldId.valueOf(OWNER_NAME), new FormFieldValue(ownerNameValue), OWNER_NAME_MN, STRING);
    ownerName.setFieldValidations(Arrays.asList(required, new FieldValidation(MAX_LENGTH, "81")));

    Object serialNumberValue = machineryCollateral.get(SERIAL_NUM);
    TaskFormField serialNumber = new TaskFormField(FormFieldId.valueOf(SERIAL_NUM), new FormFieldValue(serialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    Object inspSerialNumberValue = machineryCollateral.get(INSPECTION_SERIAL_NUM);
    TaskFormField inspSerialNumber = new TaskFormField(FormFieldId.valueOf(INSPECTION_SERIAL_NUM), new FormFieldValue(inspSerialNumberValue), "", STRING);
    serialNumber.setFieldValidations(Collections.EMPTY_LIST);

    taskFormFields.add(collateralCode);
    taskFormFields.add(formNum);
    taskFormFields.add(manufacturer);
    taskFormFields.add(machineNum);
    taskFormFields.add(machineModel);
    taskFormFields.add(manufactureYear);
    taskFormFields.add(custId);
    taskFormFields.add(remarks);
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
    return new TaskForm(TaskFormId.valueOf("14. Барьцаа засварлах - Тоног төхөөрөмж"), TaskId.valueOf("user_task_new_core_update_machinery_collateral"),
        "user_task_new_core_create_machinery_collateral", taskFormFields);
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
