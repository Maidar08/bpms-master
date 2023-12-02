/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.util.PropertyUtil.createRequestBodyForPropertyInfoService;
import static mn.erin.bpm.domain.ohs.xac.util.PropertyUtil.toPropertyInfo;
import static mn.erin.bpm.domain.ohs.xac.util.PropertyUtil.validatePropertyPersonInfo;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderFunction;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderRequestType;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.createRequestBody;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.createRequestBodySalaryInfo;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.createVehicleInfoRequest;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.createVehicleOwnerRequest;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getAddressInfoFromJson;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getCustomerIDCardFromJson;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.getSalaryInfosFromJson;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.toVehicleInfo;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.toVehicleOwners;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_PROPERTY_ID_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROPERTY_INFO_PROPERTY_ID_BLANK_MESSAGE;

/**
 * @author Tamir
 */
public class XacXypCustomerService implements CustomerService
{
  private static final Logger LOG = LoggerFactory.getLogger(XacXypCustomerService.class);

  private final Environment environment;
  private final AuthenticationService authenticationService;

  public XacXypCustomerService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public AddressInfo getCustomerAddress(String regNumber, String employeeRegNumber) throws BpmServiceException
  {
    String endPoint = XacHttpUtil.getWso2EndPoint(environment);
    String source = getHeaderSource(environment);

    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_ADDRESS_INFO);
    String securityCode = generateSecurityCode(source, function);

    LOG.info("######### Xyp ADDRESS INFO SOURCE : [{}]", source);

