/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.model.loan.LoanId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ADVAMOUNT;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_0;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_1;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_2;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_3;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_4;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_5;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_6;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ARG_7;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ATTENTIONAL;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.BALANCE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.BORROWER_ID_IS_NULL_FROM_MONGOL_BANK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.CID;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.CONTINUE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.CURRENCY_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.CUSTOMER_CID_NULL;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.FIELD_1;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.FIELD_20;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.INSECURE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ITEM;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOANCODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_CLASS;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_NOT_FOUND;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_TYPE_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.MONGOL_BANK_EXPIRE_DATE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.MONGOL_BANK_STARTED_DATE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.NO_SESSION;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.NO_SESSION_FROM_MONGOL_BANK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.OK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.REQUEST;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.REQUIRED_PARAMETERS_NULL_CONFIRM_MONGOL_BANK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESULT_ITEM_NULL_FROM_MONGOL_BANK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RETURN;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.SEARCH_BY_CO_BORROWER;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.SEARCH_BY_PRIMARY_BORROWER;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.STATUS_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.UNCERTAIN;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.UNSATISFACTORY;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.convertJsonStringToMap;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateRequestId;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderFunction;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderRequestType;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getNewCoreHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getOnlineLeasingHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getWso2EndPoint;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_CID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_CID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_RELATION_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_RELATION_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REGISTER_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ADDRESS;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_FIRST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_EXECUTIVE_LAST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_FIRST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_ID_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_LAST_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_LEGAL_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_OCCUPANCY;
import static mn.erin.domain.bpm.BpmModuleConstants.MONGOL_BANK_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFromJson;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
public class XacMongolBankService implements LoanService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacMongolBankService.class);
  public static final String NULL_RESPONSE_DURING_CONFIRM_REQUEST = "Null response from MONGOL BANK during confirm request!";

  private final Environment environment;
  private final AuthenticationService authenticationService;

  public XacMongolBankService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public String getCustomerCID(String searchValueType, String searchValue, String searchType, boolean isSearchByCoBorrower, String branchNumber, String userId,
      String userName) throws BpmServiceException
  {

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_CID);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_CID))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = createCIDRequestBody(searchValueType, searchValue, searchType, isSearchByCoBorrower, branchNumber, userId, userName);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null == jsonResponse)
      {
        String errorCode = "BPMS058";
        throw new BpmServiceException(errorCode, "Null response from MONGOL BANK!");
      }

      String customerCID = jsonResponse.getString(CID);

      if (customerCID.equalsIgnoreCase(NO_SESSION))
      {
        LOGGER.error(NO_SESSION_FROM_MONGOL_BANK);
        String errorCode = "BPMS059";
        throw new BpmServiceException(errorCode, NO_SESSION_FROM_MONGOL_BANK);
      }
      return customerCID;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public LoanEnquire getLoanEnquire(String customerCID) throws BpmServiceException
  {
    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_ENQUIRE_INFO);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_ENQUIRE_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(ARG_0, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null == jsonResponse)
      {
        return new LoanEnquire(LoanEnquireId.valueOf(LOAN_NOT_FOUND), BorrowerId.valueOf(LOAN_NOT_FOUND));
      }

      return getLoanEnquireFromJson(jsonResponse, customerCID);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public boolean confirmLoanEnquire(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException
  {
    if (null == loanEnquireId || null == borrowerId)
    {
      String errorCode = "BPMS060";
      throw new BpmServiceException(errorCode, REQUIRED_PARAMETERS_NULL_CONFIRM_MONGOL_BANK);
    }

    String enquireId = loanEnquireId.getId();
    String borrowerIdString = borrowerId.getId();

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_CONFIRM_ENQUIRE_INFO);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_CONFIRM_ENQUIRE_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    // generated id from Mongol bank.
    requestBody.put(ARG_0, enquireId);
    // borrower id.
    requestBody.put(ARG_1, borrowerIdString);
    // customer CID
    requestBody.put(ARG_2, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {

      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null == jsonResponse)
      {
        LOGGER.error(NULL_RESPONSE_DURING_CONFIRM_REQUEST);
        String errorCode = "BPMS058";
        throw new BpmServiceException(errorCode, NULL_RESPONSE_DURING_CONFIRM_REQUEST);
      }

      String confirmResult = jsonResponse.getString(CONTINUE);

      return confirmResult.equalsIgnoreCase(OK);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public List<Loan> getLoanList(String customerCID, BorrowerId borrowerId) throws BpmServiceException
  {
    List<Loan> loanList = new ArrayList<>();

    if (null == customerCID)
    {
      throw new BpmServiceException(CUSTOMER_CID_NULL_CODE, CUSTOMER_CID_NULL);
    }

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_INFO);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    // generated id from Mongol bank.
    requestBody.put(ARG_0, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null == jsonResponse)
      {
        return Collections.emptyList();
      }

      if (jsonResponse.has(RETURN))
      {
        if (jsonResponse.get(RETURN) instanceof JSONObject)
        {
          JSONObject jsonLoanInfo = jsonResponse.getJSONObject(RETURN);

          loanList.add(toLoanFromJson(jsonLoanInfo));
        }
        else
        {
          JSONArray results = jsonResponse.getJSONArray(RETURN);

          if (results.isEmpty())
          {
            String errorCode = "BPMS062";
            throw new BpmServiceException(errorCode, RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK);
          }

          for (int index = 0; index < results.length(); index++)
          {
            JSONObject jsonLoan = (JSONObject) results.get(index);

            if (null != jsonLoan)
            {
              loanList.add(toLoanFromJson(jsonLoan));
            }
          }
        }
      }
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
    return loanList;
  }

  @Override
  public LoanEnquire getLoanEnquireWithFile(LoanEnquireId loanEnquireId, BorrowerId borrowerId, String customerCID) throws BpmServiceException
  {
    if (null == customerCID)
    {
      String errorCode = "BPMS061";
      throw new BpmServiceException(errorCode, CUSTOMER_CID_NULL);
    }

    if (null == borrowerId)
    {
      String errorCode = "BPMS063";
      throw new BpmServiceException(errorCode, BORROWER_ID_IS_NULL_FROM_MONGOL_BANK);
    }
    String borrowerIdString = borrowerId.getId();

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_INFO_PDF);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_LOAN_INFO_PDF))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();

    requestBody.put(ARG_0, borrowerIdString);
    requestBody.put(ARG_1, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {

      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null == jsonResponse)
      {
        return new LoanEnquire(LoanEnquireId.valueOf(LOAN_NOT_FOUND), BorrowerId.valueOf(LOAN_NOT_FOUND));
      }

      String pdfFileAsBase64 = jsonResponse.getString(RETURN);

      if (null == pdfFileAsBase64)
      {
        String errorCode = "BPMS064";
        throw new BpmServiceException(errorCode, PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_MESSAGE);
      }

      byte[] encodedByte = Base64.getEncoder().encode(pdfFileAsBase64.getBytes());

      LoanEnquire loanEnquire = new LoanEnquire(loanEnquireId, borrowerId);
      loanEnquire.setEnquireAsFile(encodedByte);

      return loanEnquire;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public LoanEnquire getLoanEnquireByCoBorrower(String customerCID) throws BpmServiceException
  {
    if (null == customerCID)
    {
      String errorCode = "BPMS061";
      throw new BpmServiceException(errorCode, CUSTOMER_CID_NULL);
    }

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_CID_BY_CO_BORROWER);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_CID_BY_CO_BORROWER))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(ARG_0, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {

      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null != jsonResponse)
      {
        return getLoanEnquireFromJson(jsonResponse, customerCID);
      }
      return new LoanEnquire(LoanEnquireId.valueOf(LOAN_NOT_FOUND), BorrowerId.valueOf(LOAN_NOT_FOUND));
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, String> getCustomerRelatedInfo(String customerCID, String relation) throws BpmServiceException
  {
    if (null == customerCID || StringUtils.isBlank(customerCID))
    {
      throw new BpmServiceException(CUSTOMER_CID_NULL_CODE, CUSTOMER_CID_NULL_MESSAGE);
    }

    if (null == relation || StringUtils.isBlank(relation))
    {
      throw new BpmServiceException(CUSTOMER_RELATION_TYPE_NULL_CODE, CUSTOMER_RELATION_TYPE_NULL_MESSAGE);
    }

    String headerSource = getHeaderSource(this.environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_RELATED_INFO);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_RELATED_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(ARG_0, customerCID);
    requestBody.put(ARG_1, relation);

    try (Response response = xacClient.post(requestBody.toString()))
    {

      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null != jsonResponse)
      {
        Object returnObject = jsonResponse.get(RETURN);

        if (returnObject instanceof JSONObject)
        {
          JSONObject responseObject = (JSONObject) returnObject;
          return convertJsonStringToMap(responseObject.toString());
        }

        if (returnObject instanceof JSONArray)
        {
          JSONArray itemArray = (JSONArray) returnObject;

          for (int i = 0; i < itemArray.length(); i++)
          {
            JSONObject itemJSON = itemArray.getJSONObject(0);
            return convertJsonStringToMap(itemJSON.toString());
          }
        }
      }
      return null;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, String> getCustomerDetail(String customerCID) throws BpmServiceException
  {
    if (null == customerCID || StringUtils.isBlank(customerCID))
    {
      throw new BpmServiceException(CUSTOMER_CID_NULL_CODE, CUSTOMER_CID_NULL_MESSAGE);
    }

    String headerSource = getNewCoreHeaderSource(environment);
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_DETAIL);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_DETAIL))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put(ARG_0, customerCID);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getMongolBankResponse(environment, xacClient, response);

      if (null != jsonResponse)
      {
        JSONObject returnObject = (JSONObject) jsonResponse.get(RETURN);
        return convertJsonStringToMap(returnObject.toString());
      }
      return null;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getCIBInfo(Map<String, String> requestParam) throws BpmServiceException
  {
    String registerNumber = requestParam.get(REGISTER_NUMBER);
    String branchId = requestParam.get(BRANCH_NUMBER);
    String name = requestParam.get("customerName");
    String requestId = requestParam.get(PROCESS_REQUEST_ID);

    if (null == registerNumber || StringUtils.isBlank(registerNumber))
    {
      throw new BpmServiceException(REGISTER_NUMBER_BLANK_CODE, REGISTER_NUMBER_BLANK_MESSAGE);
    }
    String headerSource = getNewCoreHeaderSource(this.environment);
    String userId = authenticationService.getCurrentUserId();
    if(requestParam.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
      headerSource = getOnlineLeasingHeaderSource(this.environment);
      userId = requestParam.get(PHONE_NUMBER);
    }
    String function = getHeaderFunction(this.environment, XacConstants.WSO2_HEADER_GET_CIB_INFO);
    String securityCode = generateSecurityCode(headerSource, function);

    XacHttpClient xacClient = new XacHttpClient.Builder(getWso2EndPoint(this.environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(this.environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_DETAIL))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(userId)
        .headerBranchId(branchId)
        .headerName(name)
        .build();

    JSONObject requestBody = new JSONObject();
    JSONObject request = new JSONObject();
    requestBody.put("regno", registerNumber);
    requestBody.put("type", "2"); // it will constant for now
    requestBody.put("custtypeid", requestParam.get("customerTypeId"));
    requestBody.put("purptypeid", EMPTY_VALUE);
    requestBody.put("hasFee", EMPTY_VALUE);
    requestBody.put("generatePdf", "Y"); //constant
    request.put(REQUEST, requestBody);

    LOGGER.info("Mongol bank - getCIBInfo service request body = {}, requestId = {}, headerSource = {} and userId = {}", request, requestId, headerSource, userId);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
//      LOGGER.info("######## Response from XAC MONGOL BANK service = {} with requestId = {}, registerNumber = {}, [{}]", function, requestId, registerNumber, jsonResponse);
      if (jsonResponse != null)
      {
        return toConvertMBankInfoNew(jsonResponse);
      }
      return null;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  private Map<String, Object> toConvertMBankInfoNew(JSONObject jsonResponse) throws BpmServiceException
  {
    Map<String, Object> mbInfo = new HashMap<>();
    mbInfo.put("mbCustomerInfo", getMbCustomerInfo(jsonResponse));
    mbInfo.put("mbCustomerLoanList", getMbCustomerLoanInfo(jsonResponse));
    mbInfo.put("mbCoBorrowerLoans", getMbCoBorrowerLoanInfo(jsonResponse));

    String base64Pdf = getValidString(jsonResponse.get("base64Pdf"));
    String pdfError = getStringValueFromJson(jsonResponse, ("PDFError"));

    if (StringUtils.isBlank(base64Pdf))
    {
      throw new BpmServiceException(PDF_FILE_AS_BASE_64_NULL_FROM_MONGOL_BANK_CODE, pdfError);
    }
    mbInfo.put("mbBase64Pdf", base64Pdf);
    return mbInfo;
  }

  private Map<String, Object> getMbCustomerInfo(JSONObject jsonResponse)
  {
    Map<String, Object> customerInfoMap = new HashMap<>();
    if (!jsonResponse.has("cust"))
    {
      return customerInfoMap;
    }
    JSONObject custInfo = (JSONObject) jsonResponse.get("cust");
    if (!jsonResponse.isEmpty())
    {
      customerInfoMap.put(MONGOL_BANK_FIRST_NAME, getStringValueFromJson(custInfo, "cust_name"));
      customerInfoMap.put(MONGOL_BANK_LAST_NAME, getStringValueFromJson(custInfo, "cust_lname"));
      customerInfoMap.put(MONGOL_BANK_LEGAL_STATE, getStringValueFromJson(custInfo, "cust_type"));
      customerInfoMap.put(MONGOL_BANK_REGISTER_NUMBER, getStringValueFromJson(custInfo, "custregno"));
      customerInfoMap.put(MONGOL_BANK_ID_NUMBER, getStringValueFromJson(custInfo, "cust_state_regno"));
      customerInfoMap.put(MONGOL_BANK_ADDRESS, getStringValueFromJson(custInfo, "cust_address"));
      customerInfoMap.put(MONGOL_BANK_OCCUPANCY, getStringValueFromJson(custInfo, "cust_work"));
      customerInfoMap.put(MONGOL_BANK_EXECUTIVE_FIRST_NAME, getStringValueFromJson(custInfo, "cust_dir_name"));
      customerInfoMap.put(MONGOL_BANK_EXECUTIVE_LAST_NAME, getStringValueFromJson(custInfo, "cust_dir_lname"));
    }
    return customerInfoMap;
  }

  private List<Loan> getMbCustomerLoanInfo(JSONObject jsonResponse)
  {
    List<Loan> customerLoanList = new ArrayList<>();
    if (!jsonResponse.has("inquiry"))
    {
      return customerLoanList;
    }
    JSONArray custInquiries = (JSONArray) jsonResponse.get("inquiry");
    if (!custInquiries.isEmpty())
    {
      for (int i = 0; i < custInquiries.length(); i++)
      {
        JSONObject inquiry = custInquiries.getJSONObject(i);
        customerLoanList.add(getLoan(inquiry));
      }
    }
    return customerLoanList;
  }

  private List<Loan> getMbCoBorrowerLoanInfo(JSONObject jsonResponse)
  {
    List<Loan> coBorrowerLoans = new ArrayList<>();

    if (!jsonResponse.has("relInquiry"))
    {
      return coBorrowerLoans;
    }
    JSONArray relInquiries = (JSONArray) jsonResponse.get("relInquiry");
    if (!relInquiries.isEmpty())
    {
      for (int i = 0; i < relInquiries.length(); i++)
      {
        JSONObject relInquiry = relInquiries.getJSONObject(i);
        coBorrowerLoans.add(getLoan(relInquiry));
      }
    }
    return coBorrowerLoans;
  }

  private Loan getLoan(JSONObject inquiry)
  {
    String loanCode = getStringValueFromJson(inquiry, LOANCODE);
    String loanClassName = getStringValueFromJson(inquiry, "cls_name");
    int rank = getLoanClassRank(loanClassName);
    String loanTypeCode = getStringValueFromJson(inquiry, "dutytype");
    String loanTypeName = getStringValueFromJson(inquiry, "dutytypename");
    String status = getStringValueFromJson(inquiry, "closed_date");
    String maturityDate2 = getStringValueFromJson(inquiry, "maturity_date2");
    String expireDate = !StringUtils.isBlank(maturityDate2) ? maturityDate2 : getStringValueFromJson(inquiry, "maturity_date");
    String startDate = getStringValueFromJson(inquiry, "adv_date");
    String currencyCode = getStringValueFromJson(inquiry, "curr_code");
    BigDecimal amount = inquiry.getBigDecimal("adv_amount");
    BigDecimal balance = inquiry.getBigDecimal("balance");

    LoanClass loanClass = new LoanClass(rank, loanClassName);
    String loanType = loanTypeCode + "-" + loanTypeName;

    //TODO: temporarily change depending on XAC
    String loanId = StringUtils.isBlank(loanCode) ? generateRequestId() : loanCode;
    Loan loan = new Loan(LoanId.valueOf(loanId), loanClass, loanType, status, startDate, expireDate, currencyCode, amount, balance);

    loan.setLoanClass(loanClass);
    loan.setType(loanType);
    loan.setStatus(status);
    loan.setStartDate(startDate);
    loan.setExpireDate(expireDate);
    loan.setCurrencyCode(currencyCode);
    loan.setAmount(amount);
    loan.setBalance(balance);
    return loan;
  }

  private Integer getLoanClassRank(String loanClassName)
  {
    if (loanClassName.equalsIgnoreCase(UNSATISFACTORY))
    {
      return 5;
    }
    else if (loanClassName.contains(INSECURE))
    {
      return 4;
    }
    else if (loanClassName.contains(UNCERTAIN))
    {
      return 3;
    }
    else if (loanClassName.contains(ATTENTIONAL))
    {
      return 2;
    }
    return 1;
  }

  private LoanEnquire getLoanEnquireFromJson(JSONObject jsonResponse, String customerCID) throws BpmServiceException
  {
    Object returnObject = jsonResponse.get(RETURN);

    if (returnObject instanceof JSONArray)
    {
      JSONArray resultArray = jsonResponse.getJSONArray(RETURN);

      if (jsonResponse.isEmpty())
      {
        String errorCode = "BPMS062";
        throw new BpmServiceException(errorCode, RESULT_JSON_ARRAY_IS_EMPTY_FROM_MONGOL_BANK);
      }

      JSONObject itemJson = resultArray.getJSONObject(0);

      if (null == itemJson)
      {
        String errorCode = "BPMS065";
        throw new BpmServiceException(errorCode, RESULT_ITEM_NULL_FROM_MONGOL_BANK);
      }

      JSONObject item = (JSONObject) itemJson.get(ITEM);
      return getEnquireFromItem(item, customerCID);
    }
    else if (returnObject instanceof JSONObject)
    {
      JSONObject returnValue = jsonResponse.getJSONObject(RETURN);

      Object itemObject = returnValue.get(ITEM);

      if (itemObject instanceof JSONObject)
      {
        JSONObject itemJson = (JSONObject) returnValue.get(ITEM);

        return getEnquireFromItem(itemJson, customerCID);
      }
      else if (itemObject instanceof JSONArray)
      {
        JSONArray itemArray = (JSONArray) itemObject;

        for (int i = 0; i < itemArray.length(); i++)
        {
          JSONObject itemJSON = itemArray.getJSONObject(0);
          return getEnquireFromItem(itemJSON, customerCID);
        }
      }
    }
    return null;
  }

  private LoanEnquire getEnquireFromItem(JSONObject item, String customerCID) throws BpmServiceException
  {
    Object borrowerIdObject = item.get(FIELD_1);
    Object enquireIdObject = item.get(FIELD_20);

    String borrowerId = String.valueOf(borrowerIdObject);
    String enquireId = String.valueOf(enquireIdObject);

    if (null == borrowerId)
    {
      String errorCode = "BPMS063";
      throw new BpmServiceException(errorCode, BORROWER_ID_IS_NULL_FROM_MONGOL_BANK);
    }

    return new LoanEnquire(LoanEnquireId.valueOf(String.valueOf(enquireId)), BorrowerId.valueOf(borrowerId), customerCID);
  }

  private Loan toLoanFromJson(JSONObject loanJSON)
  {
    String loanId = String.valueOf(loanJSON.get(LOANCODE));
    String type = String.valueOf(loanJSON.get(LOAN_TYPE_CODE));

    BigDecimal amount = loanJSON.getBigDecimal(ADVAMOUNT);
    BigDecimal balance = loanJSON.getBigDecimal(BALANCE);

    String currencyCode = String.valueOf(loanJSON.get(CURRENCY_CODE));
    String loanClassName = String.valueOf(loanJSON.get(LOAN_CLASS));

    String status = String.valueOf(loanJSON.get(STATUS_CODE));

    String expireDate = String.valueOf(loanJSON.get(MONGOL_BANK_EXPIRE_DATE));
    String startedDate = String.valueOf(loanJSON.get(MONGOL_BANK_STARTED_DATE));

    Integer rank = getLoanClassRank(loanClassName);
    Loan loan = new Loan(LoanId.valueOf(loanId), new LoanClass(rank, loanClassName));

    loan.setType(type);
    loan.setAmount(amount);

    loan.setBalance(balance);
    loan.setCurrencyCode(currencyCode);

    loan.setStartDate(startedDate);
    loan.setExpireDate(expireDate);
    loan.setStatus(status);

    return loan;
  }

  private JSONObject createCIDRequestBody(String searchValueType, String searchValue, String searchType, boolean isSearchByCoborrower, String branchNumber,
      String userId,
      String userName)
  {
    JSONObject requestBody = new JSONObject();

    // search value type : register, passport no or customer name.
    requestBody.put(ARG_0, searchValueType);
    // search type : citizen, organization, foundation etc...
    requestBody.put(ARG_1, searchType);
    // search value: register number value
    requestBody.put(ARG_2, searchValue);

    // Changed false value from XAC request.
    requestBody.put(ARG_3, false);

    // if search by co-borrower sets "1" otherwise sets "0".
    if (isSearchByCoborrower)
    {
      requestBody.put(ARG_4, SEARCH_BY_CO_BORROWER);
    }
    else
    {
      requestBody.put(ARG_4, SEARCH_BY_PRIMARY_BORROWER);
    }
    // branch number
    requestBody.put(ARG_5, branchNumber);
    // current user name.
    requestBody.put(ARG_6, userName);
    // current user id.
    requestBody.put(ARG_7, userId);

    return requestBody;
  }
}
