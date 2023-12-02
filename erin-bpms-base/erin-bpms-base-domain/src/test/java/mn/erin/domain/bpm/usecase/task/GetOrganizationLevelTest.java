package mn.erin.domain.bpm.usecase.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.OrganizationService;
import mn.erin.domain.bpm.usecase.GetOrganizationLevel;

public class GetOrganizationLevelTest
{
  private OrganizationService organizationService;
  private AuthenticationService authenticationService;
  private MembershipRepository membershipRepository;
  private GetOrganizationLevel useCase;

  @Before
  public void setUp()
  {
    organizationService = Mockito.mock(OrganizationService.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    useCase = new GetOrganizationLevel(organizationService, authenticationService, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_organization_service_is_null()
  {
    new GetOrganizationLevel(null, authenticationService, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_is_null()
  {
    new GetOrganizationLevel(organizationService, null, membershipRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_membership_repository_is_null()
  {
    new GetOrganizationLevel(organizationService, authenticationService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_blank() throws UseCaseException
  {
    useCase.execute("");
  }
}
