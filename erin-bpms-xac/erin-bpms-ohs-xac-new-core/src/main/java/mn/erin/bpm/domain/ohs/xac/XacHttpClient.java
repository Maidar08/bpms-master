/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.Validate;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.common.http.HttpCall;

import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.XAC_CBS_RESPONSE_BODY_CONTENT_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NO;

/**
 * Represents XAC specific http client for calling REST APIs
 *
 * @author EBazarragchaa
 */
public final class XacHttpClient
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacHttpClient.class);
  private static final String HEADER_FUNCTION = "Function";

  final String endpoint;
  final Map<String, String> headers;

  public XacHttpClient(XacHttpClient.Builder builder)
  {
    this.endpoint = builder.endpoint;
    this.headers = new HashMap<>(builder.headers);
  }

  public Response get()
  {
    return null;
  }

  public Response post(String jsonBody)
  {
    String url = endpoint + headers.get(HEADER_FUNCTION);
    return HttpCall.post(url, headers, jsonBody);
  }

  public Response post(String jsonBody, String function)
  {
    String url = endpoint + function;
    return HttpCall.post(url, headers, jsonBody);
  }

  public Response postTextXml(String textXmlBody)
  {
    String url = endpoint + headers.get(HEADER_FUNCTION);
    return HttpCall.postTextXml(url, headers, textXmlBody);
  }

  public Response postTextXml(String textXmlBody, String URL)
  {
    String url = endpoint + URL;
    return HttpCall.postTextXml(url, headers, textXmlBody);
  }

  public Response postTextXmlNoHeader(String textXmlBody)
  {
    String url = endpoint;
    return HttpCall.postTextXml(url, headers, textXmlBody);
  }

  public String read(ResponseBody responseBody)
  {
    try
    {
      byte[] payload = responseBody.source().readByteArray();
      return new String(payload, StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage());
      return "";
    }
  }

  public JSONObject toJson(String content) throws XacHttpException
  {
    try
    {
      return new JSONObject(content);
    }
    catch (JSONException e)
    {
      LOGGER.error("Response content is invalid JSON", e);
      String message = String.format("Invalid response JSON = [%s] content from XAC service!", content);

      throw new XacHttpException(XAC_CBS_RESPONSE_BODY_CONTENT_NULL_ERROR_CODE, message);
    }
  }
  public JSONObject toTaxJson(String content, JSONObject requestBody) throws XacHttpException
  {

    try
    {
      return new JSONObject(content);
    }
    catch (JSONException e)
    {
      LOGGER.error("Response content is invalid JSON", e);
      String message = String.format("Invalid response JSON = [%s] content from XAC service!", e.getMessage());

      if (requestBody.has(INVOICE_NO)) {
        String invoiceNumber = (String) requestBody.get("invoiceNo");
        LOGGER.error("INVALID RESPONSE JSON = [{}] from XAC service to download tax invoice with [{}] request BODY! ", content, requestBody);
        message = String.format("Failed to download invoice with %s number", invoiceNumber);
      }
      throw new XacHttpException(XAC_CBS_RESPONSE_BODY_CONTENT_NULL_ERROR_CODE, message);
    }
  }

  public Map<String, String> headerParam()
  {
    return this.headers;
  }

  public static class Builder
  {
    final String endpoint;
    Map<String, String> headers;

    public Builder(String endpoint, String headerSource)
    {
      this.endpoint = Validate.notBlank(endpoint, "XacHttpClient endpoint is missing!");
      this.headers = new HashMap<>();
      this.headers.put("Source", Validate.notBlank(headerSource, "XacHttpClient header 'Source' is missing!"));
    }

    public Builder(String endpoint, String contentType, String soapAction)
    {
      this.endpoint = Validate.notBlank(endpoint, "XacHttpClient endpoint is missing!");
      this.headers = new HashMap<>();
      this.headers.put("Content-Type", Validate.notBlank(contentType, "XacHttpClient header 'Content-type' is missing!"));
      this.headers.put("SOAPAction", Validate.notBlank(soapAction, "XacHttpClient header 'SOAPAction' is missing!"));
    }

    public Builder headerContentType(String contentType)
    {
      this.headers.put("Content-Type", Validate.notBlank(contentType, "XacHttpClient header 'Content-Type' is missing!"));
      return this;
    }

    public Builder headerSoap(String soapAction)
    {
      this.headers.put("SOAPAction", soapAction);
      return this;
    }

    public Builder headerFunction(String headerFunction)
    {
      this.headers.put(HEADER_FUNCTION, Validate.notBlank(headerFunction, "XacHttpClient header 'Function' is missing!"));
      return this;
    }

    public Builder headerRequestId(String headerRequestId)
    {
      this.headers.put("RequestId", Validate.notBlank(headerRequestId, "XacHttpClient header 'RequestId' is missing!"));
      return this;
    }

    public Builder headerRequestType(String headerRequestType)
    {
      this.headers.put("RequestType", Validate.notBlank(headerRequestType, "XacHttpClient header 'RequestType' is missing!"));
      return this;
    }

    public Builder headerSecurityCode(String headerSecurityCode)
    {
      this.headers.put("SecurityCode", Validate.notBlank(headerSecurityCode, "XacHttpClient header 'SecurityCode' is missing!"));
      return this;
    }

    public Builder headerUserId(String userId)
    {
      this.headers.put("UserId", Validate.notBlank(userId, "XacHttpClient header 'UserId' is missing!"));
      return this;
    }

    public Builder headerBranch(String branch)
    {
      this.headers.put("Branch", Validate.notBlank(branch, "XacHttpClient header 'Branch' is missing!"));
      return this;
    }

    public Builder headerMessageId(String messageId)
    {
      this.headers.put("MessageID", Validate.notBlank(messageId, "XacHttpClient header 'MessageID' is missing!"));
      return this;
    }

    public XacHttpClient build()
    {
      return new XacHttpClient(this);
    }

    public Builder headerLanguageType(String languageId)
    {
      this.headers.put("LanguageId", Validate.notBlank(languageId, "XacHttpClient header 'LanguageId' is missing!"));

      return this;
    }

    public Builder headerPassword(String password)
    {
      this.headers.put("Password", Validate.notBlank(password, "XacHttpClient header 'password' is missing!"));

      return this;
    }

    public Builder headerSessionId(String sessionId)
    {
      this.headers.put("SessionID", Validate.notBlank(sessionId, "XacHttpClient header 'session Id' is missing!"));

      return this;
    }

    public Builder headerName(String name){
      this.headers.put("Name",  Validate.notBlank(name, "XacHttpClient header 'Name' is missing!"));
      return this;
    }

    public Builder headerBranchId(String branchId){
      this.headers.put("BranchID",  Validate.notBlank(branchId, "XacHttpClient header 'BranchID' is missing!"));
      return this;
    }
  }
}
