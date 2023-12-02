package mn.erin.domain.bpm.usecase.organization;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.organization.OrganizationSalary;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;

public class GetSalaryOrganizationRequestsTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetSalaryOrganizationRequests";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private OrganizationSalaryRepository organizationSalaryRepository;
  private GetSalaryOrganizationRequests useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    organizationSalaryRepository = Mockito.mock(OrganizationSalaryRepository.class);
    useCase = new GetSalaryOrganizationRequests(authenticationService, authorizationService, organizationSalaryRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetSalaryOrganizationRequests(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new GetSalaryOrganizationRequests(null, authorizationService, organizationSalaryRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetSalaryOrganizationRequests(authenticationService, null, organizationSalaryRepository);
  }

  @Ignore
  @Test
  public void when_empty_list_returned() throws UseCaseException
  {
    Mockito.when(organizationSalaryRepository.findAll()).thenReturn(new ArrayList<>());

    GetAllOrganizationRequestsOutput output = new GetSalaryOrganizationRequests(authenticationService, authorizationService, organizationSalaryRepository)
        .execute(null);
    Assert.assertTrue(output.getOrganizationRequests().isEmpty());
  }

  @Ignore
  @Test
  public void when_nonempty_list_returned() throws UseCaseException
  {
    Collection<OrganizationSalary> organizationRequests = new ArrayList<>();
    OrganizationSalary organizationSalary1 = new OrganizationSalary(OrganizationRequestId.valueOf("id"));
    OrganizationSalary organizationSalary2 = new OrganizationSalary(OrganizationRequestId.valueOf("id1"));
    organizationRequests.add(organizationSalary1);
    organizationRequests.add(organizationSalary2);

    Mockito.when(organizationSalaryRepository.findAll()).thenReturn(organizationRequests);

    GetAllOrganizationRequestsOutput output = useCase.execute(null);

    Assert.assertEquals(2, output.getOrganizationRequests().size());
  }
}