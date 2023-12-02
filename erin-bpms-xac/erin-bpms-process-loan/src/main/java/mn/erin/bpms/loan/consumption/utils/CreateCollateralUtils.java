package mn.erin.bpms.loan.consumption.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.util.StringUtils;

import mn.erin.domain.bpm.constants.CollateralConstants;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.COLLATERAL_CODE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DEDUCTION_RATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DELIMITER_COLON;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.DELIMITER_PERCENT_SIGN;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_DATE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTION_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.INSPECTOR_ID;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.OWNER_NAME;
import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.XAC_SERVICE_DATE_FORMATTER;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_DESCRIPTION_ERROR_CODE;

/**
 * @author Odgavaa
 */

public final class CreateCollateralUtils
{
  private CreateCollateralUtils()
  {
  }

  public static SimpleDateFormat dateFormatter = new SimpleDateFormat(XAC_SERVICE_DATE_FORMATTER);

  public static Map<String, Object> getGenericInfo(DelegateExecution execution) throws BpmServiceException
  {
    String collateralCodeDesc = String.valueOf(execution.getVariable(COLLATERAL_CODE));
    String collateralCode = getFirstValueByDelimiter(collateralCodeDesc, DELIMITER_COLON,
        "Collateral code is invalid =" + collateralCodeDesc);

    String deductionRateWithPercent = String.valueOf(execution.getVariable(DEDUCTION_RATE));
    String deductionRate = getFirstValueByDelimiter(deductionRateWithPercent, DELIMITER_PERCENT_SIGN,
        "Deduction rate is invalid! =" + deductionRateWithPercent);
    String collateralAmount = String.valueOf(execution.getVariable(CollateralConstants.AMOUNT_OF_COLLATERAL));

    Map<String, Object> genericInfo = new HashMap<>();

    genericInfo.put(COLLATERAL_CODE, collateralCode);
    genericInfo.put(DEDUCTION_RATE, deductionRate);
    genericInfo.put(AMOUNT_OF_COLLATERAL, collateralAmount);

    return genericInfo;
  }

  public static Map<String, Object> getInspectionInfo(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> inspectionInfo = new HashMap<>();

    String inspectionTypeDesc = String.valueOf(execution.getVariable(INSPECTION_TYPE));
    String typeCode = getFirstValueByDelimiter(inspectionTypeDesc, DELIMITER_COLON, "Inspection type description is invalid!");

    String inspectionAmountValue = String.valueOf(execution.getVariable(INSPECTION_AMOUNT_VALUE));
    String inspectorId = String.valueOf(execution.getVariable(INSPECTOR_ID));
    Date inspectionDate = (Date) execution.getVariable(INSPECTION_DATE);

    String inspectionDateFormatted = "";

    if (null != inspectionDate)
    {
      inspectionDateFormatted = dateFormatter.format(inspectionDate);
    }

    inspectionInfo.put(INSPECTION_TYPE, typeCode);
    inspectionInfo.put(INSPECTION_AMOUNT_VALUE, inspectionAmountValue);

    inspectionInfo.put(INSPECTOR_ID, inspectorId);
    inspectionInfo.put(INSPECTION_DATE, inspectionDateFormatted);

    return inspectionInfo;
  }

  public static Map<String, Object> getOwnershipInfo(DelegateExecution execution) throws BpmServiceException
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

  public static String getFirstValueByDelimiter(String value, String delimiter, String errorMessage) throws BpmServiceException
  {
    if (StringUtils.isEmpty(value))
    {
      return "";
    }

    String[] splitValue = value.split(delimiter);

    if (splitValue.length < 1)
    {
      throw new BpmServiceException(INVALID_DESCRIPTION_ERROR_CODE, errorMessage);
    }

    return splitValue[0].replaceAll("\\s+", "");
  }

  public static void removeFormValues(String caseInstanceId, DelegateExecution execution, String[] formVariableNames)
  {
    CaseService caseService = execution.getProcessEngineServices().getCaseService();

    execution.removeVariables(Arrays.asList(formVariableNames));
    caseService.removeVariables(caseInstanceId, Arrays.asList(formVariableNames));
  }

}
