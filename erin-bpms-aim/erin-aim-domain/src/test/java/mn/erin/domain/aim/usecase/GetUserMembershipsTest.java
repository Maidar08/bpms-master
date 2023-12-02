package mn.erin.domain.aim.usecase;

import java.util.ArrayList;
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
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMemberships;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.base.usecase.UseCaseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserMembershipsTest extends BaseUseCaseTest
{
  @Mock(name = "membershipRepository")
  private MembershipRepository membershipRepository;

  private GetUserMemberships getUserMemberships;

  @Before
  public void setup()
  {
    MockitoAnnotations.initMocks(this);
    getUserMemberships = new GetUserMemberships(authenticationService, authorizationService, membershipRepository, tenantIdProvider);
    mockRequiredAuthServices();
  }

  @Test
  public void should_return_user_memberships() throws AimRepositoryException, UseCaseException
  {
    Membership membership1 = new Membership(MembershipId.valueOf("membership1"), UserId.valueOf("user"), GroupId.valueOf("group1"), RoleId.valueOf("role"));
    Membership membership2 = new Membership(MembershipId.valueOf("membership2"), UserId.valueOf("user"), GroupId.valueOf("group2"), RoleId.valueOf("role"));

    List<Membership> memberships = new ArrayList<>();
    memberships.add(membership1);
    memberships.add(membership2);

    when(membershipRepository.listAllByUserId(TenantId.valueOf(TENANT_ID), UserId.valueOf("user"))).thenReturn(memberships);

    List<GetMembershipOutput> result = getUserMemberships.execute(new GetUserMembershipsInput("user"));
    assertEquals(2, result.size());
  }
}
