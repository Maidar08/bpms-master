package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.CreateCollateralInput;
import mn.erin.domain.bpm.usecase.collateral.CreateImmovableCollateral;
import mn.erin.domain.bpm.usecase.process.DeleteProcessParameters;
import mn.erin.domain.bpm.usecase.process.DeleteProcessParametersInput;

import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.ADDRESS_1;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.ADDRESS_2;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.ADDRESS_3;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.BUILDER_NAME;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.BUILT_AREA;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.CITY;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.COLLATERAL_CODE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.CONDITION_OF_CONTRACT;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DELIMITER_COLON;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DELIMITER_PERCENT_SIGN;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DUE_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.FORM_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.HOUSE_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTOR_ID;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.LAND_AREA;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.LEASED_IND;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_NAME;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.PROPERTY_DOC_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.PROPERTY_ID;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.REMARKS;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.REVIEW_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.STREET_NAME;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.STREET_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.YEAR_OF_CONSTRUCTION;
import static mn.erin.bpms.loan.consumption.utils.CreateCollateralUtils.getFirstValueByDelimiter;
import static mn.erin.bpms.loan.consumption.utils.CreateCollateralUtils.removeFormValues;
import static mn.erin.bpms.loan.consumption.utils.CustomDateUtils.dateFormatter;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toImmovablePropCollateralInfoMap;

/**
 * @author Tamir
 */
public class CreateImmovableCollateralServiceTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateImmovableCollateralServiceTask.class);
  private final String[] formVariableNames = new String[] {
      "collateralType", "collateralCode", "reviewDate", "yearOfConstruction", "conditionOfContract", "purposeOfUsage", "formNumber", "propertyId",
      "propertyDocNumber",
      "builtArea", "dueDate", "landArea", "city", "address1", "address2", "builderName", "houseNumber", "address3",
      "streetName", "streetNumber", "remarks", "inspectionType", "inspectionAmountValue", "amountOfCollateral", "deductionRate",
      "collateralAssessment", "inspectorId", "inspectionDate", "ownershipType", "ownerCifNumber", "ownerName", "immovableNumber"
  };

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;
  private final NewCoreBankingService newCoreBankingService;

  public CreateImmovableCollateralServiceTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository, NewCoreBankingService newCoreBankingService)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRepository = processRepository;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    CreateImmovableCollateral createImmovableCollateral = new CreateImmovableCollateral(newCoreBankingService);
    Map<String, Object> variables = execution.getVariables();
    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);
    String createdCollId = createImmovableCollateral.execute(toImmovablePropCollateralInfoMap(variables, caseVariables));
    LOG.info("###### SUCCESSFUL CREATED IMMOVABLE COLLATERAL = [{}] with REQUEST ID = [{}]", createdCollId, requestId);

    removeFormValues(caseInstanceId, execution, formVariableNames);
    removeProcessParameters(caseInstanceId, Arrays.asList(formVariableNames));
  }

  private void removeProcessParameters(String caseInstanceId, List<String> parameterNames) throws UseCaseException
  {
    DeleteProcessParameters useCase = new DeleteProcessParameters(authenticationService, authorizationService, processRepository);

    Map<ParameterEntityType, List<String>> deleteParams = new HashMap<>();
    deleteParams.put(ParameterEntityType.FORM, parameterNames);

    useCase.execute(new DeleteProcessParametersInput(caseInstanceId, deleteParams));
  }

  private CreateCollateralInput createInput(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> genericInfo = getGenericInfo(execution);
    Map<String, Object> collateralInfo = getImmovableCollInfo(execution);

    Map<String, Object> inspectionInfo = getInspectionInfo(execution);
    Map<String, Object> ownershipInfo = getOwnershipInfo(execution);

    return new CreateCollateralInput(genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
  }

  private Map<String, Object> getGenericInfo(DelegateExecution execution) throws BpmServiceException
  {
    String collateralCodeDesc = String.valueOf(execution.getVariable(COLLATERAL_CODE));
    String collateralCode = getFirstValueByDelimiter(collateralCodeDesc, DELIMITER_COLON,
        "Collateral code is invalid =" + collateralCodeDesc);

    String deductionRateWithPercent = String.valueOf(execution.getVariable(DEDUCTION_RATE));
    String deductionRate = getFirstValueByDelimiter(deductionRateWithPercent, DELIMITER_PERCENT_SIGN,
        "Deduction rate is invalid! =" + deductionRateWithPercent);

    Map<String, Object> genericInfo = new HashMap<>();

    genericInfo.put(COLLATERAL_CODE, collateralCode);
    genericInfo.put(DEDUCTION_RATE, deductionRate);

    return genericInfo;
  }

  private Map<String, Object> getImmovableCollInfo(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> immovableCollInfo = new HashMap<>();

    String houseNumber = String.valueOf(execution.getVariable(HOUSE_NUMBER));
    String purposeDesc = String.valueOf(execution.getVariable(PURPOSE_OF_USAGE));
    String purposeCode = getFirstValueByDelimiter(purposeDesc, DELIMITER_COLON, "Purpose of usage is invalid =" + purposeDesc);

    String formNumber = String.valueOf(execution.getVariable(FORM_NUMBER));
    String propertyId = String.valueOf(execution.getVariable(PROPERTY_ID));

    immovableCollInfo.put(HOUSE_NUMBER, houseNumber);
    immovableCollInfo.put(PURPOSE_OF_USAGE, purposeCode);

    immovableCollInfo.put(FORM_NUMBER, formNumber);
    immovableCollInfo.put(PROPERTY_ID, propertyId);

    String propertyDocNumber = String.valueOf(execution.getVariable(PROPERTY_DOC_NUMBER));
    String builtArea = String.valueOf(execution.getVariable(BUILT_AREA));
    String landArea = String.valueOf(execution.getVariable(LAND_AREA));
    String leasedInd = String.valueOf(execution.getVariable(LEASED_IND));

    if (leasedInd.equalsIgnoreCase(YES_MN_VALUE))
    {
      leasedInd = "Y";
    }
    else
    {
      leasedInd = "N";
    }

    Date reviewDate = (Date) execution.getVariable(REVIEW_DATE);
    Date dueDate = (Date) execution.getVariable(DUE_DATE);

    String reviewDateFormatted = "";
    String dueDateFormatted = "";

    if (null != reviewDate)
    {
      reviewDateFormatted = dateFormatter.format(reviewDate);
    }

    if (null != dueDate)
    {
      dueDateFormatted = dateFormatter.format(dueDate);
    }

    immovableCollInfo.put(PROPERTY_DOC_NUMBER, propertyDocNumber);
    immovableCollInfo.put(BUILT_AREA, builtArea);
    immovableCollInfo.put(LAND_AREA, landArea);
    immovableCollInfo.put(LEASED_IND, leasedInd);
    immovableCollInfo.put(REVIEW_DATE, reviewDateFormatted);
    immovableCollInfo.put(DUE_DATE, dueDateFormatted);

    String conditionOfContract = String.valueOf(execution.getVariable(CONDITION_OF_CONTRACT));
    String remarks = String.valueOf(execution.getVariable(REMARKS));
    String yearOfConstruction = String.valueOf(execution.getVariable(YEAR_OF_CONSTRUCTION));

    String district = String.valueOf(execution.getVariable(ADDRESS_1));
    String quarter = String.valueOf(execution.getVariable(ADDRESS_2));
    String localityCode = String.valueOf(execution.getVariable(ADDRESS_3));

    immovableCollInfo.put(CONDITION_OF_CONTRACT, conditionOfContract);
    immovableCollInfo.put(REMARKS, remarks);
    immovableCollInfo.put(YEAR_OF_CONSTRUCTION, yearOfConstruction);

    immovableCollInfo.put(ADDRESS_1, district);
    immovableCollInfo.put(ADDRESS_2, quarter);
    immovableCollInfo.put(ADDRESS_3, localityCode);

    String city = String.valueOf(execution.getVariable(CITY));
    String streetName = String.valueOf(execution.getVariable(STREET_NAME));
    String streetNumber = String.valueOf(execution.getVariable(STREET_NUMBER));
    String amountOfCollateral = String.valueOf(execution.getVariable(AMOUNT_OF_COLLATERAL));
    String collateralAssessment = String.valueOf(execution.getVariable(COLLATERAL_ASSESSMENT));
    String builderName = String.valueOf(execution.getVariable(BUILDER_NAME));

    String cityCode = getFirstValueByDelimiter(city, DELIMITER_COLON, "City description is invalid = " + city);

    immovableCollInfo.put(CITY, cityCode);
    immovableCollInfo.put(STREET_NAME, streetName);

    immovableCollInfo.put(STREET_NUMBER, streetNumber);
    immovableCollInfo.put(AMOUNT_OF_COLLATERAL, amountOfCollateral);

    immovableCollInfo.put(COLLATERAL_ASSESSMENT, collateralAssessment);
    immovableCollInfo.put(BUILDER_NAME, builderName);

    immovableCollInfo.put(CIF_NUMBER, String.valueOf(execution.getVariable(CIF_NUMBER)));

    return immovableCollInfo;
  }

  private Map<String, Object> getInspectionInfo(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> inspectionInfo = new HashMap<>();

    String inspectionTypeDesc = String.valueOf(execution.getVariable(INSPECTION_TYPE));
    String typeCode = getFirstValueByDelimiter(inspectionTypeDesc, DELIMITER_COLON, "Inspection type description is invalid!");

    String inspectionAmountValue = String.valueOf(execution.getVariable(INSPECTION_AMOUNT_VALUE));
    String inspectorId = String.valueOf(execution.getVariable(INSPECTOR_ID));
    Date inspectionDate = (Date) execution.getVariable(INSPECTION_DATE);

    String inspectionDateFormatted = "";

    if (null == inspectionDate)
    {
      inspectionDateFormatted = dateFormatter.format(inspectionDate);
    }

    inspectionInfo.put(INSPECTION_TYPE, typeCode);
    inspectionInfo.put(INSPECTION_AMOUNT_VALUE, inspectionAmountValue);

    inspectionInfo.put(INSPECTOR_ID, inspectorId);
    inspectionInfo.put(INSPECTION_DATE, inspectionDateFormatted);

    return inspectionInfo;
  }

  private Map<String, Object> getOwnershipInfo(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> ownershipInfo = new HashMap<>();

    String ownershipTypeDesc = String.valueOf(execution.getVariable(OWNERSHIP_TYPE));
    String typeCode = getFirstValueByDelimiter(ownershipTypeDesc, DELIMITER_COLON, "Ownership description is invalid!");

    String ownerCifNumber = String.valueOf(execution.getVariable(OWNER_CIF_NUMBER));
    String ownerName = String.valueOf(execution.getVariable(OWNER_NAME));

    if (ownerCifNumber.equalsIgnoreCase("null"))
    {
      ownerCifNumber = "";
    }

    ownershipInfo.put(OWNERSHIP_TYPE, typeCode);
    ownershipInfo.put(OWNER_CIF_NUMBER, ownerCifNumber);
    ownershipInfo.put(OWNER_NAME, ownerName);

    return ownershipInfo;
  }
}
