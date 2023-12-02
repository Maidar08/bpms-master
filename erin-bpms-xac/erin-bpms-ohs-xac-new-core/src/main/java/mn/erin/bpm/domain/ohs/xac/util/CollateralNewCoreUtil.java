package mn.erin.bpm.domain.ohs.xac.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralId;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.currency.CurrencyType;
import mn.erin.domain.bpm.model.loan_contract.LinkageCollateralInfo;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.APPORTION_METHOD;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.CEILING_LIMIT_AMT_VAL_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLLATERAL_CODE_VARIABLE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLL_APPART_METHOD_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLL_REFERENCE_TEXT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLL_REFERENCE_TYPE_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLL_REFERENCE_VAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_CODE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_CRNCY_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.CUSTOMER_CIF;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.DEDUCTION_RATE_VARIABLE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.DELIMITER_COLON;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.FORM_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_AMOUNT_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_AMOUNT_VALUE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_EMPLOYEE_ID_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_SERIAL_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_TYPE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTOR_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSP_SERIAL_NUM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_APPORTION_METHOD;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_DERIVE_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_FORM_NUM;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MACHINE_MODEL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MACHINE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MANUFACTURER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MANUFACTURER_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_REMARKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINE_MODEL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MANUFACTURER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MANUFACTURER_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MARGIN_PCNT_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHERS_RECEIVED_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHER_COLLATERAL_ASSESSMENT_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_CIF_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_NAME_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_TYPE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.RECEIVED_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.REMARKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.REVIEW_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SERIAL_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SERIAL_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.VISIT_DATE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_BUILT_AREA;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_COL_VALUE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_CUSTOMER_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_FORM_DERIVE_VAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_GENERAL_COL_VAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_LAND_AREA;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_REMARKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_REVIEW_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_RMKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ACCOUNT_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.BLANK_STRING;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_AVAILABLE_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_CURRENCY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_END_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_HAIRCUT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_LIABLE_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_LINKED_REF_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_OWNER_NAMES;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_REVALUE_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_START_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUSTOMER_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.EMPTY_COLLATERAL_ACC_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_ACCOUNT_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_CIF;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_AVAILABLE_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_CURRENCY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_END_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_MARGIN_PERCENT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_OWNER_NAMES;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_REVALUE_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_START_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_COLLATERAL_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_FORAC_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_OLLATERAL_DESCRIPTION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ZERO_VALUE_STRING;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_DATE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_DATE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3;
import static mn.erin.domain.bpm.constants.CollateralConstants.BUILDER_NAME;
import static mn.erin.domain.bpm.constants.CollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.domain.bpm.constants.CollateralConstants.STREET_NAME;

public final class CollateralNewCoreUtil
{
  private static final Logger LOG = LoggerFactory.getLogger(CollateralNewCoreUtil.class);

  private static final String NULL_STRING = "null";

  private CollateralNewCoreUtil()
  {

  }

  public static JSONObject toCollGenericInfoJSON(String currency, Map<String, Object> genericInfo)
  {
    JSONObject values = new JSONObject();

    BigDecimal ceilingLimitAmount = new BigDecimal(String.valueOf(genericInfo.get(AMOUNT_OF_COLLATERAL)));

    values.put(COLTRL_CODE_REQUEST, getValidString(genericInfo.get(COLLATERAL_CODE_VARIABLE)));
    values.put(MARGIN_PCNT_REQUEST, new BigDecimal(String.valueOf(genericInfo.get(DEDUCTION_RATE_VARIABLE))));

    values.put(COLTRL_CRNCY_REQUEST, getValidString(currency));
    values.put(CEILING_LIMIT_AMT_VAL_REQUEST, ceilingLimitAmount);

    return values;
  }

  public static JSONObject toMachineryColGenericInfo(String cuurency, Map<String, Object> genericInfo)
  {
    JSONObject values = new JSONObject();
    BigDecimal ceilingLimitAmount = new BigDecimal(String.valueOf(genericInfo.get(AMOUNT_OF_COLLATERAL)));
    values.put(COLTRL_CODE_REQUEST, genericInfo.get(COLLATERAL_CODE_VARIABLE));
    values.put(COLTRL_CRNCY_REQUEST, cuurency);
    values.put(CEILING_LIMIT_AMT_VAL_REQUEST, ceilingLimitAmount);
    values.put(MARGIN_PCNT_REQUEST, genericInfo.get(DEDUCTION_RATE));
    return values;
  }

