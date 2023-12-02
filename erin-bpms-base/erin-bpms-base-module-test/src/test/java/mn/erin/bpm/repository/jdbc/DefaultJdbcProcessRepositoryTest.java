package mn.erin.bpm.repository.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessParameterRepository;
import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRepository;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * Tests for {@link DefaultJdbcProcessRepository} which runs on local Oracle database. Please configure your test Oracle database according to
 * jdbc-datasource-test.properties
 * @author Zorig
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestBpmJdbcBeanConfig.class })
//@Ignore Activate this on Teamcity
@Transactional
// TODO : please fix failing test.
@Ignore
public class DefaultJdbcProcessRepositoryTest
{
  private static final LocalDateTime STARTED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);;
  private static final LocalDateTime FINISHED_DATE = LocalDateTime.of(LocalDate.ofYearDay(2020, 101), LocalTime.MIDNIGHT);

  @Inject
  private ProcessRepository processRepository;

  @Inject
  private JdbcProcessRepository jdbcProcessRepository;

  @Inject
  private JdbcProcessParameterRepository jdbcProcessParameterRepository;

  @Before
  public void cleanup()
  {
    // clean up database before each test run
    // so each test runs on a blank database
    jdbcProcessRepository.deleteAll();
    jdbcProcessParameterRepository.deleteAll();
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessThrowsExceptionWhenDuplicateId() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessThrowsExceptionWhenIdIsBlank() throws BpmRepositoryException
  {
    processRepository.createProcess(" ", STARTED_DATE, FINISHED_DATE, new HashMap<>());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessThrowsExceptionWhenStartedDateIsNull() throws BpmRepositoryException
  {
    processRepository.createProcess("123", null, FINISHED_DATE, new HashMap<>());
  }

  @Test
  public void createProcessWithBigDecimalParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.SALARY, Collections.singletonMap("amount", new BigDecimal(123.2))));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.SALARY).get("amount") instanceof BigDecimal );
    Assert.assertEquals(new BigDecimal(123.2).setScale(1, RoundingMode.FLOOR), returnProcess.getProcessParameters().get(ParameterEntityType.SALARY).get("amount"));
  }

  @Test
  public void createProcessWithStringParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("firstName", "String")));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("firstName") instanceof String );
    Assert.assertEquals("String", returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("firstName"));
  }

  @Test
  public void createProcessWithDoubleParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.SALARY, Collections.singletonMap("01/01/2020 salary", 3.14d)));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.SALARY).get("01/01/2020 salary") instanceof BigDecimal );
    Assert.assertEquals(new BigDecimal(3.14d).setScale(2, RoundingMode.FLOOR), returnProcess.getProcessParameters().get(ParameterEntityType.SALARY).get("01/01/2020 salary"));
  }

  @Test
  public void createProcessWithLongParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("long", 121454555L)));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("long") instanceof Long);
    Assert.assertEquals(121454555L, returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("long"));
  }

  @Test
  public void createProcessWithBooleanParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("bool", false)));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("bool") instanceof Boolean);
    Assert.assertEquals(false, returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("bool"));
  }

  @Test
  public void createProcessWithIntegerParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("age", 20)));
    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("age") instanceof Integer);
    Assert.assertEquals(20, returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("age"));
  }

  @Test
  public void createProcessWithJsonParameter() throws BpmRepositoryException
  {
    //TODO: long is being saved as an integer,

    JSONObject jsonObject = new JSONObject();
    jsonObject.put(STARTED_DATE.toString(), 123);
    jsonObject.put(FINISHED_DATE.toString(), 321);
    //jsonObject.put("long", 121454555L);
    jsonObject.put("bigDecimal", new BigDecimal(123.23).setScale(2, RoundingMode.FLOOR));
    jsonObject.put("double", 3.14d);
    jsonObject.put("boolean", true);
    jsonObject.put("string", "String");
    String jsonString = jsonObject.toString();
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("datesToNumbers", jsonString)));

    Process returnProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertTrue(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("datesToNumbers") instanceof String);
    Assert.assertEquals(jsonString, returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("datesToNumbers"));

    JSONObject checkJson = new JSONObject(returnProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("datesToNumbers").toString());
    Assert.assertEquals(123, checkJson.get(STARTED_DATE.toString()));
    Assert.assertEquals(321, checkJson.get(FINISHED_DATE.toString()));
    //Assert.assertEquals(121454555, checkJson.get("long"));
    Assert.assertEquals(3.14d, checkJson.get("double"));
    Assert.assertEquals(true, checkJson.get("boolean"));
    Assert.assertEquals("String", checkJson.get("string"));

  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessWithNotSupportedParameter() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.singletonMap(ParameterEntityType.CUSTOMER, Collections.singletonMap("float", .000003f)));
  }

  @Test
  public void createProcessWithMultipleParameters() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    assertProcessWithParameters(returnedProcess, "123");
  }

  @Test
  public void createProcessWithNoParameters() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertNotNull(returnedProcess);
    Assert.assertTrue(returnedProcess.getProcessParameters().isEmpty());
  }

  @Test
  public void createProcessReturnsCorrectProcessObject() throws BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Map<String, Serializable> paramNameAndValue = new HashMap<>();
    paramNameAndValue.put("firstName", "Zorig");
    paramNameAndValue.put("age", 24);
    parameters.put(ParameterEntityType.CUSTOMER, paramNameAndValue);
    Process createdProcess = processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, parameters);

    Assert.assertNotNull(createdProcess);
    Assert.assertEquals("123", createdProcess.getId().getId());
    Assert.assertEquals(STARTED_DATE, createdProcess.getStartedTime());
    Assert.assertEquals(FINISHED_DATE, createdProcess.getFinishedTime());
    Assert.assertEquals(1, createdProcess.getProcessParameters().size());
    Assert.assertEquals(2, createdProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).size());
    Assert.assertEquals("Zorig", createdProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("firstName"));
    Assert.assertEquals(24, createdProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).get("age"));
  }

  @Test(expected = NullPointerException.class)
  public void findByIdThrowsExceptionWhenEntityIdIsNull()
  {
    processRepository.findById(null);
  }

  @Test
  public void findByIdWhenNonExistent()
  {
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertNull(returnedProcess);
  }

  @Test
  public void findByIdWithNoParameters() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    assertProcessNoParameters(returnedProcess, "123");
  }

  @Test
  public void findByIdWithMultipleParameters() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    assertProcessWithParameters(returnedProcess, "123");
  }

  @Test
  public void findAllEmptyResult()
  {
    Collection<Process> returnedProcesses = processRepository.findAll();
    Assert.assertTrue(returnedProcesses.isEmpty());
  }

  @Test
  public void findAllWithNoParameters() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
    processRepository.createProcess("456", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());

    Collection<Process> returnedProcesses = processRepository.findAll();

    Assert.assertEquals(2, returnedProcesses.size());

    Iterator<Process> returnedProcessIterator = returnedProcesses.iterator();
    assertProcessNoParameters(returnedProcessIterator.next(), "123");
    assertProcessNoParameters(returnedProcessIterator.next(), "456");
  }

  @Test
  public void findAllWithMultipleParameters() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    createProcessWithParameters("456");

    Collection<Process> returnedProcesses = processRepository.findAll();
    Iterator<Process> returnedProcessesIterator = returnedProcesses.iterator();

    assertProcessWithParameters(returnedProcessesIterator.next(), "123");
    assertProcessWithParameters(returnedProcessesIterator.next(), "456");
  }

  @Test
  public void findAllWithParametersAndNoParameters() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, FINISHED_DATE, Collections.emptyMap());
    createProcessWithParameters("456");

    Collection<Process> returnedProcesses = processRepository.findAll();
    Iterator<Process> returnedProcessesIterator = returnedProcesses.iterator();

    assertProcessNoParameters(returnedProcessesIterator.next(), "123");
    assertProcessWithParameters(returnedProcessesIterator.next(), "456");
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateParametersThrowsExceptionWhenParameterMapIsNull() throws BpmRepositoryException
  {
    processRepository.updateParameters("id", null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateParametersThrowsExceptionWhenProcessInstanceIdIsBlank() throws BpmRepositoryException
  {
    processRepository.updateParameters(null, new HashMap<>());
  }

  @Test
  public void updateParametersWhenParameterDataTypeIsNull() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap("firstName", null));

    int numUpdated = processRepository.updateParameters("123", parameters);

    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));

    Assert.assertEquals(0, numUpdated);
    assertProcessWithParameters(returnedProcess, "123");
  }

  @Test
  public void updateParametersWhenParameterIsNonExistent() throws BpmRepositoryException
  {
    createProcessWithParameters("123");

    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap("lastName", "Mag"));

    int numUpdated = processRepository.updateParameters("123", parameters);
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));

    Assert.assertEquals(1, numUpdated);

    JSONObject salaryHistory = new JSONObject();
    salaryHistory.put("1/1/2020", 9999);
    salaryHistory.put("2/1/2020", 8888);

    Assert.assertNotNull(returnedProcess);
    Assert.assertEquals("123", returnedProcess.getId().getId());
    Assert.assertEquals(STARTED_DATE, returnedProcess.getStartedTime());
    Assert.assertEquals(FINISHED_DATE, returnedProcess.getFinishedTime());
    Map<ParameterEntityType, Map<String, Serializable>> returnedProcessParameters = returnedProcess.getProcessParameters();

    Assert.assertEquals(2, returnedProcessParameters.size());
    Assert.assertEquals("Zorig", returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("firstName"));
    Assert.assertEquals("Mag", returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("lastName"));
    Assert.assertEquals(24, returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("age"));
    JSONObject returnedSalaryHistory = new JSONObject(returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory").toString());
    Assert.assertEquals(salaryHistory.toString(), returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory"));
    Assert.assertEquals(9999, returnedSalaryHistory.get("1/1/2020"));
    Assert.assertEquals(8888, returnedSalaryHistory.get("2/1/2020"));
  }

  @Test
  public void updateParametersWhenParameterExists() throws BpmRepositoryException
  {
    createProcessWithParameters("123");

    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    parameters.put(ParameterEntityType.CUSTOMER, Collections.singletonMap("firstName", "Erin"));

    int numUpdated = processRepository.updateParameters("123", parameters);
    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));

    JSONObject salaryHistory = new JSONObject();
    salaryHistory.put("1/1/2020", 9999);
    salaryHistory.put("2/1/2020", 8888);

    Assert.assertEquals(1, numUpdated);
    Assert.assertNotNull(returnedProcess);
    Assert.assertEquals("123", returnedProcess.getId().getId());
    Assert.assertEquals(STARTED_DATE, returnedProcess.getStartedTime());
    Assert.assertEquals(FINISHED_DATE, returnedProcess.getFinishedTime());
    Map<ParameterEntityType, Map<String, Serializable>> returnedProcessParameters = returnedProcess.getProcessParameters();

    Assert.assertEquals(2, returnedProcessParameters.size());
    Assert.assertEquals("Erin", returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("firstName"));
    Assert.assertEquals(24, returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("age"));
    JSONObject returnedSalaryHistory = new JSONObject(returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory").toString());
    Assert.assertEquals(salaryHistory.toString(), returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory"));
    Assert.assertEquals(9999, returnedSalaryHistory.get("1/1/2020"));
    Assert.assertEquals(8888, returnedSalaryHistory.get("2/1/2020"));
  }

  @Test
  public void updateParameterWhenProcessIsNonExistent() throws BpmRepositoryException
  {
    int numUpdated = processRepository.updateParameters("123", Collections.emptyMap());
    Assert.assertEquals(0, numUpdated);
  }

  @Test(expected = BpmRepositoryException.class)
  public void deleteProcessThrowsExceptionWhenIdIsBlank() throws BpmRepositoryException
  {
    processRepository.deleteProcess("");
  }

  @Test
  public void deleteProcessWhenNonExistent() throws BpmRepositoryException
  {
    boolean isDeleted = processRepository.deleteProcess("123");
    Assert.assertTrue(isDeleted);
  }

  @Test
  public void deleteProcessWhenExistent() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    boolean isDeleted = processRepository.deleteProcess("123");
    Assert.assertTrue(isDeleted);

    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertNull(returnedProcess);
  }

  @Test(expected = BpmRepositoryException.class)
  public void filterByEntityTypeThrowsExceptionWhenIdIsBlank() throws BpmRepositoryException
  {
    processRepository.filterByInstanceIdAndEntityType("", ParameterEntityType.ACCOUNT);
  }

  @Test(expected = BpmRepositoryException.class)
  public void filterByEntityTypeThrowsExceptionWhenEntityTypeIsNull() throws BpmRepositoryException
  {
    processRepository.filterByInstanceIdAndEntityType("123", null);
  }

  @Test
  public void filterByEntityIdWhenEmptyResult() throws BpmRepositoryException
  {
    Process returnedProcess = processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER);
    Assert.assertNull(returnedProcess);
  }

  @Test
  public void filterByEntityIdWhenNonEmptyResult() throws BpmRepositoryException
  {
    createProcessWithParameters("123");

    Process returnedProcess = processRepository.filterByInstanceIdAndEntityType("123", ParameterEntityType.CUSTOMER);

    Assert.assertNotNull(returnedProcess);
    Assert.assertFalse(returnedProcess.getProcessParameters().isEmpty());
    Assert.assertEquals(1, returnedProcess.getProcessParameters().size());
    Assert.assertEquals(2, returnedProcess.getProcessParameters().get(ParameterEntityType.CUSTOMER).size());

    for (Map.Entry<ParameterEntityType, Map<String, Serializable>> parameter: returnedProcess.getProcessParameters().entrySet())
    {
      Assert.assertTrue(parameter.getKey().toString().equals("CUSTOMER"));
    }
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateFinishedDateThrowsExceptionWhenIdIsBlank() throws BpmRepositoryException
  {
    processRepository.updateFinishedDate("", FINISHED_DATE);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateFinishedDateThrowsExceptionWhenDateIsNull() throws BpmRepositoryException
  {
    processRepository.updateFinishedDate("123", null);
  }

  @Test
  public void updateFinishedDateSuccessful() throws BpmRepositoryException
  {
    processRepository.createProcess("123", STARTED_DATE, null, new HashMap<>());
    boolean isUpdated = processRepository.updateFinishedDate("123", STARTED_DATE);
    Assert.assertTrue(isUpdated);

    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));

    Assert.assertEquals(STARTED_DATE, returnedProcess.getFinishedTime());
  }

  @Test
  public void updateFinishedDateUnsuccessful() throws BpmRepositoryException
  {
    boolean isUpdated = processRepository.updateFinishedDate("123", STARTED_DATE);
    Assert.assertFalse(isUpdated);
  }

  @Test(expected = BpmRepositoryException.class)
  public void deleteEntityThrowsExeptionWhenNullEntityType() throws BpmRepositoryException
  {
    processRepository.deleteEntity("123", null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void deleteEntityThrowsExeptionWhenIdIsBlank() throws BpmRepositoryException
  {
    processRepository.deleteEntity("", ParameterEntityType.SALARY);
  }

  @Test
  public void deleteEntityReturnsFalseWhenEntityDoesNotExist() throws BpmRepositoryException
  {
    boolean isDeleted = processRepository.deleteEntity("123", ParameterEntityType.SALARY);
    Assert.assertFalse(isDeleted);
  }

  @Test
  public void deleteEntityReturnsTrueWhenEntityIsDeleted() throws BpmRepositoryException
  {
    createProcessWithParameters("123");
    boolean isDeleted = processRepository.deleteEntity("123", ParameterEntityType.SALARY);

    Assert.assertTrue(isDeleted);

    Process returnedProcess = processRepository.findById(new ProcessInstanceId("123"));
    Assert.assertFalse(returnedProcess.getProcessParameters().containsKey(ParameterEntityType.SALARY));
  }

  private void assertProcessNoParameters(Process returnedProcess, String id)
  {
    Assert.assertNotNull(returnedProcess);
    Assert.assertEquals(id, returnedProcess.getId().getId());
    Assert.assertEquals(STARTED_DATE, returnedProcess.getStartedTime());
    Assert.assertEquals(FINISHED_DATE, returnedProcess.getFinishedTime());
    Assert.assertTrue(returnedProcess.getProcessParameters().isEmpty());
  }

  private void assertProcessWithParameters(Process returnedProcess, String id)
  {
    JSONObject salaryHistory = new JSONObject();
    salaryHistory.put("1/1/2020", 9999);
    salaryHistory.put("2/1/2020", 8888);

    Assert.assertNotNull(returnedProcess);
    Assert.assertEquals(id, returnedProcess.getId().getId());
    Assert.assertEquals(STARTED_DATE, returnedProcess.getStartedTime());
    Assert.assertEquals(FINISHED_DATE, returnedProcess.getFinishedTime());
    Map<ParameterEntityType, Map<String, Serializable>> returnedProcessParameters = returnedProcess.getProcessParameters();

    Assert.assertEquals(2, returnedProcessParameters.size());
    Assert.assertEquals(2, returnedProcessParameters.get(ParameterEntityType.CUSTOMER).size());
    Assert.assertEquals(1, returnedProcessParameters.get(ParameterEntityType.SALARY).size());
    Assert.assertEquals("Zorig", returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("firstName"));
    Assert.assertEquals(24, returnedProcessParameters.get(ParameterEntityType.CUSTOMER).get("age"));
    JSONObject returnedSalaryHistory = new JSONObject(returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory").toString());
    Assert.assertEquals(salaryHistory.toString(), returnedProcessParameters.get(ParameterEntityType.SALARY).get("salaryHistory"));
    Assert.assertEquals(9999, returnedSalaryHistory.get("1/1/2020"));
    Assert.assertEquals(8888, returnedSalaryHistory.get("2/1/2020"));
  }

  private void createProcessWithParameters(String id) throws BpmRepositoryException
  {
    Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
    Map<String, Serializable> paramNameAndValue = new HashMap<>();
    paramNameAndValue.put("firstName", "Zorig");
    paramNameAndValue.put("age", 24);
    parameters.put(ParameterEntityType.CUSTOMER, paramNameAndValue);
    JSONObject salaryHistory = new JSONObject();
    salaryHistory.put("1/1/2020", 9999);
    salaryHistory.put("2/1/2020", 8888);
    parameters.put(ParameterEntityType.SALARY, Collections.singletonMap("salaryHistory", salaryHistory.toString()));

    processRepository.createProcess(id, STARTED_DATE, FINISHED_DATE, parameters);
  }

}
