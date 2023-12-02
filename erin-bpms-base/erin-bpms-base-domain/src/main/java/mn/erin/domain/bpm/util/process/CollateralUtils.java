package mn.erin.domain.bpm.util.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.usecase.collateral.CreateCollateralInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3;
import static mn.erin.domain.bpm.constants.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILDER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILT_AREA;
import static mn.erin.domain.bpm.constants.CollateralConstants.CHASSIS_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.CIF;
import static mn.erin.domain.bpm.constants.CollateralConstants.CITY;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.constants.CollateralConstants.COLLATERAL_CODE;
import static mn.erin.domain.bpm.constants.CollateralConstants.CONDITION_OF_CONTRACT;
import static mn.erin.domain.bpm.constants.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.DELIMITER_COLON;
import static mn.erin.domain.bpm.constants.CollateralConstants.DELIMITER_PERCENT_SIGN;
import static mn.erin.domain.bpm.constants.CollateralConstants.DUE_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ENGINE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.FINANCIAL_LEASING_SUPPLIER;
import static mn.erin.domain.bpm.constants.CollateralConstants.FORM_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.HOUSE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.IMMOVABLE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.INSPECTOR_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.LAND_AREA;
import static mn.erin.domain.bpm.constants.CollateralConstants.LEASED_IND;
import static mn.erin.domain.bpm.constants.CollateralConstants.MACHINE_MODEL;
import static mn.erin.domain.bpm.constants.CollateralConstants.MACHINE_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURER;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURER_YEAR;
import static mn.erin.domain.bpm.constants.CollateralConstants.MANUFACTURE_YEAR;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.OWNER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_DOC_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.PROPERTY_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.domain.bpm.constants.CollateralConstants.RECEIVED_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.REMARKS;
import static mn.erin.domain.bpm.constants.CollateralConstants.REVIEW_DATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.SERIAL_NUM;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_MODEL;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.constants.CollateralConstants.XAC_SERVICE_DATE_FORMATTER_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.YEAR_OF_CONSTRUCTION;
import static mn.erin.domain.bpm.util.process.BpmUtils.getFirstValueByDelimiter;
import static mn.erin.domain.bpm.util.process.BpmUtils.getReadOnlyFieldValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;

/**
 * @author Lkhagvadorj.A
 **/

public class CollateralUtils
{
  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(XAC_SERVICE_DATE_FORMATTER_2);

  private CollateralUtils()
  {
  }

  public static CreateCollateralInput toMachineryCollateralInfoMap(Map<String, Object> variables, Map<String, Object> caseVariables)
      throws BpmServiceException, ParseException
  {
    Map<String, Object> colInfo = setColInfo(variables);
    Map<String, Object> machineryInfo = setMachineryInfo(variables, caseVariables);
    Map<String, Object> inspectionInfo = setInspectionInfo(variables);
    Map<String, Object> ownershipInfo = setOwnershipInfo(variables);
    return new CreateCollateralInput(colInfo, machineryInfo, inspectionInfo, ownershipInfo);
  }

  public static CreateCollateralInput toOtherCollateralInfoMap(Map<String, Object> variables, Map<String, Object> caseVariables)
      throws BpmServiceException, ParseException
  {
    Map<String, Object> colInfo = setColInfo(variables);
    Map<String, Object> otherCollateralInfo = setOtherCollInfo(variables, caseVariables);
    Map<String, Object> inspectionInfo = setInspectionInfo(variables);
    Map<String, Object> ownershipInfo = setOwnershipInfo(variables);
    return new CreateCollateralInput(colInfo, otherCollateralInfo, inspectionInfo, ownershipInfo);
  }