  public static String getValidString(Object value)
  {
    if (null == value)
    {
      return "";
    }

    if (NULL_STRING.equalsIgnoreCase(String.valueOf(value)))
    {
      return "";
    }

    return String.valueOf(value);
  }

  public static JSONObject toImmovableCollInfo(String collValueType, String isConfirmedStr, Map<String, Object> immovableCollInfo)
  {
    JSONObject values = new JSONObject();
    JSONObject postAddress = new JSONObject();

    JSONObject postAddressJSON = new JSONObject();

    values.put("HouseNum", getValidString(immovableCollInfo.get("houseNumber")));
    values.put("PurposeOfUsage", getValidString(immovableCollInfo.get(PURPOSE_OF_USAGE)));

    values.put(FORM_NUMBER, getValidString(immovableCollInfo.get(FORM_NUMBER)));
    values.put("PropertyId", getValidString(immovableCollInfo.get("propertyId")));

    values.put("LeasedInd", getValidString(immovableCollInfo.get("leasedInd")));
    values.put("PropDocNum", getValidString(immovableCollInfo.get("propertyDocNumber")));

    Object builtAreaObj = immovableCollInfo.get("builtArea");

    if (null == builtAreaObj || builtAreaObj.equals(NULL_STRING))
    {
      values.put(XAC_BUILT_AREA, "");
    }
    else
    {
      BigDecimal builtArea = new BigDecimal(String.valueOf(builtAreaObj));
      values.put(XAC_BUILT_AREA, builtArea);
    }

    Object landAreaObj = immovableCollInfo.get("landArea");

    if (null == landAreaObj || landAreaObj.equals(NULL_STRING))
    {
      values.put(XAC_LAND_AREA, "");
    }
    else
    {

      BigDecimal landArea = new BigDecimal(String.valueOf(landAreaObj));
      values.put(XAC_LAND_AREA, landArea);
    }

    values.put("BuilderName", getValidString(immovableCollInfo.get("immovableNumber")));

    values.put(XAC_REVIEW_DATE, getValidString(immovableCollInfo.get(REVIEW_DATE)));
    values.put("DueDt", getValidString(immovableCollInfo.get("dueDate")));

    values.put("YearOfConstruction", getValidString(immovableCollInfo.get("yearOfConstruction")));
    values.put("AgeOfBldgYears", immovableCollInfo.get("conditionOfContract"));

    postAddress.put("Addr3", getValidString(immovableCollInfo.get(ADDRESS_1)));
    postAddress.put("Addr1", getValidString(immovableCollInfo.get(ADDRESS_2)));

    postAddress.put("Addr2", getValidString(immovableCollInfo.get(BUILDER_NAME)));
    postAddress.put("City", getValidString(immovableCollInfo.get("city")));

    // Static value : Confirmed with XAC.
    values.put("AppartngMethod", COLL_APPART_METHOD_VALUE);
    postAddress.put("State", "");
    postAddress.put("Country", "");

    postAddressJSON.put("PostAddr", postAddress);

    JSONObject propertyAddress = new JSONObject();

    propertyAddress.put("streetNum", getValidString(immovableCollInfo.get("streetNumber")));
    propertyAddress.put(STREET_NAME, getValidString(immovableCollInfo.get(STREET_NAME)));

    // Static value : Confirmed with XAC.
    propertyAddress.put("localityCode", "");

    values.put("PropertyAddress", propertyAddress);

    values.put(XAC_CUSTOMER_ID, getValidString(immovableCollInfo.get("cifNumber")));
    values.put(XAC_REMARKS, getValidString(immovableCollInfo.get(REMARKS)));

    JSONObject collValueRecord = new JSONObject();

    BigDecimal collateralAssessment = new BigDecimal(String.valueOf(immovableCollInfo.get(COLLATERAL_ASSESSMENT)));

    collValueRecord.put("DeriveAmtValue", collateralAssessment);
    collValueRecord.put(XAC_COL_VALUE_TYPE, collValueType);
    collValueRecord.put(XAC_FORM_DERIVE_VAL, isConfirmedStr);

    values.put(XAC_GENERAL_COL_VAL, collValueRecord);
    values.put("ImmovPropContactInfo", postAddressJSON);

    return values;
  }

