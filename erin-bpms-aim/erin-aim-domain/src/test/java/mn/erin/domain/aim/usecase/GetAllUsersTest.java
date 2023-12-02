package mn.erin.domain.aim.usecase;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.aim.usecase.user.GetUserOutput;
import mn.erin.domain.base.usecase.UseCaseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllUsersTest extends BaseUseCaseTest
{
  @Mock(name = "userRepository")
  private UserRepository userRepository;

  private GetAllUsers getAllUsers;

  @Before
  public void setup()
  {
    MockitoAnnotations.initMocks(this);
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userRepository, tenantIdProvider);
    mockRequiredAuthServices();
  }

  @Test
  public void should_return_all_users() throws UseCaseException
  {
    List<User> allUsers = new ArrayList<>();

    for (int i = 0; i < 10; i++)
    {
      UserInfo userInfo = new UserInfo("FirstName", "LastName");
      ContactInfo contactInfo = new ContactInfo("email@email.com", "12345");
      User user = new User(UserId.valueOf("" + i), TenantId.valueOf("tenant"), userInfo, contactInfo);
      allUsers.add(user);
    }

    when(userRepository.getAllUsers(TenantId.valueOf(TENANT_ID))).thenReturn(allUsers);

    List<GetUserOutput> result = getAllUsers.execute(null);
    assertEquals(10, result.size());

    for (GetUserOutput output : result)
    {
      assertNotNull(output.getId());
      assertNotNull(output.getTenantId());
      assertNotNull(output.getFirstName());
      assertNotNull(output.getLastName());
      assertNotNull(output.getDisplayName());
      assertNotNull(output.getEmail());
      assertNotNull(output.getPhoneNumber());
    }
  }
}
