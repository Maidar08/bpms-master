package mn.erin.bpms.organization.request.webapp.util;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;

/**
 * @author Odgavaa
 */
public final class OrganizationRequestRestUtil
{
  private OrganizationRequestRestUtil()
  {

  }

  private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRequestRestUtil.class);

  public static final String XAC_TENANT_ID = "xac";
  public static final String DECODED_AUTH_SEPARATOR = ":";
  public static final String ENCODED_AUTH_SEPARATOR = "\\s+";

  public static boolean isAuthenticatedUser(AuthenticationService authenticationService, String authString)
  {
    String[] authParts = authString.split(ENCODED_AUTH_SEPARATOR);
    String authInfo = authParts[1];

    byte[] bytes = null;

    Base64.Decoder decoder = Base64.getDecoder();
    bytes = decoder.decode(authInfo);

    String decodedAuth = new String(bytes);
    String[] userAuthentication = decodedAuth.split(DECODED_AUTH_SEPARATOR);

    String userName = userAuthentication[0];
    String password = userAuthentication[1];

    String token = null;

    try
    {
      token = authenticationService.authenticate(XAC_TENANT_ID, userName, password, false);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }

    return null != token;
  }
}
