package mn.erin.bpm.domain.ohs.xac;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.account.Account;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldId;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.customer.CreateUDFieldsRequestBody;
import mn.erin.domain.bpm.usecase.customer.CreateUDFieldsRequestBodyInput;
import mn.erin.domain.bpm.usecase.customer.CreateUDFieldsRequestBodyOutput;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_DESC;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.AMT_FINANCED;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CRACCBRN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUSTID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.DRACCBRN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.DUE_DATE_SON;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.EFFECDATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ERROR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.FREQ_UNIT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.INSSTDT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.INTEREST_RATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LIABILITY;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LIABILITYAMT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MAT_TYP;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NOO_FINS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.PENALTY_RATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.PRO_CHARGE_RATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REGISTER_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.RESPONSIBILITY;
import static mn.erin.bpm.domain.ohs.xac.util.CollateralNewCoreUtil.toCollaterals;
import static mn.erin.bpm.domain.ohs.xac.util.CustomerUtil.getAddress;
import static mn.erin.bpm.domain.ohs.xac.util.CustomerUtil.toCustomer;
import static mn.erin.bpm.domain.ohs.xac.util.CustomerUtil.toCustomerFullInfo;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getNotNullString;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE;

/**
 * @author EBazarragchaa
 */
public class XacCoreBankingService implements CoreBankingService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacCoreBankingService.class);

  private static final String BRANCH_NUMBER = "101";
  private static final String SYS_DATE = "SYSDATE";
  public static final String UDEID = "UDEID";
  public static final String UDEVAL = "UDEVAL";
  public static final String INSURANCE_C_FIELD_ID = "INSURANCE_C";
  public static final String COLLATERAL_AMOUNT_FIELD_ID = "COLLATERAL_AMOUNT";

  private final Environment environment;
  private final AuthenticationService authenticationService;

  public XacCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = environment;
    this.authenticationService = authenticationService;
  }

  @Override
  public Customer findCustomerByPersonId(String personId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(personId))
    {
      String errorCode = "CBS010";
      throw new BpmServiceException(errorCode, "Person id cannot be empty!");
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CHECK_CUSTOMER_BY_PERSON_ID);
    String securityCode = generateSecurityCode(source, function);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(getEndPoint(), source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_CHECK_CUSTOMER_BY_PERSON_ID))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerBranch(BRANCH_NUMBER)
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("regno", personId);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      try
      {
        JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
        return toCustomer(jsonResponse);
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
  public Customer findCustomerByCifNumber(String cifNumber) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_GET_CUSTOMER_BY_ID);

    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_CUSTOMER_BY_ID))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerBranch(BRANCH_NUMBER)
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("custno", cifNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);

      String registerNumber = String.valueOf(jsonResponse.get("UIDVAL"));

      return toCustomerFullInfo(jsonResponse, registerNumber, getAddress(jsonResponse));
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
  public boolean checkRiskyCustomer(String registerNumber) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CHECK_RISKY_CUSTOMER);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_CHECK_RISKY_CUSTOMER))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject json = new JSONObject();
    JSONObject request = new JSONObject();

    request.put("registerNo", registerNumber);
    request.put("languageId", "EN");

    json.put(XacHttpConstants.REQUEST, request);

    try (Response response = xacClient.post(json.toString()))
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
  public Account createCustomerAccount(String cifNumber, String accountType)
  {
    return null;
  }

  @Override
  public String getCustomerResource(String customerCif) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CALCULATE_DEPOSIT);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_CALCULATE_DEPOSIT))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put("cif", customerCif);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      JSONArray accountInteractions = (JSONArray) jsonResponse.get("res");
      JSONObject lastInteraction = accountInteractions.getJSONObject(accountInteractions.length() - 1);
      String resource = (String) lastInteraction.get("totalvalue");

      return resource;
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
  public int getCustomerLoanPeriodInformation(String customerCif) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CALCULATE_LOAN_CYCLE);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_CALCULATE_LOAN_CYCLE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put("cif", customerCif);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      return (int) jsonResponse.get("TotalCycle");
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
  public Map<String, UDField> getUDFields(String productCode) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_GET_UD_FIELDS);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_UD_FIELDS))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put("prodcode", productCode);
    LOGGER.info("############### GETS UD FIELDS FROM CBS with PRODUCT CODE = [{}]", productCode);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONArray jsonResponse = XacHttpUtil.getResultResponseArray(environment, xacClient, response);
      return fromJsonArrayToMap(jsonResponse);
    }
    catch (ClassCastException e)
    {
      throw new BpmServiceException(XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE, e);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public Map<String, UDField> getUDFieldsByFunction(String bodyFunction) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_GET_UD_FIELDS_BY_FUNCTION);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_UD_FIELDS_BY_FUNCTION))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put("function", bodyFunction);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONArray jsonResponse = XacHttpUtil.getResultResponseArray(environment, xacClient, response);
      return fromJsonArrayToMap(jsonResponse);
    }
    catch (ClassCastException e)
    {
      throw new BpmServiceException(XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE, e);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public int createLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers) throws BpmServiceException
  {
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CREATE_CL_ACCOUNT);
    String url = getEndPoint() + function;

    LOGGER.info("########### CREATE ACCOUNT URL = [{}]", url);

    String currentUserId = authenticationService.getCurrentUserId();
    String branchId = accountInformation.get("BRN");

    JSONObject jsonRequestBody = createLoanAccountCreationRequest(productCode, accountInformation, coBorrowers);
    String resultString = JavaPostRequest.createLoanAccount(environment, url, jsonRequestBody.toString(), currentUserId, branchId);

    try
    {
      if (null == resultString)
      {
        String errorCode = "CBS016";
        LOGGER.error("############ RESULT STRING is null from XAC service.");
        throw new BpmServiceException(errorCode, "#### RESULT STRING is null!");
      }
      JSONObject responseJSON = new JSONObject(resultString);

      if (responseJSON.has(RESPONSE))
      {
        JSONObject res = (JSONObject) responseJSON.get(RESPONSE);
        int accountNumber = (int) res.get("ACCNO");

        return accountNumber;
      }

      if (responseJSON.has(ERROR))
      {
        if (responseJSON.get(ERROR) instanceof JSONObject)
        {
          JSONObject error = responseJSON.getJSONObject(ERROR);
          String errorCode = error.getString(ERROR_CODE);
          String errorDesc = error.getString(ERROR_DESC);

          LOGGER.error("########## ACCOUNT CREATION ERROR: ={} - ={}", errorCode, errorDesc);
          throw new BpmServiceException(errorCode, errorDesc);
        }
        else if (responseJSON.get(ERROR) instanceof JSONArray)
        {
          JSONArray errorArray = responseJSON.getJSONArray(ERROR);
          JSONObject error = errorArray.getJSONObject(0);
          String errorCode = error.getString(ERROR_CODE);
          String errorDesc = error.getString(ERROR_DESC);

          LOGGER.error("ACCOUNT CREATION ERROR:   ={}", errorDesc);
          throw new BpmServiceException(errorCode, errorDesc);
        }
      }

      return 0;
    }
    catch (JSONException e)
    {
      throw new BpmServiceException("#### JSONException : ", e);
    }
  }

  @Override
  public int createCollateralLoanAccount(String productCode, Map<String, String> accountInformation, List<Map<String, String>> coBorrowers,
      Map<String, Map<String, Object>> collateralMap)
      throws BpmServiceException
  {
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CREATE_CL_ACCOUNT);
    String url = getEndPoint() + function;

    LOGGER.info("########### CREATE ACCOUNT URL = [{}]", url);

    String currentUserId = authenticationService.getCurrentUserId();
    String branchId = accountInformation.get("BRN");

    try
    {
      JSONObject jsonRequestBody = createLoanAccountCreationCollateralRequest(productCode, accountInformation, coBorrowers, collateralMap);
      String resultString = JavaPostRequest.createLoanAccount(environment, url, jsonRequestBody.toString(), currentUserId, branchId);

      LOGGER.info("################### CREATE LOAN ACCOUNT WITH COLLATERAL JSON BODY = [{}]", jsonRequestBody.toString());

      if (null == resultString)
      {
        String errorCode = "CBS016";
        LOGGER.error("############ RESULT STRING is null from XAC service.");
        throw new BpmServiceException(errorCode, "#### RESULT STRING is null!");
      }
      JSONObject responseJSON = new JSONObject(resultString);

      if (responseJSON.has(RESPONSE))
      {
        JSONObject res = (JSONObject) responseJSON.get(RESPONSE);
        int accountNumber = (int) res.get("ACCNO");

        return accountNumber;
      }

      if (responseJSON.has(ERROR))
      {
        if (responseJSON.get(ERROR) instanceof JSONObject)
        {
          JSONObject error = responseJSON.getJSONObject(ERROR);
          String errorCode = error.getString(ERROR_CODE);
          String errorDesc = error.getString(ERROR_DESC);

          LOGGER.error("########## ACCOUNT CREATION ERROR: ={} - ={}", errorCode, errorDesc);
          throw new BpmServiceException(errorCode, errorDesc);
        }
        else if (responseJSON.get(ERROR) instanceof JSONArray)
        {
          JSONArray errorArray = responseJSON.getJSONArray(ERROR);
          JSONObject error = errorArray.getJSONObject(0);
          String errorCode = error.getString(ERROR_CODE);
          String errorDesc = error.getString(ERROR_DESC);

          LOGGER.error("ACCOUNT CREATION ERROR:   ={}", errorDesc);
          throw new BpmServiceException(errorCode, errorDesc);
        }
      }

      return 0;
    }
    catch (JSONException e)
    {
      throw new BpmServiceException("#### JSONException : ", e);
    }
  }

  @Override
  public List<XacAccount> getAccountsListFC(String regNumber, String customerID) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_GET_ACCOUNT_LIST_FC);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_ACCOUNT_LIST_FC))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put(REGISTER_NO, regNumber);
    request.put(CUST_NO, customerID);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      if (jsonResponse.isEmpty())
      {
        String errorCode = "CBS012";
        throw new BpmServiceException(errorCode, "Not found XAC account info");
      }
      JSONArray accountsJSONArray = (JSONArray) jsonResponse.get("account");
      List<XacAccount> accounts = fromJsonArrayToListOfAccounts(accountsJSONArray);
      return accounts;
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
  public List<Collateral> getCollateralsByCifNumber(String cifNumber) throws BpmServiceException
  {
    if (StringUtils.isEmpty(cifNumber))
    {
      return Collections.emptyList();
    }

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_GET_COLLATERAL_LIST);

    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_GET_COLLATERAL_LIST))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    requestBody.put(CUST_NO, cifNumber);

    LOGGER.info("############ Started downloading collateral infos related customer CIF number = [{}]", cifNumber);
    long start = System.currentTimeMillis();

    try (Response response = xacClient.post(requestBody.toString()))
    {
      long finish = System.currentTimeMillis();
      long spentTime = finish - start;

      LOGGER.info("########### Spent time = [{}] for download collateral infos from CBS related customer CIF number = [{}]", spentTime, cifNumber);
      JSONObject jsonResponse = XacHttpUtil.getCollateralResponse(environment, xacClient, response);

      return toCollaterals(jsonResponse);
    }
    catch (JSONException | XacHttpException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public boolean createCollateral(String branch, String cif, Map<String, String> collateralCreationInfo, Map<String, Serializable> udFields)
      throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(XacConstants.WSO2_HEADER_CREATE_COLLATERAL);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_CREATE_COLLATERAL))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerBranch(branch)
        .build();

    JSONObject request = new JSONObject();
    request.put("Collateral-IO", createCollateralBody(branch, cif, collateralCreationInfo, udFields));

    LOGGER.info("################### CREATE COLLATERAL JSON BODY = [{}]", request.toString());

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponseOfCreateCollateral(environment, xacClient, response);
      LOGGER.info("################### COLLATERAL JSON RESPONSE = [{}]", jsonResponse != null ? jsonResponse.toString() : null);

      if (jsonResponse == null)
      {
        return false;
      }

      return jsonResponse.has("COLLATERAL_CODE");
    }
    catch (ClassCastException e)
    {
      throw new BpmServiceException(XAC_CORE_BANKING_SERVICE_ERROR_MESSAGE, e);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  private JSONObject createCollateralBody(String branch, String cif, Map<String, String> collateralCreationInfo, Map<String, Serializable> udFields)
  {
    JSONObject collateralIO = new JSONObject();

    collateralIO.put(LIABILITY, cif);
    collateralIO.put("LIABILITYBRANCH", branch);
    collateralIO.put("COLLATERALDESCRIPTION", collateralCreationInfo.get("collateralDescription"));
    collateralIO.put("COLLATERALCODE", collateralCreationInfo.get("collateralId"));
    collateralIO.put("COLLATERALCATEGORY", collateralCreationInfo.get("collateralCategory"));
    collateralIO.put("COLLATERALCURRENCY", "MNT");
    collateralIO.put("HAIRCUT", collateralCreationInfo.get("haircut"));
    collateralIO.put("COLLATERALVALUE", collateralCreationInfo.get("collateralValue"));
    collateralIO.put("STARTDATE", collateralCreationInfo.get("collateralCreationStartDate"));
    collateralIO.put("ENDDATE", collateralCreationInfo.get("collateralCreationEndDate"));
    collateralIO.put("AVAILABLE", "Y");
    collateralIO.put("AUTOPOOLCREATE", "N");

    JSONArray udfDetails = new JSONArray();

    if (!udFields.containsKey("OWNED_TYPE"))
    {
      JSONObject ownedTypeJsonUDField = new JSONObject();
      ownedTypeJsonUDField.put("FIELD_NAME", "OWNED_TYPE");
      ownedTypeJsonUDField.put("FIELD_VALUE", "OT00");
      udfDetails.put(ownedTypeJsonUDField);
    }

    for (Map.Entry<String, Serializable> udField : udFields.entrySet())
    {
      JSONObject udFieldToEnter = new JSONObject();

      String key = udField.getKey();
      String value = (String) udField.getValue();

      //remove % from value
      if (value != null)
      {
        value = value.replace("%", "");

        udFieldToEnter.put("FIELD_NAME", key);
        udFieldToEnter.put("FIELD_VALUE", value);

        udfDetails.put(udFieldToEnter);
      }
    }

    collateralIO.put("UDFDETAILS", udfDetails);

    return collateralIO;
  }

  private JSONObject createLoanAccountCreationCollateralRequest(String productCode, Map<String, String> fields,
      List<Map<String, String>> coBorrowers, Map<String, Map<String, Object>> collateralMap) throws BpmServiceException
  {
    String valueDate = environment.getProperty("account.creation.valdt");

    if (StringUtils.isEmpty(valueDate))
    {
      LOGGER.info("######## Gets system VALUE DATE for LOAN ACCOUNT REQUEST, Reason for that property is empty.");
      valueDate = DateTimeUtils.toShortIsoString(new Date());
    }
    else if (valueDate.equalsIgnoreCase(SYS_DATE))
    {
      valueDate = DateTimeUtils.toShortIsoString(new Date());
    }

    CreateUDFieldsRequestBody createUDFieldsRequestBody = new CreateUDFieldsRequestBody();
    Map<String, UDField> udFieldsMap = getUDFields(productCode);
    CreateUDFieldsRequestBodyInput createUDFieldsRequestBodyInput = new CreateUDFieldsRequestBodyInput(productCode, fields, udFieldsMap);
    CreateUDFieldsRequestBodyOutput createUDFieldsRequestBodyOutput = createUDFieldsRequestBody.execute(createUDFieldsRequestBodyInput);

    JSONObject requestBody = new JSONObject();

    JSONObject accountMasterFull = new JSONObject();
    accountMasterFull.put("BRN", fields.get("BRN"));
    accountMasterFull.put("PROD", productCode);
    accountMasterFull.put(CUSTID, fields.get(CUSTID));
    accountMasterFull.put("VALDT", valueDate); //these are not needed when service is creating schedule
    accountMasterFull.put(AMT_FINANCED, fields.get(AMT_FINANCED));
    accountMasterFull.put("CCY", fields.get("CCY"));
    accountMasterFull.put(MAT_TYP, fields.get(MAT_TYP));
    accountMasterFull.put(NOO_FINS, fields.get(NOO_FINS));
    accountMasterFull.put("FREQ", fields.get("FREQ"));
    accountMasterFull.put(FREQ_UNIT, fields.get(FREQ_UNIT));
    accountMasterFull.put(INSSTDT, fields.get(INSSTDT));
    accountMasterFull.put(CRACCBRN, fields.get(CRACCBRN));
    accountMasterFull.put("CRPRODAC", fields.get("CRPRODACC"));
    accountMasterFull.put(DRACCBRN, fields.get(DRACCBRN));
    accountMasterFull.put("DRPRODAC", fields.get("DRPRODACC"));
    accountMasterFull.put(DUE_DATE_SON, fields.get(DUE_DATE_SON));

    //-----------------------------------------

    JSONObject effectiveDate = new JSONObject();
    effectiveDate.put("EFFDT", valueDate);

    JSONArray udeValsArray = new JSONArray();

    if (null != fields.get(INSURANCE_C_FIELD_ID)
        && null != fields.get(COLLATERAL_AMOUNT_FIELD_ID))
    {
      JSONObject insuranceCJSON = new JSONObject();
      JSONObject totalCollateralJSON = new JSONObject();

      insuranceCJSON.put(UDEID, INSURANCE_C_FIELD_ID);
      insuranceCJSON.put(UDEVAL, fields.get(INSURANCE_C_FIELD_ID));

      totalCollateralJSON.put(UDEID, COLLATERAL_AMOUNT_FIELD_ID);
      totalCollateralJSON.put(UDEVAL, fields.get(COLLATERAL_AMOUNT_FIELD_ID));

      udeValsArray.put(insuranceCJSON);
      udeValsArray.put(totalCollateralJSON);
    }

    JSONObject interestRate = new JSONObject();
    interestRate.put(UDEID, INTEREST_RATE);

    Object interestRateN = null;
    try
    {
      interestRateN = Integer.valueOf(fields.get(INTEREST_RATE));
    }
    catch (Exception e)
    {
      interestRateN = Double.valueOf(fields.get(INTEREST_RATE));
    }

    interestRate.put("UDEVAL", interestRateN);
    udeValsArray.put(interestRate);

    JSONObject fees = new JSONObject();
    fees.put(UDEID, PRO_CHARGE_RATE);

    Object feesN = null;
    try
    {
      feesN = Integer.valueOf(fields.get(PRO_CHARGE_RATE));
    }
    catch (Exception e)
    {
      feesN = Double.valueOf(fields.get(PRO_CHARGE_RATE));
    }
    fees.put("UDEVAL", feesN);
    //fees.put("UDEVAL", Double.valueOf(1.4));
    udeValsArray.put(fees);

    JSONObject penaltyRate = new JSONObject();
    penaltyRate.put(UDEID, PENALTY_RATE);

    Object penaltyRateN = null;
    try
    {
      penaltyRateN = Integer.valueOf(fields.get(PENALTY_RATE));
    }
    catch (Exception e)
    {
      penaltyRateN = Double.valueOf(fields.get(PENALTY_RATE));
    }
    penaltyRate.put("UDEVAL", penaltyRateN);
    udeValsArray.put(penaltyRate);

    JSONObject loanStatusFlag = new JSONObject();
    loanStatusFlag.put(UDEID, "LOAN_STATUS_FLAG");
    loanStatusFlag.put("UDEVAL", "0");
    udeValsArray.put(loanStatusFlag);

    effectiveDate.put("Ude-Vals", udeValsArray);

    //-----------------------------------------
    //CO_BORROWERS

    if (!coBorrowers.isEmpty())
    {
      JSONArray coBorrowersJSONArray = new JSONArray();

      for (Map<String, String> coBorrower : coBorrowers)
      {
        JSONObject coBorrowerJSON = new JSONObject();

        coBorrowerJSON.put(CUSTID, coBorrower.get("cifCB"));
        coBorrowerJSON.put(CUST_NAME, coBorrower.get("fullNameCB"));
        coBorrowerJSON.put(RESPONSIBILITY, "CBW");
        coBorrowerJSON.put(LIABILITY, fields.get(LIABILITY));
        coBorrowerJSON.put(LIABILITYAMT, fields.get(LIABILITYAMT));
        coBorrowerJSON.put(EFFECDATE, valueDate);

        coBorrowersJSONArray.put(coBorrowerJSON);
      }

      JSONObject originalBorrowerJSON = new JSONObject();
      originalBorrowerJSON.put(CUSTID, fields.get(CUSTID));
      originalBorrowerJSON.put(CUST_NAME, fields.get("customerName"));
      originalBorrowerJSON.put(RESPONSIBILITY, "BRW");
      originalBorrowerJSON.put(LIABILITY, fields.get(LIABILITY));
      originalBorrowerJSON.put(LIABILITYAMT, fields.get(LIABILITYAMT));
      originalBorrowerJSON.put(EFFECDATE, valueDate);

      coBorrowersJSONArray.put(originalBorrowerJSON);

      accountMasterFull.put("Othr-Applicants", coBorrowersJSONArray);
    }

    //------------------------------------------ Collateral Array.

    JSONArray collateralLinkages = new JSONArray();

    for (Map.Entry<String, Map<String, Object>> collateralEntry : collateralMap.entrySet())
    {
      String collateralId = collateralEntry.getKey();

      Map<String, Object> values = collateralEntry.getValue();

      String loanAmount = String.valueOf(values.get("loanAmount"));
      String amountOfAssessment = String.valueOf(values.get("amountOfAssessment"));

      String collateralConnectionRate = String.valueOf(values.get("collateralConnectionRate"));
      String collOrder = String.valueOf(values.get("order"));

      String haircut = String.valueOf(values.get("haircut"));
      String description = String.valueOf(values.get("description"));

      JSONObject collateralLinkageJSON = new JSONObject();

      collateralLinkageJSON.put("LINKAGE_TYPE", "C");
      collateralLinkageJSON.put("LINKED_REF_NO", collateralId);
      collateralLinkageJSON.put("UTILIZATION_NO", collOrder);

      collateralLinkageJSON.put("CCY", "MNT");
      collateralLinkageJSON.put("OVERALL_AMOUNT", amountOfAssessment);
      collateralLinkageJSON.put("LIMITAMT", loanAmount);
      collateralLinkageJSON.put("LINKED_PER", collateralConnectionRate);
      collateralLinkageJSON.put("BRANCH", fields.get("BRN"));

      collateralLinkageJSON.put("HAIRCUT", haircut);
      //collateralLinkageJSON.put("DESCRIPTION", description);
      collateralLinkageJSON.put("DESCRIPTION", "");

      collateralLinkages.put(collateralLinkageJSON);
    }

    //-------------------------------------------

    accountMasterFull.put("Effec-Date", effectiveDate);

    accountMasterFull.put("Clvws-Account-Udf-Date", createUDFieldsRequestBodyOutput.getDateUdFields());
    accountMasterFull.put("Clvws-Account-Udf-Num", createUDFieldsRequestBodyOutput.getNumberUdFields());
    accountMasterFull.put("Clvws-Account-Udf-Char", createUDFieldsRequestBodyOutput.getTextUdFields());
    accountMasterFull.put("Coll-Linkages", collateralLinkages);

    requestBody.put("Account-Master-Full", accountMasterFull);

    return requestBody;
  }

  private JSONObject createLoanAccountCreationRequest(String productCode, Map<String, String> fields,
      List<Map<String, String>> coBorrowers) throws BpmServiceException
  {
    String valueDate = environment.getProperty("account.creation.valdt");

    if (StringUtils.isEmpty(valueDate))
    {
      LOGGER.info("######## Gets system VALUE DATE for LOAN ACCOUNT REQUEST, Reason for that property is empty.");
      valueDate = DateTimeUtils.toShortIsoString(new Date());
    }
    else if (valueDate.equalsIgnoreCase(SYS_DATE))
    {
      valueDate = DateTimeUtils.toShortIsoString(new Date());
    }

    CreateUDFieldsRequestBody createUDFieldsRequestBody = new CreateUDFieldsRequestBody();
    Map<String, UDField> udFieldsMap = getUDFields(productCode);
    CreateUDFieldsRequestBodyInput createUDFieldsRequestBodyInput = new CreateUDFieldsRequestBodyInput(productCode, fields, udFieldsMap);
    CreateUDFieldsRequestBodyOutput createUDFieldsRequestBodyOutput = createUDFieldsRequestBody.execute(createUDFieldsRequestBodyInput);

    JSONObject requestBody = new JSONObject();

    JSONObject accountMasterFull = new JSONObject();
    accountMasterFull.put("BRN", fields.get("BRN"));
    accountMasterFull.put("PROD", productCode);
    accountMasterFull.put(CUSTID, fields.get(CUSTID));
    accountMasterFull.put("VALDT", valueDate); //these are not needed when service is creating schedule
    //accountMasterFull.put("MATDT", fields.get("MATDT"));
    accountMasterFull.put(AMT_FINANCED, fields.get(AMT_FINANCED));
    accountMasterFull.put("CCY", fields.get("CCY"));
    accountMasterFull.put(MAT_TYP, fields.get(MAT_TYP));
    accountMasterFull.put(NOO_FINS, fields.get(NOO_FINS));
    accountMasterFull.put("FREQ", fields.get("FREQ"));
    accountMasterFull.put(FREQ_UNIT, fields.get(FREQ_UNIT));
    accountMasterFull.put(INSSTDT, fields.get(INSSTDT));
    accountMasterFull.put(CRACCBRN, fields.get(CRACCBRN));
    accountMasterFull.put("CRPRODAC", fields.get("CRPRODACC"));
    accountMasterFull.put(DRACCBRN, fields.get(DRACCBRN));
    accountMasterFull.put("DRPRODAC", fields.get("DRPRODACC"));
    accountMasterFull.put(DUE_DATE_SON, fields.get(DUE_DATE_SON));

    //-----------------------------------------

    JSONObject effectiveDate = new JSONObject();
    effectiveDate.put("EFFDT", valueDate);

    JSONArray udeValsArray = new JSONArray();

    JSONObject interestRate = new JSONObject();
    interestRate.put(UDEID, INTEREST_RATE);

    Object interestRateN = null;
    try
    {
      interestRateN = Integer.valueOf(fields.get(INTEREST_RATE));
    }
    catch (Exception e)
    {
      interestRateN = Double.valueOf(fields.get(INTEREST_RATE));
    }

    interestRate.put("UDEVAL", interestRateN);
    udeValsArray.put(interestRate);

    JSONObject fees = new JSONObject();
    fees.put(UDEID, PRO_CHARGE_RATE);

    Object feesN = null;
    try
    {
      feesN = Integer.valueOf(fields.get(PRO_CHARGE_RATE));
    }
    catch (Exception e)
    {
      feesN = Double.valueOf(fields.get(PRO_CHARGE_RATE));
    }
    fees.put("UDEVAL", feesN);
    udeValsArray.put(fees);

    JSONObject penaltyRate = new JSONObject();
    penaltyRate.put(UDEID, PENALTY_RATE);

    Object penaltyRateN = null;
    try
    {
      penaltyRateN = Integer.valueOf(fields.get(PENALTY_RATE));
    }
    catch (Exception e)
    {
      penaltyRateN = Double.valueOf(fields.get(PENALTY_RATE));
    }
    penaltyRate.put("UDEVAL", penaltyRateN);
    udeValsArray.put(penaltyRate);

    JSONObject loanStatusFlag = new JSONObject();
    loanStatusFlag.put(UDEID, "LOAN_STATUS_FLAG");
    loanStatusFlag.put("UDEVAL", "0");
    udeValsArray.put(loanStatusFlag);

    effectiveDate.put("Ude-Vals", udeValsArray);

    //-----------------------------------------
    //CO_BORROWERS

    if (!coBorrowers.isEmpty())
    {
      JSONArray coBorrowersJSONArray = new JSONArray();

      for (Map<String, String> coBorrower : coBorrowers)
      {
        JSONObject coBorrowerJSON = new JSONObject();

        coBorrowerJSON.put(CUSTID, coBorrower.get("cifCB"));
        coBorrowerJSON.put(CUST_NAME, coBorrower.get("fullNameCB"));
        coBorrowerJSON.put(RESPONSIBILITY, "CBW");
        coBorrowerJSON.put(LIABILITY, fields.get(LIABILITY));
        coBorrowerJSON.put(LIABILITYAMT, fields.get(LIABILITYAMT));
        coBorrowerJSON.put(EFFECDATE, valueDate);

        coBorrowersJSONArray.put(coBorrowerJSON);
      }

      JSONObject originalBorrowerJSON = new JSONObject();
      originalBorrowerJSON.put(CUSTID, fields.get(CUSTID));
      originalBorrowerJSON.put(CUST_NAME, fields.get("customerName"));
      originalBorrowerJSON.put(RESPONSIBILITY, "BRW");
      originalBorrowerJSON.put(LIABILITY, fields.get(LIABILITY));
      originalBorrowerJSON.put(LIABILITYAMT, fields.get(LIABILITYAMT));
      originalBorrowerJSON.put(EFFECDATE, valueDate);

      coBorrowersJSONArray.put(originalBorrowerJSON);

      accountMasterFull.put("Othr-Applicants", coBorrowersJSONArray);
    }

    //------------------------------------------

    accountMasterFull.put("Effec-Date", effectiveDate);

    accountMasterFull.put("Clvws-Account-Udf-Date", createUDFieldsRequestBodyOutput.getDateUdFields());
    accountMasterFull.put("Clvws-Account-Udf-Num", createUDFieldsRequestBodyOutput.getNumberUdFields());
    accountMasterFull.put("Clvws-Account-Udf-Char", createUDFieldsRequestBodyOutput.getTextUdFields());

    requestBody.put("Account-Master-Full", accountMasterFull);

    return requestBody;
  }

  private String getEndPoint()
  {
    return environment.getProperty(XacConstants.WSO2_ENDPOINT);
  }

  private String getHeaderSource()
  {
    return environment.getProperty(XacConstants.WSO2_HEADER_SOURCE);
  }

  private String getHeaderFunction(String prefix)
  {
    return environment.getProperty(prefix + ".function");
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
    String branchId = getNotNullString(jsonObject, "branchid");
    String whereAboutsUnknown = getNotNullString(jsonObject, "whereabouts_unknown");
    String deceased = getNotNullString(jsonObject, "deceased");
    String accountType = getNotNullString(jsonObject, "productid");
    String classType = getNotNullString(jsonObject, "classtype");
    String frozen = getNotNullString(jsonObject, "frozen");

    String classification = getNotNullString(jsonObject, "classification");
    String accountId = getNotNullString(jsonObject, "accountid");
    String accountName = getNotNullString(jsonObject, "accountname");
    String balance = getNotNullString(jsonObject, "balance");
    String ownerType = getNotNullString(jsonObject, "ownertype");
    String productGroup = getNotNullString(jsonObject, "productgroup");
    String productName = getNotNullString(jsonObject, "productname");
    String hamtran = getNotNullString(jsonObject, "hamtran");
    String currencyId = getNotNullString(jsonObject, "currencyid");

    boolean isFrozen = (frozen.equals("N")) ? false : true;
    boolean isDeceased = (deceased.equals("N")) ? false : true;

    return new XacAccount(AccountId.valueOf(accountId), accountType, branchId, whereAboutsUnknown, isDeceased, classType,
        isFrozen, classification, accountName, balance, ownerType, productGroup, productName, hamtran, currencyId);
  }

  private UDField fromJsonObjectToUdFieldEntity(JSONObject jsonObject)
  {
    UDFieldId fieldName = UDFieldId.valueOf((String) jsonObject.get("field_name"));
    String fieldDescription = (String) jsonObject.get("field_description");
    String fieldType = (String) jsonObject.get("field_type");
    String fieldNumber = (String) jsonObject.get("field_no");
    String mandatoryString = (String) jsonObject.get("mandatory");
    boolean mandatory = (mandatoryString.equals("N")) ? false : true;
    String fixedLength = (String) jsonObject.get("fixed_length");
    String defaultValue = (jsonObject.get("default_value").equals(JSONObject.NULL)) ? null : (String) jsonObject.get("default_value");
    String updateAllowedString = (String) jsonObject.get("update_allowed");
    boolean updateAllowed = (updateAllowedString.equals("N")) ? false : true;
    String uniqueFieldString = (String) jsonObject.get("unique_field");
    boolean uniqueField = (uniqueFieldString.equals("N")) ? false : true;
    List<JSONObject> values = getValues(jsonObject);

    UDField udField = new UDField(fieldName, fieldDescription, fieldType, fieldNumber, mandatory, fixedLength, defaultValue, updateAllowed, uniqueField);

    for (Object object : values)
    {
      JSONObject jsonObjectValue = (JSONObject) object;
      String itemId = (String) jsonObjectValue.get("item");
      String itemDescription = jsonObjectValue.get("item_desc").equals(JSONObject.NULL) ? null : (String) jsonObjectValue.get("item_desc");
      String isDefaultString = (String) jsonObjectValue.get("is_default");
      boolean isDefault = isDefaultString.equals("N") ? false : true;
      UDFieldValue udFieldValueToAdd = new UDFieldValue(itemId, itemDescription, isDefault);

      udField.addValues(udFieldValueToAdd);
    }

    return udField;
  }

  private Map<String, UDField> fromJsonArrayToMap(JSONArray jsonArray)
  {
    Map<String, UDField> jsonObjectMap = new HashMap<>();

    for (Object object : jsonArray)
    {
      JSONObject jsonObject = (JSONObject) object;

      UDField udField = fromJsonObjectToUdFieldEntity(jsonObject);

      jsonObjectMap.put((String) jsonObject.get("field_name"), udField);
    }

    return jsonObjectMap;
  }

  private List<JSONObject> getValues(JSONObject jsonObject)
  {
    if (!jsonObject.toMap().containsKey("values"))
    {
      return Collections.emptyList();
    }

    JSONArray values = (JSONArray) jsonObject.get("values");
    List<JSONObject> jsonObjectValues = new ArrayList<>();

    for (Object valueObject : values)
    {
      JSONObject value = (JSONObject) valueObject;
      jsonObjectValues.add(value);
    }

    return jsonObjectValues;
  }

  private static String format(double num)
  {
    return new DecimalFormat("#.##").format(num);
  }
}
