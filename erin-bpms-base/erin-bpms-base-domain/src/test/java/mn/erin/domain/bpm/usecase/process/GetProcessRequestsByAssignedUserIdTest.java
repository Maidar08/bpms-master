package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Zorig
 */
public class GetProcessRequestsByAssignedUserIdTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetProcessRequestsByAssignedUserId";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRequestRepository requestRepository;
  private GetProcessRequestsByAssignedUserId useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    requestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new GetProcessRequestsByAssignedUserId(authenticationService, authorizationService, requestRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProcessRequestsByAssignedUserId(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new GetProcessRequestsByAssignedUserId(null, authorizationService, requestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetProcessRequestsByAssignedUserId(authenticationService, null, requestRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_user_id_null() throws UseCaseException
  {
    GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput(null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_user_id_blank() throws UseCaseException
  {
    GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput("");
    useCase.execute(input);
  }

  @Test
  public void when_empty_list_returned() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.findAllByAssignedUserId(new UserId("User 1"))).thenReturn(new ArrayList<>());

    GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput("User 1");
    GetProcessRequestsOutput output = useCase.execute(input);

    Assert.assertTrue(output.getProcessRequests().isEmpty());
  }

  @Test
  public void when_nonempty_list_returned() throws BpmRepositoryException, UseCaseException
  {
    List<ProcessRequest> processRequestList = new ArrayList<>();

    ProcessRequestId processRequestId = new ProcessRequestId("1");
    ProcessTypeId processTypeId = new ProcessTypeId("Process Type 1");
    GroupId groupId = new GroupId("Erin Group");
    String requestedUserId = "Requested User";
    ProcessRequestState state = ProcessRequestState.NEW;
    LocalDateTime createdTime = LocalDateTime.now();
    Map<String, Serializable> parameters = new HashMap<>();
    UserId assignedUserId = new UserId("User 1");
    ProcessRequest processRequest1 = new ProcessRequest(processRequestId, processTypeId, groupId, requestedUserId, createdTime, state, parameters);
    processRequest1.setAssignedUserId(assignedUserId);
    processRequestList.add(processRequest1);
    processRequestList.add(processRequest1);

    Mockito.when(requestRepository.getRequestsByAssignedUserId("User 1", null, null)).thenReturn(processRequestList);

    GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput("User 1");
    GetProcessRequestsOutput output = useCase.execute(input);

    Assert.assertEquals(2, output.getProcessRequests().size());

    Iterator<ProcessRequest> iterator = output.getProcessRequests().iterator();
    Assert.assertEquals("User 1", iterator.next().getAssignedUserId().getId());
    Assert.assertEquals("User 1", iterator.next().getAssignedUserId().getId());
  }
}