  public static JSONObject toModifyImmovableCollInfo(String collValueType, String isConfirmedStr, Map<String, Object> immovableCollInfo)
  {
    JSONObject values = new JSONObject();
    JSONObject postAddress = new JSONObject();

    JSONObject postAddressJSON = new JSONObject();

    values.put("HouseNum", getValidString(immovableCollInfo.get("houseNumber")) );
    values.put("PurposeOfUsage", getValidString(immovableCollInfo.get(PURPOSE_OF_USAGE)));

    values.put(FORM_NUMBER, getValidString(immovableCollInfo.get(FORM_NUMBER)));
    values.put("PropertyId", getValidString(immovableCollInfo.get("propertyId")));

    values.put("LeasedInd", getValidString(immovableCollInfo.get("leasedInd")));
    values.put("PropDocNum", getValidString(immovableCollInfo.get("propertyDocNumber")));

    Object builtAreaObj = immovableCollInfo.get("builtArea");

    if (null == builtAreaObj || builtAreaObj.equals(NULL_STRING))
    {
      values.put(XAC_BUILT_AREA, "");
    }
    else
    {
      builtAreaObj = String.valueOf(builtAreaObj).equals("") ? "0" : String.valueOf(builtAreaObj);
      BigDecimal builtArea = new BigDecimal(String.valueOf(builtAreaObj));
      values.put(XAC_BUILT_AREA, builtArea);
    }

    Object landAreaObj = immovableCollInfo.get("landArea");

    if (null == landAreaObj || landAreaObj.equals(NULL_STRING))
    {
      values.put(XAC_LAND_AREA, "");
    }
    else
    {

      BigDecimal landArea = new BigDecimal(String.valueOf(landAreaObj));
      values.put(XAC_LAND_AREA, landArea);
    }

    values.put("BuilderName", getValidString(immovableCollInfo.get("immovableNumber")));

    values.put(XAC_REVIEW_DATE, getValidString(immovableCollInfo.get(REVIEW_DATE)));
    values.put("DueDt", getValidString(immovableCollInfo.get("dueDate")));

    values.put("YearOfConstruction", getValidString(immovableCollInfo.get("yearOfConstruction")));
    values.put("AgeOfBldgYears", immovableCollInfo.get("conditionOfContract"));

    postAddress.put("Addr3", getValidString(immovableCollInfo.get(ADDRESS_1)));
    postAddress.put("Addr1", getValidString(immovableCollInfo.get(ADDRESS_2)));

    postAddress.put("Addr2", getValidString(immovableCollInfo.get(BUILDER_NAME)));
    postAddress.put("City", getValidString(immovableCollInfo.get("city")));

    // Static value : Confirmed with XAC.
    values.put("AppartngMethod", COLL_APPART_METHOD_VALUE);
    postAddress.put("State", "");
    postAddress.put("Country", "");

    postAddressJSON.put("PostAddr", postAddress);

    JSONObject propertyAddress = new JSONObject();

    propertyAddress.put("streetNum", getValidString(immovableCollInfo.get("streetNumber")));
    propertyAddress.put(STREET_NAME, getValidString(immovableCollInfo.get(STREET_NAME)));

    // Static value : Confirmed with XAC.
    propertyAddress.put("localityCode", "");

    values.put("PropertyAddress", propertyAddress);

    values.put(XAC_CUSTOMER_ID, getValidString(immovableCollInfo.get("cifNumber")));
    values.put(XAC_REMARKS, getValidString(immovableCollInfo.get(REMARKS)));

    JSONObject collValueRecord = new JSONObject();

    BigDecimal collateralAssessment = new BigDecimal(String.valueOf(immovableCollInfo.get(COLLATERAL_ASSESSMENT)));

    collValueRecord.put("DeriveValueAmt", collateralAssessment);
    collValueRecord.put(XAC_COL_VALUE_TYPE, collValueType);
    collValueRecord.put(XAC_FORM_DERIVE_VAL, isConfirmedStr);

    values.put(XAC_GENERAL_COL_VAL, collValueRecord);
    values.put("ImmovPropContactInfo", postAddressJSON);

    return values;
  }

