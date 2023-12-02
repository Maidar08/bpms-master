package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

public class GetAccountReferenceTest
{
  private BranchBankingService branchBankingService;
  private GetAccountReference useCase;
  private MembershipRepository membershipRepository;
  private AuthenticationService authenticationService;
  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    authenticationService = Mockito.mock(AuthenticationService.class);
    membershipRepository = Mockito.mock(MembershipRepository.class);
    useCase = new GetAccountReference(branchBankingService, authenticationService, membershipRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_blank() throws UseCaseException
  {
    useCase.execute(new GetAccountInfoInput("", "", false));
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException, AimRepositoryException
  {
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("userId");
    Mockito.when(membershipRepository.findByUserId(getUserId())).thenReturn(getUserMembership());
    Mockito.when(branchBankingService.getAccountReference("123", "108", "12345678")).thenReturn(getAccountMap());
    Map<String, Object> actualResponse = useCase.execute(new GetAccountInfoInput("12345678", "123", false));

    Assert.assertEquals(actualResponse.size(), 2);
    Assert.assertEquals(actualResponse.get("key1"), "value1");
    Assert.assertEquals(actualResponse.get("key2"), "value2");
  }

  private Map<String, Object> getAccountMap()
  {
    Map<String, Object> accountMap = new HashMap<>();

    accountMap.put("key1", "value1");
    accountMap.put("key2", "value2");

    return accountMap;

  }

  private UserId getUserId() {
    return new UserId("userId");
  }

  private Membership getUserMembership() {
    return new Membership(MembershipId.valueOf("m1"), getUserId(), new GroupId("108"), new RoleId("roleId"));
  }
}
