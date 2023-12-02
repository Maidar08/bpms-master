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
import mn.erin.domain.bpm.model.organization.OrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;

public class GetLeasingOrganizationRequestsTest
{
    private static final String CURRENT_USER = "admin";
    private static final String PERMISSION_STR = "bpms.bpm.GetLeasingOrganizationRequests";

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;

    private OrganizationLeasingRepository organizationLeasingRepository;
    private GetLeasingOrganizationRequests useCase;

    @Before
    public void setUp()
    {
      authenticationService = Mockito.mock(AuthenticationService.class);
      authorizationService = Mockito.mock(AuthorizationService.class);

      organizationLeasingRepository = Mockito.mock(OrganizationLeasingRepository.class);
      useCase = new GetLeasingOrganizationRequests(authenticationService, authorizationService, organizationLeasingRepository);

      Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
      Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
    }

    @Test(expected = NullPointerException.class)
    public void when_repo_null()
    {
      new GetLeasingOrganizationRequests(authenticationService, authorizationService, null);
    }

    @Test(expected = NullPointerException.class)
    public void when_authentication_service_null()
    {
      new GetLeasingOrganizationRequests(null, authorizationService, organizationLeasingRepository);
    }

    @Test(expected = NullPointerException.class)
    public void when_authorization_service_null()
    {
      new GetLeasingOrganizationRequests(authenticationService, null, organizationLeasingRepository);
    }

    @Ignore
    @Test
    public void when_empty_list_returned() throws UseCaseException
    {
      Mockito.when(organizationLeasingRepository.findAll()).thenReturn(new ArrayList<>());

      GetAllOrganizationRequestsOutput output = new GetLeasingOrganizationRequests(authenticationService, authorizationService, organizationLeasingRepository)
          .execute(null);
      Assert.assertTrue(output.getOrganizationRequests().isEmpty());
    }

    @Ignore
    @Test
    public void when_nonempty_list_returned() throws UseCaseException
    {
      Collection<OrganizationLeasing> organizationRequests = new ArrayList<>();
      OrganizationLeasing organizationLeasing1 = new OrganizationLeasing(OrganizationRequestId.valueOf("id"));
      OrganizationLeasing organizationLeasing2 = new OrganizationLeasing(OrganizationRequestId.valueOf("id1"));
      organizationRequests.add(organizationLeasing1);
      organizationRequests.add(organizationLeasing2);

      Mockito.when(organizationLeasingRepository.findAll()).thenReturn(organizationRequests);

      GetAllOrganizationRequestsOutput output = useCase.execute(null);

      Assert.assertEquals(2, output.getOrganizationRequests().size());
    }

}