  public static JSONObject toMachineryColInfo(String apportionMethod, String collValueType, String isConfirmedStr, Map<String, Object> machineryInfo)
  {
    JSONObject values = new JSONObject();
    JSONObject generalColValue = new JSONObject();

    values.put(MACHINERY_FORM_NUM, machineryInfo.get(FORM_NUMBER));
    values.put(MACHINERY_MANUFACTURER, machineryInfo.get(MANUFACTURER));
    values.put(MACHINERY_MACHINE_NUMBER, machineryInfo.get(MACHINE_NUMBER));
    values.put(MACHINERY_MACHINE_MODEL, machineryInfo.get(MACHINE_MODEL));
    values.put(XAC_REVIEW_DATE, machineryInfo.get(REVIEW_DATE));
    values.put(MACHINERY_MANUFACTURER_YEAR, machineryInfo.get(MANUFACTURER_YEAR));
    values.put(XAC_CUSTOMER_ID, machineryInfo.get(CIF_NUMBER));
    values.put(MACHINERY_REMARKS, machineryInfo.get(REMARKS));
    values.put(MACHINERY_APPORTION_METHOD, apportionMethod);

    BigDecimal colAssesment;
    if (null != machineryInfo.get(COLLATERAL_ASSESSMENT))
    {
      colAssesment = new BigDecimal(String.valueOf(machineryInfo.get(COLLATERAL_ASSESSMENT)));
    }
    else
    {
      colAssesment = new BigDecimal("5000000000");
    }
    generalColValue.put(XAC_COL_VALUE_TYPE, collValueType);
    generalColValue.put(MACHINERY_DERIVE_VALUE, colAssesment);
    generalColValue.put(XAC_FORM_DERIVE_VAL, isConfirmedStr);

    values.put(XAC_GENERAL_COL_VAL, generalColValue);

    return values;
  }

  public static JSONObject toVehicleCollInfo(String apportionMethod, String collateralValueType, String fromDriveValue, Map<String, Object> vehicleInfo)
  {

    JSONObject vehicleInfoValue = new JSONObject();

    vehicleInfoValue.put(FORM_NUMBER, getValidString(vehicleInfo.get(FORM_NUMBER)));
    vehicleInfoValue.put("RegNum", getValidString(vehicleInfo.get("vehicleRegisterNumber")));
    vehicleInfoValue.put(XAC_REVIEW_DATE, getValidString(vehicleInfo.get(REVIEW_DATE)));
    vehicleInfoValue.put("ManufactureYear", getValidString(vehicleInfo.get("manufactureYear")));
    vehicleInfoValue.put("ChassisNum", getValidString(vehicleInfo.get("chassisNumber")));
    vehicleInfoValue.put("EngineNum", getValidString(vehicleInfo.get("engineNumber")));
    vehicleInfoValue.put("VehicleModel", getValidString(vehicleInfo.get("vehicleModel")));
    vehicleInfoValue.put("NatureOfCharge", getValidString(vehicleInfo.get("financialLeasingSupplier")));
    vehicleInfoValue.put(XAC_CUSTOMER_ID, getValidString(vehicleInfo.get(CIF_NUMBER)));
    vehicleInfoValue.put("Rmks", getValidString(vehicleInfo.get(REMARKS)));
    vehicleInfoValue.put("VehicleType", getValidString(vehicleInfo.get(PURPOSE_OF_USAGE)));
    vehicleInfoValue.put("ApportionMethod", apportionMethod);

    JSONObject addressValue = new JSONObject();
    addressValue.put(ADDRESS_1, getValidString(vehicleInfo.get(ADDRESS_1)));
    addressValue.put(ADDRESS_2, getValidString(vehicleInfo.get(ADDRESS_2)));
    addressValue.put(ADDRESS_3, getValidString(vehicleInfo.get(ADDRESS_3)));

    JSONObject genericCollateralValueRec = new JSONObject();
    genericCollateralValueRec.put(XAC_COL_VALUE_TYPE, collateralValueType);

    BigDecimal collateralAssessment = new BigDecimal(String.valueOf(vehicleInfo.get(COLLATERAL_ASSESSMENT)));

    genericCollateralValueRec.put("DeriveValueAmt", collateralAssessment);
    genericCollateralValueRec.put(XAC_FORM_DERIVE_VAL, fromDriveValue);

    vehicleInfoValue.put("VehiclesAddr", addressValue);
    vehicleInfoValue.put(XAC_GENERAL_COL_VAL, genericCollateralValueRec);

    return vehicleInfoValue;
  }

