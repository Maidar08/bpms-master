package mn.erin.bpm.domain.ohs.xac;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.OrganizationService;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderFunction;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderRequestType;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.domain.bpm.BpmMessagesConstants.COMPANY_NAME_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COMPANY_NAME_NULL_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.COMPANY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.CORPORATE_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.FCNAME;

/**
 * @author Zorig
 */
public class XacOrganizationService implements OrganizationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(XacOrganizationService.class);

  private final Environment environment;
  private final AuthenticationService authenticationService;

  public XacOrganizationService(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public Map<String, String> getOrganizationLevel(String cifNumber, String branch) throws BpmServiceException
  {
    if (StringUtils.isBlank(cifNumber) || StringUtils.isBlank(branch))
    {
      String errorCode = "BPMS074";
      throw new BpmServiceException(errorCode, "Cif Number and Branch number must not be blank!");
    }

    LOGGER.info("#########  Xac Organization Service calling getOrganizationInfo with: CIF - " + cifNumber + " and  branch - " + branch);

    String source = getHeaderSource(environment);
    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_GET_ORGANIZATION_INFO);
    String securityCode = generateSecurityCode(source, function);

    XacHttpClient xacClient = new XacHttpClient
        .Builder(XacHttpUtil.getWso2EndPoint(environment), source)
        .headerFunction(function)
        .headerSecurityCode(securityCode)
        .headerRequestId(getTimeStamp())
        .headerRequestType(getHeaderRequestType(environment, XacConstants.WSO2_HEADER_GET_ORGANIZATION_INFO))
        .headerContentType(APPLICATION_JSON)
        .headerUserId(authenticationService.getCurrentUserId())
        .build();

    JSONObject requestBody = new JSONObject();
    requestBody.put("cif", cifNumber);
    requestBody.put("branch", branch);

    try (Response response = xacClient.post(requestBody.toString()))
    {
      JSONObject jsonResponse = XacHttpUtil.getResultResponseOfOrganization(environment, xacClient, response);
      Map<String, String> organizationInfoMap = new HashMap<>();

      String companyName = jsonResponse.getString(COMPANY_NAME);

      if (StringUtils.isEmpty(companyName))
      {
        companyName = jsonResponse.getString(FCNAME);

        if (StringUtils.isEmpty(companyName))
        {
          LOGGER.error("######## Company name is empty with company CIF = [{}]", cifNumber);
          throw new BpmServiceException(COMPANY_NAME_NULL_ERROR_CODE, COMPANY_NAME_NULL_ERROR_MESSAGE);
        }
      }

      organizationInfoMap.put("name", companyName);
      organizationInfoMap.put("rank", jsonResponse.getString(CORPORATE_RANK));

      return organizationInfoMap;
    }
    catch (XacHttpException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new BpmServiceException(e.getCode(), e.getMessage(), e);
    }
  }
}