  public static CreateCollateralInput toImmovablePropCollateralInfoMap(Map<String, Object> variables, Map<String, Object> caseVariables)
      throws BpmServiceException, ParseException
  {
    Map<String, Object> genericInfo = getGenericInfo(variables);
    Map<String, Object> collateralInfo = getImmovableCollInfo(variables, caseVariables);
    Map<String, Object> inspectionInfo = setInspectionInfo(variables);
    Map<String, Object> ownershipInfo = setOwnershipInfo(variables);

    return new CreateCollateralInput(genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
  }

  public static Map<String, Object> getGenericInfo(Map<String, Object> variables) throws BpmServiceException
  {
    String collateralCodeDesc = String.valueOf(variables.get(COLLATERAL_CODE));
    String collateralCode = getFirstValueByDelimiter(collateralCodeDesc, DELIMITER_COLON,
        "Collateral code is invalid =" + collateralCodeDesc);

    String deductionRateWithPercent = String.valueOf(variables.get(DEDUCTION_RATE));
    String deductionRate = getFirstValueByDelimiter(deductionRateWithPercent, DELIMITER_PERCENT_SIGN,
        "Deduction rate is invalid! =" + deductionRateWithPercent);

    String collateralAmount = String.valueOf(variables.get(AMOUNT_OF_COLLATERAL));

    Map<String, Object> genericInfo = new HashMap<>();

    genericInfo.put(COLLATERAL_CODE, collateralCode);
    genericInfo.put(DEDUCTION_RATE, deductionRate);
    genericInfo.put(AMOUNT_OF_COLLATERAL, collateralAmount);

    return genericInfo;
  }

  private static Map<String, Object> getImmovableCollInfo(Map<String, Object> variables, Map<String, Object> caseVariables)
      throws BpmServiceException, ParseException
  {
    Map<String, Object> immovableCollInfo = new HashMap<>();

    String houseNumber = String.valueOf(variables.get(HOUSE_NUMBER));
    String localityCode = String.valueOf(variables.get(ADDRESS_3));
    String purposeDesc = String.valueOf(variables.get(PURPOSE_OF_USAGE));
    String purposeCode = StringUtils.isBlank(purposeDesc) ?
        "" :
        getFirstValueByDelimiter(purposeDesc, DELIMITER_COLON, "Purpose of usage is invalid =" + purposeDesc);

    String formNumber = String.valueOf(variables.get(FORM_NUMBER));
    String propertyId = String.valueOf(variables.get(PROPERTY_ID));

    immovableCollInfo.put(HOUSE_NUMBER, houseNumber + '/' + localityCode);
    immovableCollInfo.put(PURPOSE_OF_USAGE, purposeCode);

    immovableCollInfo.put(FORM_NUMBER, formNumber);
    immovableCollInfo.put(PROPERTY_ID, propertyId);

    String propertyDocNumber = String.valueOf(variables.get(PROPERTY_DOC_NUMBER));
    String builtArea = String.valueOf(variables.get(BUILT_AREA));
    String landArea = String.valueOf(variables.get(LAND_AREA));
    String leasedInd = String.valueOf(variables.get(LEASED_IND));

    if (leasedInd.equalsIgnoreCase(YES_MN_VALUE))
    {
      leasedInd = "Y";
    }
    else
    {
      leasedInd = "N";
    }

    Date reviewDate;
    String reviewDateString = "";
    if (variables.get(REVIEW_DATE) instanceof String)
    {
      reviewDate = dateFormatter.parse(String.valueOf(variables.get(REVIEW_DATE)));
      reviewDateString = dateFormatter.format(reviewDate);
    }
    else if (null != variables.get(REVIEW_DATE))
    {
      reviewDate = (Date) variables.get(REVIEW_DATE);
      reviewDateString = dateFormatter.format(reviewDate);
    }

    Date dueDate;
    String dueDateString = "";
    if (variables.get(DUE_DATE) instanceof String)
    {
      dueDate = dateFormatter.parse(String.valueOf(variables.get(DUE_DATE)));
      dueDateString = dateFormatter.format(dueDate);
    }
    else if (null != variables.get(DUE_DATE))
    {
      dueDate = (Date) variables.get(DUE_DATE);
      dueDateString = dateFormatter.format(dueDate);
    }

    immovableCollInfo.put(PROPERTY_DOC_NUMBER, propertyDocNumber);
    immovableCollInfo.put(BUILT_AREA, builtArea);
    immovableCollInfo.put(LAND_AREA, landArea);
    immovableCollInfo.put(LEASED_IND, leasedInd);
    immovableCollInfo.put(REVIEW_DATE, reviewDateString);
    immovableCollInfo.put(DUE_DATE, dueDateString);

    String conditionOfContract = String.valueOf(variables.get(CONDITION_OF_CONTRACT));
    String remarks = String.valueOf(variables.get(REMARKS));
    String yearOfConstruction = String.valueOf(variables.get(YEAR_OF_CONSTRUCTION));

    String district = String.valueOf(variables.get(ADDRESS_1));
    String quarter = String.valueOf(variables.get(ADDRESS_2));

    immovableCollInfo.put(CONDITION_OF_CONTRACT, conditionOfContract);
    immovableCollInfo.put(REMARKS, remarks);
    immovableCollInfo.put(YEAR_OF_CONSTRUCTION, yearOfConstruction);

    immovableCollInfo.put(ADDRESS_1, district);
    immovableCollInfo.put(ADDRESS_2, quarter);
    immovableCollInfo.put(ADDRESS_3, localityCode);

    String city = String.valueOf(variables.get(CITY));
    String streetName = String.valueOf(variables.get(STREET_NAME));
    String streetNumber = String.valueOf(variables.get(STREET_NUMBER));
    String amountOfCollateral = String.valueOf(variables.get(AMOUNT_OF_COLLATERAL));
    String immovableNumber = String.valueOf(variables.get(IMMOVABLE_NUMBER));
    String builderName = String.valueOf(variables.get(BUILDER_NAME));

    Object collateralAssessment = variables.get(COLLATERAL_ASSESSMENT);
    String assessment = null;
    if (null == collateralAssessment)
    {
      assessment = getReadOnlyFieldValue(caseVariables, BpmModuleConstants.COLLATERAL_ASSESSMENT);
    }
    else
    {
      assessment = getStringValue(collateralAssessment);
    }

    String cityCode = getFirstValueByDelimiter(city, DELIMITER_COLON, "City description is invalid = " + city);

    immovableCollInfo.put(CITY, cityCode);
    immovableCollInfo.put(STREET_NAME, streetName);

    immovableCollInfo.put(STREET_NUMBER, streetNumber);
    immovableCollInfo.put(AMOUNT_OF_COLLATERAL, amountOfCollateral);

    immovableCollInfo.put(COLLATERAL_ASSESSMENT, assessment);
    immovableCollInfo.put(IMMOVABLE_NUMBER, immovableNumber);
    immovableCollInfo.put(BUILDER_NAME, builderName);

    immovableCollInfo.put(CIF_NUMBER, String.valueOf(variables.get(CIF_NUMBER)));

    return immovableCollInfo;
  }

  public static CreateCollateralInput toVehicleCollateralMap(Map<String, Object> variables, Map<String, Object> caseVariables)
      throws BpmServiceException, ParseException
  {
    Map<String, Object> colInfo = setColInfo(variables);
    Map<String, Object> vehicleInfo = setVehicleInfo(variables, caseVariables);
    Map<String, Object> inspectionInfo = setInspectionInfo(variables);
    Map<String, Object> ownershipInfo = setOwnershipInfo(variables);
    return new CreateCollateralInput(colInfo, vehicleInfo, inspectionInfo, ownershipInfo);
  }

  private static Map<String, Object> setVehicleInfo(Map<String, Object> variables, Map<String, Object> caseVariables) throws BpmServiceException, ParseException
  {

    String registerNumber = String.valueOf(variables.get(VEHICLE_REGISTER_NUMBER));
    String formNumber = String.valueOf(variables.get(FORM_NUMBER));
    String chassisNumber = String.valueOf(variables.get(CHASSIS_NUMBER));
    String engineNumber = String.valueOf(variables.get(ENGINE_NUMBER));
    String manufactureYear = String.valueOf(variables.get(MANUFACTURE_YEAR));
    String vehicleModel = String.valueOf(variables.get(VEHICLE_MODEL));

    String financialLeasingSupplierDesc = String.valueOf(variables.get(FINANCIAL_LEASING_SUPPLIER));
    String financialLeasingSupplier = getFirstValueByDelimiter(financialLeasingSupplierDesc, DELIMITER_COLON,
        " Financial Leasing Supplier is invalid =" + financialLeasingSupplierDesc);

    String purposeOfUsageDesc = String.valueOf(variables.get(PURPOSE_OF_USAGE));
    String purposeOfUsage = getFirstValueByDelimiter(purposeOfUsageDesc, DELIMITER_COLON,
        " Purpose of usage is invalid =" + purposeOfUsageDesc);

    String remarks = String.valueOf(variables.get(REMARKS));
    String address1 = String.valueOf(variables.get(ADDRESS_1));
    String address2 = String.valueOf(variables.get(ADDRESS_2));
    String address3 = String.valueOf(variables.get(ADDRESS_3));
    String customerCif = String.valueOf(variables.get(CIF));
    Date reviewDate;
    String reviewDateString = "";
    if (variables.get(REVIEW_DATE) instanceof String)
    {
      reviewDate = dateFormatter.parse(String.valueOf(variables.get(REVIEW_DATE)));
      reviewDateString = dateFormatter.format(reviewDate);
    }
    else if (null != variables.get(REVIEW_DATE))
    {
      reviewDate = (Date) variables.get(REVIEW_DATE);
      reviewDateString = dateFormatter.format(reviewDate);
    }
    Object collateralAssessment = variables.get(COLLATERAL_ASSESSMENT);
    String assessmentValue = null;

    if (null == collateralAssessment)
    {
      assessmentValue = getReadOnlyFieldValue(caseVariables, BpmModuleConstants.COLLATERAL_ASSESSMENT);
    }
    else {
      assessmentValue = getStringValue(collateralAssessment);
    }

    Map<String, Object> vehicleInfo = new HashMap<>();
    vehicleInfo.put(FORM_NUMBER, formNumber);
    vehicleInfo.put(VEHICLE_REGISTER_NUMBER, registerNumber);
    vehicleInfo.put(CHASSIS_NUMBER, chassisNumber);
    vehicleInfo.put(ENGINE_NUMBER, engineNumber);
    vehicleInfo.put(MANUFACTURE_YEAR, manufactureYear);
    vehicleInfo.put(VEHICLE_MODEL, vehicleModel);
    vehicleInfo.put(FINANCIAL_LEASING_SUPPLIER, financialLeasingSupplier);
    vehicleInfo.put(PURPOSE_OF_USAGE, purposeOfUsage);
    vehicleInfo.put(REMARKS, remarks);
    vehicleInfo.put(ADDRESS_1, address1);
    vehicleInfo.put(ADDRESS_2, address2);
    vehicleInfo.put(ADDRESS_3, address3);
    vehicleInfo.put(CIF, customerCif);
    vehicleInfo.put(COLLATERAL_ASSESSMENT, assessmentValue);
    vehicleInfo.put(REVIEW_DATE, reviewDateString);

    return vehicleInfo;
  }

  private static Map<String, Object> setColInfo(Map<String, Object> variables) throws BpmServiceException
  {
    String collateralCodeDesc = String.valueOf(variables.get(COLLATERAL_CODE));
    String collateralCode = getFirstValueByDelimiter(collateralCodeDesc, DELIMITER_COLON,
        "Collateral code is invalid =" + collateralCodeDesc);

    String collateralAmount = String.valueOf(variables.get(AMOUNT_OF_COLLATERAL));
    String deductionRate = String.valueOf(variables.get(DEDUCTION_RATE));

    deductionRate = getFirstValueByDelimiter(deductionRate, DELIMITER_PERCENT_SIGN,
        "Collateral deduction rate is invalid =" + deductionRate);

    Map<String, Object> colInfo = new HashMap<>();
    colInfo.put(COLLATERAL_CODE, collateralCode);
    colInfo.put(AMOUNT_OF_COLLATERAL, collateralAmount);
    colInfo.put(DEDUCTION_RATE, deductionRate);

    return colInfo;
  }

  private static Map<String, Object> setMachineryInfo(Map<String, Object> variables, Map<String, Object> caseVariables) throws ParseException
  {
    Map<String, Object> machineryInfo = new HashMap<>();

    Date reviewDate;
    String reviewDateString = "";
    if (variables.get(REVIEW_DATE) instanceof String)
    {
      reviewDate = dateFormatter.parse(String.valueOf(variables.get(REVIEW_DATE)));
      reviewDateString = dateFormatter.format(reviewDate);
    }
    else if (null != variables.get(REVIEW_DATE))
    {
      reviewDate = (Date) variables.get(REVIEW_DATE);
      reviewDateString = dateFormatter.format(reviewDate);
    }

    Object collateralAssessment = variables.get(COLLATERAL_ASSESSMENT);
    String assessment = null;
    if (null == collateralAssessment)
    {
      assessment = getReadOnlyFieldValue(caseVariables, BpmModuleConstants.COLLATERAL_ASSESSMENT);
    }
    else
    {
      assessment = getStringValue(collateralAssessment);
    }

    machineryInfo.put(FORM_NUMBER, variables.get(FORM_NUMBER));
    machineryInfo.put(MANUFACTURER, variables.get(MANUFACTURER));
    machineryInfo.put(MACHINE_NUMBER, variables.get(MACHINE_NUMBER));
    machineryInfo.put(MACHINE_MODEL, variables.get(MACHINE_MODEL));
    machineryInfo.put(REVIEW_DATE, reviewDateString);
    machineryInfo.put(MANUFACTURER_YEAR, variables.get(MANUFACTURER_YEAR));
    machineryInfo.put(CIF, variables.get(CIF));
    machineryInfo.put(REMARKS, variables.get(REMARKS));
    machineryInfo.put(COLLATERAL_ASSESSMENT, assessment);

    return machineryInfo;
  }

  private static Map<String, Object> setOtherCollInfo(Map<String, Object> variables, Map<String, Object> caseVariables) throws ParseException
  {
    String formNumber = String.valueOf(variables.get(FORM_NUMBER));
    String customerCif = String.valueOf(variables.get(CIF));
    Date receivedDate;
    Date reviewDate;
    String reviewDateString = "";
    String receivedDateString = "";

    if (variables.get(REVIEW_DATE) instanceof String)
    {
      reviewDate = dateFormatter.parse(String.valueOf(variables.get(REVIEW_DATE)));
      reviewDateString = dateFormatter.format(reviewDate);
    }
    else if (null != variables.get(REVIEW_DATE))
    {
      reviewDate = (Date) variables.get(REVIEW_DATE);
      reviewDateString = dateFormatter.format(reviewDate);
    }

    if (variables.get(RECEIVED_DATE) instanceof String)
    {
      receivedDate = dateFormatter.parse(String.valueOf(variables.get(RECEIVED_DATE)));
      receivedDateString = dateFormatter.format(receivedDate);
    }
    else if (null != variables.get(RECEIVED_DATE))
    {
      receivedDate = (Date) variables.get(RECEIVED_DATE);
      receivedDateString = dateFormatter.format(receivedDate);
    }

    String remarks = String.valueOf(variables.get(REMARKS));
    Object collateralAssessment = variables.get(BpmModuleConstants.COLLATERAL_ASSESSMENT);

    String assessment = null;
    if (null == collateralAssessment)
    {
      assessment = getReadOnlyFieldValue(caseVariables, BpmModuleConstants.COLLATERAL_ASSESSMENT);
    }
    else {
      assessment = getStringValue(collateralAssessment);
    }

    Map<String, Object> otherCollInfo = new HashMap<>();

    otherCollInfo.put(FORM_NUMBER, formNumber);
    otherCollInfo.put(CIF, customerCif);
    otherCollInfo.put(RECEIVED_DATE, receivedDateString);
    otherCollInfo.put(REVIEW_DATE, reviewDateString);
    otherCollInfo.put(REMARKS, remarks);
    otherCollInfo.put(COLLATERAL_ASSESSMENT, assessment);
    return otherCollInfo;
  }

  private static Map<String, Object> setInspectionInfo(Map<String, Object> variables) throws BpmServiceException, ParseException
  {
    Map<String, Object> inspectionInfo = new HashMap<>();
    String inspectionTypeDesc = String.valueOf(variables.get(INSPECTION_TYPE));
    String collateralCode = getFirstValueByDelimiter(inspectionTypeDesc, DELIMITER_COLON,
        "Collateral inspection type is invalid =" + inspectionTypeDesc);

    Date inspectionDate;
    String inspectionDateString;
    if (null == variables.get(INSPECTION_DATE))
    {
      inspectionDateString = "";
    }
    if (variables.get(INSPECTION_DATE) instanceof String)
    {
      inspectionDate = dateFormatter.parse(String.valueOf(variables.get(INSPECTION_DATE)));
      inspectionDateString = dateFormatter.format(inspectionDate);
    }
    else
    {
      inspectionDate = (Date) variables.get(INSPECTION_DATE);
      inspectionDateString = dateFormatter.format(inspectionDate);
    }

    inspectionInfo.put(INSPECTION_TYPE, collateralCode);
    inspectionInfo.put(INSPECTOR_ID, variables.get(INSPECTOR_ID));
    inspectionInfo.put(INSPECTION_AMOUNT_VALUE, variables.get(INSPECTION_AMOUNT_VALUE));
    inspectionInfo.put(INSPECTION_DATE, inspectionDateString);
    inspectionInfo.put(INSPECTION_SERIAL_NUM, variables.get(INSPECTION_SERIAL_NUM));

    return inspectionInfo;
  }

  private static Map<String, Object> setOwnershipInfo(Map<String, Object> variables) throws BpmServiceException
  {
    String ownershipTypeDesc = String.valueOf(variables.get(OWNERSHIP_TYPE));
    String ownerCifNumber = getStringValue(variables.get(OWNER_CIF_NUMBER));
    String typeCode = getFirstValueByDelimiter(ownershipTypeDesc, DELIMITER_COLON, "Ownership description is invalid!");

    Map<String, Object> ownershipInfo = new HashMap<>();
    ownershipInfo.put(OWNERSHIP_TYPE, typeCode);
    ownershipInfo.put(OWNER_CIF_NUMBER, ownerCifNumber);
    ownershipInfo.put(OWNER_NAME, variables.get(OWNER_NAME));
    ownershipInfo.put(SERIAL_NUM, variables.get(SERIAL_NUM));
    return ownershipInfo;
  }
}