  public static JSONObject toOtherCollateralInfo(String apportionMethod, Map<String, Object> otherCollInfo)
  {
    JSONObject collateralValues = new JSONObject();

    collateralValues.put(FORM_NUMBER, getValidString(otherCollInfo.get(FORM_NUMBER)));
    collateralValues.put(XAC_REVIEW_DATE, getValidString(otherCollInfo.get(REVIEW_DATE)));
    collateralValues.put(OTHERS_RECEIVED_DATE, getValidString(otherCollInfo.get(RECEIVED_DATE)));
    collateralValues.put(CUST_ID, getValidString(otherCollInfo.get(CUSTOMER_CIF)));
    collateralValues.put(XAC_RMKS, getValidString(otherCollInfo.get(REMARKS)));
    collateralValues.put(APPORTION_METHOD, apportionMethod);

    BigDecimal collateralAssessment = new BigDecimal(String.valueOf(otherCollInfo.get(COLLATERAL_ASSESSMENT)));

    collateralValues.put(OTHER_COLLATERAL_ASSESSMENT_VALUE, collateralAssessment);

    return collateralValues;
  }

  public static JSONObject toInspectionInfo(Map<String, Object> inspectionInfo)
  {
    JSONObject values = new JSONObject();

    values.put(INSPECTION_TYPE_REQUEST, inspectionInfo.get(INSPECTION_TYPE));
    values.put(INSPECTION_EMPLOYEE_ID_REQUEST, getValidString(inspectionInfo.get(INSPECTOR_ID)));

    BigDecimal inspAmountValue = new BigDecimal(String.valueOf(inspectionInfo.get(INSPECTION_AMOUNT_VALUE)));
    values.put(INSPECTION_AMOUNT_VALUE_REQUEST, inspAmountValue);
    values.put(VISIT_DATE_REQUEST, getValidString(inspectionInfo.get(INSPECTION_DATE)));

    return values;
  }

  public static JSONObject toModifyInspectionInfo(Map<String, Object> inspectionInfo)
  {
    JSONObject values = new JSONObject();

    values.put(INSPECTION_TYPE_REQUEST, inspectionInfo.get(INSPECTION_TYPE));
    values.put(INSPECTION_EMPLOYEE_ID_REQUEST, getValidString(inspectionInfo.get(INSPECTOR_ID)));

    BigDecimal inspAmountValue = new BigDecimal(String.valueOf(inspectionInfo.get(INSPECTION_AMOUNT_VALUE)));
    values.put(INSPECTION_AMOUNT_VALUE_REQUEST, inspAmountValue);
    values.put(VISIT_DATE_REQUEST, getValidString(inspectionInfo.get(INSPECTION_DATE)));
    values.put(INSP_SERIAL_NUM_SERVICE, getValidString(inspectionInfo.get(INSPECTION_SERIAL_NUMBER)));

    return values;
  }

  public static JSONObject toOwnershipInfo(Map<String, Object> ownershipInfo)
  {
    JSONObject values = new JSONObject();

    values.put(OWNER_TYPE_REQUEST, getValidString(ownershipInfo.get(OWNERSHIP_TYPE)));
    values.put(OWNER_CIF_REQUEST, getValidString(ownershipInfo.get(OWNER_CIF_NUMBER)));
    values.put(OWNER_NAME_REQUEST, getValidString(ownershipInfo.get(OWNER_NAME)));
    return values;
  }

  public static JSONObject toModifyOwnershipInfo(Map<String, Object> ownershipInfo)
  {

    JSONObject values = new JSONObject();
    values.put(OWNER_TYPE_REQUEST, getValidString(ownershipInfo.get(OWNERSHIP_TYPE)));
    values.put(OWNER_CIF_REQUEST, getValidString(ownershipInfo.get(OWNER_CIF_NUMBER)));
    values.put(OWNER_NAME_REQUEST, getValidString(ownershipInfo.get(OWNER_NAME)));
    values.put(SERIAL_NUMBER_SERVICE, getValidString(ownershipInfo.get(SERIAL_NUMBER)));
    return values;
  }

