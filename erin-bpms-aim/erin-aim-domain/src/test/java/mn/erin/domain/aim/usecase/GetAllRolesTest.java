package mn.erin.domain.aim.usecase;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.usecase.role.GetAllRoles;
import mn.erin.domain.aim.usecase.role.GetRoleOutput;
import mn.erin.domain.base.usecase.UseCaseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllRolesTest extends BaseUseCaseTest
{
  @Mock(name = "roleRepository")
  private RoleRepository roleRepository;

  private GetAllRoles getAllRoles;

  @Before
  public void setup()
  {
    MockitoAnnotations.initMocks(this);
    getAllRoles = new GetAllRoles(authenticationService, authorizationService, roleRepository, tenantIdProvider);
    mockRequiredAuthServices();
  }

  @Test
  public void should_return_all_roles() throws AimRepositoryException, UseCaseException
  {
    Role role = new Role(RoleId.valueOf("role"), TenantId.valueOf("tenant"), "ROLE");
    when(roleRepository.listAll(TenantId.valueOf(TENANT_ID))).thenReturn(Collections.singletonList(role));
    List<GetRoleOutput> result = getAllRoles.execute(null);
    assertEquals(1, result.size());
  }
}
