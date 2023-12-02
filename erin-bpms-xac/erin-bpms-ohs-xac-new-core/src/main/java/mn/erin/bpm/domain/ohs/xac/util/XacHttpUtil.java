package mn.erin.bpm.domain.ohs.xac.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.XacConstants;
import mn.erin.bpm.domain.ohs.xac.XacHttpClient;
import mn.erin.bpm.domain.ohs.xac.XacHttpConstants;
import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.domain.bpm.constants.CollateralConstants;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.GET_ACC_LIEN_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_REPORT_ABSOLUTE_PATH;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_REPORT_ABSOLUTE_PATH_EMPLOYEE_LOAN;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_DESC;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.ERR_MSG_RESPONSE_NULL;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.FAILURE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_NOT_FOUND;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE_NULL;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.RESPONSE_STATUS;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.SUCCESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacAccountConstants.ACCT_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.APPARTNG_METHOD_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.APPORT_AMT_CCY_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.APPORT_AMT_VAL_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.CHASSIS_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_AMT_CCY_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_AMT_VAL_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_CODE_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_CODE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_NATURE_IND_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.COLTRL_TYPE_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.CRNCY_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.CUSTOMER_CIF;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.ENGINE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.FINANCIAL_LEASING_SUPPLIER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.FORM_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.HOUSE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSPECTION_SERIAL_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.INSP_SERIAL_NUM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.LINKAGE_COLTRL_INFO_REC;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_FORM_NUM;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MACHINE_MODEL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MACHINE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MANUFACTURER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINERY_MANUFACTURER_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINE_MODEL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MACHINE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MANUFACTURER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MANUFACTURER_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MANUFACTURE_YEAR;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MARGIN_PCNT_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.MARGIN_PCNT_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHERS_FORM_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHERS_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHERS_RECEIVED_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHER_AMOUNT_VALUE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OTHER_COL_VALUE_BEFORE_LIEN;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNERSHIP_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_CIF_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_CIF_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_NAME;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_NAME_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.OWNER_TYPE_REQUEST;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.PURPOSE_OF_USAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.RECEIVED_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.REMARKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.RESPONSE_COLLATERAL_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.REVIEW_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SEL_FLG_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SERIAL_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SERIAL_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.SRL_NUM_COLL_LINKAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.VEHICLES_INFO;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.VEHICLE_MODEL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.VEHICLE_REGISTER_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_CUSTOMER_ID;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_REMARKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_REVIEW_DATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacCollateralConstants.XAC_RMKS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.ACCOUNT_NUMBER_NOT_EXIST_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.ACCOUNT_NUMBER_NOT_EXIST_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.ERROR_NOT_FOUND_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.HTTP_CONNECTION_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.CUST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_LOAN_PRODUCT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PURPOSE_OF_USAGE_FIELD_ID;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.RESPONSE_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.RESPONSE_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.RESPONSE_NULL_CODE;
import static mn.erin.domain.bpm.BpmNewCoreMessagesConstants.RESPONSE_NULL_MESSAGE;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_1;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_2;
import static mn.erin.domain.bpm.constants.CollateralConstants.ADDRESS_3;
import static mn.erin.domain.bpm.constants.CollateralConstants.IMMOVABLE_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFrmJSONObject;

/**
 * @author Tamir
 */
public final class XacHttpUtil
{
  public static final String XAC_RESPONSE_JSON_PARSE_ERROR = "CBS004";
  public static final String XAC_CBS_RESPONSE_BODY_NULL_ERROR_CODE = "CBS001";
  public static final String XAC_CBS_RESPONSE_BODY_CONTENT_NULL_ERROR_CODE = "CBS002";
  public static final String XAC_CBS_SERVICE_RESPONSE_NULL_ERROR_CODE = "CBS003";

  public static final String RESPONSE_BODY_IS_NULL_ERROR_CODE = "BBS002";
  public static final String RESPONSE_IS_NULL_ERROR_CODE = "BBS003";
  private static final String XAC_RESPONSE_NOT_JSON_ERROR_CODE = "BBS100";
  public static final String XAC_RESPONSE_NOT_JSON_ERROR_MESSAGE = "Xac service response is not valid type. Response is supposed to be type of JSON!";

  private XacHttpUtil()
  {

  }

  public static JSONObject createCollLinkageBody(String accountNumber, String linkageType, Map<String, Object> collaterals)
  {
    JSONObject linkages = new JSONObject();

    linkages.put("entityId", accountNumber);
    linkages.put("entityType", linkageType);

    JSONArray linkDetailsArray = new JSONArray();

    for (Map.Entry<String, Object> collateral : collaterals.entrySet())
    {
      String collateralID = collateral.getKey();

      if (!collateralID.isEmpty())
      {
        Map<?, ?> detail = (Map<?, ?>) collateral.getValue();
        JSONObject detailJSON = new JSONObject();

        String serialNumber = String.valueOf(detail.get(SRL_NUM_COLL_LINKAGE));
        String collType = String.valueOf(detail.get(COLTRL_TYPE_COLL_LINKAGE));

        String collCode = String.valueOf(detail.get(COLTRL_CODE_COLL_LINKAGE));
        String collAmount = String.valueOf(detail.get(COLTRL_AMT_VAL_COLL_LINKAGE));

        String collAmountCCY = String.valueOf(detail.get(COLTRL_AMT_CCY_COLL_LINKAGE));
        String currency = String.valueOf(detail.get(CRNCY_COLL_LINKAGE));

        String apportAmtVal = String.valueOf(detail.get(APPORT_AMT_VAL_COLL_LINKAGE));
        String apportAmtCCY = String.valueOf(detail.get(APPORT_AMT_CCY_COLL_LINKAGE));

        String marginPcnt = String.valueOf(detail.get(MARGIN_PCNT_COLL_LINKAGE));
        String collNatureInd = String.valueOf(detail.get(COLTRL_NATURE_IND_COLL_LINKAGE));

        String appartingMethod = String.valueOf(detail.get(APPARTNG_METHOD_COLL_LINKAGE));
        String selFlg = String.valueOf(detail.get(SEL_FLG_COLL_LINKAGE));

        detailJSON.put(COLTRL_ID, collateralID);
        detailJSON.put(SRL_NUM_COLL_LINKAGE, serialNumber);
        detailJSON.put(COLTRL_TYPE_COLL_LINKAGE, collType);

        detailJSON.put(COLTRL_CODE_COLL_LINKAGE, collCode);
        detailJSON.put(COLTRL_AMT_VAL_COLL_LINKAGE, collAmount);

        detailJSON.put(COLTRL_AMT_CCY_COLL_LINKAGE, collAmountCCY);
        detailJSON.put(CRNCY_COLL_LINKAGE, currency);

        detailJSON.put(APPORT_AMT_VAL_COLL_LINKAGE, apportAmtVal);
        detailJSON.put(APPORT_AMT_CCY_COLL_LINKAGE, apportAmtCCY);

        detailJSON.put(MARGIN_PCNT_COLL_LINKAGE, marginPcnt);
        detailJSON.put(COLTRL_NATURE_IND_COLL_LINKAGE, collNatureInd);

        detailJSON.put(APPARTNG_METHOD_COLL_LINKAGE, appartingMethod);
        detailJSON.put(SEL_FLG_COLL_LINKAGE, selFlg);

        linkDetailsArray.put(detailJSON);
      }
    }

    linkages.put(LINKAGE_COLTRL_INFO_REC, linkDetailsArray);

    return linkages;
  }

