package mn.erin.domain.aim.usecase;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mn.erin.domain.aim.BaseUseCaseTest;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.usecase.membership.CreateMemberships;
import mn.erin.domain.aim.usecase.membership.CreateMembershipsInput;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.base.usecase.UseCaseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateMembershipsTest extends BaseUseCaseTest
{
  private static final String USER_ID = "user";
  private static final String GROUP_ID = "group";
  private static final String ROLE_ID = "role";
  private static final String TENANT_ID = "erin";

  @Mock(name = "membershipRepository")
  private MembershipRepository membershipRepository;

  private CreateMemberships createMemberships;

  @Before
  public void setup()
  {
    MockitoAnnotations.initMocks(this);
    createMemberships = new CreateMemberships(authenticationService, authorizationService, membershipRepository);
    mockRequiredAuthServices();
  }

  @Test(expected = UseCaseException.class)
  public void should_throw_exception() throws AimRepositoryException, UseCaseException
  {
    when(membershipRepository.create(UserId.valueOf(USER_ID), GroupId.valueOf(GROUP_ID), RoleId.valueOf(ROLE_ID), TenantId.valueOf(TENANT_ID)))
      .thenThrow(AimRepositoryException.class);

    createMemberships.execute(new CreateMembershipsInput(GROUP_ID, ROLE_ID, Collections.singletonList(USER_ID), TENANT_ID));
  }

  @Test
  public void should_create_membership() throws AimRepositoryException, UseCaseException
  {
    Membership membership = new Membership(MembershipId.valueOf("membership"), UserId.valueOf(USER_ID), GroupId.valueOf(GROUP_ID), RoleId.valueOf(ROLE_ID));
    membership.setTenantId(TenantId.valueOf(TENANT_ID));

    when(membershipRepository.create(UserId.valueOf(USER_ID), GroupId.valueOf(GROUP_ID), RoleId.valueOf(ROLE_ID), TenantId.valueOf(TENANT_ID)))
      .thenReturn(membership);
    when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);
    List<GetMembershipOutput> membershipOutputs = createMemberships
      .execute(new CreateMembershipsInput(GROUP_ID, ROLE_ID, Collections.singletonList(USER_ID), TENANT_ID));
    assertEquals(1, membershipOutputs.size());
  }
}
