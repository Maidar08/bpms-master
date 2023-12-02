package mn.erin.domain.aim.usecase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.usecase.group.GetAllGroups;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class GetAllGroupsTest extends BaseUseCaseTest
{
  private static final String TENANT_ID = "test";
  GetAllGroups getAllGroups;
  GroupRepository groupRepository;

  @Before
  public void setUpGroup()
  {
    groupRepository = Mockito.mock(GroupRepository.class);
    getAllGroups = new GetAllGroups(authenticationService, authorizationService, groupRepository, tenantIdProvider);
    mockRequiredAuthServices();
  }

  @Test(expected = UseCaseException.class)
  public void throw_aim_repository_exception() throws UseCaseException, AimRepositoryException
  {
    Mockito.when(tenantIdProvider.getCurrentUserTenantId()).thenReturn(TENANT_ID);
    Mockito.when(groupRepository.getAllRootGroups(TenantId.valueOf(TENANT_ID))).thenThrow(AimRepositoryException.class);
    getAllGroups.executeImpl(null);
  }
}
