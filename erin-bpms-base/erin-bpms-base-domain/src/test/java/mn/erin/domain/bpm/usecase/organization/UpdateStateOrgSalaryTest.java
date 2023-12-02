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
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

public class UpdateStateOrgSalaryTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetSalaryOrganizationRequests";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private BpmsServiceRegistry bpmsServiceRegistry;

  private OrganizationSalaryRepository organizationSalaryRepository;
  private UpdateStateOrgSalary useCase;
  private Environment environment;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    bpmsServiceRegistry = Mockito.mock(BpmsServiceRegistry.class);
    organizationSalaryRepository = Mockito.mock(OrganizationSalaryRepository.class);
    useCase = new UpdateStateOrgSalary(authenticationService, authorizationService,  organizationSalaryRepository, bpmsServiceRegistry, environment);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateStateOrgSalary(authenticationService, authorizationService, null, bpmsServiceRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new UpdateStateOrgSalary(null, authorizationService, organizationSalaryRepository, bpmsServiceRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new UpdateStateOrgSalary(authenticationService, null, organizationSalaryRepository, bpmsServiceRegistry,  environment);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_blank() throws UseCaseException
  {
    Map<String, Object> input = new HashMap<>();
    input.put("id", "");
    useCase.execute(input);
  }
}
