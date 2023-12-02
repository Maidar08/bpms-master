package mn.erin.bpm.domain.ohs.xac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil;
import mn.erin.bpm.domain.ohs.xac.util.CustomerUtilNewCore;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldId;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.loan_contract.LinkageCollateralInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.HEADER_ADD_LOAN_REPAYMENT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.HEADER_ADD_LOAN_SCH_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_CREATE_ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_GET_ACCOUNT_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_GET_LOAN_ACCOUNT_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_CREATE_IMMOVABLE_COLL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_CREATE_MACHINERY_COLL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_GET_IMMOVABLE_PROPERTY_COLLATERAL_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_GET_REFERENCE_CODES;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_LINK_COLLATERALS;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_MODIFY_IMMOVABLE_PROP_COLLATERAL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_MODIFY_MACHINERY_COLLATERAL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_MODIFY_OTHER_COLLATERAL_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_WSO2_HEADER_MODIFY_VEHICLE_PROP_COLLATERAL;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_APPORTION_METHOD;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_IS_CONFIRMED;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_VALUE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_INQUIRE_COLLATERAL_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCOUNT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_CURR_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_MGR_USER_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_OCCP_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ADDITIONAL_SPECIAL_CONDITION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.BORROWER_CATEGORY_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.BRANCH_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.BRANCH_ID_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CIF_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CO_BORROWER_CUST_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CO_BORROWER_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.CUST_ID_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.EQ_INSTALL_DETAILS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.EQ_INSTALL_FLG;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_1;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_10;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_2;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_3;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_5;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_6;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_7;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_8;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_CODE_9;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.FREE_TEXT_1;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.GUARD_COVER_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.HOL_STAT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.IAS_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INDUSTRY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INSTALL_FREQ;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INSTALL_START_DT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INTEREST_TABLE_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INT_FREQ;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.INT_START_DT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_AMOUNT_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_PERIOD_DAYS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_PERIOD_DAYS_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_PERIOD_MONTHS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.LOAN_PERIOD_MONTHS_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.MIS_CODE_DETAILS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.MODE_OF_ADVN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.NATURE_OF_ADVN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.NO_OF_INSTALL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.OPER_ACCT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.OPER_ACCT_ID_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.PAYMENT_PLAN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.PRODUCT_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.PRODUCT_DESCRIPTION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.PRODUCT_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.PURPOSE_OF_ADVN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.RELATED_PARTY_DETAILS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REL_PARTY_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REL_PARTY_CUST_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REL_PARTY_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REPAYMENT_METHOD;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REPAY_METHOD_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REPMT_REC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.REQUEST_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.SCHM_CODE_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.SECTOR_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.START_DT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.SUB_SECTOR_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.WEEK_DAY;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.WEEK_NUM;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLLATERAL_GEN_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHERS_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNERSHIP_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.RESPONSE_COLLATERAL_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.VEHICLES_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CIF;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_CUSTOMER_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NEW_CORE_REGISTER_ID;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toCollGenericInfoJSON;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toCollReferenceCodes;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toImmovableCollInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toInspectionInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toMachineryColGenericInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toMachineryColInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toModifyImmovableCollInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toModifyInspectionInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toModifyOwnershipInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toOtherCollateralInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toOwnershipInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toVehicleCollInfo;
import static mn.erin.bpm.domain.ohs.xac.util.CustomerUtilNewCore.toCustomerInfo;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.createCollReferenceCodesBody;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateRequestId;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getOnlineLeasingHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getResponse;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getResultResponse;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.toImmovablePropertyCollateralMap;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.toMachineryCollateralMap;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.toOtherCollateralMap;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.toVehicleCollMap;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getNotNullString;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.COLLATERAL_TYPE_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.COLLATERAL_TYPE_IS_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_INCORRECT_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_INCORRECT_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.CUSTOMER_TYPE_IS_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.XAC_CORE_BANKING_SERVICE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;

/**
 * @author Tamir
 */
