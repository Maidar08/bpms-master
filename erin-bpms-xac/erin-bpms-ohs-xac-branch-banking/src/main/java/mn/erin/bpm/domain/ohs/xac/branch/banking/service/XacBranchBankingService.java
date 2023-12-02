package mn.erin.bpm.domain.ohs.xac.branch.banking.service;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacHttpClient;
import mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants;
import mn.erin.bpm.domain.ohs.xac.branch.banking.util.BranchBankingUtil;
import mn.erin.bpm.domain.ohs.xac.branch.banking.util.CustomInvoiceUtil;
import mn.erin.bpm.domain.ohs.xac.branch.banking.util.TaxUtil;
import mn.erin.bpm.domain.ohs.xac.branch.banking.util.TransactionUtil;
import mn.erin.bpm.domain.ohs.xac.branch.banking.util.XacHttpUtils;
import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.provider.ExtSessionInfo;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_ADD_CASH_CREDIT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_ADD_LOAN_REPAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_ADD_TRANSACTION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_ENDPOINT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_FISSO_LOGIN;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_GET_ACCOUNT_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_GET_ACCOUNT_REFERENCE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_GET_C_ACCOUNT_NAMES;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_GET_TRANSACTION_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_HEADER_GET_ASSET_INVOICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_HEADER_GET_CUSTOM_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_HEADER_GET_INVOICE_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_HEADER_GET_TIN_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_LOAN_POS_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_LOAN_SCHEDULED_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_SET_CUSTOM_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_SET_PAY_OFF_PROC;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_SET_PAY_OFF_PROC_URL;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_SET_TAX_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_TRANSACTION_E_BANK_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_URL_FUNCTION_GET_CUSTOM_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_URL_FUNCTION_GET_TAX_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_URL_FUNCTION_GET_TRANSACTION_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_URL_FUNCTION_SET_CUSTOM_INFO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USER_OTP_SEND;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USER_STATUS_CHANGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USSD_SEARCH_USER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USSD_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USSD_USER_CANCEL;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants.BB_USSD_USER_ENDORSE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACC_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BRANCH;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CHANNEL;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CIF;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CONTENT_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOMER_DETAIL;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOM_BRANCH;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DESCRIPTION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DESTINATION;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.IS_UPDATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.LANGUAGE_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.MOBILE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PHONE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.USER_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_ID_LIST_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_ID_LIST_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_ID_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_ID_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.BOTH_OF_PHONE_AND_CIF_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.BOTH_OF_PHONE_AND_CIF_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CHANNEL_ID_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.CHANNEL_ID_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.DESTINATION_IS_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.DESTINATION_IS_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.END_DT_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.END_DT_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.EXT_SESSION_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.EXT_SESSION_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.MOBILE_NUMBER_IS_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.MOBILE_NUMBER_IS_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.PASSWORD_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.PASSWORD_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.REGISTRATION_NUMBER_IS_NULL_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.REGISTRATION_NUMBER_IS_NULL_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_BODY_IS_NULL_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_BODY_IS_NULL_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.SEARCH_TYPE_IS_NULL_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.SEARCH_TYPE_IS_NULL_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.START_DT_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.START_DT_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.TRANSACTION_PARAMETERS_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.TRANSACTION_PARAMETERS_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.TRANSACTION_SESSION_ID_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.TRANSACTION_SESSION_ID_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_NAME_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_NAME_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_STATUS_IS_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_STATUS_IS_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.XAC_SERVICE_PARAMETER_NULL_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.XAC_SERVICE_PARAMETER_NULL_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacHttpConstants.REQUEST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.BranchBankingUtil.ussdUserInfoToXacSchema;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.TaxUtil.toTaxTransactionInfo;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.XacHttpUtils.getArrayResponse;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.XacHttpUtils.getResponse;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.util.XacHttpUtils.getTaxResponse;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateRequestId;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_ID;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
public class XacBranchBankingService implements BranchBankingService
{
  private final AuthenticationService authenticationService;
  private final Environment environment;
  private final ExtSessionInfoCache extSessionInfoCache;

