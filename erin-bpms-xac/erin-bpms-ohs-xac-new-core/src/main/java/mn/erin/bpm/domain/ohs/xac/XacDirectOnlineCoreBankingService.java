package mn.erin.bpm.domain.ohs.xac;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants;
import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.HEADER_SOURCE_EGW;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_SOURCE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_ACC_LIEN_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_BNPL_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_ADD_ACC_LIEN;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_FIND_ORGANIZATION_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_FUNCTION_ADD_TRANSACTION;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT_CHARGE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_GET_ACC_LIEN;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_GET_LOAN_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_GET_REPAYMENT_SCHEDULE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_MODIFY_ACC_LIEN;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_SET_PAY_OFF_PROCESS;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_HEADER_SOURCE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_INTERNET_BANK_HEADER_SOURCE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO_HEADER_GET_DBSR_LIST;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.BALANCE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.CURRENCY_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.FAILURE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_TYPE_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE_STATUS;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.STATUS;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.STATUS_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.SUCCESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCOUNT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_CURR_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.SCHM_CODE_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.SERVICE_PARAMETERS_EMPTY_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.BRANCH_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.BRANCH_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLECTED_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CONFMISC10;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_NAME_LOWER_CASE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.DAN_REGISTRATION_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.DISTRICT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ERATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ERATE_MAX;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.EXP_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LANGUAGE_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LIEN_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LOAN_CLASS_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LOAN_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.LOAN_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MODULE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REGISTER_NUM;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REPAYMENT_SCHEDULE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.SCHM_CODE_DESC_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.SCHM_TYPE_XAC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.START_DATE;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateRequestId;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderFunction;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getNewCoreEndPoint;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getOnlineLeasingHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getPropertyByKey;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getResultResponse;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getSalaryOrgInfoResponse;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_ID;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.BRANCH_NUMBER_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BRANCH_NUMBER_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_REGISTER_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_REGISTER_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.DAN_REGISTER_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.DAN_REGISTER_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.DISTRICT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.DISTRICT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getBigDecValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Lkhagvadorj.A
 **/

public class XacDirectOnlineCoreBankingService implements DirectOnlineCoreBankingService
{
  private final Environment environment;
  private final AuthenticationService authenticationService;
  private static final Logger LOGGER = LoggerFactory.getLogger(XacDirectOnlineCoreBankingService.class);

  private static String USER_ID = "CAMUNDA";
  private static final String BRANCH_NUMBER = "101";
  private static final String ERROR = "error";
  private static final String RESPONSE_IS_EMPTY = "Response is empty!";

  private static final List<String> variableNames = Arrays.asList("Date", "BranchID", "AccountID", "ProductID", "Currency", "Date1", "MiscFee", "CapitalAmount",
      "LifeAmount", "ISDmd", "ClearBalance", "PrincipalDue", "InterestDue", "InterestAmount", "OverdueAmount", "PenaltyAmount", "TotalAmountDue",
      "ClosingLoanAmount");

