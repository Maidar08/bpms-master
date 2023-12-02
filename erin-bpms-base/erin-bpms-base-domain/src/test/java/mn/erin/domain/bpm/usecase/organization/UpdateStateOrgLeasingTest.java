package mn.erin.domain.bpm.usecase.organization;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

public class UpdateStateOrgLeasingTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetSalaryOrganizationRequests";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private BpmsServiceRegistry bpmsServiceRegistry;
  private OrganizationLeasingRepository organizationLeasingRepository;
  private UpdateStateOrgLeasing useCase;
  private Environment environment;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    bpmsServiceRegistry = Mockito.mock(BpmsServiceRegistry.class);
    organizationLeasingRepository = Mockito.mock(OrganizationLeasingRepository.class);
    useCase = new UpdateStateOrgLeasing(authenticationService, authorizationService, organizationLeasingRepository, bpmsServiceRegistry, environment);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateStateOrgLeasing(authenticationService, authorizationService, null, bpmsServiceRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new UpdateStateOrgLeasing(null, authorizationService, organizationLeasingRepository, bpmsServiceRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new UpdateStateOrgLeasing(authenticationService, null, organizationLeasingRepository, bpmsServiceRegistry, environment);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_blank() throws UseCaseException
  {
    Map<String, Object> input = new HashMap<>();
    input.put("id", "");
    useCase.execute(input);
  }
}