  public static Map<String, List<String>> toCollReferenceCodes(List<String> types, JSONArray referenceCodeArray)
  {
    Map<String, List<String>> referenceCodes = new HashMap<>();

    for (String type : types)
    {
      List<String> refCodesForType = new ArrayList<>();

      for (int index = 0; index < referenceCodeArray.length(); index++)
      {
        JSONObject typeJSON = (JSONObject) referenceCodeArray.get(index);

        if (type.equalsIgnoreCase(typeJSON.getString(COLL_REFERENCE_TYPE_NAME)))
        {
          String value = String.valueOf(typeJSON.get(COLL_REFERENCE_VAL));
          String text = String.valueOf(typeJSON.get(COLL_REFERENCE_TEXT));

          refCodesForType.add(value + DELIMITER_COLON + text);
        }
      }

      referenceCodes.put(type, refCodesForType);
    }
    return referenceCodes;
  }

  public static List<Collateral> toCollaterals(JSONObject rootCollateralJSON) throws BpmServiceException
  {
    if (null == rootCollateralJSON)
    {
      return Collections.emptyList();
    }

    JSONArray collateralArray = (JSONArray) rootCollateralJSON.get(COLLATERAL);

    if (null == collateralArray || collateralArray.length() == 0)
    {
      return Collections.emptyList();
    }

    List<Collateral> collaterals = new ArrayList<>();

    for (int index = 0; index < collateralArray.length(); index++)
    {
      JSONObject collateralJson = (JSONObject) collateralArray.get(index);

      collaterals.add(toCollateral(collateralJson));
    }
    return collaterals;
  }

  public static List<Collateral> toCollaterals(JSONArray collateralArray) throws BpmServiceException
  {
    if (null == collateralArray || collateralArray.length() == 0)
    {
      return Collections.emptyList();
    }

    List<Collateral> collaterals = new ArrayList<>();

    // TODO remove it
    //    int colLength = Math.min(collateralArray.length(), 4);
    int colLength = collateralArray.length();

    for (int index = 0; index < colLength; index++)
    {
      JSONObject collateralJson = (JSONObject) collateralArray.get(index);

      collaterals.add(toCollateralNewCore(collateralJson));
    }

    return collaterals;
  }

  public static Collateral toCollateralNewCore(JSONObject collateralJson) throws BpmServiceException
  {
    String code = (collateralJson.isNull(NEW_CORE_COLLATERAL_ID)
        ? BLANK_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_ID));

