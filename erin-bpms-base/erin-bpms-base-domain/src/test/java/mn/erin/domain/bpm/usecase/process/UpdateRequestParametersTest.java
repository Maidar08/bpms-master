package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author Zorig
 */
public class UpdateRequestParametersTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.UpdateAssignedUser";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRequestRepository requestRepository;
  private UpdateRequestParameters useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    requestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new UpdateRequestParameters(authenticationService, authorizationService, requestRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateRequestParameters(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new UpdateRequestParameters(null, authorizationService, requestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new UpdateRequestParameters(authenticationService, null, requestRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_map_null() throws UseCaseException
  {
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_map_empty() throws UseCaseException
  {
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", new HashMap<>());
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_null() throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(null, 1);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", parameters);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_blank() throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("", 1);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", parameters);
    useCase.execute(input);
  }


  @Test(expected = UseCaseException.class)
  public void when_parameter_value_blank() throws UseCaseException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("name", "");
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", parameters);
    useCase.execute(input);
  }

  @Test
  public void when_successful_update() throws UseCaseException, BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("name", "Zorig");
    UpdateRequestParametersInput input = new UpdateRequestParametersInput("id", parameters);
    Mockito.when(requestRepository.updateParameters("id", parameters)).thenReturn(true);
    UpdateRequestParametersOutput output = useCase.execute(input);
    Mockito.verify(requestRepository, Mockito.times(1)).updateParameters("id", parameters);
    Assert.assertTrue(output.isUpdated());
  }
}
