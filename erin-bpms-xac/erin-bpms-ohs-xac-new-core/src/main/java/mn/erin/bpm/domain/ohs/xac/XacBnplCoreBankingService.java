package mn.erin.bpm.domain.ohs.xac;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.HEADER_SOURCE_GET_BNPL_INVOICE_INFO;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.NEW_CORE_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_BNPL_HEADER_REQUEST_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_INTERNET_BANK_HEADER_SOURCE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_SET_BNPL_STATE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.INVOICE_NO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REQUEST;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.checkResponseStatus;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateRequestId;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderFunction;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getNewCoreEndPoint;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getPropertyByKey;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getResultResponse;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_NUM;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.STATE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;

/**
 * @author Bilguunbor
 */
public class XacBnplCoreBankingService implements BnplCoreBankingService
{
  private final Environment environment;
  private final AuthenticationService authenticationService;
  private static final Logger LOGGER = LoggerFactory.getLogger(XacBnplCoreBankingService.class);

  public XacBnplCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = environment;
    this.authenticationService = authenticationService;
  }

  @Override
  public Void setBnplInvoiceState(Map<String, String> invoiceInput) throws BpmServiceException
  {
    String headerSource = getHeaderSource(environment, WSO2_INTERNET_BANK_HEADER_SOURCE);
    String function = getHeaderFunction(environment, WSO2_SET_BNPL_STATE);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, WSO2_BNPL_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    String invoiceNumber = invoiceInput.get(INVOICE_NUM);
    String invoiceState = invoiceInput.get(INVOICE_STATE);
    JSONObject requestParam = new JSONObject();
    requestParam.put(INVOICE_NO, invoiceNumber);
    requestParam.put(STATE, invoiceState);

    JSONObject request = new JSONObject();
    request.put(REQUEST, requestParam);

    LOGGER.info(BNPL_LOG + "SET BNPL INVOICE STATE  ON INVOICE NUMBER = {} ", invoiceNumber);

    try (Response response = xacClient.post(request.toString()))
    {
      if (checkResponseStatus(environment, xacClient, response))
      {
        LOGGER.info(BNPL_LOG + "SUCCESSFULLY SET INVOICE STATE AS {} ON INVOICE NUMBER = {} ", invoiceState, invoiceNumber);
      }
      return null;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getBnplInvoiceInfo(String invoiceNo) throws BpmServiceException
  {
    String headerSource = getHeaderSource(environment, WSO2_INTERNET_BANK_HEADER_SOURCE);
    String function = getHeaderFunction(environment, HEADER_SOURCE_GET_BNPL_INVOICE_INFO);
    XacHttpClient xacClient = new XacHttpClient.Builder(getNewCoreEndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(generateRequestId())
        .headerRequestType(getPropertyByKey(environment, NEW_CORE_HEADER_REQUEST_TYPE))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .headerLanguageType("EN")
        .build();

    JSONObject requestParam = new JSONObject();
    requestParam.put(INVOICE_NO, invoiceNo);
    JSONObject request = new JSONObject();
    request.put(REQUEST, requestParam);

    LOGGER.info("####### Getting Bnpl Invoice info from XacService with invoiceNo = [{}]", invoiceNo);

    try (Response response = xacClient.post(request.toString()))
    {
      JSONObject jsonResponse = getResultResponse(environment, xacClient, response);

      LOGGER.info(BNPL_LOG + "Invoice info from xac service with invoiceNo = {}, [{}]", invoiceNo, jsonResponse);
      Map<String, Object> result = new HashMap<>();
      result.put("invoiceNo", getStringValue(jsonResponse, INVOICE_NO));
      result.put("sumPrice", getStringValue(jsonResponse, "sumPrice"));
      result.put("prepaymentAmt", getStringValue(jsonResponse, "prepaymentAmt"));
      result.put("Terminal_ID", getStringValue(jsonResponse, "Terminal_ID"));
      result.put("CreatedOn", getStringValue(jsonResponse, "CreatedOn"));
      result.put("State", getStringValue(jsonResponse, "State"));
      result.put("process_request_id", getStringValue(jsonResponse, "process_request_id"));
      result.put("reqUserID", getStringValue(jsonResponse, "reqUserID"));
      result.put("UpdatedOn", getStringValue(jsonResponse, "UpdatedOn"));
      result.put("UpdatedBy", getStringValue(jsonResponse, "UpdatedBy"));
      result.put("SettlementRefNo", getStringValue(jsonResponse, "SettlementRefNo"));
      return result;
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }
}