    String name = (collateralJson.isNull(NEW_CORE_COLLATERAL_NAME)
        ? BLANK_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_NAME));

    String accountNumber = (collateralJson.isNull(NEW_CORE_FORAC_ID)
        ? EMPTY_COLLATERAL_ACC_NUMBER : collateralJson.getString(NEW_CORE_FORAC_ID));

    String currencyTypeStr = (collateralJson.isNull(NEW_CORE_COLLATERAL_CURRENCY_TYPE)
        ? CurrencyType.MNT.getValue() : collateralJson.getString(NEW_CORE_COLLATERAL_CURRENCY_TYPE));
    CurrencyType currencyType = CurrencyType.toCurrencyType(currencyTypeStr);

    String value = (collateralJson.isNull(NEW_CORE_COLLATERAL_VALUE)
        ? ZERO_VALUE_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_VALUE));

    String startDateStr = collateralJson.isNull(NEW_CORE_COLLATERAL_START_DATE) ? BLANK_STRING : (String) collateralJson.get(NEW_CORE_COLLATERAL_START_DATE);
    String endDateStr = collateralJson.isNull(NEW_CORE_COLLATERAL_END_DATE) ? BLANK_STRING : (String) collateralJson.get(NEW_CORE_COLLATERAL_END_DATE);

    String colType = collateralJson.isNull(NEW_CORE_COLLATERAL_TYPE) ? BLANK_STRING : (String) collateralJson.get(NEW_CORE_COLLATERAL_TYPE);

    String ownerNames = collateralJson.isNull(NEW_CORE_COLLATERAL_OWNER_NAMES) ? BLANK_STRING : (String) collateralJson.get(NEW_CORE_COLLATERAL_OWNER_NAMES);
    List<String> ownerNameList = new ArrayList<>();

    if (StringUtils.isBlank(ownerNames))
    {
      ownerNameList.add(BLANK_STRING);
    }
    else
    {
      ownerNameList.add(ownerNames);
    }

    Collateral collateral = new Collateral(CollateralId.valueOf(code), AccountId.valueOf(accountNumber), name, currencyType);

    if (StringUtils.isBlank(startDateStr))
    {
      collateral.setStartDate(null);
    }
    else
    {
      collateral.setStartDate(toLocalDate(startDateStr));
    }

    if (StringUtils.isBlank(endDateStr))
    {
      collateral.setEndDate(null);
    }
    else
    {
      collateral.setEndDate(toLocalDate(endDateStr));
    }

    collateral.setOwnerNames(ownerNameList);
    collateral.setAmountOfAssessment(new BigDecimal(value));

    collateral.setCollateralInfo(toCollateralInfoNewCore(collateralJson));

    collateral.setType(colType);

    return collateral;
  }

  public static Collateral toCollateral(JSONObject collateralJson) throws BpmServiceException
  {
    String code = (collateralJson.isNull(COLLATERAL_CODE)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_CODE));

    String name = (collateralJson.isNull(COLLATERAL_NAME)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_NAME));

    String accountNumber = (collateralJson.isNull(ACCOUNT_NO)
        ? EMPTY_COLLATERAL_ACC_NUMBER : collateralJson.getString(ACCOUNT_NO));

    String currencyTypeStr = (collateralJson.isNull(COLLATERAL_CURRENCY_TYPE)
        ? CurrencyType.MNT.getValue() : collateralJson.getString(COLLATERAL_CURRENCY_TYPE));
    CurrencyType currencyType = CurrencyType.toCurrencyType(currencyTypeStr);

    String value = (collateralJson.isNull(COLLATERAL_VALUE)
        ? ZERO_VALUE_STRING : collateralJson.getString(COLLATERAL_VALUE));

    String startDateStr = (String) collateralJson.get(COLLATERAL_START_DATE);
    String endDateStr = (String) collateralJson.get(COLLATERAL_END_DATE);

    String ownerNames = (String) collateralJson.get(COLLATERAL_OWNER_NAMES);
    List<String> ownerNameList = new ArrayList<>();

    if (StringUtils.isBlank(ownerNames))
    {
      ownerNameList.add(BLANK_STRING);
    }
    else
    {
      ownerNameList.add(ownerNames);
    }

    Collateral collateral = new Collateral(CollateralId.valueOf(code), AccountId.valueOf(accountNumber), name, currencyType);

    collateral.setStartDate(toLocalDate(startDateStr));
    collateral.setEndDate(toLocalDate(endDateStr));

    collateral.setOwnerNames(ownerNameList);
    collateral.setAmountOfAssessment(new BigDecimal(value));

    collateral.setCollateralInfo(toCollateralInfo(collateralJson));

    return collateral;
  }

  public static CollateralInfo toCollateralInfoNewCore(JSONObject collateralJson) throws BpmServiceException
  {
    String customerName = (collateralJson.isNull(NEW_CORE_ACCOUNT_NAME))
        ? BLANK_STRING : collateralJson.getString(NEW_CORE_ACCOUNT_NAME);

    String liableNumber = (collateralJson.isNull(NEW_CORE_CIF)
        ? ZERO_VALUE_STRING : collateralJson.getString(NEW_CORE_CIF));

    String hairCut = (collateralJson.isNull(NEW_CORE_COLLATERAL_MARGIN_PERCENT)
        ? ZERO_VALUE_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_MARGIN_PERCENT));

    String availableAmount = (collateralJson.isNull(NEW_CORE_COLLATERAL_AVAILABLE_AMOUNT)
        ? ZERO_VALUE_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_AVAILABLE_AMOUNT));

    String description = (collateralJson.isNull(NEW_CORE_OLLATERAL_DESCRIPTION)
        ? BLANK_STRING : collateralJson.getString(NEW_CORE_OLLATERAL_DESCRIPTION));

    String linkedRefNo = (collateralJson.isNull(COLLATERAL_LINKED_REF_NO)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_LINKED_REF_NO));

    String revalueDateString = (collateralJson.isNull(NEW_CORE_COLLATERAL_REVALUE_DATE)
        ? BLANK_STRING : collateralJson.getString(NEW_CORE_COLLATERAL_REVALUE_DATE));

    CollateralInfo collateralInfo = new CollateralInfo(customerName, liableNumber,
        new BigDecimal(hairCut), new BigDecimal(availableAmount));

    collateralInfo.setDescription(description);
    collateralInfo.setLinkedReferenceNumber(linkedRefNo);
    if (StringUtils.isBlank(revalueDateString))
    {
      collateralInfo.setRevalueDate(null);
    }
    else
    {
      collateralInfo.setRevalueDate(toLocalDate(revalueDateString));
    }

    return collateralInfo;
  }

  public static CollateralInfo toCollateralInfo(JSONObject collateralJson) throws BpmServiceException
  {
    String customerName = (collateralJson.isNull(CUSTOMER_NAME))
        ? BLANK_STRING : collateralJson.getString(CUSTOMER_NAME);

    String liableNumber = (collateralJson.isNull(COLLATERAL_LIABLE_NO)
        ? ZERO_VALUE_STRING : collateralJson.getString(COLLATERAL_LIABLE_NO));

    String hairCut = (collateralJson.isNull(COLLATERAL_HAIRCUT)
        ? ZERO_VALUE_STRING : collateralJson.getString(COLLATERAL_HAIRCUT));

    String availableAmount = (collateralJson.isNull(COLLATERAL_LIABLE_NO)
        ? ZERO_VALUE_STRING : collateralJson.getString(COLLATERAL_AVAILABLE_AMOUNT));

    String description = (collateralJson.isNull(COLLATERAL_DESCRIPTION)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_DESCRIPTION));

    String linkedRefNo = (collateralJson.isNull(COLLATERAL_LINKED_REF_NO)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_LINKED_REF_NO));

    String revalueDateString = (collateralJson.isNull(COLLATERAL_REVALUE_DATE)
        ? BLANK_STRING : collateralJson.getString(COLLATERAL_REVALUE_DATE));

    CollateralInfo collateralInfo = new CollateralInfo(customerName, liableNumber,
        new BigDecimal(hairCut), new BigDecimal(availableAmount));

    collateralInfo.setDescription(description);
    collateralInfo.setLinkedReferenceNumber(linkedRefNo);
    collateralInfo.setRevalueDate(toLocalDate(revalueDateString));

    return collateralInfo;
  }

  public static List<String> toCollateralCodeList(JSONArray collateralCodeArray)
  {
    List<String> codeCollection = new ArrayList<>();

    for (int i = 0; i < collateralCodeArray.length(); i++)
    {
      JSONObject collJSON = (JSONObject) collateralCodeArray.get(i);
      String collateralCode = collJSON.getString("ColtrlCode");
      String collateralDesc = collJSON.getString("ColtrlDesc");

      codeCollection.add(collateralCode + ": " + collateralDesc);
    }

    return codeCollection;
  }

  public static LocalDate toLocalDate(String dateStr) throws BpmServiceException
  {
    if (StringUtils.isBlank(dateStr))
    {
      return null;
    }

    try
    {
      //      DateFormat format = new SimpleDateFormat(COLLATERAL_DATE_FORMAT, Locale.ENGLISH);
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
      Date date = format.parse(dateStr);

      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage(), e);

      String message = String.format(COLLATERAL_DATE_ERROR_MESSAGE, dateStr);
      throw new BpmServiceException(COLLATERAL_DATE_ERROR_CODE, message);
    }
  }

  public static List<LinkageCollateralInfo> toLinkageCollateralInfos(JSONArray linkageCollateralInfoArray)
  {
    if (linkageCollateralInfoArray.isEmpty())
    {
      return Collections.emptyList();
    }

    List<LinkageCollateralInfo> linkageCollateralInfoList = new ArrayList<>();

    for (int i = 0; i < linkageCollateralInfoArray.length(); i++)
    {
      JSONObject linkageCollateralInfo = linkageCollateralInfoArray.getJSONObject(i);
      linkageCollateralInfoList.add(toLinkageCollateralInfo(linkageCollateralInfo));
    }

    return linkageCollateralInfoList;
  }

  private static LinkageCollateralInfo toLinkageCollateralInfo(JSONObject object)
  {
    String collateralId = getValidString(object.get("ColtrlId"));
    String collateralCode = getValidString(object.get("ColtrlCode"));
    String collateralType = object.getString("ColtrlType");
    String currency = object.getString("Currency");
    double apportionedAmount = object.getDouble("ApportionedAmount");
    String apportioningMethod = object.getString("ApportionigMethod");
    String collateralNatureInd = object.getString("ColtrlNatureInd");
    String withdrawReasonCode = getValidString(object.get("WithdrawReasonCode"));

    if(collateralType.equals("O") || collateralType.equals("V") ){
      collateralType = "OV";
    }
    return new LinkageCollateralInfo(collateralId, collateralCode, collateralType, currency, apportionedAmount, apportioningMethod,
        collateralNatureInd, withdrawReasonCode, false);
  }
}