public class XacNewCoreBankingService implements NewCoreBankingService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacNewCoreBankingService.class);

  private final Environment environment;
  private final AuthenticationService authenticationService;

  public XacNewCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = environment;
    this.authenticationService = authenticationService;
  }

  @Override
  public Map<String, Object> findCustomerByCifNumber(Map<String, String> input) throws BpmServiceException
  {

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GET_CUSTOMER_INFO);
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      if (!StringUtils.isBlank(input.get(PHONE_NUMBER))){
        userId = input.get(PHONE_NUMBER);
      }
    }

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerLanguageType(getLanguageId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("CustId", input.get(CIF_NUMBER));

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("####### Started downloading CUSTOMER INFO = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getCustomerInfoResponse(environment, xacHttpClient, response);
      String registerNumber = jsonResponse.getString("RegisterID");

      return toCustomerInfo(jsonResponse, registerNumber);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }

    catch (JSONException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public Customer findCustomerByPersonIdAndType(String personId, String customerType) throws BpmServiceException
  {
    if (org.springframework.util.StringUtils.isEmpty(personId))
    {
      throw new BpmServiceException(CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_CODE, CUSTOMER_REGISTER_NUMBER_IS_NULL_ERROR_MESSAGE);
    }
    else if (org.springframework.util.StringUtils.isEmpty(customerType))
    {
      throw new BpmServiceException(CUSTOMER_TYPE_IS_NULL_ERROR_CODE, CUSTOMER_TYPE_IS_NULL_ERROR_MESSAGE);
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GET_CUSTOMER_BY_ID);
    String securityCode = generateSecurityCode(source, function);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(getNewCoreEndPoint(), source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();

    boolean isCorporate = false;

    requestBody.put("CustId", "");

    if (customerType.equalsIgnoreCase("Retail"))
    {
      requestBody.put("CustType", customerType);
    }
    else if (customerType.equalsIgnoreCase("Corporate"))
    {
      requestBody.put("CustType", customerType);
      isCorporate = true;
    }
    else
    {
      throw new BpmServiceException(CUSTOMER_TYPE_IS_INCORRECT_CODE, CUSTOMER_TYPE_IS_INCORRECT_MESSAGE);
    }

    requestBody.put("RegNo", personId);
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    try (Response response = xacClient.post(request.toString()))
    {
      try
      {
        JSONObject jsonResponse = XacHttpUtil.getCustomerJSONResponse(environment, xacClient, response);

        if (isCorporate)
        {
          return CustomerUtilNewCore.toCorporateCustomer(jsonResponse);
        }

        return CustomerUtilNewCore.toRetailCustomer(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
      catch (JSONException e)
      {
        throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public boolean checkRiskyCustomer(String registerNumber) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_CHECK_RISKY_CUSTOMER);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();

    request.put("registerNo", registerNumber);
    request.put("languageId", "EN");

    requestBody.put(REQUEST, request);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      String riskyStatus = jsonResponse.getString(XacHttpConstants.STATUS);

      return riskyStatus.equals(XacHttpConstants.YES);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
    catch (JSONException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public List<String> getCollateralCode(String colType) throws BpmServiceException
  {
    if (StringUtils.isEmpty(colType))
    {
      throw new BpmServiceException(COLLATERAL_TYPE_IS_NULL_ERROR_CODE, COLLATERAL_TYPE_IS_NULL_ERROR_MESSAGE);
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GET_COLLATERAL_CODE);
    String securityCode = generateSecurityCode(source, function);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(getNewCoreEndPoint(), source)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(securityCode)
        .headerContentType(APPLICATION_JSON)
        .build();

    JSONObject type = new JSONObject();

    type.put("type", colType);

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, type);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        JSONArray jsonResponse = XacHttpUtil.getCollateralsCodeArray(environment, xacClient, response);
        return CollateralNewCoreUtil.toCollateralCodeList(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String createImmovableCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_CREATE_IMMOVABLE_COLL);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    String collType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String isConfirmedStr = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJSON = toCollGenericInfoJSON(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toImmovableCollInfo(collType, isConfirmedStr, collateralInfo);

    JSONObject inspectionInfoJSON = toInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toOwnershipInfo(ownershipInfo);

    Map<String, JSONObject> values = new HashMap<>();

    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put("ImmovPropInfo", collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put("ownershipInfoRec", ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String createMachineryCollateral(Map<String, Object> genericInfo, Map<String, Object> collateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_CREATE_MACHINERY_COLL);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    // For Collateral Info
    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    // For Machinery Info
    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);
    String collType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String isConfirmedStr = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJSON = toMachineryColGenericInfo(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toMachineryColInfo(apportionMethod, collType, isConfirmedStr, collateralInfo);

    JSONObject inspectionInfoJSON = toInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toOwnershipInfo(ownershipInfo);

    Map<String, JSONObject> values = new HashMap<>();

    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put("MachineryInfo", collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put("OwnershipInfoRec", ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String createVehicleCollateral(Map<String, Object> collateralGenInfo, Map<String, Object> vehicleInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo)
      throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_WSO2_HEADER_ADD_VEHICLE_COLLATERAL);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestType(getHeaderRequestType())
        .headerRequestId(XacHttpUtil.generateRequestId())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestValues = new JSONObject();

    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);
    String currencyType = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);
    String collateralValueType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String fromDriveValue = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJson = toCollGenericInfoJSON(currencyType, collateralGenInfo);
    JSONObject vehicleInfoJson = toVehicleCollInfo(apportionMethod, collateralValueType, fromDriveValue, vehicleInfo);
    JSONObject inspectionInfoJson = toInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJson = toOwnershipInfo(ownershipInfo);

    requestValues.put(COLLATERAL_GEN_INFO, genericInfoJson);
    requestValues.put(VEHICLES_INFO, vehicleInfoJson);
    requestValues.put(INSPECTION_INFO, inspectionInfoJson);
    requestValues.put(OWNERSHIP_INFO, ownershipInfoJson);

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, requestValues);

    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      return XacHttpUtil.getCreatedCollateralResponse(environment, xacHttpClient, response);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getMachineryCollateral(String collateralId) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_WSO2_HEADER_GET_MACHINERY_COLLATERAL_INFO);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestType(getHeaderRequestType())
        .headerRequestId(XacHttpUtil.generateRequestId())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject body = new JSONObject();
    body.put(RESPONSE_COLLATERAL_ID, collateralId);

    JSONObject request = new JSONObject();
    request.put(REQUEST, body);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      return toMachineryCollateralMap(resultResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getImmovablePropertyCollateral(String collateralId) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_GET_IMMOVABLE_PROPERTY_COLLATERAL_INFO);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestType(getHeaderRequestType())
        .headerRequestId(XacHttpUtil.generateRequestId())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject body = new JSONObject();
    body.put(RESPONSE_COLLATERAL_ID, collateralId);

    JSONObject request = new JSONObject();
    request.put(REQUEST, body);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      return toImmovablePropertyCollateralMap(resultResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getVehicleCollInfo(String collateralId) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_WSO2_HEADER_GET_VEHICLE_COLLATERAL_INFO);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();
    JSONObject requestBody = new JSONObject();
    requestBody.put(RESPONSE_COLLATERAL_ID, collateralId);
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      return toVehicleCollMap(resultResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getOtherCollateral(String collateralId) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_WSO2_HEADER_GET_OTHER_COLLATERAL_INFO);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestType(getHeaderRequestType())
        .headerRequestId(XacHttpUtil.generateRequestId())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject body = new JSONObject();
    body.put(RESPONSE_COLLATERAL_ID, collateralId);

    JSONObject request = new JSONObject();
    request.put(REQUEST, body);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      return toOtherCollateralMap(resultResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public String modifyMachineryCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_MODIFY_MACHINERY_COLLATERAL);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    // For Collateral Info
    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    // For Machinery Info
    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);
    String collType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String isConfirmedStr = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJSON = toMachineryColGenericInfo(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toMachineryColInfo(apportionMethod, collType, isConfirmedStr, collateralInfo);

    JSONObject inspectionInfoJSON = toModifyInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toModifyOwnershipInfo(ownershipInfo);

    JSONObject values = new JSONObject();

    values.put(RESPONSE_COLLATERAL_ID, collateralId);
    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put("MachineryInfo", collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put("OwnershipInfoRec", ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String modifyImmovablePropCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_MODIFY_IMMOVABLE_PROP_COLLATERAL);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    String collType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String isConfirmedStr = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJSON = toCollGenericInfoJSON(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toModifyImmovableCollInfo(collType, isConfirmedStr, collateralInfo);

    JSONObject inspectionInfoJSON = toModifyInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toModifyOwnershipInfo(ownershipInfo);

    JSONObject values = new JSONObject();

    values.put(RESPONSE_COLLATERAL_ID, collateralId);
    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put("ImmovPropInfo", collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put("ownershipInfoRec", ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String modifyVehicleCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_MODIFY_VEHICLE_PROP_COLLATERAL);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    // For Collateral Info
    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);
    String collValueType = environment.getProperty(PROPERTY_KEY_COLLATERAL_VALUE_TYPE);
    String isConfirmedStr = environment.getProperty(PROPERTY_KEY_COLLATERAL_IS_CONFIRMED);

    JSONObject genericInfoJSON = toCollGenericInfoJSON(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toVehicleCollInfo(apportionMethod, collValueType, isConfirmedStr, collateralInfo);

    JSONObject inspectionInfoJSON = toModifyInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toModifyOwnershipInfo(ownershipInfo);

    JSONObject values = new JSONObject();

    values.put(RESPONSE_COLLATERAL_ID, collateralId);
    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put(VEHICLES_INFO, collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put(OWNERSHIP_INFO, ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public String modifyOtherCollateral(String collateralId, Map<String, Object> genericInfo, Map<String, Object> collateralInfo,
      Map<String, Object> inspectionInfo, Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_MODIFY_OTHER_COLLATERAL_INFO);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    // For Collateral Info
    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);

    // For Machinery Info
    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);

    JSONObject genericInfoJSON = toCollGenericInfoJSON(collCurrency, genericInfo);
    JSONObject collateralInfoJSON = toOtherCollateralInfo(apportionMethod, collateralInfo);

    JSONObject inspectionInfoJSON = toModifyInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJSON = toModifyOwnershipInfo(ownershipInfo);

    JSONObject values = new JSONObject();

    values.put(RESPONSE_COLLATERAL_ID, collateralId);
    values.put(COLLATERAL_GEN_INFO, genericInfoJSON);
    values.put(OTHERS_INFO, collateralInfoJSON);
    values.put(INSPECTION_INFO, inspectionInfoJSON);
    values.put(OWNERSHIP_INFO, ownershipInfoJSON);

    requestBody.put(REQUEST, values);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        return XacHttpUtil.getCreatedCollateralResponse(environment, xacClient, response);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
      }
    }
  }

  @Override
  public boolean linkCollaterals(String accountNumber, String linkageType, Map<String, Object> collaterals) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_LINK_COLLATERALS);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject collLinkageBody = XacHttpUtil.createCollLinkageBody(accountNumber, linkageType, collaterals);

    JSONObject requestBody = new JSONObject();

    requestBody.put(REQUEST, collLinkageBody);

    LOGGER.info("###### COLLATERAL LINKAGE REQUEST BODY = [{}] with ACCOUNT NUMBER = [{}]", requestBody, accountNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      return XacHttpUtil.getCollLinkageResult(environment, xacClient, response);
    }
    catch (JSONException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getLoanAccountInfo(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String userId = authenticationService.getCurrentUserId();
    if(!input.get(PROCESS_TYPE_ID).isEmpty() && input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(NEW_CORE_HEADER_GET_LOAN_ACCOUNT_INFO);
    String urlFunction = getUrlFunction(NEW_CORE_HEADER_GET_LOAN_ACCOUNT_INFO);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerLanguageType(getLanguageId())
        .headerUserId(userId)
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(ACCOUNT_ID, input.get(ACCOUNT_NUMBER));
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("####### Started downloading LOAN ACCOUNT INFO = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacHttpClient.post(request.toString(), urlFunction))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      Map<String, Object> accountInfoMap = new HashMap<>();
      final String productName = getStringValue(resultResponse.get("ProductName"));
      final String productId = getStringValue(resultResponse.get("ProductID"));
      accountInfoMap.put(PRODUCT_NAME, productName);
      accountInfoMap.put(LOAN_AMOUNT, getStringValue(resultResponse.get("LoanAmount")));
      accountInfoMap.put(CIF_NUMBER, getStringValue(resultResponse.get("CustomerID")));
      accountInfoMap.put("productDescription", productId + "-" + productName);
      return accountInfoMap;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public List<LinkageCollateralInfo> getInquireCollateralDetails(String entityId, String entityType) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(PROPERTY_KEY_INQUIRE_COLLATERAL_INFO);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerLanguageType(getLanguageId())
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    requestBody.put("EntityId", entityId);
    requestBody.put("EntityType", entityType);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      JSONObject resultResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      return CollateralNewCoreUtil.toLinkageCollateralInfos(resultResponse.getJSONArray("LinkageColtrlInfo"));
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, Object> getAccountInfo(Map<String, String> input) throws BpmServiceException
  {
    String function = getHeaderFunction(NEW_CORE_HEADER_GET_ACCOUNT_INFO);
    String headerSource = getOtherHeaderSource();
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();

    requestBody.put(ACCOUNT_ID, input.get(ACCOUNT_NUMBER));
    request.put(REQUEST, requestBody);

    LOGGER.info("###############  " + "GET ACCOUNT INFO SERVICE \n BODY = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      try
      {
        JSONObject resultResponse = getResultResponse(environment, xacHttpClient, response);
        Map<String, Object> parameterMap = new HashMap<>();

        final String productId = getStringValue(resultResponse.get("AccountClass"));
        final String productName = getStringValue(resultResponse.get("AccountClassName"));

        parameterMap.put(PRODUCT_NAME, productName);
        parameterMap.put(LOAN_AMOUNT, getStringValue(resultResponse.get("OverDraftMaxLimit")));
        parameterMap.put(CIF_NUMBER, getStringValue(resultResponse.get("CustomerId")));
        parameterMap.put(PRODUCT_DESCRIPTION, productId + "-" + productName);

        return parameterMap;
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, List<String>> getCollReferenceCodes(List<String> types) throws BpmServiceException
  {
    if (types.isEmpty())
    {
      return Collections.emptyMap();
    }

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(NEW_CORE_WSO2_HEADER_GET_REFERENCE_CODES);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = createCollReferenceCodesBody(types);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONArray referenceCodeArray = XacHttpUtil.getCollReferenceCodeArray(environment, xacClient, response);
      return toCollReferenceCodes(types, referenceCodeArray);
    }
    catch (JSONException | XacHttpException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public List<Collateral> getCollateralsByCifNumber(String cifNumber) throws BpmServiceException
  {
    if (StringUtils.isEmpty(cifNumber))
    {
      return Collections.emptyList();
    }

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GETC_COLLATERAL_LIST);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.NEW_CORE_HEADER_GETC_COLLATERAL_LIST))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject body = new JSONObject();

    body.put(CIF, cifNumber);
    body.put("type", "");
    body.put("accno", "");

    JSONObject request = new JSONObject();

    request.put(REQUEST, body);

    LOGGER.info("############ Started downloading collateral infos related customer CIF number = [{}]", cifNumber);
    long start = System.currentTimeMillis();

    try (Response response = xacClient.post(request.toString()))
    {
      long finish = System.currentTimeMillis();
      long spentTime = finish - start;

      LOGGER.info("########### Spent time = [{}] for download collateral infos from CBS related customer CIF number = [{}]", spentTime, cifNumber);

      JSONArray jsonResponse = XacHttpUtil.getNewCollateralResponse(environment, xacClient, response);

      return CollateralNewCoreUtil.toCollaterals(jsonResponse);
    }
    catch (JSONException | XacHttpException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public List<XacAccount> getAccountsList(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      if (!StringUtils.isBlank(input.get(PHONE_NUMBER))){
        userId = input.get(PHONE_NUMBER);
      }
    }
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GET_ACCOUNT_LIST);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_ACCOUNT_LIST_FC))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject request = new JSONObject();

    request.put(NEW_CORE_REGISTER_ID, input.get(REGISTER_NUMBER));
    request.put(NEW_CORE_CUSTOMER_ID, input.get(CIF_NUMBER));

    LOGGER.info("####### Started downloading ACCOUNT LIST = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacClient.post(new JSONObject().put(REQUEST, request).toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      if (jsonResponse.isEmpty())
      {
        String errorCode = "CBS012";
        throw new BpmServiceException(errorCode, "Not found XAC account info");
      }
      JSONArray accountsJSONArray = (JSONArray) jsonResponse.get("AccList");
      return fromJsonArrayToListOfAccounts(accountsJSONArray);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
    catch (JSONException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public Map<String, UDField> getUDFields(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(XacConstants.NEW_CORE_HEADER_GETC_UD_FIELDS);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_UD_FIELDS))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject request = new JSONObject();
    request.put("prodcode", input.get(PRODUCT_CODE));
    LOGGER.info("############### GETS UD FIELDS FROM CBS with PRODUCT CODE = [{}] with headerSource = {} and userId = {}", input.get(PRODUCT_CODE), headerSource, userId);

    try (Response response = xacClient.post(new JSONObject().put(REQUEST, request).toString()))
    {
      JSONArray jsonResponse = XacHttpUtil.getResultResponseArray(environment, xacClient, response);
      return fromJsonArrayToUDFMap(jsonResponse);
    }
    catch (ClassCastException e)
    {
      throw new BpmServiceException(XAC_CORE_BANKING_SERVICE_ERROR_CODE, XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE, e);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public String createLoanAccount(Map<String, Object> genericInfos, Map<String, Object> additionalInfos, List<Map<String, Object>> coBorrowers)
      throws BpmServiceException
  {
    String requestId = String.valueOf(genericInfos.get(REQUEST_ID));

    String headerSource = getHeaderSource();
    String userId = authenticationService.getCurrentUserId();
    if(genericInfos.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = String.valueOf(genericInfos.get(PHONE_NUMBER));
    }
    String function = getHeaderFunction(NEW_CORE_HEADER_CREATE_ACCOUNT);

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject requestJSON = new JSONObject();
    JSONObject requestBodyJSON = getAccountGenInfoJSON(genericInfos);

    requestBodyJSON.put(PAYMENT_PLAN, getAccountPaymentPlanJSON(genericInfos));
    requestBodyJSON.put(MIS_CODE_DETAILS, getAccountAdditionalInfoJSON(additionalInfos));
    requestBodyJSON.put(ACCT_MGR_USER_ID, additionalInfos.get("TypeOfAdvance"));

    List<JSONObject> coBorrowerInfoJSON = getCoBorrowerInfoJSON(coBorrowers);
    if (!coBorrowerInfoJSON.isEmpty())
    {
      requestBodyJSON.put(RELATED_PARTY_DETAILS, coBorrowerInfoJSON);
    }

    requestJSON.put(REQUEST, requestBodyJSON);

    LOGGER.info("########### CREAT LOAN ACCOUNT REQUEST BODY = {} WITH REQUEST ID = [{}] with headerSource = {} and userId = {}",
        requestJSON, requestId, headerSource, userId);

    try (Response response = xacClient.post(requestJSON.toString()))
    {
      return XacHttpUtil.getCreatedAccountResponse(environment, xacClient, response);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  private JSONObject getAccountGenInfoJSON(Map<String, Object> genericInfos)
  {
    JSONObject creationInfoBody = new JSONObject();

    String schmCode = String.valueOf(genericInfos.get(PRODUCT_CODE));

    String custId = String.valueOf(genericInfos.get(CIF_NUMBER));
    String acctCurr = String.valueOf(genericInfos.get(CURRENCY));

    String branchId = String.valueOf(genericInfos.get(BRANCH_ID));
    String repayMethod = String.valueOf(genericInfos.get(REPAYMENT_METHOD));

    String loanPeriodMonths = String.valueOf(genericInfos.get(LOAN_PERIOD_MONTHS));
    String loanPeriodDays = String.valueOf(genericInfos.get(LOAN_PERIOD_DAYS));

    String operAcctId = String.valueOf(genericInfos.get(OPER_ACCT_ID));
    String loanAmount = String.valueOf(genericInfos.get(LOAN_AMOUNT));
    String natureOfAdvn = String.valueOf(genericInfos.get(NATURE_OF_ADVN));

    String yearlyInterestRate = String.valueOf(genericInfos.get(INTEREST_TABLE_CODE));

    creationInfoBody.put(CUST_ID_XAC, custId);
    creationInfoBody.put(SCHM_CODE_XAC, schmCode);

    creationInfoBody.put(ACCT_CURR_XAC, acctCurr);
    creationInfoBody.put(BRANCH_ID_XAC, branchId);

    creationInfoBody.put(REPAY_METHOD_XAC, repayMethod);
    creationInfoBody.put(LOAN_PERIOD_MONTHS_XAC, loanPeriodMonths);

    creationInfoBody.put(LOAN_PERIOD_DAYS_XAC, loanPeriodDays);
    creationInfoBody.put(OPER_ACCT_ID_XAC, operAcctId);

    creationInfoBody.put(LOAN_AMOUNT_XAC, loanAmount);
    creationInfoBody.put(INTEREST_TABLE_CODE, yearlyInterestRate);
    creationInfoBody.put(NATURE_OF_ADVN, natureOfAdvn);

    return creationInfoBody;
  }

  private JSONObject getAccountPaymentPlanJSON(Map<String, Object> genericInfo)
  {
    JSONObject installFreqJSON = getPaymentFrequencyJSON(INSTALL_FREQ, genericInfo);
    JSONObject interestFreqJSON = getPaymentFrequencyJSON(INT_FREQ, genericInfo);

    JSONObject paymentPlanJSON = new JSONObject();
    JSONObject eqInstallDetailsJSON = new JSONObject();

    eqInstallDetailsJSON.put(EQ_INSTALL_FLG, genericInfo.get(EQ_INSTALL_FLG));
    eqInstallDetailsJSON.put(CODE, genericInfo.get(CODE));

    JSONObject repmtRecJSON = new JSONObject();

    repmtRecJSON.put(INSTALL_START_DT, genericInfo.get(INSTALL_START_DT));
    repmtRecJSON.put(INT_START_DT, genericInfo.get(INT_START_DT));
    repmtRecJSON.put(NO_OF_INSTALL, genericInfo.get(NO_OF_INSTALL));

    repmtRecJSON.put(INSTALL_FREQ, installFreqJSON);
    repmtRecJSON.put(INT_FREQ, interestFreqJSON);

    paymentPlanJSON.put(EQ_INSTALL_DETAILS, eqInstallDetailsJSON);
    paymentPlanJSON.put(REPMT_REC, repmtRecJSON);

    return paymentPlanJSON;
  }

  private JSONObject getPaymentFrequencyJSON(String key, Map<String, Object> infos)
  {
    JSONObject frequencyJSON = new JSONObject();
    Object info = infos.get(key);

    if (info instanceof Map)
    {
      frequencyJSON.put(CAL, ((Map<?, ?>) info).get(CAL));
      frequencyJSON.put(TYPE, ((Map<?, ?>) info).get(TYPE));

      frequencyJSON.put(WEEK_DAY, ((Map<?, ?>) info).get(WEEK_DAY));
      frequencyJSON.put(WEEK_NUM, ((Map<?, ?>) info).get(WEEK_NUM));

      frequencyJSON.put(HOL_STAT, ((Map<?, ?>) info).get(HOL_STAT));
      frequencyJSON.put(START_DT, ((Map<?, ?>) info).get(START_DT));
    }

    return frequencyJSON;
  }

  private JSONObject getAccountAdditionalInfoJSON(Map<String, Object> additionalInfos)
  {
    JSONObject misCodeDetailsJSON = new JSONObject();

    misCodeDetailsJSON.put(BORROWER_CATEGORY_CODE, additionalInfos.get("BorrowerCategoryCode"));
    misCodeDetailsJSON.put(PURPOSE_OF_ADVN, additionalInfos.get("PurposeOfAdvance"));
    misCodeDetailsJSON.put(INDUSTRY_TYPE, additionalInfos.get("CustomerIndustryType"));

    misCodeDetailsJSON.put(SECTOR_CODE, "");
    misCodeDetailsJSON.put(SUB_SECTOR_CODE, "");
    misCodeDetailsJSON.put(ACCT_OCCP_CODE, "");
    misCodeDetailsJSON.put(GUARD_COVER_CODE, "");
    misCodeDetailsJSON.put(IAS_CODE, "");

    misCodeDetailsJSON.put(FREE_CODE_1, additionalInfos.get(FREE_CODE_1));
    misCodeDetailsJSON.put(FREE_CODE_2, additionalInfos.get(FREE_CODE_2));
    misCodeDetailsJSON.put(FREE_CODE_3, additionalInfos.get(FREE_CODE_3));
    misCodeDetailsJSON.put(FREE_CODE_5, additionalInfos.get(FREE_CODE_5));

    misCodeDetailsJSON.put(FREE_CODE_6, additionalInfos.get(FREE_CODE_6));
    misCodeDetailsJSON.put(FREE_CODE_7, additionalInfos.get(FREE_CODE_7));
    misCodeDetailsJSON.put(FREE_CODE_8, additionalInfos.get(FREE_CODE_8));
    misCodeDetailsJSON.put(FREE_CODE_9, additionalInfos.get(FREE_CODE_9));
    misCodeDetailsJSON.put(FREE_CODE_10, additionalInfos.get(FREE_CODE_10));
    misCodeDetailsJSON.put(FREE_TEXT_1, additionalInfos.get(FREE_TEXT_1));
    misCodeDetailsJSON.put(ADDITIONAL_SPECIAL_CONDITION, additionalInfos.get("additionalSpecialCondition"));

    /* ADDITIONAL INFO */
    misCodeDetailsJSON.put("freeCode8", additionalInfos.get("freeCode8"));
    misCodeDetailsJSON.put("freeText15", additionalInfos.get("freeText15"));

    String modeOfAdvance = (String) additionalInfos.get("modeOfAdvance");

    misCodeDetailsJSON.put(MODE_OF_ADVN, removeDashAndGetId(modeOfAdvance));
    return misCodeDetailsJSON;
  }

  private String removeDashAndGetId(String modeOfAdvance)
  {
    String[] splitModeOfAdvance = modeOfAdvance.split("-");
    return splitModeOfAdvance[0];
  }

  private List<JSONObject> getCoBorrowerInfoJSON(List<Map<String, Object>> coBorrowers)
  {
    if (coBorrowers.isEmpty())
    {
      return Collections.emptyList();
    }
    List<JSONObject> coBorrowerJsonArray = new ArrayList<>();
    for (Map<String, Object> coBorrower : coBorrowers)
    {
      JSONObject coBorrowerJson = new JSONObject();
      Object custId = coBorrower.get(CO_BORROWER_CUST_ID);
      Object type = coBorrower.get(CO_BORROWER_TYPE);
      coBorrowerJson.put(REL_PARTY_TYPE, "J");
      coBorrowerJson.put(REL_PARTY_CODE, type);
      coBorrowerJson.put(REL_PARTY_CUST_ID, custId);

      coBorrowerJsonArray.add(coBorrowerJson);
    }
    return coBorrowerJsonArray;
  }

  private String getHeaderSource()
  {
    return environment.getProperty(XacConstants.NEW_CORE_HEADER_SOURCE);
  }

  private String getOtherHeaderSource()
  {
    return environment.getProperty(XacConstants.WSO2_HEADER_SOURCE_ACCOUNT_INFO);
  }

  private String getHeaderFunction(String prefix)
  {
    return environment.getProperty(prefix + ".function");
  }

  private String getUrlFunction(String prefix)
  {
    return environment.getProperty(prefix + ".url");
  }

  private String getHeaderRequestType()
  {
    return environment.getProperty(NEW_CORE_HEADER_REQUEST_TYPE);
  }

  private String getNewCoreEndPoint()
  {
    return environment.getProperty(XacConstants.NEW_CORE_ENDPOINT);
  }

  private String getCheckRiskyEndPoint()
  {
    return environment.getProperty(XacConstants.NEW_CORE_CHECK_RISKY_ENDPOINT);
  }

  private String getLanguageId()
  {
    return environment.getProperty(XacConstants.LANGUAGE_ID);
  }

  @Override
  public String createOtherCollateral(Map<String, Object> collateralGenInfo, Map<String, Object> otherCollateralInfo, Map<String, Object> inspectionInfo,
      Map<String, Object> ownershipInfo) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.NEW_CORE_WSO2_HEADER_ADD_OTHER_COLLATERAL);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestType(getHeaderRequestType())
        .headerRequestId(XacHttpUtil.generateRequestId())
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestValues = new JSONObject();

    String currencyType = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY_TYPE);
    String apportionMethod = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTION_METHOD);

    JSONObject genericInfoJson = toCollGenericInfoJSON(currencyType, collateralGenInfo);
    JSONObject otherCollInfoJson = toOtherCollateralInfo(apportionMethod, otherCollateralInfo);
    JSONObject inspectionInfoJson = toInspectionInfo(inspectionInfo);
    JSONObject ownershipInfoJson = toOwnershipInfo(ownershipInfo);

    requestValues.put(COLLATERAL_GEN_INFO, genericInfoJson);
    requestValues.put(OTHERS_INFO, otherCollInfoJson);
    requestValues.put(INSPECTION_INFO, inspectionInfoJson);
    requestValues.put(OWNERSHIP_INFO, ownershipInfoJson);

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, requestValues);

    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      return XacHttpUtil.getCreatedCollateralResponse(environment, xacHttpClient, response);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
    }
  }

  private String getHeaderRequestType(String prefix)
  {
    return environment.getProperty(prefix + ".requestType");
  }

  private List<XacAccount> fromJsonArrayToListOfAccounts(JSONArray accountsJSONArray)
  {
    List<XacAccount> accountsToReturn = new ArrayList<>();

    for (Object object : accountsJSONArray)
    {
      JSONObject account = (JSONObject) object;

      accountsToReturn.add(fromJsonObjectToXacAccountEntity(account));
    }
    return accountsToReturn;
  }

  private XacAccount fromJsonObjectToXacAccountEntity(JSONObject jsonObject)
  {
    String branchId = getNotNullString(jsonObject, "BRANCHID");
    String whereAboutsUnknown = getNotNullString(jsonObject, "WHEREABOUTS_UNKNOWN");
    String deceased = getNotNullString(jsonObject, "DECEASED");
    String accountType = getNotNullString(jsonObject, "PRODUCTID");
    String classType = getNotNullString(jsonObject, "CLASSTYPE");
    String frozen = getNotNullString(jsonObject, "FROZEN");

    String classification = getNotNullString(jsonObject, "CLASSIFICATION");
    String accountId = getNotNullString(jsonObject, "ACCOUNTID");
    String accountName = getNotNullString(jsonObject, "ACCOUNTNAME");
    String balance = getNotNullString(jsonObject, "BALANCE");
    String ownerType = getNotNullString(jsonObject, "OWNERTYPE");
    String productGroup = getNotNullString(jsonObject, "PRODUCTGROUP");
    String productName = getNotNullString(jsonObject, "PRODUCTNAME");
    String hamtran = getNotNullString(jsonObject, "HAMTRAN");
    String currencyId = getNotNullString(jsonObject, "CURRENCYID");
    String schemaType = getNotNullString(jsonObject, "SCHMTYPE");

    boolean isFrozen = !frozen.equals("N");
    boolean isDeceased = !deceased.equals("N");

    XacAccount account = new XacAccount(AccountId.valueOf(accountId), accountType, branchId, whereAboutsUnknown, isDeceased, classType,
        isFrozen, classification, accountName, balance, ownerType, productGroup, productName, hamtran, currencyId);

    account.setSchemaType(schemaType);

    return account;
  }

  private Map<String, UDField> fromJsonArrayToUDFMap(JSONArray jsonArray)
  {
    Map<String, UDField> jsonObjectMap = new HashMap<>();
    Map<String, List<UDFieldValue>> udFieldProperties = new HashMap<>();

    for (Object object : jsonArray)
    {
      JSONObject jsonObject = (JSONObject) object;
      String fieldName = String.valueOf(jsonObject.get("field_name"));
      if (!jsonObjectMap.containsKey(fieldName))
      {
        jsonObjectMap.put(fieldName, fromJsonObjectToUdFieldEntity(jsonObject));
      }

      setUDFieldProperty(udFieldProperties, jsonObject, fieldName);
    }

    for (Map.Entry<String, UDField> entry : jsonObjectMap.entrySet())
    {
      List<UDFieldValue> udFieldValues = udFieldProperties.get(entry.getKey());
      UDField udField = entry.getValue();
      udField.setValues(udFieldValues);
    }

    return jsonObjectMap;
  }

  private void setUDFieldProperty(Map<String, List<UDFieldValue>> udFieldProperties, JSONObject jsonObject, String fieldName)
  {
    if (udFieldProperties.containsKey(fieldName))
    {
      List<UDFieldValue> udFieldValues = udFieldProperties.get(fieldName);
      String itemId = String.valueOf(jsonObject.get("item"));
      String itemDesc = String.valueOf(jsonObject.get("item_desc"));
      udFieldValues.add(new UDFieldValue(itemId, itemDesc, false));
    }
    else
    {
      List<UDFieldValue> udFieldValues = new ArrayList<>();
      String itemId = String.valueOf(jsonObject.get("item"));
      udFieldValues.add(new UDFieldValue(itemId, itemId, false));
      udFieldProperties.put(fieldName, udFieldValues);
    }
  }

  private UDField fromJsonObjectToUdFieldEntity(JSONObject jsonObject)
  {
    UDFieldId fieldName = UDFieldId.valueOf((String) jsonObject.get("field_name"));
    String fieldDescription = (String) jsonObject.get("field_desc");

    return new UDField(fieldName, fieldDescription, null, null, false, null, null, false, false);
  }

  @Override
  public Map<String, Object> addUnschLoanRepayment(String instanceId, JSONObject variables) throws BpmServiceException
  {
    String function = getHeaderFunction(HEADER_ADD_LOAN_REPAYMENT);

    String headerSource = getOtherHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    LOGGER.info("############### ADD LOAN REPAYMENT \n BODY = [{}]  \n WITH PROCESS REQUEST ID = [{}]", requestBody, instanceId);
    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONObject resultResponse = getResponse(environment, xacHttpClient, response);
      return XacHttpUtil.toLoanPayment(resultResponse);
    }
    catch (XacHttpException e)
    {
      LOGGER.info("############### ERROR OCCURED WHEN ADD LOAN REPAYMENT \n BODY = [{}] \n WITH PROCESS REQUEST ID =[{}]", requestBody, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> makeScheduledLoanPayment(String instanceId, JSONObject variables) throws BpmServiceException
  {
    String headerSource = getOtherHeaderSource();
    String function = getHeaderFunction(HEADER_ADD_LOAN_SCH_PAYMENT);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(), headerSource)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    LOGGER.info("############### MAKE LOAN SCHEDULED PAYMENT \n BODY = [{}] \n WITH PROCESS REQUEST ID = [{}]", requestBody, instanceId);

    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONObject resultResponse = getResponse(environment, xacHttpClient, response);
      return XacHttpUtil.toScheduledLoanPayment(resultResponse);
    }
    catch (XacHttpException e)
    {
      LOGGER.info("############### ERROR OCCURRED TO MAKE LOAN SCHEDULED PAYMENT \n BODY = [{}] \n WITH PROCESS REQUEST ID = [{}]", requestBody, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }
}
