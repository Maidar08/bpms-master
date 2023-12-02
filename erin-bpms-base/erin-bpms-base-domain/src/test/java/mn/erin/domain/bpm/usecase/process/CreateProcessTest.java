package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class CreateProcessTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.CreateProcess";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRepository processRepository;
  private CreateProcess useCase;

  private static final LocalDateTime STARTED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
  private static final LocalDateTime FINISHED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 101), LocalTime.MIDNIGHT);

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new CreateProcess(authenticationService, authorizationService, processRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new CreateProcess(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new CreateProcess(null, authorizationService, processRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new CreateProcess(authenticationService, null, processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_map_null() throws UseCaseException
  {
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE, "user", "bb",  null);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_null() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap(null, "Value"));
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE,"user", "bb", parameters);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_entity_type_null() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(null, Collections.singletonMap("Key", "Value"));
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE,"user", "bb", parameters);
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_parameter_name_blank() throws UseCaseException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap(" ", "Value"));
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE, "user", "bb", parameters);
    useCase.execute(input);
  }

  @Ignore
  @Test
  public void when_parameter_value_blank() throws UseCaseException, BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();

    Map<String, Serializable> parametersValues = new HashMap<>();
    parametersValues.put("key", " ");

    parameters.put(ParameterEntityType.CUSTOMER, parametersValues);
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE, "user", "bb", parameters);

    String processInstanceId = input.getProcessInstanceId();
    LocalDateTime startedDate = input.getStartedDate();
    LocalDateTime finishedDate = input.getFinishedDate();
    String createdUser = input.getCreatedUser();
    String processTypeCategory = input.getProcessTypeCategory();
    Map<ParameterEntityType, Map<String, Serializable>> inputParams = input.getParameters();


    Mockito.when(processRepository.createProcess(input.getProcessInstanceId(), input.getStartedDate(), input.getFinishedDate(), input.getCreatedUser(), input.getProcessTypeCategory(), parameters))
        .thenReturn(new Process(ProcessInstanceId.valueOf(processInstanceId), startedDate, finishedDate, createdUser, processTypeCategory, inputParams));
    CreateProcessOutput output = useCase.execute(input);

    Assert.assertNotNull(output.getProcess());
  }

  @Test
  public void when_successful() throws UseCaseException, BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap("Key", "Value"));
    CreateProcessInput input = new CreateProcessInput("id", STARTED_DATE, "user", "bb",  parameters);

    Mockito.when(processRepository.createProcess("id", STARTED_DATE, null, parameters))
        .thenReturn(new Process(new ProcessInstanceId("id"), STARTED_DATE,  parameters));

    CreateProcessOutput output = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).createProcess("id", STARTED_DATE, null, parameters);

    Assert.assertEquals("id", output.getProcess().getId().getId());
    Assert.assertNull(output.getProcess().getFinishedTime());
    Assert.assertEquals(STARTED_DATE, output.getProcess().getStartedTime());
    Assert.assertEquals(parameters, output.getProcess().getProcessParameters());
  }
}