  private static final Logger LOGGER = LoggerFactory.getLogger(XacBranchBankingService.class);
  private static final String HASH_CONSTANT = "###############  ";

  public XacBranchBankingService(AuthenticationService authenticationService, Environment environment, ExtSessionInfoCache extSessionInfoCache)
  {
    this.authenticationService = authenticationService;
    this.environment = environment;
    this.extSessionInfoCache = Objects.requireNonNull(extSessionInfoCache, "ExtSessionInfoCache is required!");
  }

  @Override
  public Collection<CustomerTransaction> findByUserIdAndDate(String userId, String transactionDate, String instanceId) throws BpmServiceException
  {
    String headerSource = getHeaderSource();
    String function = getHeaderFunction(BB_GET_TRANSACTION_LIST);
    String urlFunction = getUrlFunction(BB_URL_FUNCTION_GET_TRANSACTION_LIST);
    XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerMessageId(generateRequestId())
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject transactionBody = new JSONObject();

    transactionBody.put(USER_ID, userId);
    transactionBody.put(TRAN_DATE, transactionDate);

    requestBody.put(REQUEST, transactionBody);

    LOGGER.info(HASH_CONSTANT + "GET CUSTOMER TRANSACTION SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", requestBody, instanceId);
    try (Response response = xacClient.post(requestBody.toString(), urlFunction))
    {
      try
      {
        JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);

        return TransactionUtil.toCustomerTransactions(jsonResponse, instanceId);
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
  public Map<String, String> getTinList(String registrationNumber, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(registrationNumber))
    {
      throw new BpmServiceException(REGISTRATION_NUMBER_IS_NULL_ERROR_CODE, REGISTRATION_NUMBER_IS_NULL_ERROR_MESSAGE);
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(BB_HEADER_GET_TIN_LIST);
    String urlFunction = getUrlFunction(BB_HEADER_GET_TIN_LIST);
    String securityCode = generateSecurityCode(source, function);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(securityCode)
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("registerNo", registrationNumber);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET TINS LIST SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), urlFunction))
    {
      try
      {
        JSONArray jsonResponse = XacHttpUtils.getResponse(environment, xacHttpClient, response, instanceId);
        LOGGER.info(HASH_CONSTANT + "XAC SERVICE RESPONSE = [{}] WITH PROCESS INSTANCE ID = [{}]", jsonResponse, instanceId);
        return TaxUtil.toTinList(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public List<TaxInvoice> getTaxInfoList(String type, String registrationNumber, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(registrationNumber))
    {
      throw new BpmServiceException(REGISTRATION_NUMBER_IS_NULL_ERROR_CODE, REGISTRATION_NUMBER_IS_NULL_ERROR_MESSAGE);
    }
    else if (StringUtils.isEmpty(type))
    {
      throw new BpmServiceException(SEARCH_TYPE_IS_NULL_ERROR_CODE, SEARCH_TYPE_IS_NULL_ERROR_MESSAGE);
    }

    String urlFunction = getUrlFunction(BB_URL_FUNCTION_GET_TAX_INFO);
    String source = getHeaderSource();
    String function = null;

    switch (type)
    {
    case "tin":
      function = getHeaderFunction(BB_HEADER_GET_TIN_LIST);
      break;
    case "invoiceNo":
      function = getHeaderFunction(BB_HEADER_GET_INVOICE_LIST);
      break;
    case "assetNo":
      function = getHeaderFunction(BB_HEADER_GET_ASSET_INVOICE);
      break;
    default:
      break;
    }

    String securityCode = generateSecurityCode(source, function);
    String userId = authenticationService.getCurrentUserId();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(userId)
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(securityCode)
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(type, registrationNumber);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET TAX INFO LIST SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), urlFunction))
    {
      try
      {
        JSONObject jsonResponse = getTaxResponse(environment, xacHttpClient, response, requestBody);
        return TaxUtil.toTaxInfoList(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public List<CustomInvoice> getCustomInfoList(String type, String searchValue, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isEmpty(searchValue))
    {
      throw new BpmServiceException(XAC_SERVICE_PARAMETER_NULL_CODE, XAC_SERVICE_PARAMETER_NULL_MESSAGE);
    }
    else if (StringUtils.isEmpty(type))
    {
      throw new BpmServiceException(SEARCH_TYPE_IS_NULL_ERROR_CODE, SEARCH_TYPE_IS_NULL_ERROR_MESSAGE);
    }
    String function = getHeaderFunction(BB_HEADER_GET_CUSTOM_INFO);

    String urlFunction = getUrlFunction(BB_URL_FUNCTION_GET_CUSTOM_INFO);
    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();
    requestBody.put(type, searchValue);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET CUSTOM INFO LIST SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), urlFunction))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return CustomInvoiceUtil.toCustomList(jsonResponse, instanceId);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public String getTransactionSessionId(String userName, String password) throws BpmServiceException
  {
    if (StringUtils.isEmpty(userName))
    {
      throw new BpmServiceException(USER_NAME_NULL_CODE, USER_NAME_NULL_MESSAGE);
    }

    if (StringUtils.isEmpty(password))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(BB_FISSO_LOGIN);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(userName)
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(source, function))
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("userName", userName);
    requestBody.put("pswd", password);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      try
      {
        JSONObject resultResponse = getResponse(environment, xacHttpClient, response);

        return String.valueOf(resultResponse.get("sessionId"));
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, String> makeTaxTransaction(Map<String, String> transactionValues, String instanceId) throws BpmServiceException
  {
    if (null == transactionValues || transactionValues.isEmpty())
    {
      throw new BpmServiceException(XAC_SERVICE_PARAMETER_NULL_CODE, XAC_SERVICE_PARAMETER_NULL_MESSAGE);
    }

    setTransactionSession();
    String source = getHeaderSource();
    String function = getHeaderFunction(BB_SET_TAX_PAYMENT);
    String urlFunction = getUrlFunction(BB_SET_TAX_PAYMENT);
    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (null == extSessionId || StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }

    if (null == encryptedPassword || StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, transactionValues);

    LOGGER.info(HASH_CONSTANT + "MAKE TAX TRANSACTION SERVICE \n BODY = [{}] \n WITH HEADER PARAMETER [{}], \n WITH PROCESS INSTANCE ID = [{}]", request,
        xacHttpClient.headerParam(),
        instanceId);

    try (Response response = xacHttpClient.post(request.toString(), urlFunction))
    {
      try
      {
        JSONObject resultResponse = getResponse(environment, xacHttpClient, response);

        return toTaxTransactionInfo(resultResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, String> makeCustomTransaction(Map<String, Object> transactionValues, String instanceId)
      throws BpmServiceException
  {
    if (null == transactionValues)
    {
      throw new BpmServiceException(XAC_SERVICE_PARAMETER_NULL_CODE, XAC_SERVICE_PARAMETER_NULL_MESSAGE);
    }

    setTransactionSession();

    String source = getHeaderSource();
    String function = getHeaderFunction(BB_SET_CUSTOM_PAYMENT);
    String url = getUrlFunction(BB_URL_FUNCTION_SET_CUSTOM_INFO);
    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();

    JSONObject request = new JSONObject();
    request.put(REQUEST, transactionValues);
    LOGGER.info(HASH_CONSTANT + "MAKE CUSTOM TRANSACTION SERVICE \n BODY = [{}] \n WITH HEADER PARAMETER [{}], \n WITH PROCESS INSTANCE ID = [{}]", request,
        xacHttpClient.headerParam(),
        instanceId);
    try (Response response = xacHttpClient.post(request.toString(), url))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        LOGGER.info("######## SET CUSTOMER INFO RESPONSE: \n {}", jsonResponse);
        return CustomInvoiceUtil.toCustomTransactionResponse(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> getAccountInfo(String instanceId, String accountId, boolean hasAccountValidation) throws BpmServiceException
  {
    if (StringUtils.isBlank(accountId))
    {
      throw new BpmServiceException(ACCOUNT_ID_NULL_CODE, ACCOUNT_ID_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_GET_ACCOUNT_INFO);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();

    requestBody.put(ACCOUNT_ID, accountId);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET ACCOUNT INFO SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return BranchBankingUtil.toAccountInfo(jsonResponse, hasAccountValidation);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
      catch (ParseException e)
      {
        throw new BpmServiceException(e.getMessage());
      }
    }
  }

  @Override
  public Map<String, AccountInfo> getAccountNames(List<String> accountIdList, String instanceId) throws BpmServiceException
  {
    if (null == accountIdList)
    {
      throw new BpmServiceException(ACCOUNT_ID_LIST_NULL_CODE, ACCOUNT_ID_LIST_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_GET_C_ACCOUNT_NAMES);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONArray accountList = new JSONArray();
    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();
    for (String accountId : accountIdList)
    {
      JSONObject account = new JSONObject();
      account.put("account", accountId);
      accountList.put(account);
    }
    requestBody.put("accList", accountList);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET CUSTOMER ACCOUNT NAME SERVICE \n BODY[{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return BranchBankingUtil.tocAccountInfoList(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> getAccountReference(String accountId, String branchId, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(accountId))
    {
      throw new BpmServiceException(ACCOUNT_ID_NULL_CODE, ACCOUNT_ID_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_GET_ACCOUNT_REFERENCE);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();

    requestBody.put(ACCOUNT_NO, accountId);
    requestBody.put(BRANCH, branchId);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET ACCOUNT REFERENCE SERVICE \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return BranchBankingUtil.toAccountReference(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public List<ETransaction> getTransactionEBankList(String accountId, String channelId, String channel, String startDt, String endDt, String instanceId)
      throws BpmServiceException
  {
    validateEBankInput(accountId, channelId, startDt, endDt);

    String function = getHeaderFunction(BB_TRANSACTION_E_BANK_LIST);
    String url = getUrlFunction(BB_TRANSACTION_E_BANK_LIST);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerMessageId(generateRequestId())
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();
    requestBody.put(ACC_ID, accountId);
    requestBody.put("ChannelId", channelId);
    requestBody.put("StartDt", startDt);
    requestBody.put("EndDt", endDt);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET E-BANK TRANSACTION LIST \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
    try (Response response = xacHttpClient.post(request.toString(), url))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return TransactionUtil.toETransactionList(jsonResponse, channel, instanceId);
      }
      catch (XacHttpException e)
      {
        LOGGER.error(HASH_CONSTANT + "ERROR OCCURRED DURING DOWNLOAD E-BANK TRANSACTION LIST WITH HEADER PARAM [{}], PROCESS INSTANCE ID = [{}]",
            xacHttpClient.headerParam(), instanceId);
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, String> makeAccountFeeTransactionTask(List<Map<String, Object>> transactionsParameters, String amount, String currency,
      String transactionSubType, String instanceId) throws BpmServiceException
  {

    if (null == transactionsParameters)
    {
      throw new BpmServiceException(TRANSACTION_PARAMETERS_NULL_CODE, TRANSACTION_PARAMETERS_NULL_MESSAGE);
    }
    setTransactionSession();

    String source = getHeaderSource();
    String function = getHeaderFunction(BB_ADD_CASH_CREDIT);
    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();

    requestBody.put("Amount", amount);
    requestBody.put("Currency", currency);
    requestBody.put("TrnSubType", transactionSubType);
    requestBody.put("Transactions", transactionsParameters);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "ADD CASH CREDIT SERVICE \n BODY = [{}] \n  WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return TransactionUtil.toAddTransaction(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, String> makeNoCashAccountFeeTransactionTask(List<Map<String, Object>> transactionParameters, String transactionType,
      String transactionSubType, String userTransactionCode, String remarks, String transactionParticulars, String valueDate, String dtlsOnResponse,
      String transactionRefNumber, String instanceId) throws BpmServiceException
  {
    if (null == transactionParameters)
    {
      throw new BpmServiceException(TRANSACTION_PARAMETERS_NULL_CODE, TRANSACTION_PARAMETERS_NULL_MESSAGE);
    }

    String source = getHeaderSource();
    String function = getHeaderFunction(BB_ADD_TRANSACTION);

    setTransactionSession();
    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), source)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(source, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();
    JSONObject requestBody = new JSONObject();

    requestBody.put("TrnType", transactionType);
    requestBody.put("TrnSubType", transactionSubType);
    requestBody.put("UserTrnCode", userTransactionCode);
    requestBody.put("Remarks", remarks);
    requestBody.put("TrnParticulars", transactionParticulars);
    requestBody.put("ValueDt", valueDate);
    requestBody.put("DtlsOnResponse", dtlsOnResponse);
    requestBody.put("TrRefNum", transactionRefNumber);
    requestBody.put("Transactions", transactionParameters);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "ADD TRANSACTION SERVICE \n BODY = [{}] \n  WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
    try (Response response = xacHttpClient.post(request.toString(), function))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return TransactionUtil.toAddTransaction(jsonResponse);
      }
      catch (XacHttpException e)
      {
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> getUserInfoFromUSSD(String cif, String phone, String branch, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(cif) && StringUtils.isBlank(phone))
    {
      throw new BpmServiceException(BOTH_OF_PHONE_AND_CIF_NULL_CODE, BOTH_OF_PHONE_AND_CIF_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_USSD_SEARCH_USER);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put(CUSTOM_BRANCH, branch);
    requestBody.put(CIF, cif);
    requestBody.put(PHONE, phone);

    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET USER FROM USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONArray jsonResponse = getArrayResponse(environment, xacHttpClient, response);
      if (jsonResponse.isEmpty())
      {
        LOGGER.info(HASH_CONSTANT + "RESPONSE BODY IS NULL WHEN TO GET USER FROM USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request,
            instanceId);
        throw new BpmServiceException(RESPONSE_BODY_IS_NULL_ERROR_CODE, RESPONSE_BODY_IS_NULL_ERROR_MESSAGE);
      }
      return BranchBankingUtil.toUSSDInfo(jsonResponse.getJSONObject(0));
    }
    catch (XacHttpException e)
    {
      LOGGER.info(HASH_CONSTANT + "ERROR OCCURRED TO GET USER FROM USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> userStatusChange(String mobile, String status, String instanceId) throws BpmServiceException
  {
    LOGGER.info("##### USSSD USER STATUS CHANGE BRANCH BANKING SERVICE, INSTANCE ID = [{}]", instanceId);
    if (StringUtils.isBlank(mobile))
    {
      throw new BpmServiceException(MOBILE_NUMBER_IS_NULL_CODE, MOBILE_NUMBER_IS_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(status))
    {
      throw new BpmServiceException(USER_STATUS_IS_NULL_CODE, USER_STATUS_IS_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_USER_STATUS_CHANGE);
    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put(MOBILE, mobile);
    requestBody.put(STATUS, status);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "USER STATUS CHANGE \n BODY = [{}] WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
    try (Response response = xacHttpClient.post(request.toString()))
    {
      try
      {
        Map<String, Object> responseFromService = getResponse(environment, xacHttpClient, response).toMap();
        Map<String, Object> responseToSend = new HashMap<>();
        responseToSend.put("message", getValidString(responseFromService.get("Desc")));
        responseToSend.put("code", getValidString(responseFromService.get("Code")));
        LOGGER.info("##### USSSD USER STATUS CHANGE BRANCH BANKING SERVICE, RESPONSE [{}] \nINSTANCE ID = [{}]", responseToSend, instanceId);
        return responseToSend;
      }
      catch (XacHttpException e)
      {
        LOGGER.error(HASH_CONSTANT + "ERROR OCCURRED DURING USER STATUS CHANGE WITH HEADER PARAM [{}], PROCESS INSTANCE ID = [{}]",
            xacHttpClient.headerParam(), instanceId);
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> updateUserUSSD(Map<String, Object> updatedUser, String languageId, String instanceId) throws BpmServiceException
  {

    String function = getHeaderFunction(BB_USSD_SERVICE);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put(IS_UPDATE, getValidString(updatedUser.get("checkRegistration")));
    requestBody.put(LANGUAGE_ID, languageId);

    Map<String, Object> dest = ussdUserInfoToXacSchema(updatedUser);
    String xml = DocumentServiceUtil.simpleDataStructuresToXML(dest, "Customer");

    requestBody.put(CUSTOMER_DETAIL, xml);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "UPDATE USSD USER INFORMATION \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
    try (Response response = xacHttpClient.post(request.toString()))
    {
      Map<String, Object> responseFromService = getResponse(environment, xacHttpClient, response).toMap();
      Map<String, Object> responseToSend = new HashMap<>();

      String desc = getValidString(responseFromService.get("Desc"));
      String code = getValidString(responseFromService.get("Code"));

      responseToSend.put("isNotification", responseFromService.get("isNotif").equals("Y"));
      responseToSend.put("message", desc);
      responseToSend.put("code", code);

      if (!code.equals("0"))
      {
        throw new BpmServiceException(code, desc);
      }
      return responseToSend;
    }
    catch (XacHttpException e)
    {
      LOGGER.error(HASH_CONSTANT + "ERROR OCCURRED DURING UPDATE USSD USER INFORMATION WITH HEADER PARAM [{}], PROCESS INSTANCE ID = [{}]",
          xacHttpClient.headerParam(), instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public boolean userOtpSend(String channel, String destination, String contentMessage, String instanceId) throws BpmServiceException
  {
    LOGGER.info("##### USSSD USER OTP SEND BRANCH BANKING SERVICE, OTP = [{}] \nINSTANCE ID = [{}]", contentMessage, instanceId);
    if (StringUtils.isBlank(destination))
    {
      throw new BpmServiceException(DESTINATION_IS_NULL_CODE, DESTINATION_IS_NULL_MESSAGE);
    }
    String function = getHeaderFunction(BB_USER_OTP_SEND);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();
    requestBody.put(CHANNEL, channel);
    requestBody.put(DESTINATION, destination);
    requestBody.put(CONTENT_MESSAGE, contentMessage);
    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "SEND OTP TO SERVICE \n BODY = [{}]  \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
    try (Response response = xacHttpClient.post(request.toString()))
    {
      try
      {
        JSONArray jsonArrayResult = getResponse(environment, xacHttpClient, response, instanceId);
        JSONObject resultObject = jsonArrayResult.getJSONObject(0);
        LOGGER.info("##### USSSD USER OTP SEND BRANCH BANKING SERVICE, RESPONSE = [{}] \nINSTANCE ID = [{}]", resultObject, instanceId);
        return resultObject.getString("Desc").equals("SUCCESS");
      }
      catch (XacHttpException e)
      {
        LOGGER.info(HASH_CONSTANT + "ERROR OCCURED WHEN SEND OTP TO SERIVCE \n BODY = [{}] \n WITH PROCESS INSTANCE ID =[{}]", request, instanceId);
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> getLoanAccountInfo(String accountNumber, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(accountNumber))
    {
      throw new BpmServiceException(ACCOUNT_ID_NULL_CODE, ACCOUNT_ID_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_LOAN_POS_INFO);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();
    requestBody.put("acctid", accountNumber);

    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "GET LOAN POS INFO \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      try
      {
        JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
        return BranchBankingUtil.toLoanAccountInfo(jsonResponse);
      }
      catch (XacHttpException e)
      {
        LOGGER.info(HASH_CONSTANT + "ERROR OCCURRED TO GET LOAN POS INFO \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
        throw new BpmServiceException(e.getCode(), e.getMessage());
      }
    }
  }

  @Override
  public Map<String, Object> addUnschLoanRepayment(String instanceId, JSONObject variables) throws BpmServiceException
  {
    setTransactionSession();

    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_ADD_LOAN_REPAYMENT);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    LOGGER.info(HASH_CONSTANT + "ADD LOAN REPAYMENT \n BODY = [{}]  \n WITH PROCESS INSTANCE ID = [{}]", requestBody, instanceId);
    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONObject resultResponse = getResponse(environment, xacHttpClient, response);
      return BranchBankingUtil.toLoanPayment(resultResponse);
    }
    catch (XacHttpException e)
    {
      LOGGER.info(HASH_CONSTANT + "ERROR OCCURED WHEN ADD LOAN REPAYMENT \n BODY = [{}] \n WITH PROCESS INSTANCE ID =[{}]", requestBody, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> makeScheduledLoanPayment(String instanceId, JSONObject variables) throws BpmServiceException
  {
    setTransactionSession();

    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(BB_LOAN_SCHEDULED_PAYMENT);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    LOGGER.info(HASH_CONSTANT + "MAKE LOAN SCHEDULED PAYMENT \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", requestBody, instanceId);

    try (Response response = xacHttpClient.post(requestBody.toString()))
    {
      JSONObject resultResponse = getResponse(environment, xacHttpClient, response);
      return BranchBankingUtil.toScheduledLoanPayment(resultResponse);
    }
    catch (XacHttpException e)
    {
      LOGGER.info(HASH_CONSTANT + "ERROR OCCURRED TO MAKE LOAN SCHEDULED PAYMENT \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", requestBody, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, String> setPayOffProc(JSONObject variables) throws BpmServiceException
  {
    if (variables == null)
    {
      throw new BpmServiceException(TRANSACTION_PARAMETERS_NULL_CODE, TRANSACTION_PARAMETERS_NULL_MESSAGE);
    }

    setTransactionSession();

    ExtSessionInfo sessionInfo = extSessionInfoCache.getSessionInfo();
    String encryptedPassword = sessionInfo.getEncryptedPassword();
    String extSessionId = sessionInfo.getExtSessionId();

    if (StringUtils.isBlank(extSessionId))
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(encryptedPassword))
    {
      throw new BpmServiceException(PASSWORD_NULL_CODE, PASSWORD_NULL_MESSAGE);
    }

    String headerSource = getHeaderSource();
    String function = getHeaderFunction(BB_SET_PAY_OFF_PROC);
    String urlFunction = getUrlFunction(BB_SET_PAY_OFF_PROC_URL);

    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerContentType(APPLICATION_JSON)
        .headerFunction(function)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerPassword(encryptedPassword)
        .headerSessionId(extSessionId)
        .headerLanguageType("EN")
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(REQUEST, variables);

    try (Response response = xacHttpClient.post(requestBody.toString(), urlFunction))
    {
      JSONObject resultResponse = getResponse(environment, xacHttpClient, response);

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
  public boolean makeUserEndorse(String id, String branch, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(id))
    {
      throw new BpmServiceException(BB_ID_IS_NULL_CODE, BB_ID_IS_NULL_MESSAGE);
    }

    String function = getHeaderFunction(BB_USSD_USER_ENDORSE);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put("userBranch", branch);
    requestBody.put("ID", id);

    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "MAKE USER ENDORSE ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
      if (jsonResponse.isEmpty())
      {
        LOGGER.info(HASH_CONSTANT + "RESPONSE BODY IS NULL WHEN TO MAKE USER ENDORSE ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request,
            instanceId);
        throw new BpmServiceException(RESPONSE_BODY_IS_NULL_ERROR_CODE, RESPONSE_BODY_IS_NULL_ERROR_MESSAGE);
      }
      return jsonResponse.getString(DESCRIPTION).equals("SUCCESS");
    }
    catch (XacHttpException e)
    {
      LOGGER.info(HASH_CONSTANT + "ERROR OCCURRED TO MAKE USER ENDORSE ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public boolean makeUserCancel(String id, String branch, String instanceId) throws BpmServiceException
  {
    if (StringUtils.isBlank(id))
    {
      throw new BpmServiceException(BB_ID_IS_NULL_CODE, BB_ID_IS_NULL_MESSAGE);
    }
    String function = getHeaderFunction(BB_USSD_USER_CANCEL);

    String headerSource = getHeaderSource();
    XacHttpClient xacHttpClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
        .headerFunction(function)
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerRequestId(generateRequestId())
        .headerRequestType(getHeaderRequestType())
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .build();

    JSONObject request = new JSONObject();
    JSONObject requestBody = new JSONObject();

    requestBody.put("userBranch", branch);
    requestBody.put("ID", id);

    request.put(REQUEST, requestBody);

    LOGGER.info(HASH_CONSTANT + "MAKE USER CANCEL ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);

    try (Response response = xacHttpClient.post(request.toString()))
    {
      JSONObject jsonResponse = getResponse(environment, xacHttpClient, response);
      if (jsonResponse.isEmpty())
      {
        LOGGER.info(HASH_CONSTANT + "RESPONSE BODY IS NULL WHEN TO MAKE USER CANCEL ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request,
            instanceId);
        throw new BpmServiceException(RESPONSE_BODY_IS_NULL_ERROR_CODE, RESPONSE_BODY_IS_NULL_ERROR_MESSAGE);
      }
      return jsonResponse.getString(DESCRIPTION).equals("SUCCESS");
    }
    catch (XacHttpException e)
    {
      LOGGER.info(HASH_CONSTANT + "ERROR OCCURRED TO MAKE USER CANCEL ON USSD \n BODY = [{}] \n WITH PROCESS INSTANCE ID = [{}]", request, instanceId);
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  private void setTransactionSession() throws BpmServiceException
  {
    ExtSessionInfo sessionInfo = this.extSessionInfoCache.getSessionInfo();
    if (null == sessionInfo)
    {
      throw new BpmServiceException(EXT_SESSION_NULL_CODE, EXT_SESSION_NULL_MESSAGE);
    }
//    if (!StringUtils.isBlank(sessionInfo.getExtSessionId()))
//    {
//      return;
//    }
    String transactionSessionId = getTransactionSessionId(sessionInfo.getUserId(), sessionInfo.getEncryptedPassword());
    if (StringUtils.isBlank(transactionSessionId))
    {
      throw new BpmServiceException(TRANSACTION_SESSION_ID_NULL_CODE, TRANSACTION_SESSION_ID_NULL_MESSAGE);
    }
    sessionInfo.setExtSessionId(transactionSessionId);
    extSessionInfoCache.setSessionInfo(sessionInfo);
  }

  private String getHeaderSource()
  {
    return environment.getProperty(XacBranchBankingConstants.BB_HEADER_SOURCE);
  }

  private String getHeaderRequestType()
  {
    return environment.getProperty(XacBranchBankingConstants.BB_HEADER_REQUEST_TYPE);
  }

  private String getHeaderFunction(String prefix)
  {
    return environment.getProperty(prefix + ".function");
  }

  private String getEndPoint()
  {
    return environment.getProperty(BB_ENDPOINT);
  }

  private String generateSecurityCode(String source, String function)
  {
    String value = source + function;
    return DigestUtils.md5Hex(value).toUpperCase();
  }

  private String getUrlFunction(String prefix)
  {
    return environment.getProperty(prefix + ".url");
  }

  private void validateEBankInput(String accountId, String channelId, String startDt, String endDt) throws BpmServiceException
  {
    if (StringUtils.isBlank(accountId))
    {
      throw new BpmServiceException(ACCOUNT_ID_NULL_CODE, ACCOUNT_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(channelId))
    {
      throw new BpmServiceException(CHANNEL_ID_NULL_CODE, CHANNEL_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(startDt))
    {
      throw new BpmServiceException(START_DT_NULL_CODE, START_DT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(endDt))
    {
      throw new BpmServiceException(END_DT_NULL_CODE, END_DT_NULL_MESSAGE);
    }
  }
}
