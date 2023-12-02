package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacHttpClient;
import mn.erin.bpm.domain.ohs.xac.XacHttpConstants;
import mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingConstants;
import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_BODY_IS_NULL_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_BODY_IS_NULL_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_IS_NULL_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.RESPONSE_IS_NULL_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.XAC_RESPONSE_NOT_JSON_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacHttpConstants.ERROR;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacHttpConstants.FAILURE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacHttpConstants.RESPONSE;

/**
 * @author Bilguunbor
 */
public class XacHttpUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacHttpUtils.class);
  private static final String HASH_CONSTANT = "###############  ";

  private XacHttpUtils()
  {

  }

  public static JSONArray getResponse(Environment environment, XacHttpClient xacClient, Response response, String instanceId) throws XacHttpException
  {
    JSONObject jsonResult = validateResponse(environment, xacClient, response);

    if (null == jsonResult.get(RESPONSE))
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }

    try
    {
      return jsonResult.getJSONArray(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      LOGGER.error(HASH_CONSTANT + "EXCEPTION OCCURRED WHEN GET TIN LIST SERVICE WITH PROCESS INSTANCE ID = [{}]", instanceId);
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }
  }

  public static JSONObject getResponse(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateResponse(environment, xacHttpClient, response);

    if (null == jsonResult.get(RESPONSE))
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }

    try
    {
      if (!(jsonResult.get(XacHttpConstants.RESPONSE) instanceof JSONObject))
      {
        throw new XacHttpException(XAC_RESPONSE_NOT_JSON_ERROR_CODE, XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE);
      }
      return (JSONObject) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }
  }


  public static JSONArray getArrayResponse(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateResponse(environment, xacHttpClient, response);

    if (null == jsonResult.get(RESPONSE))
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }

    try
    {
      if (!(jsonResult.get(XacHttpConstants.RESPONSE) instanceof JSONArray))
      {
        throw new XacHttpException(XAC_RESPONSE_NOT_JSON_ERROR_CODE, XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE);
      }
      return (JSONArray) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }
  }


  private static JSONObject validateResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_IS_NULL_ERROR_CODE, RESPONSE_BODY_IS_NULL_ERROR_MESSAGE);
    }

    String content = xacClient.read(responseBody);
    JSONObject jsonResult = xacClient.toJson(content);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);
    throwException(responseStatus, jsonResult);

    return jsonResult;
  }

  public static JSONObject getTaxResponse(Environment environment, XacHttpClient xacHttpClient, Response response, JSONObject requestBody)
      throws XacHttpException
  {
    JSONObject jsonResult = validateTaxResponse(environment, xacHttpClient, response, requestBody);

    if (null == jsonResult.get(RESPONSE))
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }

    try
    {
      if (!(jsonResult.get(XacHttpConstants.RESPONSE) instanceof JSONObject))
      {
        throw new XacHttpException(XAC_RESPONSE_NOT_JSON_ERROR_CODE, XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE);
      }
      return (JSONObject) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_IS_NULL_ERROR_MESSAGE);
    }
  }

  private static JSONObject validateTaxResponse(Environment environment, XacHttpClient xacClient, Response response, JSONObject requestBody)
      throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_IS_NULL_ERROR_CODE, RESPONSE_BODY_IS_NULL_ERROR_MESSAGE);
    }

    String content = xacClient.read(responseBody);

    JSONObject jsonResult = xacClient.toTaxJson(content, requestBody);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);
    throwException(responseStatus, jsonResult);

    return jsonResult;
  }

  private static void throwException(String responseStatus, JSONObject jsonResult) throws XacHttpException
  {
    if (responseStatus.equalsIgnoreCase(FAILURE))
    {
      JSONObject error;
      if (jsonResult.has(ERROR))
      {
        error = (JSONObject) jsonResult.get(ERROR);
      }
      else
      {
        error = (JSONObject) jsonResult.get(RESPONSE);
      }

      String code = error.has("ErrorCode") ? error.getString("ErrorCode") : error.getString("code");

      if (error.has("ErrorDesc"))
      {
        String desc = error.getString("ErrorDesc");
        throw new XacHttpException(code, desc);
      }
      else if (error.has("ErrorMessage") && error.has("ErrorSource"))
      {
        String message = "message: " + error.getString("ErrorMessage") + "  source: " + error.get("ErrorSource");
        throw new XacHttpException(code, message);
      }
      else if (error.has("message"))
      {
        String message = error.getString("message");
        throw new XacHttpException(code, message);
      }
    }
  }

  private static String getWso2EndPoint(Environment environment)
  {
    return environment.getProperty(XacBranchBankingConstants.BB_ENDPOINT);
  }
}
