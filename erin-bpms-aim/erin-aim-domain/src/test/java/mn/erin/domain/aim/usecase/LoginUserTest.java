package mn.erin.domain.aim.usecase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.usecase.user.LoginUser;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Tamir
 */
public class LoginUserTest
{
  private LoginUser useCase;
  private AuthenticationService authenticationService;
  private MembershipRepository membershipRepository;
  private RoleRepository roleRepository;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    roleRepository = Mockito.mock(RoleRepository.class);

    useCase = new LoginUser(authenticationService, membershipRepository, roleRepository, null, null);
  }

  @Test(expected = UseCaseException.class)
  public void whenInputNull() throws UseCaseException
  {
    useCase.execute(null);
  }


}