  public XacDirectOnlineCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = environment;
    this.authenticationService = authenticationService;
  }
  @Override
  public Map<String, Object> findOrganizationInfo(Map<String, String> input) throws BpmServiceException
  {
    if (StringUtils.isBlank(input.get(DAN_REGISTER)))
    {
      throw new BpmServiceException(DAN_REGISTER_NUMBER_NULL_CODE, DAN_REGISTER_NUMBER_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.get(DISTRICT)))
    {
      throw new BpmServiceException(DISTRICT_NULL_CODE, DISTRICT_NULL_MESSAGE);
    }
    String headerSource = getHeaderSource(environment);
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(environment, WSO2_HEADER_FIND_ORGANIZATION_INFO);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject request = new JSONObject();
    request.put(DAN_REGISTRATION_NUMBER, input.get(DAN_REGISTER));
    request.put(DISTRICT, input.get(DISTRICT));
    LOGGER.info("####### Started downloading ORGANIZATION INFO = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = getSalaryOrgInfoResponse(environment, xacClient, response);
      return orgInfo(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getAccountLoanInfo(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = environment.getProperty(NEW_CORE_HEADER_SOURCE);
    String function = getHeaderFunction(environment, WSO2_HEADER_GET_LOAN_INFO);
    String userId = USER_ID;
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      if (!StringUtils.isBlank(input.get(PHONE_NUMBER))){
        userId = input.get(PHONE_NUMBER);
      }
    }

    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerBranch(BRANCH_NUMBER)
        .build();

    Map<String, Object> request = new HashMap<>();
    request.put("acctid", input.get(BpmBranchBankingConstants.ACCOUNT_ID));
    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, request);

    LOGGER.info("####### Started downloading ACCOUNT LOAN INFO = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      return jsonObjectToMap(getResultResponse(environment, xacClient, response));
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Loan getDbsrList(Map<String, String> input) throws BpmServiceException
  {
    if (StringUtils.isBlank(input.get(ACCOUNT_NUMBER)))
    {
      throw new BpmServiceException("Given account number is null!");
    }

    String headerSource = getHeaderSource(environment);
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(environment, WSO_HEADER_GET_DBSR_LIST);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerUserId(userId)
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put("accno", input.get(ACCOUNT_NUMBER));
    request.put(REQUEST, requestBody);

    LOGGER.info("####### Started downloading DBSR_LIST = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONArray jsonResponse = XacHttpUtil.getResultResponseArray(environment, xacClient, response);
      return getLoanInformation(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> createLoanDisbursement(Map<String, Object> requestBody)
  {
    String source = getHeaderSource(environment, NEW_CORE_HEADER_SOURCE);
    String function = getHeaderFunction(environment, WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), source)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(USER_ID)
        .headerBranch(BRANCH_NUMBER)
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("###### LoanDisbursement (CreateLoanDisb) SERVICE WITH REQUEST BODY = \n [{}]", request);
    Map<String, Object> result = new HashMap<>();
    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      if (null == jsonResponse || jsonResponse.isEmpty())
      {
        result.put(STATUS, FAILURE);
        result.put(ERROR, RESPONSE_IS_EMPTY);
        return result;
      }
      result.put(STATUS, SUCCESS);
      return result;
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      result.put(STATUS, FAILURE);
      result.put(ERROR, e.getMessage());
      return result;
    }
  }

  @Override
  public Map<String, Object> createLoanDisbursementCharge(Map<String, Object> requestBody)
  {
    String source = getHeaderSource(environment, WSO2_HEADER_SOURCE);
    String userId = USER_ID;
    if(requestBody.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      source = getOnlineLeasingHeaderSource(this.environment);
      userId = String.valueOf(requestBody.get(PHONE_NUMBER));
    }
    String function = getHeaderFunction(environment, WSO2_HEADER_FUNCTION_CREATE_DISBURSEMENT_CHARGE);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), source)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("###### LoanDisbursement (CreateLoanDisbCharge) SERVICE WITH REQUEST BODY = \n [{}] with headerSource = {} and userId = {}", request, source, userId);
    Map<String, Object> result = new HashMap<>();
    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      if (null == jsonResponse || jsonResponse.isEmpty())
      {
        result.put(STATUS, FAILURE);
        result.put(ERROR, RESPONSE_IS_EMPTY);
        return result;
      }
      LOGGER.info("###### LoanDisbursement (CreateLoanDisbCharge) SERVICE RESPONSE = [{}]", jsonResponse);
      String collectedAmount = jsonResponse.has(COLLECTED_AMOUNT) ? getValidString(jsonResponse.get(COLLECTED_AMOUNT)) : "";
      String confmisc10 = jsonResponse.has(CONFMISC10) ?  getValidString(jsonResponse.get(CONFMISC10))  : "";
      result.put(STATUS, SUCCESS);
      result.put(COLLECTED_AMOUNT, collectedAmount);
      result.put(CONFMISC10, confmisc10);
      return result;
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      result.put(STATUS, FAILURE);
      result.put(ERROR, e.getMessage());
      return result;
    }
  }

  @Override
  public Map<String, Object> addTransaction(Map<String, Object> requestBody)
  {
    String headerSource = getHeaderSource(environment, WSO2_HEADER_SOURCE);
    if(requestBody.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      USER_ID = String.valueOf(requestBody.get(PHONE_NUMBER));
    }
    String headerFunction = getHeaderFunction(environment, WSO2_HEADER_FUNCTION_ADD_TRANSACTION);
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(headerFunction)
        .headerSecurityCode(generateSecurityCode(headerSource, headerFunction))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(USER_ID)
        .headerBranch(BRANCH_NUMBER)
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("######## AddTransaction: SERVICE WITH REQUEST BODY = \n [{}] with headerSource = {} and userId = {}" , request, headerSource, USER_ID);
    Map<String, Object> result = new HashMap<>();
    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacHttpClient, response);
      if (null == jsonResponse || jsonResponse.isEmpty() || jsonResponse.getString(RESPONSE_STATUS).equals(FAILURE))
      {
        result.put(STATUS, FAILURE);
        result.put(ERROR, RESPONSE_IS_EMPTY);
        return result;
      }
      result.put(STATUS, SUCCESS);
      return result;
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage());
      result.put(STATUS, FAILURE);
      result.put(ERROR, e.getMessage());
      return result;
    }
  }

  @Override
  public List<Loan> getCustomerLoanListFromMB(String registerNumber, String branchNumber) throws BpmServiceException
  {
    if (StringUtils.isBlank(registerNumber))
    {
      throw new BpmServiceException(CUSTOMER_REGISTER_NUMBER_NULL_CODE, CUSTOMER_REGISTER_NUMBER_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(branchNumber))
    {
      throw new BpmServiceException(BRANCH_NUMBER_IS_NULL_CODE, BRANCH_NUMBER_IS_NULL_MESSAGE);
    }

    String headerSource = getPropertyByKey(this.environment, XacConstants.WSO2_INTERNET_BANK_HEADER_SOURCE);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_FUNCTION_GET_LOAN_INFO_LIST);
    String securityCode = generateSecurityCode(headerSource, function);
    String userId = authenticationService.getCurrentUserId();

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(this.environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject request = new JSONObject();
    request.put(REGISTER_NUM, registerNumber);
    request.put(XacServiceConstants.BRANCH_NUMBER, branchNumber);
    request.put(CUST_NAME_LOWER_CASE, userId);
    request.put(LOAN_TYPE, "0");
    request.put(LANGUAGE_ID, "MN");
    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, request);

    LOGGER.info("######## Downloading loan info list from MONGOL BANK with REQUEST BODY [{}]", requestBody);
    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONArray jsonResponse = XacHttpUtil.getResultResponseArray(environment, xacHttpClient, response);
      return toLoanInfoList(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, String> setPayOffProc(JSONObject variables) throws BpmServiceException
  {
    if (variables == null)
    {
      throw new BpmServiceException(SERVICE_PARAMETERS_EMPTY_MESSAGE);
    }

    String source = getHeaderSource(environment, NEW_CORE_HEADER_SOURCE);
    String function = getHeaderFunction(environment, WSO2_HEADER_SET_PAY_OFF_PROCESS);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_INTERNET_BANK_HEADER_REQUEST_TYPE))
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONObject resultResponse = getResultResponse(environment, xacHttpClient, response);

      String trnId = resultResponse.getString(TRAN_ID);
      String trnDt = resultResponse.getString(BpmBranchBankingConstants.TRAN_DATE);

      Map<String, String> responseMap = new HashMap<>();
      responseMap.put(TRAN_ID, trnId);
      responseMap.put(BpmBranchBankingConstants.TRAN_DATE, trnDt);

      return responseMap;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getAccLien(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = getHeaderSource(environment, WSO2_INTERNET_BANK_HEADER_SOURCE);
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(environment, WSO2_HEADER_GET_ACC_LIEN);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_ACC_LIEN_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerLanguageType("EN")
        .build();

    JSONObject requestParam = new JSONObject();
    requestParam.put(ACCOUNT_ID, input.get(ACCOUNT_ID));
    requestParam.put(MODULE_TYPE, input.get(MODULE_TYPE));
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestParam);

    LOGGER.info("####### Getting AccLien info from XacService with account number = [{}] and request id = [{}]  with headerSource = {} and userId = {}",
        input.get(ACCOUNT_ID), request, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      Map<String, Object> result = new HashMap<>();
      JSONObject jsonResponse = getResultResponse(environment, xacClient, response);

      String responseStatus = getStringValue(jsonResponse, RESPONSE_STATUS);

      if (responseStatus.equals(FAILURE)){
        result.put(ERROR_CODE, getStringValue(jsonResponse, ERROR_CODE));
        result.put(ERROR_MESSAGE, getStringValue(jsonResponse, ERROR_MESSAGE));
        result.put(STATUS, FAILURE);
        return result;
      }

      List<Map<String, Object>> lienDtls = new ArrayList<>();
      if (jsonResponse.has("LienDtls"))
      {
        lienDtls = jsonToArrayListAccLien(jsonResponse.getJSONArray("LienDtls"));
      }
      result.put(STATUS, SUCCESS);
      result.put("acctId", getStringValue(jsonResponse, ACCT_ID));
      result.put("acctCurr", getStringValue(jsonResponse, ACCT_CURR_XAC));
      result.put("schmCode", getStringValue(jsonResponse, SCHM_CODE_XAC));
      result.put("schmType", getStringValue(jsonResponse, SCHM_TYPE_XAC));
      result.put("schmCodeDesc", getStringValue(jsonResponse, SCHM_CODE_DESC_XAC));
      result.put("branchId", getStringValue(jsonResponse, BRANCH_ID));
      result.put("branchName", getStringValue(jsonResponse, BRANCH_NAME));
      result.put("moduleType", getStringValue(jsonResponse, MODULE_TYPE));
      result.put("lienDtls", lienDtls);
      return result;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getRepaymentSchedule(Map<String, String> input) throws BpmServiceException
  {
    String headerSource = getHeaderSource(environment, HEADER_SOURCE_EGW);
    String userId = authenticationService.getCurrentUserId();
    if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = input.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(environment, WSO2_HEADER_GET_REPAYMENT_SCHEDULE);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_BNPL_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .build();

    JSONObject requestParam = new JSONObject();
    requestParam.put(XacServiceConstants.ACCOUNT_ID, input.get("acid"));
    requestParam.put(REPAYMENT_SCHEDULE_TYPE, input.get("project"));
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestParam);

    LOGGER.info("####### Started downloading REPAYMENT SCHEDULE = [{}] with headerSource = {} and userId = {}", request, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = getResultResponse(environment, xacClient, response);
      List<Map<String, Object>> schedule = jsonToArrayList(jsonResponse.getJSONArray("Schedule"));
      Map<String, Object> result = new HashMap<>();
      result.put("accountID", getStringValue(jsonResponse, "AccountID"));
      result.put("branchID", getStringValue(jsonResponse,"BranchID"));
      result.put("currency", getStringValue(jsonResponse,"Currency"));
      result.put("customerID", getStringValue(jsonResponse,"CustomerID"));
      result.put("customerName", getStringValue(jsonResponse,"CustomerName"));
      result.put("loanAmount", getStringValue(jsonResponse,"LoanAmount"));
      result.put("productID", getStringValue(jsonResponse,"ProductID"));
      result.put("schedule", schedule);
      return result;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }


  @Override
  public Map<String, Object> modifyAccLien(Map<String, Object> requestBody) throws BpmServiceException
  {
    String headerSource = getHeaderSource(environment, WSO2_INTERNET_BANK_HEADER_SOURCE);
    String userId = authenticationService.getCurrentUserId();
    if(requestBody.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = String.valueOf(requestBody.get(PHONE_NUMBER));
    }
    String function = getHeaderFunction(environment, WSO2_HEADER_MODIFY_ACC_LIEN);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, NEW_CORE_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerLanguageType("EN")
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info("####### Modify AccLien info from XacService with REQUEST BODY [{}] with headerSource = {} and userId = {}", requestBody, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = getResultResponse(environment, xacClient, response);
      Map<String, Object> result = new HashMap<>();
      String responseStatus = getStringValue(jsonResponse, RESPONSE_STATUS);

      if (responseStatus.equals(FAILURE)){
        result.put(ERROR_CODE, getStringValue(jsonResponse, ERROR_CODE));
        result.put(ERROR_MESSAGE, getStringValue(jsonResponse, ERROR_MESSAGE));
        result.put(STATUS, FAILURE);
        return result;
      }
      result.put("acctId", getStringValue(jsonResponse, ACCT_ID));
      result.put("schmCode", getStringValue(jsonResponse, SCHM_CODE_XAC));
      result.put("schmType", getStringValue(jsonResponse, SCHM_TYPE_XAC));
      result.put("schmCodeDesc", getStringValue(jsonResponse, SCHM_CODE_DESC_XAC));
      result.put("branchId", getStringValue(jsonResponse, BRANCH_ID));
      result.put("branchName", getStringValue(jsonResponse, BRANCH_NAME));
      result.put("lienId", getStringValue(jsonResponse, LIEN_ID));
      result.put(STATUS, SUCCESS);
      return result;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

@Override
public Map<String, Object> addAccLien(Map<String, Object> requestBody) throws BpmServiceException
    {
      String headerSource = getHeaderSource(environment, WSO2_INTERNET_BANK_HEADER_SOURCE);
      String userId = authenticationService.getCurrentUserId();
      if(requestBody.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
        headerSource = getOnlineLeasingHeaderSource(this.environment);
        userId = String.valueOf(requestBody.get(PHONE_NUMBER));
      }
      String function = getHeaderFunction(environment, WSO2_HEADER_ADD_ACC_LIEN);
      XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
      .headerFunction(function)
      .headerSecurityCode(generateSecurityCode(headerSource, function))
      .headerRequestId(generateRequestId())
      .headerRequestType(getPropertyByKey(environment, WSO2_BNPL_HEADER_REQUEST_TYPE))
      .headerContentType(APPLICATION_JSON)
      .headerUserId(userId)
      .headerLanguageType("EN")
      .build();

      JSONObject request = new JSONObject();
      request.put(REQUEST, requestBody);

      LOGGER.info("####### Add AccLien info from XacService with REQUEST BODY [{}] with headerSource = {} and userId = {}", requestBody, headerSource, userId);

      try (Response response = xacClient.post(request.toString()))
      {
      JSONObject jsonResponse = getResultResponse(environment, xacClient, response);
      Map<String, Object> result = new HashMap<>();
      String responseStatus = getStringValue(jsonResponse, RESPONSE_STATUS);

      if (responseStatus.equals(FAILURE)){
        result.put(ERROR_CODE, getStringValue(jsonResponse, ERROR_CODE));
        result.put(ERROR_MESSAGE, getStringValue(jsonResponse, ERROR_MESSAGE));
        result.put(STATUS, FAILURE);
        return result;
      }

      result.put("acctId", getStringValue(jsonResponse, ACCT_ID));
      result.put("schmCode", getStringValue(jsonResponse, SCHM_CODE_XAC));
      result.put("schmType", getStringValue(jsonResponse, SCHM_TYPE_XAC));
      result.put("schmCodeDesc", getStringValue(jsonResponse, SCHM_CODE_DESC_XAC));
      result.put("branchId", getStringValue(jsonResponse, BRANCH_ID));
      result.put("branchName", getStringValue(jsonResponse, BRANCH_NAME));
      result.put("lienId", getStringValue(jsonResponse, LIEN_ID));
      result.put(STATUS, SUCCESS);
      return result;
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  private Map<String, Object> orgInfo(JSONObject jsonResponse)
  {
    Map<String, Object> organizationInfo = new HashMap<>();
    if (null == jsonResponse)
    {
      organizationInfo.put(IS_SALARY_ORGANIZATION, false);
    }
    else
    {
      BigDecimal minInterestRate = jsonResponse.has(ERATE) ? new BigDecimal(String.valueOf(jsonResponse.get(ERATE))) : new BigDecimal(0);
      BigDecimal maxInterestRate = jsonResponse.has(ERATE_MAX) ? new BigDecimal(String.valueOf(jsonResponse.get(ERATE_MAX))) : new BigDecimal(0);
      if (jsonResponse.has("contractbranch"))
      {
        organizationInfo.put(BpmModuleConstants.BRANCH_NUMBER, jsonResponse.get("contractbranch"));
      }
      organizationInfo.put("erate", minInterestRate);
      organizationInfo.put("erate_max", maxInterestRate);
      organizationInfo.put(IS_SALARY_ORGANIZATION, true);
    }
    return organizationInfo;
  }

  private List<Loan> toLoanInfoList(JSONArray jsonResponse)
  {
    List<Loan> loanInfoList = new ArrayList<>();
    for (int i = 0; i < jsonResponse.length(); i++)
    {
      JSONObject loan = jsonResponse.getJSONObject(i);
      String responseStatusCode = getStringValue(loan, STATUS_CODE);
      if (!responseStatusCode.equals("noLoan"))
      {
        String loanId = getStringValue(loan, LOAN_CODE);
        String loanClassName = getStringValue(loan, LOAN_CLASS_CODE);
        String loanType = getStringValue(loan, LOAN_TYPE_CODE);
        String startDate = getStringValue(loan, START_DATE);
        String expireDate = getStringValue(loan, EXP_DATE);
        String currencyCode = getStringValue(loan, CURRENCY_CODE);
        BigDecimal amount = getBigDecValue(loan, LOAN_AMOUNT);
        BigDecimal balance = getBigDecValue(loan, BALANCE);
        Loan loanInfo = new Loan(LoanId.valueOf(loanId), new LoanClass(0, loanClassName), loanType, responseStatusCode, startDate, expireDate, currencyCode,
            amount, balance);
        loanInfoList.add(loanInfo);
      }
    }
    return loanInfoList;
  }

  private Loan getLoanInformation(JSONArray responseArray)
  {
    if (responseArray.length() > 0 && responseArray.getJSONObject(0).get("DisbursedAmount") != null)
    {
      JSONObject loanInformation = (JSONObject) responseArray.get(0);

      String disbursedAmount = getStringValue(loanInformation, "DisbursedAmount");
      String disbursedDate = getStringValue(loanInformation, "DisbursedDate");
      String maturityDate = getStringValue(loanInformation, "MaturityDate");
      LoanId loanId = LoanId.valueOf(getStringValue(loanInformation, "AccNO"));
      double amount = Double.parseDouble(disbursedAmount);

      Loan loan = new Loan(loanId, null);
      loan.setAmount(BigDecimal.valueOf(amount));
      loan.setStartDate(disbursedDate);
      loan.setExpireDate(maturityDate);
      return loan;
    } else return new Loan(null, null);
  }

  private Map<String, Object> jsonObjectToMap(JSONObject jsonResponse)
  {
    Map<String, Object> loanInfo = new HashMap<>();

    for (String variable : variableNames)
    {
      if (jsonResponse.has(variable))
      {
        loanInfo.put(variable, !jsonResponse.isNull(variable) ? jsonResponse.get(variable) : "");
      }
    }
    return loanInfo;
  }
  private List<Map<String, Object>> jsonToArrayList(JSONArray jsonResponse)
  {
    List<Map<String, Object>> scheduleInfoList = new ArrayList<>();
    for (int i = 0; i < jsonResponse.length(); i++)
    {
      JSONObject array = jsonResponse.getJSONObject(i);
        String capitalAmount = getStringValue(array, "CapitalAmount");
        String dueDate = getStringValue(array, "DueDate");
        BigDecimal installmentAmount = getBigDecValue(array, "InstallmentAmount");
        BigDecimal installmentID = getBigDecValue(array, "InstallmentID");
        BigDecimal interestAmount = getBigDecValue(array, "InterestAmount");
        String lifeAmount = getStringValue(array, "LifeAmount");
        BigDecimal osPrincipal = getBigDecValue(array, "OSPrincipal");
        BigDecimal paidInterest = getBigDecValue(array, "PaidInterest");
        BigDecimal paidPrinciple = getBigDecValue(array, "PaidPrinciple");
        BigDecimal principalAmount = getBigDecValue(array, "PrincipalAmount");

        Map<String, Object> schedule = new HashMap<>();
        schedule.put("capitalAmount", capitalAmount);
        schedule.put("dueDate", dueDate);
        schedule.put("installmentAmount", installmentAmount);
        schedule.put("installmentID", installmentID);
        schedule.put("interestAmount", interestAmount);
        schedule.put("lifeAmount",  lifeAmount);
        schedule.put("oSPrincipal", osPrincipal);
        schedule.put("paidInterest", paidInterest);
        schedule.put("paidPrinciple", paidPrinciple);
        schedule.put("principalAmount", principalAmount);
      scheduleInfoList.add(schedule);
    }
    return scheduleInfoList;
  }
  private List<Map<String, Object>> jsonToArrayListAccLien(JSONArray jsonResponse)
  {
    List<Map<String, Object>> lienDtlsInfoList = new ArrayList<>();
    for (int i = 0; i < jsonResponse.length(); i++)
    {
      JSONObject array = jsonResponse.getJSONObject(i);
      String RequestDept = getStringValue(array, "RequestDept");
      String SrlNo = getStringValue(array, "SrlNo");
      String PhoneNum = getStringValue(array, "PhoneNum");
      String AgentId = getStringValue(array, "AgentId");

      JSONObject NewLienAmt = (JSONObject) array.get("NewLienAmt");
      BigDecimal amountValue = NewLienAmt.getBigDecimal("amountValue");
      String currencyCode = getStringValue(NewLienAmt, "currencyCode");

      JSONObject OldLienAmt = (JSONObject) array.get("OldLienAmt");
      BigDecimal amountValue2 = OldLienAmt.getBigDecimal("amountValue");
      String currencyCode2 = getStringValue(OldLienAmt, "currencyCode");

      JSONObject LienDt = (JSONObject) array.get("LienDt");
      String StartDt = getStringValue(LienDt, "StartDt");
      String EndDt = "";
      if (LienDt.has("EndDt")){
        EndDt = LienDt.getString("EndDt");
      }

      String ReasonCode = getStringValue(array, "ReasonCode");
      String Rmks = getStringValue(array, "Rmks");
      String IsDeleted = getStringValue(array, "IsDeleted");
      String LienId = getStringValue(array, "LienId");
      String ExpiryTime = getStringValue(array, "ExpiryTime");
      String LienTimeZone = getStringValue(array, "LienTimeZone");


      Map<String, Object> lienDtls = new HashMap<>();
      lienDtls.put("requestDept", RequestDept);
      lienDtls.put("srlNo", SrlNo);
      lienDtls.put("phoneNum", PhoneNum);
      lienDtls.put("agentId", AgentId);

      Map<String, Object> newLienAmtValue = new HashMap<>();
      newLienAmtValue.put("amountValue", amountValue);
      newLienAmtValue.put("currencyCode", currencyCode);
      lienDtls.put("newLienAmt", newLienAmtValue);

      Map<String, Object> oldLienAmtValue = new HashMap<>();
      oldLienAmtValue.put("amountValue", amountValue2);
      oldLienAmtValue.put("currencyCode", currencyCode2);
      lienDtls.put("oldLienAmt", oldLienAmtValue);

      Map<String, Object> lienDtValue = new HashMap<>();
      lienDtValue.put("startDt", StartDt);
      if (!EndDt.isEmpty()){
        lienDtValue.put("endDt", EndDt);
      }
      lienDtls.put("lienDt", lienDtValue);

      lienDtls.put("reasonCode", ReasonCode);
      lienDtls.put("rmks", Rmks);
      lienDtls.put("isDeleted", IsDeleted);
      lienDtls.put("lienId", LienId);
      lienDtls.put("expiryTime", ExpiryTime);
      lienDtls.put("lienTimeZone", LienTimeZone);

      lienDtlsInfoList.add(lienDtls);
    }
    return lienDtlsInfoList;
  }
}
