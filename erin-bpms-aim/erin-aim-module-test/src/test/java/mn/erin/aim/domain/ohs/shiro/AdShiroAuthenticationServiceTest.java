package mn.erin.aim.domain.ohs.shiro;

import javax.inject.Inject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.service.AuthenticationService;

/**
 * @author EBazarragchaa
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestAdAimShiroBeanConfig.class })
public class AdShiroAuthenticationServiceTest
{
  private static final TenantId TENANT_ID = TenantId.valueOf("erin");

  @Inject
  private AuthenticationService authenticationService;

  @Inject
  private SecurityManager securityManger;

  @Before
  public void setup()
  {
    ThreadContext.bind(securityManger);
  }

  @Test
  public void authenticationServiceIsNotNull()
  {
    Assert.assertNotNull(authenticationService);
  }

  @Test(expected = NullPointerException.class)
  public void authenticateThrowsExceptionNullTenant()
  {
    authenticationService.authenticate(null, "test1", "secret", false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void authenticateThrowsExceptionEmptyTenant()
  {
    authenticationService.authenticate("", "test1", "secret");
  }

  @Test(expected = NullPointerException.class)
  public void authenticateThrowsExceptionNullUserId()
  {
    authenticationService.authenticate(TENANT_ID.getId(), null, "secret");
  }

  @Test(expected = IllegalArgumentException.class)
  public void authenticateThrowsExceptionEmptyUserId()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "", "secret");
  }

  @Test(expected = NullPointerException.class)
  public void authenticateThrowsExceptionNullPassword()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "test1", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void authenticateThrowsExceptionEmptyPassword()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "test1", "");
  }

  @Test
  @Ignore // only local
  public void authenticateReturnsSessionId()
  {
    String ticket = authenticationService.authenticate(TENANT_ID.getId(), "erin", "1Xacbank!");

    Assert.assertNotNull(ticket);
  }

  @Test(expected = AuthenticationException.class)
  public void authenticateThrowsExceptionWrongPassword()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "erin", "Xacbank!");
  }

  @Test(expected = AuthenticationException.class)
  public void authenticateThrowsExceptionWrongUserId()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "erin1", "1Xacbank!");
  }

  @Test
  @Ignore // only local
  public void getCurrentUserIdReturnsLoggedUserId()
  {
    authenticationService.authenticate(TENANT_ID.getId(), "erin", "1Xacbank!");

    String userId = authenticationService.getCurrentUserId();

    Assert.assertNotNull(userId);
    Assert.assertEquals("erin", userId);
  }
}
