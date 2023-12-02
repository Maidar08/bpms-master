package mn.erin.domain.bpm.usecase.process;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Zorig
 */
public class UpdateAssignedUserTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.UpdateAssignedUser";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRequestRepository requestRepository;
  private UpdateAssignedUser useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    requestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new UpdateAssignedUser(authenticationService, authorizationService, requestRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateAssignedUser(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new UpdateAssignedUser(null, authorizationService, requestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new UpdateAssignedUser(authenticationService, null, requestRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_assigned_user_null() throws UseCaseException
  {
    UpdateAssignedUserInput input = new UpdateAssignedUserInput("id", null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_assigned_user_blank() throws UseCaseException
  {
    UpdateAssignedUserInput input = new UpdateAssignedUserInput("id", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_process_requestid_blank() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.updateAssignedUser("", new UserId("User 1"))).thenThrow(BpmRepositoryException.class);
    UpdateAssignedUserInput input = new UpdateAssignedUserInput("", "User 1");
    useCase.execute(input);
  }

  @Test
  public void successfulUpdate() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.updateAssignedUser("1", new UserId("User 1"))).thenReturn(true);

    UpdateAssignedUserInput input = new UpdateAssignedUserInput("1", "User 1");
    UpdateAssignedUserOutput output = useCase.execute(input);

    Assert.assertTrue(output.isUpdated());
  }

  @Test
  public void unsuccessfulUpdate() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.updateAssignedUser("1", new UserId("User 1"))).thenReturn(false);

    UpdateAssignedUserInput input = new UpdateAssignedUserInput("1", "User 1");
    UpdateAssignedUserOutput output = useCase.execute(input);

    Assert.assertFalse(output.isUpdated());
  }
}
