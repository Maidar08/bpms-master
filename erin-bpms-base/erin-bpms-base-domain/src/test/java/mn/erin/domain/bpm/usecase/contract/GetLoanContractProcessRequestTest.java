package mn.erin.domain.bpm.usecase.contract;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractParameter;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;

public class GetLoanContractProcessRequestTest
{
  private static final Permission permission = new AimModulePermission("GetLoanContractProcessRequest");
  private static final String GROUP_ID = "108";

  private LoanContractRequestRepository loanContractRequestRepository;
  private AuthorizationService authorizationService;
  private AuthenticationService authenticationService;
  private GetLoanContractProcessRequest useCase;
  private GetLoanContractProcessRequestInput input;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);
    loanContractRequestRepository = Mockito.mock(LoanContractRequestRepository.class);
    useCase = new GetLoanContractProcessRequest(authenticationService, authorizationService, loanContractRequestRepository);
    input = new GetLoanContractProcessRequestInput(GROUP_ID, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_contract_request_repository_is_null()
  {
    new GetLoanContractProcessRequest(null, null, null);
  }

  @Test
  public void when_permission_granted()
  {
    Assert.assertEquals(permission, useCase.getPermission());
  }

  @Test
  public void when_success() throws UseCaseException
  {
    List<LoanContractRequest> requests = generateLoanContractRequestList();
    Mockito.when(loanContractRequestRepository.findByGroupId(GROUP_ID, null, null)).thenReturn(requests);

    Assert.assertEquals(useCase.executeImpl(input).size(), 1);
  }

  private List<LoanContractRequest> generateLoanContractRequestList()
  {
    LoanContractRequest loanContractRequest = new LoanContractRequest(ProcessRequestId.valueOf("123"), "123", "", "123",
        BigDecimal.valueOf(Double.parseDouble("123123")),
        LocalDateTime.now(), "branchspecialist", "109",
        TenantId.valueOf("123123"), "www", "cif", "desc");
    List<LoanContractRequest> loanContractRequests = new ArrayList<>();
    loanContractRequests.add(loanContractRequest);
    return loanContractRequests;
  }

  private List<LoanContractParameter> getProcessInstanceId()
  {
    Map<String, Object> map = new HashMap<>();
    map.put("cifNumber", "123");
    return Arrays.asList(new LoanContractParameter(ProcessInstanceId.valueOf("123"), "", map, "", null));
  }
}
