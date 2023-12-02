package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class UpdateProcessParametersTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.UpdateProcessParameters";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRepository processRepository;
  private UpdateProcessParameters useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateProcessParameters(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new UpdateProcessParameters(null, authorizationService, processRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new UpdateProcessParameters(authenticationService, null, processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_map_null() throws UseCaseException
  {
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", null);
    useCase.execute(input);
  }

  @Test
  public void when_parameter_map_empty() throws UseCaseException
  {
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", new HashMap<>());

    Assert.assertEquals(0, useCase.execute(input).getNumUpdated());
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_null() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String,Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap(null, "Value"));
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", parameters);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_entity_type_null() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String,Serializable>> parameters = new HashMap<>();
    parameters.put(null, Collections.singletonMap("Key", "Value"));
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", parameters);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_blank() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String,Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap(" ", "Value"));
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", parameters);
    useCase.execute(input);
  }

  @Test
  public void when_parameter_value_blank() throws UseCaseException, BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String,Serializable>> parameters = new HashMap<>();

    Map<String, Serializable> parametersValues = new HashMap<>();
    parametersValues.put("key", " ");

    parameters.put(ParameterEntityType.CUSTOMER, parametersValues);

    Mockito.when(processRepository.updateParameters("id", parameters)).thenReturn(1);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", parameters);
    UpdateProcessParametersOutput output = useCase.execute(input);

    Assert.assertEquals(1, output.getNumUpdated());
  }

  @Test
  public void when_successful_update() throws UseCaseException, BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap("name", "Zorig"));
    UpdateProcessParametersInput input = new UpdateProcessParametersInput("id", parameters);
    Mockito.when(processRepository.updateParameters("id", parameters)).thenReturn(1);
    UpdateProcessParametersOutput output = useCase.execute(input);
    Mockito.verify(processRepository, Mockito.times(1)).updateParameters("id", parameters);
    Assert.assertEquals(1, output.getNumUpdated());
  }

}