  public static String getCreatedAccountResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject resultJSON = validateNewCoreResponse(environment, xacClient, response);

    JSONObject accountCreatedResponse = resultJSON.getJSONObject(RESPONSE);

    if (!accountCreatedResponse.has(ACCT_ID))
    {
      throw new XacHttpException(ACCOUNT_NUMBER_NOT_EXIST_ERROR_CODE, ACCOUNT_NUMBER_NOT_EXIST_ERROR_MESSAGE);
    }

    return accountCreatedResponse.getString(ACCT_ID);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(XacHttpUtil.class);

  public static JSONObject getMongolBankResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);

    try
    {
      boolean aBoolean = jsonResult.getBoolean(LOAN_NOT_FOUND);

      if (aBoolean)
      {
        return null;
      }
    }
    catch (JSONException e)
    {
      LOGGER.info("MONGOL Bank service successful.");
    }

    Object responseObject = jsonResult.get(RESPONSE);
    if (JSONObject.NULL == responseObject)
    {
      return null;
    }

    return jsonResult.getJSONObject(RESPONSE);
  }

  public static JSONObject getCollateralResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(XAC_CBS_RESPONSE_BODY_NULL_ERROR_CODE, ERR_MSG_RESPONSE_NULL);
    }

    String content = xacClient.read(responseBody);

    JSONObject jsonResult = xacClient.toJson(content);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE))
    {
      JSONObject error = (JSONObject) jsonResult.get(ERROR);

      String errorCode = error.getString(ERROR_CODE);
      String errorDesc = error.getString(ERROR_DESC);

      LOGGER.info("######## Xac server error. errorCode={}, errorDesc={}", errorCode, errorDesc);
      throw new XacHttpException(errorCode, errorDesc);
    }

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(XAC_CBS_SERVICE_RESPONSE_NULL_ERROR_CODE, RESPONSE_NULL);
    }

    try
    {
      return (JSONObject) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(XAC_CBS_SERVICE_RESPONSE_NULL_ERROR_CODE, RESPONSE_NULL);
    }
  }

  public static String getCreatedCollateralResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject validatedJSON = validateNewCoreResponse(environment, xacClient, response);

    JSONObject createCollJSON = validatedJSON.getJSONObject(RESPONSE);

    if (null == createCollJSON)
    {
      throw new XacHttpException("Created collateral response is null!");
    }

    return createCollJSON.getString(RESPONSE_COLLATERAL_ID);
  }

  public static JSONArray getNewCollateralResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException(HTTP_CONNECTION_ERROR_CODE, "Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(XAC_CBS_RESPONSE_BODY_NULL_ERROR_CODE, ERR_MSG_RESPONSE_NULL);
    }

    String content = xacClient.read(responseBody);

    JSONObject jsonResult = xacClient.toJson(content);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE))
    {
      JSONObject error = (JSONObject) jsonResult.get(ERROR);

      String errorCode = error.getString(ERROR_CODE);
      String errorDesc = error.getString(ERROR_DESC);

      LOGGER.info("######## Xac server error. errorCode={}, errorDesc={}", errorCode, errorDesc);
      throw new XacHttpException(errorCode, errorDesc);
    }

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(XAC_CBS_SERVICE_RESPONSE_NULL_ERROR_CODE, RESPONSE_NULL);
    }

    try
    {
      return (JSONArray) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(XAC_CBS_SERVICE_RESPONSE_NULL_ERROR_CODE, RESPONSE_NULL);
    }
  }

  public static JSONObject createCollReferenceCodesBody(List<String> types)
  {
    JSONArray typeArray = new JSONArray();

    for (String type : types)
    {
      JSONObject typeNameJSON = new JSONObject();
      typeNameJSON.put("name", type);

      typeArray.put(typeNameJSON);
    }

    JSONObject requestJSON = new JSONObject();

    JSONObject typeJSON = new JSONObject();
    typeJSON.put("type", typeArray);

    JSONObject typesJSON = new JSONObject();
    typesJSON.put("types", typeJSON);

    requestJSON.put("Request", typesJSON);

    return requestJSON;
  }

  public static JSONArray getCollReferenceCodeArray(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      return (JSONArray) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static JSONObject getResultResponseOfCreateCollateral(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);

    if (jsonResult.has(ERROR_CODE) && jsonResult.get(ERROR_CODE).equals("EXISTS"))
    {
      return null;
    }

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      return (JSONObject) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static JSONObject getCustomerInfoResponse(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {

    JSONObject jsonObject = validateThenReturn(environment, xacHttpClient, response);

    if (null == jsonObject.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      return jsonObject.getJSONObject(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static boolean getCollLinkageResult(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject validatedJSON = validateNewCoreResponse(environment, xacClient, response);

    JSONObject linkageJSON = validatedJSON.getJSONObject(RESPONSE);

    if (linkageJSON.has("coltrlSrlNum"))
    {
      return true;
    }
    return false;
  }

  public static JSONObject getResultResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE)){
      return jsonResult;
    }

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      return jsonResult.getJSONObject(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static JSONObject getCustomerJSONResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateCustomerResponse(environment, xacClient, response);
    JSONArray customersJsonArray = jsonResult.getJSONArray("Customers");

    return customersJsonArray.getJSONObject(0);
  }

  public static Object getResponseFromJSON(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      if (jsonResult.get(XacHttpConstants.RESPONSE) instanceof JSONArray)
      {
        return jsonResult.getJSONArray(RESPONSE);
      }
      return jsonResult.get(RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static JSONObject getResultResponseOfOrganization(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturnOfOrganization(environment, xacClient, response);
    LOGGER.info("Result Response: " + jsonResult);

    if (null == jsonResult.get(XacHttpConstants.RESPONSE))
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }

    try
    {
      return (JSONObject) jsonResult.get(XacHttpConstants.RESPONSE);
    }
    catch (JSONException e)
    {
      throw new XacHttpException(RESPONSE_NULL_CODE, RESPONSE_NULL_MESSAGE);
    }
  }

  public static JSONArray getResultResponseArray(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateThenReturn(environment, xacClient, response);
    JSONArray responseJson = (JSONArray) jsonResult.get(XacHttpConstants.RESPONSE);

    if (null == responseJson)
    {
      throw new XacHttpException(RESPONSE_NULL);
    }
    return responseJson;
  }

  public static JSONArray getCollateralsCodeArray(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateCollateralJsonThenReturn(environment, xacClient, response);
    JSONArray responseJson = (JSONArray) jsonResult.get(XacHttpConstants.RESPONSE);

    if (null == responseJson)
    {
      throw new XacHttpException(RESPONSE_NULL);
    }

    return responseJson;
  }

  public static Map<String, Object> toMachineryCollateralMap(JSONObject resultResponse)
  {
    Map<String, Object> colInfoMap = new HashMap<>();

    JSONObject coltrlGenInfo = (JSONObject) resultResponse.get("ColtrlGenInfo");
    Object collateralCode = getStringValue(coltrlGenInfo.get(COLTRL_CODE_REQUEST));
    JSONObject immovPropInfo = (JSONObject) resultResponse.get("MachineryInfo");
    JSONObject collValueBeforeLoan = (JSONObject) immovPropInfo.get("ColtrlAmt");
    Object amountValue = getStringValue(collValueBeforeLoan.get("amountValue"));
    Object deductionRate = coltrlGenInfo.has(MARGIN_PCNT_REQUEST) ? getStringValue(coltrlGenInfo.get(MARGIN_PCNT_REQUEST)) : "";

    JSONObject machineryInfo = (JSONObject) resultResponse.get("MachineryInfo");
    Object formNum = getStringValue(machineryInfo.get(MACHINERY_FORM_NUM));
    Object manufacturer = getStringValue(machineryInfo.get(MACHINERY_MANUFACTURER));
    Object machineNumber = getStringValue(machineryInfo.get(MACHINERY_MACHINE_NUMBER));
    Object machineModel = getStringValue(machineryInfo.get(MACHINERY_MACHINE_MODEL));
    Object reviewDate = machineryInfo.has(XAC_REVIEW_DATE) ? getStringValue(machineryInfo.get(XAC_REVIEW_DATE)) : "";
    Object manufacturerYear = machineryInfo.has(MACHINERY_MANUFACTURER_YEAR) ? getStringValue(machineryInfo.get(MACHINERY_MANUFACTURER_YEAR)) : "";
    Object customerId = getStringValue(machineryInfo.get(XAC_CUSTOMER_ID));
    Object remarks = getStringValue(machineryInfo.get(XAC_RMKS));

    JSONArray genColtrlValueRec = (JSONArray) machineryInfo.get("GenColtrlValueRec");
    JSONObject machineryColValueRec = (JSONObject) genColtrlValueRec.get(0);
    JSONObject deriveValue = (JSONObject) machineryColValueRec.get("DeriveValue");
    Object collateralAssessment = getStringValue(deriveValue.get("amountValue"));

    JSONArray inspectionInfoArray = (JSONArray) resultResponse.get("InspectionInfoRec");
    JSONObject inspectionInfo = getLatestInspectionInfoJson(inspectionInfoArray, "InspSrlNum");

    JSONArray ownershipInfoArray = (JSONArray) resultResponse.get("OwnershipInfoRec");
    JSONObject ownershipInfo = getLatestOwnershipInfoJson(ownershipInfoArray);

    colInfoMap.put(COLLATERAL_CODE_FORM_FIELD_ID, collateralCode);
    colInfoMap.put(AMOUNT_OF_COLLATERAL, amountValue);
    colInfoMap.put(DEDUCTION_RATE, deductionRate);

    colInfoMap.put(FORM_NUMBER, formNum);
    colInfoMap.put(MANUFACTURER, manufacturer);
    colInfoMap.put(MACHINE_NUMBER, machineNumber);
    colInfoMap.put(MACHINE_MODEL, machineModel);
    colInfoMap.put(REVIEW_DATE, reviewDate);
    colInfoMap.put(MANUFACTURER_YEAR, manufacturerYear);
    colInfoMap.put(CIF_NUMBER, customerId);
    colInfoMap.put(REMARKS, remarks);
    colInfoMap.put(COLLATERAL_ASSESSMENT, collateralAssessment);

    setColInspectionInfo(inspectionInfo, colInfoMap);
    setColOwnershipInfo(ownershipInfo, colInfoMap);

    return colInfoMap;
  }

  public static Map<String, Object> toOtherCollateralMap(JSONObject resultResponse)
  {
    Map<String, Object> colInfoMap = new HashMap<>();
    JSONObject coltrlGenInfo = (JSONObject) resultResponse.get("ColtrlGenInfo");
    Object collateralCode = coltrlGenInfo.get(COLTRL_CODE_REQUEST);
    JSONObject immovPropInfo = (JSONObject) resultResponse.get("OthersInfo");
    JSONObject collValueBeforeLoan = (JSONObject) immovPropInfo.get("ColtrlAmt");
    Object collateralAmount = getStringValue(collValueBeforeLoan.get("amountValue"));
    Object deductionRate = coltrlGenInfo.has(MARGIN_PCNT_REQUEST) ? coltrlGenInfo.get(MARGIN_PCNT_REQUEST) : "";

    JSONObject othersInfo = (JSONObject) resultResponse.get(OTHERS_INFO);
    Object formNumber = String.valueOf(othersInfo.get(OTHERS_FORM_NUMBER)).equals("null") ? "" : othersInfo.get(OTHERS_FORM_NUMBER);
    Object cifNumber = String.valueOf(othersInfo.get(XAC_CUSTOMER_ID)).equals("null") ? "" : othersInfo.get(XAC_CUSTOMER_ID);

    Object remarks = String.valueOf(othersInfo.get(XAC_RMKS)).equals("null") ? "" : othersInfo.get(XAC_RMKS);
    Object reviewDate = String.valueOf(othersInfo.get(XAC_REVIEW_DATE)).equals("null") ? "" : othersInfo.get(XAC_REVIEW_DATE);
    Object receivedDate = String.valueOf(othersInfo.get(OTHERS_RECEIVED_DATE)).equals("null") ? "" : othersInfo.get(OTHERS_RECEIVED_DATE);

    JSONObject collateralValueBeforeLien = (JSONObject) othersInfo.get(OTHER_COL_VALUE_BEFORE_LIEN);
    Object collateralAssessment = String.valueOf(collateralValueBeforeLien.get(OTHER_AMOUNT_VALUE)).equals("null") ?
        "" :
        collateralValueBeforeLien.get(OTHER_AMOUNT_VALUE);

    JSONArray inspectionInfoArray = (JSONArray) resultResponse.get("InspectionInfoRec");
    JSONObject inspectionInfo = getLatestInspectionInfoJson(inspectionInfoArray, "InspSrlNum");

    JSONArray ownershipInfoArray = (JSONArray) resultResponse.get("ownershipInfoRec");
    JSONObject ownershipInfo = getLatestOwnershipInfoJson(ownershipInfoArray);

    colInfoMap.put(COLLATERAL_CODE_FORM_FIELD_ID, collateralCode);
    colInfoMap.put(AMOUNT_OF_COLLATERAL, collateralAmount);
    colInfoMap.put(DEDUCTION_RATE, deductionRate);
    colInfoMap.put(FORM_NUMBER, formNumber);
    colInfoMap.put(CUSTOMER_CIF, cifNumber);
    colInfoMap.put(REMARKS, remarks);
    colInfoMap.put(REVIEW_DATE, reviewDate);
    colInfoMap.put(RECEIVED_DATE, receivedDate);
    colInfoMap.put(COLLATERAL_ASSESSMENT, collateralAssessment);

    setColInspectionInfo(inspectionInfo, colInfoMap);

    setColOwnershipInfo(ownershipInfo, colInfoMap);

    return colInfoMap;
  }

  public static Map<String, Object> toImmovablePropertyCollateralMap(JSONObject resultResponse)
  {
    Map<String, Object> colInfoMap = new HashMap<>();

    JSONObject coltrlGenInfo = (JSONObject) resultResponse.get("ColtrlGenInfo");
    Object collateralCode = coltrlGenInfo.get(COLTRL_CODE_REQUEST);
    JSONObject immovPropInfo = (JSONObject) resultResponse.get("ImmovPropInfo");
    JSONObject collValueBeforeLoan = (JSONObject) immovPropInfo.get("ColtrlAmt");
    Object collateralAmount = collValueBeforeLoan.get("amountValue");
    Object deductionRate = coltrlGenInfo.has(MARGIN_PCNT_REQUEST) ? coltrlGenInfo.get(MARGIN_PCNT_REQUEST) : "";

    JSONObject immovPropContactInfo = (JSONObject) immovPropInfo.get("ImmovPropContactInfo");
    JSONObject postAddr = (JSONObject) immovPropContactInfo.get("PostAddr");

    Object purposeOfUsage = getStringValue(immovPropInfo.get(PURPOSE_OF_USAGE));
    Object formNumber = getStringValue(immovPropInfo.get("formNumber"));
    Object propertyId = getStringValue(immovPropInfo.get("PropertyId"));
    Object leasedInd = getStringValue(immovPropInfo.get("LeasedInd"));
    Object propDocNum = getStringValue(immovPropInfo.get("PropertyDocumentNum"));
    Object builtArea = getStringValue(immovPropInfo.get("BuiltArea"));
    Object landArea = getStringValue(immovPropInfo.get("LandArea"));
    Object builderName = getStringValue(postAddr.get("Addr2"));
    Object reviewDate = immovPropInfo.has(XAC_REVIEW_DATE) ? immovPropInfo.get(XAC_REVIEW_DATE) : "";
    Object dueDate = getStringValue(immovPropInfo.get("DueDt"));
    Object yearOfConstruction = getStringValue(immovPropInfo.get("YearOfConstruction"));
    Object conditionOfContract = getStringValue(immovPropInfo.get("AgeOfBldgYears"));
    Object immovableNumber = getStringValue(immovPropInfo.get("BuilderName"));

    Object address1 = getStringValue(postAddr.get("Addr3"));
    Object address2 = getStringValue(postAddr.get("Addr1"));
    Object city = getStringValue(postAddr.get("City"));

    JSONObject propertyAddress = (JSONObject) immovPropInfo.get("PropertyAddress");
    Object streetNumber = getStringValue(propertyAddress.get("streetNum"));
    Object streetName = getStringValue(propertyAddress.get("streetName"));

    Object cifNumber = getStringValue(immovPropInfo.get(XAC_CUSTOMER_ID));
    Object remarks = getStringValue(immovPropInfo.get(XAC_REMARKS));

    JSONArray genColtrlValueRec = (JSONArray) immovPropInfo.get("GenColtrlValueRec");
    JSONObject deriveAmtValue = (JSONObject) ((JSONObject) genColtrlValueRec.get(0)).get("DeriveValue");
    Object collateralAssessment = getStringValue(deriveAmtValue.get("amountValue"));

    JSONArray inspectionInfoArray = (JSONArray) resultResponse.get("InspectionInfoRec");
    JSONObject inspectionInfo = getLatestInspectionInfoJson(inspectionInfoArray, INSP_SERIAL_NUM_SERVICE);

    JSONObject ownershipInfoRec = getLatestOwnershipInfoJson((JSONArray) resultResponse.get("ownershipInfoRec"));

    colInfoMap.put(COLLATERAL_CODE_FORM_FIELD_ID, collateralCode);
    colInfoMap.put(DEDUCTION_RATE, deductionRate);

    String houseNumberString = getStringValue(immovPropInfo.get(HOUSE_NUMBER));
    String houseNumber = EMPTY_VALUE;
    String address3 = EMPTY_VALUE;
    if (houseNumberString != null && !houseNumberString.equals(NULL_STRING) && !StringUtils.isBlank(houseNumberString))
    {
      if (houseNumberString.contains("/"))
      {
        String[] splitValues = getStringValue(immovPropInfo.get(HOUSE_NUMBER)).split("/");
        houseNumber = splitValues[0];
        address3 = splitValues[1];
      }
      else
      {
        houseNumber = houseNumberString;
      }
    }

    colInfoMap.put(CollateralConstants.HOUSE_NUMBER, houseNumber);
    colInfoMap.put(CollateralConstants.PURPOSE_OF_USAGE, purposeOfUsage);
    colInfoMap.put(CollateralConstants.FORM_NUMBER, formNumber);
    colInfoMap.put(CollateralConstants.PROPERTY_ID, propertyId);
    colInfoMap.put(CollateralConstants.LEASED_IND, leasedInd);
    colInfoMap.put(CollateralConstants.PROPERTY_DOC_NUMBER, propDocNum);
    colInfoMap.put(CollateralConstants.BUILT_AREA, builtArea);
    colInfoMap.put(CollateralConstants.LAND_AREA, landArea);
    colInfoMap.put(CollateralConstants.BUILDER_NAME, builderName);
    colInfoMap.put(CollateralConstants.REVIEW_DATE, reviewDate);
    colInfoMap.put(CollateralConstants.DUE_DATE, dueDate);
    colInfoMap.put(CollateralConstants.YEAR_OF_CONSTRUCTION, yearOfConstruction);
    colInfoMap.put(CollateralConstants.CONDITION_OF_CONTRACT, conditionOfContract);
    colInfoMap.put(CollateralConstants.ADDRESS_1, address1);
    colInfoMap.put(CollateralConstants.ADDRESS_2, address2);
    colInfoMap.put(CollateralConstants.ADDRESS_3, address3);
    colInfoMap.put(CollateralConstants.CITY, city);
    colInfoMap.put(CollateralConstants.STREET_NUMBER, streetNumber);
    colInfoMap.put(CollateralConstants.STREET_NAME, streetName);
    colInfoMap.put(CollateralConstants.CIF, cifNumber);
    colInfoMap.put(CollateralConstants.REMARKS, remarks);
    colInfoMap.put(CollateralConstants.AMOUNT_OF_COLLATERAL, collateralAmount);
    colInfoMap.put(COLLATERAL_ASSESSMENT, collateralAssessment);
    colInfoMap.put(IMMOVABLE_NUMBER, immovableNumber);

    setColInspectionInfo(inspectionInfo, colInfoMap);
    setColOwnershipInfo(ownershipInfoRec, colInfoMap);

    return colInfoMap;
  }

  public static Map<String, Object> toVehicleCollMap(JSONObject resultResponse)
  {
    Map<String, Object> vehicleColInfoMap = new HashMap<>();

    JSONObject coltrlGenInfo = (JSONObject) resultResponse.get("ColtrlGenInfo");
    Object collateralCode = getStringValue(coltrlGenInfo.get(COLTRL_CODE_REQUEST));
    JSONObject immovPropInfo = (JSONObject) resultResponse.get("VehiclesInfo");
    JSONObject collValueBeforeLoan = (JSONObject) immovPropInfo.get("ColtrlAmt");
    Object amountValue = collValueBeforeLoan.get("amountValue");
    Object deductionRate = coltrlGenInfo.has(MARGIN_PCNT_REQUEST) ? getStringValue(coltrlGenInfo.get(MARGIN_PCNT_REQUEST)) : "";

    JSONObject vehicleInfo = (JSONObject) resultResponse.get(VEHICLES_INFO);
    Object vehicleRegisterNumber = getStringValue(vehicleInfo.get("RegNum"));
    Object formNumber = getStringValue(vehicleInfo.get("FormNum"));
    Object chassisNumber = getStringValue(vehicleInfo.get("ChassisNum"));
    Object engineNumber = getStringValue(vehicleInfo.get("EngineNum"));
    Object manufactureYear = getStringValue(vehicleInfo.get("ManufactureYear"));
    Object vehicleModel = getStringValue(vehicleInfo.get("VehicleModel"));
    Object financialLeasingSupplier = getStringValue(vehicleInfo.get("NatureOfCharge"));
    Object purposeOfUsage = vehicleInfo.has("WithdrawReasonCode") ? getStringValue(vehicleInfo.get("WithdrawReasonCode")) : "";
    JSONObject vehicleAddress = (JSONObject) vehicleInfo.get("VehiclesAddr");
    Object address1 = getStringValue(vehicleAddress.get("address1"));
    Object address2 = getStringValue(vehicleAddress.get("address2"));
    Object address3 = getStringValue(vehicleAddress.get("address3"));

    Object remarks = getStringValue(vehicleInfo.get("Rmks"));
    Object cifNumber = getStringValue(vehicleInfo.get(CUST_ID));

    JSONArray genCollateralValueArr = (JSONArray) vehicleInfo.get("GenColtrlValueRec");
    JSONObject collateralValue = (JSONObject) genCollateralValueArr.get(0);
    JSONObject driveValue = (JSONObject) collateralValue.get("DeriveValue");
    Object collateralAssessment = getStringValue(driveValue.get("amountValue"));

    Object reviewDate = vehicleInfo.has(XAC_REVIEW_DATE) ? getStringValue(vehicleInfo.get(XAC_REVIEW_DATE)) : "";

    JSONArray inspectionInfoArray = (JSONArray) resultResponse.get("InspectionInfoRec");
    JSONObject inspectionInfo = getLatestInspectionInfoJson(inspectionInfoArray, "InspSrlNum");

    JSONArray ownershipInfoArray = (JSONArray) resultResponse.get("OwnershipInfoRec");
    JSONObject ownershipInfo = getLatestOwnershipInfoJson(ownershipInfoArray);

    vehicleColInfoMap.put(COLLATERAL_CODE_FORM_FIELD_ID, collateralCode);
    vehicleColInfoMap.put(AMOUNT_OF_COLLATERAL, amountValue);
    vehicleColInfoMap.put(DEDUCTION_RATE, deductionRate);

    vehicleColInfoMap.put(VEHICLE_REGISTER_NUMBER, vehicleRegisterNumber);
    vehicleColInfoMap.put(FORM_NUMBER, formNumber);
    vehicleColInfoMap.put(PURPOSE_OF_USAGE_FIELD_ID, purposeOfUsage);
    vehicleColInfoMap.put(CHASSIS_NUMBER, chassisNumber);
    vehicleColInfoMap.put(ENGINE_NUMBER, engineNumber);
    vehicleColInfoMap.put(MANUFACTURE_YEAR, manufactureYear);
    vehicleColInfoMap.put(VEHICLE_MODEL, vehicleModel);
    vehicleColInfoMap.put(FINANCIAL_LEASING_SUPPLIER, financialLeasingSupplier);
    vehicleColInfoMap.put(REMARKS, remarks);
    vehicleColInfoMap.put(ADDRESS_1, address1);
    vehicleColInfoMap.put(ADDRESS_2, address2);
    vehicleColInfoMap.put(ADDRESS_3, address3);
    vehicleColInfoMap.put(CIF_NUMBER, cifNumber);
    vehicleColInfoMap.put(REVIEW_DATE, reviewDate);
    vehicleColInfoMap.put(COLLATERAL_ASSESSMENT, collateralAssessment);
    vehicleColInfoMap.put(DEDUCTION_RATE, deductionRate);

    setColInspectionInfo(inspectionInfo, vehicleColInfoMap);
    setColOwnershipInfo(ownershipInfo, vehicleColInfoMap);

    return vehicleColInfoMap;
  }

  //Direct Online Salary Loan
  public static JSONObject getSalaryOrgInfoResponse(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateSalaryOrgResponse(environment, xacHttpClient, response);

    JSONObject jsonResponse = jsonResult.has(RESPONSE) ? jsonResult.getJSONObject(RESPONSE) : jsonResult.getJSONObject(ERROR);
    if (jsonResponse.has(ERROR_CODE))
    {
      String errorCode = (String) jsonResponse.get(ERROR_CODE);
      String errorDescription = (String) jsonResponse.get(ERROR_DESC);
      if (errorCode.equalsIgnoreCase(LOAN_NOT_FOUND))
      {
        return null;
      }
      else
      {
        LOGGER.info("######## Xac server error. errorCode={}, errorDesc={}", errorCode, errorDescription);
        throw new XacHttpException(errorCode, errorDescription);
      }
    }
    else
    {
      return jsonResponse;
    }
  }

  public static boolean checkResponseStatus(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    JSONObject jsonResult = xacHttpClient.toJson(xacHttpClient.read(responseBody));

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE))
    {
      JSONObject errorObject = jsonResult.getJSONObject(ERROR);

      String errorCode = errorObject.getString(ERROR_CODE);
      String errorMessage = errorObject.getString(ERROR_MESSAGE);

      throw new XacHttpException(errorCode, errorMessage);
    }

    return true;
  }

  private static void setColOwnershipInfo(JSONObject ownershipInfo, Map<String, Object> collateralMap)
  {
    Object ownershipType = getStringValue(ownershipInfo.get(OWNER_TYPE_REQUEST));
    Object ownerCifNumber = getStringValue(ownershipInfo.get(OWNER_CIF_REQUEST));
    Object ownerName = getStringValue(ownershipInfo.get(OWNER_NAME_REQUEST));
    Object serialNumber = getStringValue(ownershipInfo.get(SERIAL_NUMBER_SERVICE));

    collateralMap.put(OWNERSHIP_TYPE, ownershipType);
    collateralMap.put(OWNER_CIF_NUMBER, ownerCifNumber);
    collateralMap.put(OWNER_NAME, ownerName);
    collateralMap.put(SERIAL_NUMBER, serialNumber);
  }

  private static void setColInspectionInfo(JSONObject inspectionInfo, Map<String, Object> collateralInfoMap)
  {
    Object inspectionType = getStringValue(inspectionInfo.get("InspType"));
    Object inspectorId = getStringValue(inspectionInfo.get("InspEmpId"));
    JSONObject inspAmt = (JSONObject) inspectionInfo.get("InspAmt");
    Object inspectionAmountValue = getStringValue(inspAmt.get("amountValue"));
    Object inspectionDate = getStringValue(inspectionInfo.get("VisitDt"));
    Object inspSerialNumber = getStringValue(inspectionInfo.get(INSP_SERIAL_NUM_SERVICE));

    collateralInfoMap.put("inspectionType", inspectionType);
    collateralInfoMap.put("inspectorId", inspectorId);
    collateralInfoMap.put("inspectionAmountValue", inspectionAmountValue);
    collateralInfoMap.put("inspectionDate", inspectionDate);
    collateralInfoMap.put(INSPECTION_SERIAL_NUMBER, inspSerialNumber);
  }

  private static JSONObject getLatestInspectionInfoJson(JSONArray inspectionInfoArray, String key)
  {
    JSONObject inspectionInfo = new JSONObject();
    int maxSerialNum = 0;
    for (Object inspectionJsonObject : inspectionInfoArray)
    {
      JSONObject inspectionJson = (JSONObject) inspectionJsonObject;
      String serialNumString = String.valueOf(inspectionJson.get(key));
      serialNumString = serialNumString.replaceAll("\\s+", "");
      int serialNum = Integer.parseInt(serialNumString);
      if (serialNum > maxSerialNum)
      {
        maxSerialNum = serialNum;
        inspectionInfo = inspectionJson;
      }
    }

    return inspectionInfo;
  }

  private static JSONObject getLatestOwnershipInfoJson(JSONArray ownershipInfoArray)
  {
    JSONObject ownershipInfo = new JSONObject();
    int maxSerialNum = 0;
    for (Object ownershipJsonObject : ownershipInfoArray)
    {
      JSONObject ownershipJson = (JSONObject) ownershipJsonObject;
      String serialNumString = String.valueOf(ownershipJson.get("SerialNum"));
      serialNumString = serialNumString.replaceAll("\\s+", "");
      int serialNum = Integer.parseInt(serialNumString);
      if (serialNum > maxSerialNum)
      {
        maxSerialNum = serialNum;
        ownershipInfo = ownershipJson;
      }
    }

    return ownershipInfo;
  }

  private static JSONObject validateThenReturn(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    JSONObject jsonResult = xacClient.toJson(xacClient.read(responseBody));

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(SUCCESS))
    {
      return jsonResult;
    }

    JSONObject error;

    if (jsonResult.get(ERROR) instanceof JSONArray)
    {
      JSONArray errorJSONArray = (JSONArray) jsonResult.get(ERROR);
      error = errorJSONArray.getJSONObject(0);
    }
    else
    {
      error = (JSONObject) jsonResult.get(ERROR);
    }

    String errorDesc = error.has(ERROR_DESC) ? error.getString(ERROR_DESC) : error.getString(ERROR_MESSAGE);
    String errorCodeDescription = "";

    if (error.getString(ERROR_CODE).equals(environment.getProperty("get.accLien.errorCode"))){

      JSONObject jsonObject = new JSONObject();

      jsonObject.put(ERROR_CODE, GET_ACC_LIEN_ERROR_CODE);

      jsonObject.put(ERROR_MESSAGE, errorDesc);

      jsonObject.put(RESPONSE_STATUS, FAILURE);

      return jsonObject;
    }
    if (error.getString(ERROR_CODE).equals("EXISTS"))
    {
      return error;
    }

    if (errorDesc.contains("[citizen]"))
    {
      errorCodeDescription = "citizen";
    }
    if (errorDesc.contains("[operator]"))
    {
      errorCodeDescription = "operator";
    }
    String errorCode = error.getString(ERROR_CODE) + errorCodeDescription;

    if (LOAN_NOT_FOUND.equalsIgnoreCase(errorCode))
    {
      JSONObject jsonObject = new JSONObject();

      jsonObject.put(LOAN_NOT_FOUND, true);

      return jsonObject;
    }

    LOGGER.info("######## Xac server error. errorCode={}, errorDesc={}", errorCode, errorDesc);
    throw new XacHttpException(errorCode, errorDesc);
  }

  private static JSONObject validateNewCoreResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException(HTTP_CONNECTION_ERROR_CODE, "Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    String content = xacClient.read(responseBody);
    JSONObject jsonResult = xacClient.toJson(content);
    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE))
    {
      try
      {
        JSONObject error = jsonResult.getJSONObject(ERROR);

        if (null != error)
        {
          String errorCode = error.getString(ERROR_CODE);
          String errorDescription = error.getString(ERROR_MESSAGE);

          throw new XacHttpException(errorCode, errorDescription);
        }
      }
      catch (JSONException e)
      {
        LOGGER.info(ERROR_NOT_FOUND_MESSAGE);
      }
    }

    return jsonResult;
  }

  private static JSONObject validateCustomerResponse(Environment environment, XacHttpClient xacClient, Response response)
      throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    String content = xacClient.read(responseBody);
    JSONObject jsonResult = xacClient.toJson(content);

    String status = jsonResult.getString("Status");

    if (status.equalsIgnoreCase("FAILURE"))
    {
      JSONObject error = jsonResult.getJSONObject(ERROR);
      String errorMessage = error.getString("ErrorMessage");

      if (errorMessage == null)
      {
        String errorCode = error.getString(ERROR_CODE);
        String errorDescription = error.getString("ErrorDescription");

        throw new XacHttpException(errorCode, errorDescription);
      }
      else
      {
        String errorCode = error.getString(ERROR_CODE);
        throw new XacHttpException(errorCode, errorMessage);
      }
    }

    JSONObject responseJSON = jsonResult.getJSONObject("Response");

    JSONArray customerJsonArray = responseJSON.getJSONArray("Customers");

    if (customerJsonArray.length() == 0)
    {
      throw new XacHttpException("Customer info is empty!");
    }

    return responseJSON;
  }

  private static JSONObject validateThenReturnOfOrganization(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (null == responseBody)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    String content = xacClient.read(responseBody);

    JSONObject jsonResult = xacClient.toJson(content);

    String responseStatus = jsonResult.getString(RESPONSE_STATUS);

    if (responseStatus.equals(FAILURE))
    {
      JSONObject error = (JSONObject) jsonResult.get(ERROR);

      String errorDesc = error.getString(ERROR_DESC);
      String errorCodeDescription = "";
      if (errorDesc.contains("[citizen]"))
      {
        errorCodeDescription = "citizen";
      }
      if (errorDesc.contains("[operator]"))
      {
        errorCodeDescription = "operator";
      }
      String errorCode = error.getString(ERROR_CODE) + errorCodeDescription;
      LOGGER.info("######## Xac server error. errorCode={}, errorDesc={}", errorCode, errorDesc);
      throw new XacHttpException(errorCode, errorDesc);
    }

    try
    {
      JSONObject error = jsonResult.getJSONObject(ERROR);

      if (null != error)
      {
        String errorDescription = error.getString(ERROR_DESC);
        throw new XacHttpException(errorDescription);
      }
    }
    catch (JSONException e)
    {
      LOGGER.info("Error not found, request response is successful.");
    }

    return jsonResult;
  }

  private static JSONObject validateCollateralJsonThenReturn(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (response == null)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }

    ResponseBody responseBody = response.body();

    if (responseBody == null)
    {
      throw new XacHttpException(RESPONSE_BODY_NULL_CODE, RESPONSE_BODY_NULL_MESSAGE);
    }

    String content = xacClient.read(responseBody);
    JSONObject jsonResult = xacClient.toJson(content);

    String status = jsonResult.getString("Status");

    if (status.equalsIgnoreCase("FAILURE"))
    {
      JSONObject error = jsonResult.getJSONObject("Error");
      String errorMessage = error.getString("ErrorMessage");

      String errorCode = error.getString(ERROR_CODE);
      throw new XacHttpException("", errorCode + ":" + errorMessage);
    }

    return jsonResult;
  }

  public static String getWso2EndPoint(Environment environment)
  {
    return environment.getProperty(XacConstants.NEW_CORE_ENDPOINT);
  }

  public static String getNewCoreEndPoint(Environment environment)
  {
    return environment.getProperty(XacConstants.NEW_CORE_ENDPOINT);
  }

  public static String getPropertyByKey(Environment environment, String key)
  {
    return environment.getProperty(key);
  }

  public static String getFunctionByProduct(Environment environment, String productId)
  {
    if (StringUtils.equals(productId, environment.getProperty(EMPLOYEE_LOAN_PRODUCT_ID)))
    {
      return environment.getProperty(PUBLISHER_REPORT_ABSOLUTE_PATH_EMPLOYEE_LOAN);
    }
    else
    {
      return environment.getProperty(PUBLISHER_REPORT_ABSOLUTE_PATH);
    }
  }

  public static String getHeaderSource(Environment environment)
  {
    return environment.getProperty(XacConstants.WSO2_HEADER_SOURCE);
  }

  public static String getHeaderSource(Environment environment, String path)
  {
    return environment.getProperty(path);
  }

  public static String getNewCoreHeaderSource(Environment environment)
  {
    return environment.getProperty(XacConstants.NEW_CORE_HEADER_SOURCE);
  }

  public static String getOnlineLeasingHeaderSource(Environment environment)
  {
    return environment.getProperty(XacConstants.NEW_CORE_ONLINE_LEASING_HEADER_SOURCE);
  }

  public static String getHeaderFunction(Environment environment, String prefix)
  {
    return environment.getProperty(prefix + ".function");
  }

  public static String getHeaderRequestType(Environment environment, String prefix)
  {
    return environment.getProperty(prefix + ".requestType");
  }

  public static String getTimeStamp()
  {
    return new SimpleDateFormat(XacHttpConstants.DATE_FORMATTER).format(new Date());
  }

  public static String generateSecurityCode(String source, String function)
  {
    String value = source + function;
    return DigestUtils.md5Hex(value).toUpperCase();
  }

  public static String generateRequestId()
  {
    String uuidFirst = UUID.randomUUID().toString().replace("-", "").substring(1, 5);
    String uuidSecond = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    String datePart = DateTime.now().toString("yyMMddHH-mmss-SSSS");

    return "" + datePart + "-" + uuidFirst + "-" + uuidSecond;
  }

  public static Map convertJsonStringToMap(String jsonString)
  {
    return new Gson().fromJson(jsonString, HashMap.class);
  }

  private static JSONObject validateSalaryOrgResponse(Environment environment, XacHttpClient xacClient, Response response) throws XacHttpException
  {
    if (null == response)
    {
      throw new XacHttpException("Connection to '" + getWso2EndPoint(environment) + "' failed!");
    }
    ResponseBody responseBody = response.body();
    if (null == responseBody)
    {
      String errorCode = "CBS001";
      throw new XacHttpException(errorCode, ERR_MSG_RESPONSE_NULL);
    }
    String content = xacClient.read(responseBody);
    return xacClient.toJson(content);
  }

  public static JSONObject getResponse(Environment environment, XacHttpClient xacHttpClient, Response response) throws XacHttpException
  {
    JSONObject jsonResult = validateResponse(environment, xacHttpClient, response);

    if (null == jsonResult.get(RESPONSE))
    {
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, RESPONSE_NULL_MESSAGE);
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
      throw new XacHttpException(RESPONSE_IS_NULL_ERROR_CODE, ERR_MSG_RESPONSE_NULL);
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
      throw new XacHttpException(RESPONSE_BODY_IS_NULL_ERROR_CODE, ERR_MSG_RESPONSE_NULL);
    }

    String content = xacClient.read(responseBody);
    JSONObject jsonResult = xacClient.toJson(content);

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

  public static Map<String, Object> toLoanPayment(JSONObject responseObject)
  {
    Map<String, Object> loanPaymentInfo = new HashMap<>();
    loanPaymentInfo.put("status", getStringValueFrmJSONObject(responseObject, "Status"));
    loanPaymentInfo.put("transactionIdScheduled", getStringValueFrmJSONObject(responseObject, "TranId1"));
    loanPaymentInfo.put("transactionIdUnscheduled", getStringValueFrmJSONObject(responseObject, "TranId2"));
    loanPaymentInfo.put("appnum", getStringValueFrmJSONObject(responseObject, "Appnum"));
    loanPaymentInfo.put("trnDate1", getStringValueFrmJSONObject(responseObject, "TranDt1"));
    loanPaymentInfo.put("trnDate2", getStringValueFrmJSONObject(responseObject, "TranDt2"));
    return loanPaymentInfo;
  }

  public static Map<String, Object> toScheduledLoanPayment(JSONObject responseObject)
  {
    Map<String, Object> loanPaymentInfo = new HashMap<>();
    loanPaymentInfo.put("transactionId", responseObject.get("TrnId"));
    loanPaymentInfo.put("transactionDate", responseObject.get("TrnDt"));
    return loanPaymentInfo;
  }
}
