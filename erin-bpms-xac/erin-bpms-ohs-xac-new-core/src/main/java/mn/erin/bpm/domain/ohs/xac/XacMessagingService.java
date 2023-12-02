package mn.erin.bpm.domain.ohs.xac;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.exception.XacHttpException;
import mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.MessagingService;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.WSO2_CAMUNDA_HEADER_SOURCE;
import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.APPLICATION_JSON;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.FAILED_TO_SEND_SMS_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.FAILED_TO_SEND_SMS_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.IP_ADDRESS_FAILED_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacMessageConstants.IP_ADDRESS_FAILED_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.ADDRESS_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.AUTH_CODE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.DELIVERY;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MSG_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.NOTIFICATION_TYPE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.QWADDRESS;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.REQUEST_IP;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.generateSecurityCode;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getOnlineLeasingHeaderSource;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getTimeStamp;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class XacMessagingService implements MessagingService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XacMessagingService.class);

    private final Environment environment;
    private final AuthenticationService authenticationService;

    public XacMessagingService(Environment environment, AuthenticationService authenticationService)
    {
        this.environment = environment;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean sendSms(Map<String, String> input) throws BpmServiceException
    {
        String headerSource = getHeaderSource(environment, WSO2_CAMUNDA_HEADER_SOURCE);
        String userId = authenticationService.getCurrentUserId();
        if(input.get(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
            headerSource = getOnlineLeasingHeaderSource(this.environment);
            userId = input.get(PHONE_NUMBER);
        }
        String function = getHeaderFunction(XacConstants.WSO2_HEADER_SEND_SMS);
        XacHttpClient xacClient = new XacHttpClient.Builder(getEndPoint(), headerSource)
            .headerFunction(function)
            .headerSecurityCode(generateSecurityCode(headerSource, function))
            .headerRequestId(getTimeStamp())
            .headerRequestType(getHeaderRequestType(XacConstants.WSO2_HEADER_SEND_SMS))
            .headerContentType(APPLICATION_JSON)
            .headerUserId(userId)
            .build();

        JSONObject request = new JSONObject();
        String ip;
        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
            LOGGER.info("############### SMS send service setting ip addredd = [{}]", ip);
        }
        catch (UnknownHostException e)
        {
            throw new BpmServiceException(IP_ADDRESS_FAILED_CODE, IP_ADDRESS_FAILED_MESSAGE);
        }

        request.put(REQUEST_IP, ip);
        request.put(AUTH_CODE, "F021D9");
        request.put(DELIVERY, "Y");
        request.put(QWADDRESS, input.get(PHONE_NUMBER));
        request.put(ADDRESS_TYPE, "A");
        request.put(MSG_TYPE, "Y");
        request.put(NOTIFICATION_TYPE, "");
        request.put(MESSAGE, input.get(MESSAGE));

        LOGGER.info("############### SMS send: setting request values, phone number: [{}], text message: [{}] with headerSource = {} and userId = {}",
            input.get(PHONE_NUMBER), input.get(MESSAGE), headerSource, userId);
        try (Response response = xacClient.post(request.toString()))
        {
            JSONObject jsonResponse = XacHttpUtil.getResultResponse(environment, xacClient, response);
            if (jsonResponse.has("Error"))
            {
                throw new BpmServiceException(FAILED_TO_SEND_SMS_CODE, FAILED_TO_SEND_SMS_MESSAGE);
            }
            LOGGER.info("############### SMS send service successfully sent.");
            return true;
        }
        catch (XacHttpException e)
        {
            LOGGER.error(ONLINE_SALARY_LOG_HASH + "{}", e.getMessage());
            throw new BpmServiceException(e.getCode(), e.getMessage(), e.getCause());
        }
        catch (JSONException e)
        {
            LOGGER.error(ONLINE_SALARY_LOG_HASH + "{}", e.getMessage());

            throw new BpmServiceException(XacHttpUtil.XAC_RESPONSE_JSON_PARSE_ERROR, e.getMessage(), e.getCause());
        }
    }

    private String getEndPoint()
    {
        return environment.getProperty(XacConstants.NEW_CORE_ENDPOINT);
    }


    private String getHeaderFunction(String prefix)
    {
        return environment.getProperty(prefix + ".function");
    }

    private String getHeaderRequestType(String prefix)
    {
        return environment.getProperty(prefix + ".requestType");
    }
}
