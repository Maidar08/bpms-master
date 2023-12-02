package mn.erin.domain.bpm.usecase.process;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static org.mockito.Mockito.any;

/**
 * @author Zorig
 */
public class GetProcessRequestByProcessInstanceIdTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetProcessRequestByProcessInstanceId";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRequestRepository requestRepository;
  private LoanContractRequestRepository loanContractRequestRepository;
  private GetProcessRequestByProcessInstanceId useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    requestRepository = Mockito.mock(ProcessRequestRepository.class);
    loanContractRequestRepository = Mockito.mock(LoanContractRequestRepository.class);
    useCase = new GetProcessRequestByProcessInstanceId(authenticationService, authorizationService, requestRepository, loanContractRequestRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProcessRequestByProcessInstanceId(authenticationService, authorizationService, null, loanContractRequestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new GetProcessRequestByProcessInstanceId(null, authorizationService, requestRepository, loanContractRequestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetProcessRequestByProcessInstanceId(authenticationService, null, requestRepository, loanContractRequestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_contract_request_repo_null()
  {
    new GetProcessRequestByProcessInstanceId(authenticationService, authorizationService, requestRepository, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test()
  public void when_repository_returns_null() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.getByProcessInstanceId("1")).thenReturn(null);
    ProcessRequest request =  useCase.execute("1");
    Assert.assertNotNull(request);
  }

  @Test()
  public void when_repositories_returns_null() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.getByProcessInstanceId(any())).thenReturn(null);
    Mockito.when(loanContractRequestRepository.findByInstanceId(any())).thenReturn(null);
    ProcessRequest request = useCase.execute("1");
    Assert.assertNotNull(request);
  }

  @Test
  public void when_loan_contract_request_returned() throws BpmRepositoryException, UseCaseException
  {
    LoanContractRequest loanContractRequest = new LoanContractRequest(
        ProcessRequestId.valueOf("id"),
        "instanceId",
        "Type",
        "acc",
        new BigDecimal(1),
        LocalDateTime.now(),
        "user",
        "grouId",
        TenantId.valueOf("xac"),
        "NEW",
        "cif",
        "desc"
    );
    Mockito.when(requestRepository.getByProcessInstanceId(any())).thenReturn(null);
    Mockito.when(loanContractRequestRepository.findByInstanceId(any())).thenReturn(loanContractRequest);
    ProcessRequest processRequest = useCase.executeImpl("1");
    Assert.assertNotNull(processRequest);
    Assert.assertEquals(loanContractRequest.getId().getId(), processRequest.getId().getId());
  }

  @Test
  public void when_repository_returns_process_request() throws BpmRepositoryException, UseCaseException
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    ProcessRequest processRequest = new ProcessRequest(new ProcessRequestId("1"), new ProcessTypeId("123"), new GroupId("Erin Group"), "Requested User",
        createdTime,
        ProcessRequestState.NEW, new HashMap<>());
    processRequest.setProcessInstanceId("1");
    Mockito.when(requestRepository.getByProcessInstanceId("1")).thenReturn(processRequest);

    ProcessRequest returnedProcessRequest = useCase.execute("1");

    Assert.assertNotNull(returnedProcessRequest);
    Assert.assertEquals("1", returnedProcessRequest.getProcessInstanceId());
  }
}
