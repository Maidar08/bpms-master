package mn.erin.domain.aim;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class BaseUseCaseTest
{
  protected static final String TENANT_ID = "-erin-";
  protected static final String USER_ID = "-user-";

  @Mock(name = "authenticationService")
  protected AuthenticationService authenticationService;

  @Mock(name = "authorizationService")
  protected AuthorizationService authorizationService;

  @Mock(name = "tenantIdProvider")
  protected TenantIdProvider tenantIdProvider;

  @Before
  public void before()
  {
    MockitoAnnotations.initMocks(this);
  }

  protected void mockRequiredAuthServices()
  {
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn(TENANT_ID);
    when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);
    when(authorizationService.hasPermission(anyString(), anyString())).thenReturn(true);
  }

  @Test
  public void baseMockTest()
  {
    Assert.assertNotNull(authenticationService);
    Assert.assertNotNull(authorizationService);
    Assert.assertNotNull(tenantIdProvider);
  }
}
