package mn.erin.domain.bpm.util.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.Environment;

import mn.erin.alfresco.connector.service.download.AlfrescoRemoteDownloadService;
import mn.erin.alfresco.connector.service.download.DownloadService;
import mn.erin.alfresco.connector.service.download.DownloadServiceException;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_DESCRIPTION_ERROR_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CHO_DEF_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleDataTypeConstants.BIG_DECIMAL;

/**
 * @author Tamir
 */
public class BpmUtils {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private BpmUtils() {

  }

  public static String convertClobToString(Clob clob) throws SQLException {
    return clob.getSubString(1, (int) clob.length());
  }

  public static Date convertStringToDate(String format, String dateString) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.parse(dateString);
  }

  public static String convertMapToJsonString(Map<String, Object> map) throws JsonProcessingException {
    return objectMapper.writeValueAsString(map);
  }

  public static Map convertJsonStringToMap(String jsonString) {
    if (StringUtils.isBlank(jsonString)) {
      return Collections.emptyMap();
    }
    return new Gson().fromJson(jsonString, HashMap.class);
  }

  public static String getStringValueFromJson(JSONObject jsonObject, String key) {
    if (jsonObject.has(key)
        && (jsonObject.get(key) != null && !String.valueOf(jsonObject.get(key)).equalsIgnoreCase(NULL_STRING))) {
      return String.valueOf(jsonObject.get(key));
    }
    return EMPTY_VALUE;
  }

  public static String getFirstValueByDelimiter(String value, String delimiter, String errorMessage)
      throws BpmServiceException {
    if (StringUtils.isEmpty(value)) {
      throw new BpmServiceException(INVALID_DESCRIPTION_ERROR_CODE, errorMessage);
    }

    String[] splitValue = value.split(delimiter);

    if (splitValue.length < 1) {
      throw new BpmServiceException(INVALID_DESCRIPTION_ERROR_CODE, errorMessage);
    }

    return splitValue[0].replaceAll("\\s+", "");
  }

  public static String getSecondValueByDelimiter(String value, String delimiter, String errorMessage)
      throws BpmServiceException {
    if (StringUtils.isEmpty(value)) {
      throw new BpmServiceException(INVALID_DESCRIPTION_ERROR_CODE, errorMessage);
    }

    String[] splitValue = value.split(delimiter);

    if (splitValue.length < 1) {
      throw new BpmServiceException(INVALID_DESCRIPTION_ERROR_CODE, errorMessage);
    }

    return splitValue[1].replaceAll("\\s+", "");
  }

  public static String getReadOnlyFieldValue(Map<String, Object> variables, String fieldId) {
    if (variables.containsKey(fieldId)) {
      return String.valueOf(variables.get(fieldId));
    }
    return null;
  }

  public static String getStringValue(Object objectValue) {
    if (null == objectValue || objectValue.toString().equals(NULL_STRING)) {
      return "";
    }
    return objectValue.toString();
  }

  public static Map<String, Object> convertVariableListToMap(List<Variable> variables) {
    Map<String, Object> variableMap = new HashMap<>();
    for (Variable variable : variables) {
      variableMap.put(variable.getId().getId(), variable.getValue());
    }
    return variableMap;
  }

  public static String toString(Object objectValue) {
    if (null == objectValue) {
      return "";
    }
    return String.valueOf(objectValue);
  }

  public static boolean isInt(Object value) {
    return value instanceof Integer;
  }

  public static int toInt(Object objectValue) {
    return Integer.parseInt(String.valueOf(objectValue));
  }

  public static String getValidString(Object value) {
    if (null == value || NULL_STRING.equalsIgnoreCase(String.valueOf(value))) {
      return "";
    }
    return String.valueOf(value);
  }

  public static String convertDateToDateString(Date date, String format) {
    DateFormat formattedDate = new SimpleDateFormat(format);
    return formattedDate.format(date);
  }

  public static String getFormattedDateString(String date, String format) throws ParseException {
    if (!date.equals(EMPTY_VALUE)) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      return dateFormat.format(dateFormat.parse(date));
    }
    return EMPTY_VALUE;
  }

  public static int convertDateStringToInt(String stringDate) {
    return Integer.parseInt(stringDate.replaceAll("[^0-9]", ""));
  }

  public static String convertDoubleToString(String value, int fractionDigit) {
    double doubleValue = Double.parseDouble(value);
    return BpmNumberUtils.getThousandSepStrWithDigit(doubleValue, fractionDigit);
  }

  public static String dateFormatter(Date date, String dateFormat) {
    DateFormat dateFormatter = new SimpleDateFormat(dateFormat);
    return dateFormatter.format(date);
  }

  public static LocalDate stringToDateByFormat(String format, String dateString) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    Date startDate = formatter.parse(dateString);
    return startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static String toUppercaseRegisterNum(String regNum) {
    String letterOfRegNum = regNum.substring(0, 2);
    String numberOfRegNum = regNum.substring(2);
    return letterOfRegNum.toUpperCase() + numberOfRegNum;
  }

  public static boolean checkAccountNumberValue(String accountNumber, String transactionType) {
    return transactionType.equals("Бэлэн бус") && (accountNumber.equals("0") || accountNumber.equals(NULL_STRING));
  }

  public static String getStringValueFrmJSONObject(JSONObject object, String key) {
    return object.has(key) ? String.valueOf(object.get(key)) : EMPTY_VALUE;
  }

  public static String getParameterDataType(Serializable parameter) {
    if (null == parameter) {
      return null;
    }

    if (isJson(parameter)) {
      return "JSON";
    }

    if (parameter instanceof String) {
      return "String";
    } else if (parameter instanceof Integer) {
      return "Integer";
    } else if (parameter instanceof BigDecimal) {
      return BIG_DECIMAL;
    } else if (parameter instanceof Double) {
      return BIG_DECIMAL;
    } else if (parameter instanceof Long) {
      return "Long";
    } else if (parameter instanceof Boolean) {
      return "Boolean";
    } else if (parameter instanceof byte[]) {
      return "byte[]";
    } else if (parameter instanceof Date) {
      return "Date";
    } else {
      return null;
    }
  }

  public static boolean isJson(Serializable parameter) {
    try {
      new JSONObject(parameter.toString());
      return true;
    } catch (Exception e) {
      try {
        new JSONArray(parameter.toString());
        return true;
      } catch (JSONException ex) {
        return false;
      }
    }
  }

  public static List<String> getPdfBase64(List<String> documentIds) throws DownloadServiceException {
    List<String> returnValue = new ArrayList<>();
    int i = 0;
    List<String> base64List = getByteArray(documentIds);
    for (String base64 : base64List) {
      if (base64.charAt(0) == 'J') {
        returnValue.add(documentIds.get(i));
        returnValue.add(base64);
        return returnValue;
      }
      i++;
    }
    return Collections.emptyList();
  }

  public static List<String> getByteArray(List<String> documentIds) throws DownloadServiceException {
    List<String> base64List = new ArrayList<>();
    for (String id : documentIds) {
      String base64 = downloadFromAlfresco(id);
      if (!org.apache.commons.lang3.StringUtils.isBlank(base64)) {
        base64List.add(base64);
      }
    }

    return base64List;
  }

  public static String downloadFromAlfresco(String id) throws DownloadServiceException {
    DownloadService downloadService = new AlfrescoRemoteDownloadService();
    byte[] content = downloadService.download(id);
    return Base64.getEncoder().encodeToString(content);
  }

  public static String getStringValueFromExecution(Map<String, Object> variables, String variableName) {
    String value = String.valueOf(variables.get(variableName));
    return (StringUtils.isBlank(value) || value.equals(NULL_STRING)) ? BLANK : value;
  }

  public static BigDecimal removeCommaAndGetBigDecimal(String stringValue) {
    return new BigDecimal(stringValue.replace(",", ""));
  }

  public static String getDefaultBranch(DefaultParameterRepository defaultParameterRepository, String processType)
      throws UseCaseException {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput(processType, "Default");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParameters.get("Default");

    return String.valueOf(defaultParam.get("defaultBranch"));
  }

  public static String convertByteArrayToBase64(byte[] byteArray) {
    byte[] decodedBytes = Base64.getDecoder().decode(byteArray);
    return new String(decodedBytes);
  }

  public static byte[] convertBase64ToByteArray(String base64String) {
    byte[] name = Base64.getEncoder().encode(base64String.getBytes());
    return Base64.getDecoder().decode(new String(name).getBytes(StandardCharsets.UTF_8));
  }

  public static String getStringValue(JSONObject jsonObject, String key) {
    return jsonObject.has(key) ? String.valueOf(jsonObject.get(key)) : EMPTY_VALUE;
  }

  public static String getStringValue(Map<String, Object> map, String key) {
    return map.containsKey(key) ? String.valueOf(map.get(key)) : EMPTY_VALUE;
  }

  public static BigDecimal getBigDecValue(JSONObject jsonObject, String key) {
    return jsonObject.has(key) ? new BigDecimal(String.valueOf(jsonObject.get(key))) : BigDecimal.ZERO;
  }

  public static String getMnBooleanValue(String booleanValue) {
    return booleanValue.equals("true") ? YES_MN_VALUE : NO_MN_VALUE;
  }

  public static Date getPlusDateDays(Date date, int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, days);
    return calendar.getTime();
  }

  public static BigDecimal toBigDecimal(Object obj) throws BpmServiceException {
    try {
      String bigDecimalString = String.valueOf(obj);
      return new BigDecimal(bigDecimalString);
    } catch (Exception e) {
      throw new BpmServiceException("Object is not in number format", e.getCause());
    }
  }

  public static Map<String, Object> getDefaultParameters(DefaultParameterRepository defaultParameterRepository,
      String processType, String entity)
      throws UseCaseException {
    GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entity);
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    return getGeneralInfo.execute(input);
  }

  public static String getDefaultStringParameter(DefaultParameterRepository defaultParameterRepository,
      String processType, String entityType, String parameterName) throws UseCaseException {
    GetGeneralInfoInput input = new GetGeneralInfoInput(processType, entityType);
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    Map<String, Object> defaultParams = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParams.get(entityType);
    return String.valueOf(defaultParam.get(parameterName));
  }

  public static String getDefaultProcessType(String processType) {
    switch (processType) {
      case ONLINE_SALARY_PROCESS_TYPE:
        return "OnlineSalary";
      case BNPL_PROCESS_TYPE_ID:
        return BNPL_LOAN_DEFAULT_PARAM_PROCESS_TYPE;
      case INSTANT_LOAN_PROCESS_TYPE_ID:
        return INSTANT_LOAN_PROCESS_TYPE_ID;
      case ONLINE_LEASING_PROCESS_TYPE_ID:
        return "OnlineLeasing";
      default:
        return processType;
    }
  }

  public static String getDefaultBranchExceptCho(DefaultParameterRepository defaultParameterRepository,
      Environment environment,
      String processType, String branchNumber) throws UseCaseException {
    String choBranch = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER),
        "Could not get CHO branch number from config file!");
    if (choBranch.equals(branchNumber) || "CHO".equals(branchNumber)) {
      return Objects.requireNonNull(environment.getProperty(INSTANT_LOAN_CHO_DEF_BRANCH));
    }
    return getDefaultBranch(defaultParameterRepository, getDefaultProcessType(processType));
  }

  public static double convertObjectToDouble(Object obj) throws BpmServiceException {
    try {
      String doubleString = String.valueOf(obj);
      return Double.parseDouble(doubleString);
    } catch (Exception e) {
      throw new BpmServiceException("Object is not in number format", e.getCause());
    }
  }
}
