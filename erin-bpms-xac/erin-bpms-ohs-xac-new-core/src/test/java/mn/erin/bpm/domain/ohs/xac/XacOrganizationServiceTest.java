package mn.erin.bpm.domain.ohs.xac;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.BpmServiceException;

/**
 * @author Zorig
 */
public class XacOrganizationServiceTest
{
  private Environment environment;
  private AuthenticationService authenticationService;
  private XacOrganizationService organizationService;

  @Before
  public void setUp()
  {
    environment = Mockito.mock(Environment.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    organizationService = new XacOrganizationService(environment, authenticationService);
  }

  @Test(expected = BpmServiceException.class)
  public void verifyBlankCifNumber() throws BpmServiceException
  {
    organizationService.getOrganizationLevel("  ", "108");
  }
}
