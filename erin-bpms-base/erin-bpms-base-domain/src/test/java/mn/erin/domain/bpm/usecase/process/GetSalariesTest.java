package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
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
public class GetSalariesTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.GetSalaries";

  private static final LocalDateTime STARTED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
  private static final LocalDateTime FINISHED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 101), LocalTime.MIDNIGHT);

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRepository processRepository;
  private GetSalaries useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new GetSalaries(authenticationService, authorizationService, processRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetSalaries(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new GetSalaries(null, authorizationService, processRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new GetSalaries(authenticationService, null, processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_blank() throws UseCaseException
  {
    useCase.execute("");
  }

  @Test
  public void when_returned_process_is_null() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.SALARY)).thenReturn(null);

    GetSalariesOutput output = useCase.execute("123");

    Assert.assertTrue(output.getSalariesInfo().isEmpty());
    Assert.assertEquals(0, output.getAverageBeforeTax());
    Assert.assertEquals(0, output.getAverageAfterTax());
  }

  @Test
  public void when_returned_process_is_not_null() throws BpmRepositoryException, UseCaseException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Map<String, Serializable> innerMap = new HashMap<>();

    JSONObject salaryInfo1 = new JSONObject();
    salaryInfo1.put("ndsh", 1);
    salaryInfo1.put("hhoat", 1);
    salaryInfo1.put("salaryBeforeTax", 1);
    salaryInfo1.put("salaryAfterTax", 1);
    innerMap.put("2020-04-01", salaryInfo1.toString());

    JSONObject salaryInfo2 = new JSONObject();
    salaryInfo2.put("ndsh", 2);
    salaryInfo2.put("hhoat", 2);
    salaryInfo2.put("salaryBeforeTax", 2);
    salaryInfo2.put("salaryAfterTax", 2);
    innerMap.put("2019-02-01", salaryInfo2.toString());

    JSONObject salaryInfo3 = new JSONObject();
    salaryInfo3.put("ndsh", 3);
    salaryInfo3.put("hhoat", 3);
    salaryInfo3.put("salaryBeforeTax", 3);
    salaryInfo3.put("salaryAfterTax", 3);
    innerMap.put("2018-04-01", salaryInfo3.toString());

    innerMap.put("averageBeforeTax", 100);
    innerMap.put("averageAfterTax", 100);

    parameters.put(ParameterEntityType.SALARY, innerMap);

    Process processToReturn = new Process(new ProcessInstanceId("123"), STARTED_DATE, FINISHED_DATE, null, null, parameters);

    Mockito.when(processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.SALARY)).thenReturn(processToReturn);

    GetSalariesOutput output = useCase.execute("123");

    Assert.assertFalse(output.getSalariesInfo().isEmpty());
    Assert.assertEquals(100, output.getAverageBeforeTax());
    Assert.assertEquals(100, output.getAverageAfterTax());

    Map<String, Serializable> firstDate = output.getSalariesInfo().get("2020-04-01");
    Map<String, Serializable> secondDate = output.getSalariesInfo().get("2019-02-01");
    Map<String, Serializable> thirdDate = output.getSalariesInfo().get("2018-04-01");

    Assert.assertEquals(1, firstDate.get("ndsh"));
    Assert.assertEquals(1, firstDate.get("hhoat"));
    Assert.assertEquals(1, firstDate.get("salaryBeforeTax"));
    Assert.assertEquals(1, firstDate.get("salaryAfterTax"));

    Assert.assertEquals(2, secondDate.get("ndsh"));
    Assert.assertEquals(2, secondDate.get("hhoat"));
    Assert.assertEquals(2, secondDate.get("salaryBeforeTax"));
    Assert.assertEquals(2, secondDate.get("salaryAfterTax"));

    Assert.assertEquals(3, thirdDate.get("ndsh"));
    Assert.assertEquals(3, thirdDate.get("hhoat"));
    Assert.assertEquals(3, thirdDate.get("salaryBeforeTax"));
    Assert.assertEquals(3, thirdDate.get("salaryAfterTax"));
  }
}
