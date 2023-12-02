package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
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
public class GetProcessEntityTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetProcessEntity";

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRepository processRepository;
  private GetProcessEntity useCase;

  private static final LocalDateTime STARTED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
  private static final LocalDateTime FINISHED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 101), LocalTime.MIDNIGHT);

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new GetProcessEntity(authenticationService, authorizationService, processRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProcessEntity(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new GetProcessEntity(null, authorizationService, processRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetProcessEntity(authenticationService, null, processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_filterByEntityType_result_null() throws UseCaseException, BpmRepositoryException
  {
    GetProcessEntityInput input = new GetProcessEntityInput("123", ParameterEntityType.CUSTOMER);

    Mockito.when(processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER)).thenReturn(null);

    Map<String, Serializable> outputMap = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER);

    Assert.assertTrue(outputMap.isEmpty());
  }

  @Test
  public void when_multiple_parameters() throws BpmRepositoryException, UseCaseException
  {
    GetProcessEntityInput input = new GetProcessEntityInput("123", ParameterEntityType.CUSTOMER);

    Process processToReturn = createProcess();
    Mockito.when(processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER)).thenReturn(processToReturn);

    Map<String, Serializable> outputMap = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER);

    assertOutputMap(outputMap);
  }

  @Test
  public void when_json_parameter() throws BpmRepositoryException, UseCaseException
  {
    GetProcessEntityInput input = new GetProcessEntityInput("123", ParameterEntityType.CUSTOMER);

    Process processToReturn = createProcessWithJsonParameter();
    Mockito.when(processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER)).thenReturn(processToReturn);

    Map<String, Serializable> outputMap = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER);

    assertOutputMapWithJsonParameters(outputMap);
  }

  private Process createProcess()
  {
    Map<ParameterEntityType, Map<String, Serializable>> parametersMap = new HashMap<>();
    Map<String, Serializable> nameAndValueMap = new HashMap<>();
    nameAndValueMap.put("firstName", "Zorig");
    nameAndValueMap.put("lastName", "Mag");
    nameAndValueMap.put("age", 24);
    nameAndValueMap.put("weight", new BigDecimal(80.012).setScale(3, RoundingMode.FLOOR));
    parametersMap.put(ParameterEntityType.CUSTOMER, nameAndValueMap);

    return new Process(new ProcessInstanceId("123"), STARTED_DATE, parametersMap);
  }

  private Process createProcessWithJsonParameter()
  {
    Map<ParameterEntityType, Map<String, Serializable>> parametersMap = new HashMap<>();
    Map<String, Serializable> nameAndValueMap = new HashMap<>();
    nameAndValueMap.put("firstName", "Zorig");
    nameAndValueMap.put("lastName", "Mag");
    nameAndValueMap.put("age", 24);
    nameAndValueMap.put("weight", new BigDecimal(80.012).setScale(3, RoundingMode.FLOOR));

    JSONObject healthRecord = new JSONObject();
    healthRecord.put("bloodType", "X");
    healthRecord.put("liquidNumber", new BigDecimal(123.91));
    healthRecord.put("hasDiabetes", false);
    nameAndValueMap.put("healthRecord", healthRecord.toString());
    parametersMap.put(ParameterEntityType.CUSTOMER, nameAndValueMap);

    return new Process(new ProcessInstanceId("123"), STARTED_DATE, parametersMap);
  }

  private void assertOutputMap(Map<String, Serializable> outputMap)
  {
    Assert.assertFalse(outputMap.isEmpty());

    Assert.assertEquals("Zorig", outputMap.get("firstName"));
    Assert.assertEquals("Mag", outputMap.get("lastName"));
    Assert.assertEquals(24, outputMap.get("age"));
    Assert.assertEquals(new BigDecimal(80.012).setScale(3, RoundingMode.FLOOR), outputMap.get("weight"));
  }

  private void assertOutputMapWithJsonParameters(Map<String, Serializable> outputMap)
  {
    Assert.assertFalse(outputMap.isEmpty());
    Assert.assertEquals(7, outputMap.size());

    Assert.assertEquals("Zorig", outputMap.get("firstName"));
    Assert.assertEquals("Mag", outputMap.get("lastName"));
    Assert.assertEquals(24, outputMap.get("age"));
    Assert.assertEquals(new BigDecimal(80.012).setScale(3, RoundingMode.FLOOR), outputMap.get("weight"));

    Assert.assertEquals("X", outputMap.get("bloodType"));
    Assert.assertEquals(new BigDecimal(123.91).setScale(3, RoundingMode.FLOOR), outputMap.get("liquidNumber"));
    Assert.assertEquals(false, outputMap.get("hasDiabetes"));
  }
}