    LOG.info("######### Xyp ADDRESS INFO FUNCTION : [{}]", function);
    LOG.info("######### Xyp ADDRESS INFO SECURITY_CODE : [{}]", securityCode);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(endPoint, source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_ADDRESS_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = createRequestBody(regNumber, employeeRegNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      return getAddressInfoFromJson(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public Customer getCustomerInfo(String regNumber, String employeeRegNumber) throws BpmServiceException
  {
    String endPoint = XacHttpUtil.getWso2EndPoint(environment);
    String source = getHeaderSource(environment);

    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_ID_CARD_INFO);
    String securityCode = generateSecurityCode(source, function);

    LOG.info("######### Xyp ENDPOINT : [{}]", endPoint);
    LOG.info("######### Xyp CUSTOMER INFO SOURCE : [{}]", source);

    LOG.info("######### Xyp CUSTOMER INFO FUNCTION : [{}]", function);
    LOG.info("######### Xyp CUSTOMER INFO SECURITY_CODE : [{}]", securityCode);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(endPoint, source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_ID_CARD_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    LOG.info("######## Xyp CUSTOMER INFO USER ID : [{}]", authenticationService.getCurrentUserId());

    JSONObject requestBody = createRequestBody(regNumber, employeeRegNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      return getCustomerIDCardFromJson(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public List<SalaryInfo> getCustomerSalaryInfos(String regNumber, String employeeRegNumber, Integer month)
      throws BpmServiceException
  {
    String endPoint = XacHttpUtil.getWso2EndPoint(environment);
    String source = getHeaderSource(environment);

    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_SALARY_INFO);
    String securityCode = generateSecurityCode(source, function);

    LOG.info("######### Xyp ENDPOINT : [{}]", endPoint);
    LOG.info("######### Xyp CUSTOMER SALARY INFO SOURCE : [{}]", source);

    LOG.info("######### Xyp CUSTOMER SALARY INFO FUNCTION : [{}]", function);
    LOG.info("######### Xyp CUSTOMER SALARY INFO SECURITY_CODE : [{}]", securityCode);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(endPoint, source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_SALARY_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = createRequestBodySalaryInfo(regNumber, employeeRegNumber, month);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
      return getSalaryInfosFromJson(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public PropertyInfo getPropertyInfo(Map<String, String> operatorInfo, Map<String, String> citizenInfo, String propertyId) throws BpmServiceException
  {
    validatePropertyPersonInfo(operatorInfo);
    validatePropertyPersonInfo(citizenInfo);

    if (StringUtils.isBlank(propertyId))
    {
      throw new BpmServiceException(PROPERTY_INFO_PROPERTY_ID_BLANK_CODE, PROPERTY_INFO_PROPERTY_ID_BLANK_MESSAGE);
    }

    String headerSource = getHeaderSource(environment);
    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_PROPERTY_INFO);

    XacHttpClient xacClient = new XacHttpClient.Builder(XacHttpUtil.getWso2EndPoint(environment), headerSource)
        .headerFunction(function)
        .headerSecurityCode(generateSecurityCode(headerSource, function))
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_PROPERTY_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = createRequestBodyForPropertyInfoService(operatorInfo, citizenInfo, propertyId);

    LOG.info("############ Getting Property Info Service with PROPERTY ID = [{}]", propertyId);
    long start = System.currentTimeMillis();

    try (Response response = xacClient.post(requestBody.toString()))
    {
      long finish = System.currentTimeMillis();
      long spentTime = finish - start;

      LOG.info("########### Spent time = [{}] for getting property info service with PROPERTY ID = [{}]", spentTime, propertyId);
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);

      LOG.info("############## Customer property INFO response = [{}] from Xyp system with PROPERTY ID = [{}]", jsonResponse.toString(), propertyId);
      return toPropertyInfo(jsonResponse);
    }
    catch (JSONException | XacHttpException e)
    {
      throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
    }
  }

  @Override
  public VehicleInfo getCustomerVehicleInfo(String regNumber, String employeeRegNumber, String plateNumber)
      throws BpmServiceException
  {
    String endPoint = XacHttpUtil.getWso2EndPoint(environment);
    String source = getHeaderSource(environment);

    String vehicleInfoFunction = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_VEHICLE_INFO);
    String securityCode = generateSecurityCode(source, vehicleInfoFunction);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(endPoint, source)
        .headerFunction(vehicleInfoFunction)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_VEHICLE_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();
    LOG.info("############ Xyp vehicle info FUNCTION = [{}], with customer REGISTER NUMBER = [{}]", vehicleInfoFunction, regNumber);
    LOG.info("############ Downloading vehicle info from Xyp system with PLATE NUMBER = [{}], customer REGISTER NUMBER = [{}]", plateNumber, regNumber);

    JSONObject requestBody = createVehicleInfoRequest(regNumber, employeeRegNumber, plateNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);

      LOG.info("########### Successful downloaded VEHICLE INFO from Xyp system with PLATE NUMBER = [{}], customer REGISTER NUMBER = [{}],"
              + " JSON RESPONSE = [{}]", plateNumber,
          regNumber, jsonResponse.toString());

      return toVehicleInfo(jsonResponse);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }

  @Override
  public List<VehicleOwner> getCustomerVehicleOwners(String regNumber, String employeeRegNumber,
      String plateNumber) throws BpmServiceException
  {
    String endPoint = XacHttpUtil.getWso2EndPoint(environment);
    String source = getHeaderSource(environment);

    String ownersInfoFunction = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_VEHICLE_OWNER_INFO);
    String securityCode = generateSecurityCode(source, ownersInfoFunction);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(endPoint, source)
        .headerFunction(ownersInfoFunction)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_CUSTOMER_VEHICLE_OWNER_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();
    LOG.info("############ Xyp vehicle owners info FUNCTION = [{}], with customer REGISTER NUMBER = [{}]", ownersInfoFunction, regNumber);
    LOG.info("############ Downloading vehicle owners info from Xyp system with PLATE NUMBER = [{}], customer REGISTER NUMBER = [{}]", plateNumber, regNumber);

    JSONObject requestBody = createVehicleOwnerRequest(regNumber, employeeRegNumber, plateNumber);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      Object responseObject = XacHttpUtil.getResponseFromJSON(environment, xacClient, response);

      LOG.info("########### Successful downloaded VEHICLE OWNERS INFO from Xyp system with PLATE NUMBER = [{}], customer REGISTER NUMBER = [{}].", plateNumber,
          regNumber);

      return toVehicleOwners(responseObject);
    }
    catch (XacHttpException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }
}
