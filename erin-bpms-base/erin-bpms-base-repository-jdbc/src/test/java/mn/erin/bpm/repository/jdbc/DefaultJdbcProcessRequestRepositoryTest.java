package mn.erin.bpm.repository.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRequestParameterRepository;
import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessRequestRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequest;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestJoined;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessRequestParameter;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

import static org.mockito.Mockito.when;

/**
 * @author EBazarragchaa
 */
@Ignore
// TODO :fix test
public class DefaultJdbcProcessRequestRepositoryTest
{
  private static final ProcessTypeId PROCESS_TYPE_ID = ProcessTypeId.valueOf("processType");
  private static final GroupId GROUP_ID = GroupId.valueOf("101");
  private static final String USER_ID = "user";
  private static final String CIF_PARAMETER_VALUE = "12345678";
  private static final String PROCESS_REQUEST_ID = "p1";

  private JdbcProcessRequestRepository mockJdbcProcessRequestRepository;
  private JdbcProcessRequestParameterRepository mockJdbcProcessRequestParameterRepository;
  private DefaultJdbcProcessRequestRepository defaultJdbcProcessRequestRepository;

  @Before
  public void initTest()
  {
    this.mockJdbcProcessRequestRepository = Mockito.mock(JdbcProcessRequestRepository.class);
    this.mockJdbcProcessRequestParameterRepository = Mockito.mock(JdbcProcessRequestParameterRepository.class);
    this.defaultJdbcProcessRequestRepository = new DefaultJdbcProcessRequestRepository(mockJdbcProcessRequestRepository,
        mockJdbcProcessRequestParameterRepository);
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestThrowsExceptionNullProcessTypeId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.createProcessRequest(null, GROUP_ID, USER_ID, new HashMap<>());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestThrowsExceptionNullGroupId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, null, USER_ID, new HashMap<>());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestThrowsExceptionNullUserId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, null, new HashMap<>());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestThrowsExceptionEmptyUserId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, "", new HashMap<>());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestThrowsExceptionNullParameters() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void whenThrowExceptionGetByCreatedDate() throws BpmRepositoryException
  {
    Date startDate = new Date();
    Date endDate = new Date();

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByCreatedDate(startDate, endDate, CIF_PARAMETER_VALUE)).thenThrow(RuntimeException.class);
    defaultJdbcProcessRequestRepository.findAllByCreatedDateInterval(CIF_PARAMETER_VALUE, startDate, endDate);
  }

  @Test
  public void whenFoundByCreatedDateInterval() throws BpmRepositoryException
  {
    Date startDate = new Date();
    Date endDate = new Date();

    JdbcProcessRequestJoined filteredDateIntervalRequest = getProcessRequestJoined(PROCESS_REQUEST_ID);
    List<JdbcProcessRequestJoined> filteredList = new ArrayList<>();
    filteredList.add(filteredDateIntervalRequest);

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByCreatedDate(startDate, endDate, CIF_PARAMETER_VALUE))
        .thenReturn(filteredList);

    String processRequestId = filteredDateIntervalRequest.getProcessRequestId();

    JdbcProcessRequestJoined allJoinedRequests = getProcessRequestJoined(PROCESS_REQUEST_ID);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId(processRequestId))
        .thenReturn(Arrays.asList(allJoinedRequests));

    Collection<ProcessRequest> foundProcessRequests = defaultJdbcProcessRequestRepository
        .findAllByCreatedDateInterval(CIF_PARAMETER_VALUE, startDate, endDate);
    Assert.assertEquals(1, foundProcessRequests.size());
  }

  @Test
  public void whenJoinedRequestContainNull() throws BpmRepositoryException
  {
    Date startDate = new Date();
    Date endDate = new Date();

    JdbcProcessRequestJoined filteredDateIntervalRequest = getProcessRequestJoined(PROCESS_REQUEST_ID);
    List<JdbcProcessRequestJoined> filteredList = new ArrayList<>();

    filteredList.add(null);
    filteredList.add(filteredDateIntervalRequest);
    filteredList.add(null);

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByCreatedDate(startDate, endDate, CIF_PARAMETER_VALUE))
        .thenReturn(filteredList);

    JdbcProcessRequestJoined allJoinedRequests = getProcessRequestJoined(PROCESS_REQUEST_ID);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId(PROCESS_REQUEST_ID))
        .thenReturn(Arrays.asList(allJoinedRequests));

    Collection<ProcessRequest> foundProcessRequests = defaultJdbcProcessRequestRepository
        .findAllByCreatedDateInterval(CIF_PARAMETER_VALUE, startDate, endDate);

    Assert.assertEquals(1, foundProcessRequests.size());
  }

  @Test
  public void createProcessRequestCreatesWithEmptyParameters() throws BpmRepositoryException
  {
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, new HashMap<>());
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);

    Assert.assertNotNull(processRequest);
    Assert.assertTrue(processRequest.getParameters().isEmpty());
  }

  @Test
  public void createProcessRequestCreatesWithStringParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("assignedUserId", "johndoe");
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "assignedUserId", "johndoe", "String");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test
  public void createProcessRequestCreatesWithIntegerParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("age", 20);
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "age", 20, "Integer");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test
  public void createProcessRequestCreatesWithBooleanParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("isSingle", true);
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "isSingle", true, "Boolean");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test
  public void createProcessRequestCreatesWithDoubleParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("amount", 3.14d);
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "amount", "3.14", "BigDecimal");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test
  public void createProcessRequestCreatesWithBigDecimalParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("amount", new BigDecimal(123467.89));
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "amount", "123,467.89", "BigDecimal");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test
  public void createProcessRequestCreatesWithLongParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("timestamp", 121454555L);
    Mockito.when(mockJdbcProcessRequestRepository.getCurrentIncrementId()).thenReturn("1");
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
    checkJdbcProcessRequestRepositoryInsertInvoked(processRequest);
    checkJdbcProcessRequestParameterRepositoryInsertInvoked(processRequest.getId().getId(), "timestamp", 121454555L, "Long");

    Assert.assertNotNull(processRequest);
    Assert.assertEquals(1, processRequest.getParameters().size());
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessRequestCreatesWithNotSupportedParameter() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("float", .000003f);
    ProcessRequest processRequest = defaultJdbcProcessRequestRepository.createProcessRequest(PROCESS_TYPE_ID, GROUP_ID, USER_ID, parameters);
  }

  @Test(expected = BpmRepositoryException.class)
  public void findAllByAssignedUserIdThrowsExceptionWhenUserIdIsNull() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.findAllByAssignedUserId(null);
  }

  @Test
  public void findAllByAssignedUserIdWhenFindQueryReturnsEmpty() throws BpmRepositoryException
  {
    Mockito.when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByAssignedUserId("Any Group Id")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("Any Group Id"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("Any Group Id");

    Assert.assertEquals(0, result.size());
  }

  @Ignore
  // TODO :fix test
  @Test
  public void findAllByAssignedUserIdProcessRequestsWithParametersAndNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    JdbcProcessRequest jdbcProcessRequest = new JdbcProcessRequest();
    jdbcProcessRequest.setProcessRequestState("NEW");
    jdbcProcessRequest.setGroupId("GR1");
    jdbcProcessRequest.setProcessTypeId("123");
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    LocalDateTime assignedTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.MIDNIGHT);
    jdbcProcessRequest.setCreatedTime(createdTime);
    jdbcProcessRequest.setAssignedTime(assignedTime);
    jdbcProcessRequest.setProcessRequestId("1");
    jdbcProcessRequest.setRequestedUserId("1");
    jdbcProcessRequest.setAssignedUserId("Assigned User 1");
    jdbcProcessRequestList.add(jdbcProcessRequest);

    JdbcProcessRequest jdbcProcessRequest2 = new JdbcProcessRequest();
    jdbcProcessRequest2.setProcessRequestState("NEW");
    jdbcProcessRequest2.setGroupId("GR1");
    jdbcProcessRequest2.setProcessTypeId("123");
    jdbcProcessRequest2.setCreatedTime(createdTime);
    jdbcProcessRequest2.setAssignedTime(assignedTime);
    jdbcProcessRequest2.setProcessRequestId("2");
    jdbcProcessRequest2.setRequestedUserId("1");
    jdbcProcessRequest2.setAssignedUserId("Assigned User 1");
    jdbcProcessRequestList.add(jdbcProcessRequest2);

    Mockito.when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByAssignedUserId("Assigned User 1")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");
    Iterator<JdbcProcessRequestJoined> jdbcProcessRequestJoinedIterator = jdbcJoinedProcessRequests1.iterator();
    while (jdbcProcessRequestJoinedIterator.hasNext())
    {
      JdbcProcessRequestJoined currentJdbcProcessRequestJoined = jdbcProcessRequestJoinedIterator.next();
      currentJdbcProcessRequestJoined.setAssignedUserId("Assigned User 1");
      currentJdbcProcessRequestJoined.setAssignedTime(assignedTime);
    }

    Mockito.when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    Mockito.when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByAssignedUserId(new UserId("Assigned User 1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByAssignedUserId("Assigned User 1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    Assert.assertEquals("1", returnedProcessRequest1.getId().getId());
    Assert.assertEquals(ProcessRequestState.NEW, returnedProcessRequest1.getState());
    Assert.assertEquals("GR1", returnedProcessRequest1.getGroupId().getId());
    Assert.assertEquals("123", returnedProcessRequest1.getProcessTypeId().getId());
    Assert.assertEquals(createdTime, returnedProcessRequest1.getCreatedTime());
    Assert.assertEquals("1", returnedProcessRequest1.getRequestedUserId());
    Assert.assertEquals("Assigned User 1", returnedProcessRequest1.getAssignedUserId().getId());
    Assert.assertEquals(assignedTime, returnedProcessRequest1.getAssignedTime());
    Assert.assertNull(returnedProcessRequest1.getProcessInstanceId());

    Assert.assertEquals("2", returnedProcessRequest2.getId().getId());
    Assert.assertEquals(ProcessRequestState.NEW, returnedProcessRequest2.getState());
    Assert.assertEquals("GR1", returnedProcessRequest2.getGroupId().getId());
    Assert.assertEquals("123", returnedProcessRequest2.getProcessTypeId().getId());
    Assert.assertEquals(createdTime, returnedProcessRequest2.getCreatedTime());
    Assert.assertEquals("1", returnedProcessRequest2.getRequestedUserId());
    Assert.assertEquals("Assigned User 1", returnedProcessRequest2.getAssignedUserId().getId());
    Assert.assertEquals(assignedTime, returnedProcessRequest2.getAssignedTime());
    Assert.assertNull(returnedProcessRequest2.getProcessInstanceId());
    Assert.assertTrue(returnedProcessRequest2.getParameters().isEmpty());


    Map<String, Serializable> parameters1 = returnedProcessRequest1.getParameters();
    assertParameters(parameters1);
  }

  @Test(expected = BpmRepositoryException.class)
  public void findAllByUserGroupIdThrowsExceptionNullGroupId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.findAllByUserGroupId(null);
  }

  @Test
  public void findAllByUserGroupIdEmptyResultWhenFindingByGroupIdQuery() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId(GROUP_ID.getId())).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(GROUP_ID);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId(GROUP_ID.getId());

    Assert.assertEquals(0, result.size());
  }

  @Test
  public void findAllByUserGroupIdProcessRequestsNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> resultProcessRequestsIterator = result.iterator();
    ProcessRequest actualProcessRequest1 = resultProcessRequestsIterator.next();
    ProcessRequest actualProcessRequest2 = resultProcessRequestsIterator.next();

    assertProcessRequestNoParameters(actualProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(actualProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllByUserGroupIdJoinedProcessRequestsWithParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");
    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests2 = createJoinedJdbcProcessRequests("2");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(jdbcJoinedProcessRequests2);

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestWithParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllByUserGroupIdProcessRequestsWithParametersAndNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Ignore
  // TODO :fix test
  @Test
  public void convertToProcessRequestFromJdbcProcessRequestJoinedNonNullAssignedUserId() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);
    jdbcProcessRequest.setAssignedUserId("Assigned User 1");
    jdbcProcessRequestList.add(jdbcProcessRequest);

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");
    jdbcJoinedProcessRequests1.get(0).setAssignedUserId("Assigned User 1");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertEquals(1, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();

    Assert.assertEquals("1", returnedProcessRequest1.getId().getId());
    Assert.assertEquals(ProcessRequestState.NEW, returnedProcessRequest1.getState());
    Assert.assertEquals("GR1", returnedProcessRequest1.getGroupId().getId());
    Assert.assertEquals("123", returnedProcessRequest1.getProcessTypeId().getId());
    Assert.assertEquals(createdTime, returnedProcessRequest1.getCreatedTime());
    Assert.assertEquals("1", returnedProcessRequest1.getRequestedUserId());
    Assert.assertEquals("Assigned User 1", returnedProcessRequest1.getAssignedUserId().getId());
    Assert.assertNull(returnedProcessRequest1.getAssignedTime());
    Assert.assertNull(returnedProcessRequest1.getProcessInstanceId());

    Map<String, Serializable> parameters1 = returnedProcessRequest1.getParameters();

    assertParameters(parameters1);
  }

  @Test
  public void convertToProcessRequestFromJdbcProcessRequestNonNullAssignedUserId() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);
    jdbcProcessRequest.setAssignedUserId("Assigned User 1");
    jdbcProcessRequestList.add(jdbcProcessRequest);

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertEquals(1, result.size());

    Iterator<ProcessRequest> resultProcessRequestsIterator = result.iterator();
    ProcessRequest actualProcessRequest1 = resultProcessRequestsIterator.next();

    Assert.assertEquals("1", actualProcessRequest1.getId().getId());
    Assert.assertEquals("123", actualProcessRequest1.getProcessTypeId().getId());
    Assert.assertEquals("GR1", actualProcessRequest1.getGroupId().getId());
    Assert.assertEquals("1", actualProcessRequest1.getRequestedUserId());
    Assert.assertEquals(createdTime, actualProcessRequest1.getCreatedTime());
    Assert.assertEquals(ProcessRequestState.NEW, actualProcessRequest1.getState());
    Assert.assertEquals("Assigned User 1", actualProcessRequest1.getAssignedUserId().getId());
    Assert.assertNull(actualProcessRequest1.getAssignedTime());
    Assert.assertNull(actualProcessRequest1.getProcessInstanceId());
    Assert.assertTrue(actualProcessRequest1.getParameters().isEmpty());
  }

  @Ignore
  // TODO :fix test
  @Test
  public void saveAsRightTypeReturnsNullWhenParseExceptionBigDecimal() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByGroupId("GR1")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = new ArrayList<>();
    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd1 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd1.setProcessRequestId("1");
    jdbcProcessRequestJoinedToAdd1.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd1.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd1.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd1.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd1.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd1.setParameterName("BigDecimalName");
    jdbcProcessRequestJoinedToAdd1.setParameterValue("f");
    jdbcProcessRequestJoinedToAdd1.setParameterType("BigDecimal");
    jdbcJoinedProcessRequests1.add(jdbcProcessRequestJoinedToAdd1);

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByUserGroupId(new GroupId("GR1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByGroupId("GR1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertEquals(1, result.size());

    Iterator<ProcessRequest> resultProcessRequestsIterator = result.iterator();
    ProcessRequest actualProcessRequest1 = resultProcessRequestsIterator.next();

    Map<String, Serializable> parameters1 = actualProcessRequest1.getParameters();

    Assert.assertEquals(true, parameters1.containsKey("BigDecimalName"));
    Assert.assertEquals(null, parameters1.get("BigDecimalName"));
  }

  @Test(expected = BpmRepositoryException.class)
  public void findAllByProcessTypeNullProcessType() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.findAllByProcessType(null, null);
  }

  @Test
  public void findAllByProcessTypeQueryFindByProcessTypeReturnsEmpty() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByProcessTypeId("123")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByProcessType("123", null);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByProcessTypeId("123");

    Assert.assertEquals(0, result.size());
  }

  @Test
  public void findAllByProcessTypeQueryFindByProcessTypeNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByProcessTypeId("123")).thenReturn(jdbcProcessRequestList);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByProcessType("123", null);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByProcessTypeId("123");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> resultProcessRequestsIterator = result.iterator();
    ProcessRequest actualProcessRequest1 = resultProcessRequestsIterator.next();
    ProcessRequest actualProcessRequest2 = resultProcessRequestsIterator.next();

    assertProcessRequestNoParameters(actualProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(actualProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllByProcessTypeJoinedProcessRequestsWithParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByProcessTypeId("123")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");
    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests2 = createJoinedJdbcProcessRequests("2");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(jdbcJoinedProcessRequests2);

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByProcessType("123", null);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByProcessTypeId("123");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestWithParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllByProcessTypeProcessRequestsWithParametersAndNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestsByProcessTypeId("123")).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAllByProcessType("123", null);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestsByProcessTypeId("123");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateAssignedUserIdThrowsExceptionWhenUserIdIsNull() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateAssignedUser("1", null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateAssignedUserIdThrowsExceptionWhenProcessRequestIdIsBlank() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateAssignedUser("", new UserId("Assigned User 1"));
  }

  @Ignore
  @Test
  public void updateAssignedUserIdUnsuccessfulUpdateQuery() throws BpmRepositoryException
  {
    //TODO: Unable to test LocalDateTime with Mockito, needs a Module test
    LocalDateTime assignedTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.MIDNIGHT);

    Mockito.when(mockJdbcProcessRequestRepository.updateAssignedUserId("123", "Assigned User 1", Mockito.any())).thenReturn(0);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateAssignedUser("123", new UserId("Assigned User 1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateAssignedUserId("123", "Assigned User 1", assignedTime);

    Assert.assertFalse(isUpdated);
  }

  @Ignore
  @Test
  public void updateAssignedUserIdSuccessfulUpdateQuery() throws BpmRepositoryException
  {
    //TODO: Unable to test LocalDateTime with Mockito, needs a Module test
    LocalDateTime assignedTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.MIDNIGHT);

    Mockito.when(mockJdbcProcessRequestRepository.updateAssignedUserId("123", "Assigned User 1", Mockito.any())).thenReturn(1);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateAssignedUser("123", new UserId("Assigned User 1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateAssignedUserId("123", "Assigned User 1", assignedTime);

    Assert.assertTrue(isUpdated);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateStateNullState() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateState("123", null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateStateBlankProcessRequestId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateState("", ProcessRequestState.STARTED);
  }

  @Test
  public void updateStateUnsuccessfulUpdateQuery() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.updateState("123", "STARTED")).thenReturn(0);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateState("123", ProcessRequestState.STARTED);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateState("123", "STARTED");

    Assert.assertFalse(isUpdated);
  }

  @Test
  public void updateStateSuccessfulUpdateQuery() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.updateState("123", "STARTED")).thenReturn(1);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateState("123", ProcessRequestState.STARTED);

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateState("123", "STARTED");

    Assert.assertTrue(isUpdated);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateProcessInstanceIdBlankProcessInstanceId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateProcessInstanceId("123", "");
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateProcessInstanceIdBlankProcessRequestId() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateProcessInstanceId("", "123");
  }

  @Test
  public void updateProcessInstanceIdUnsuccessfulUpdateQuery() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.updateProcessInstanceId("123", "123")).thenReturn(0);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateProcessInstanceId("123", "123");

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateProcessInstanceId("123", "123");

    Assert.assertFalse(isUpdated);
  }

  @Test
  public void updateProcessInstanceIdSuccessfulUpdateQuery() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.updateProcessInstanceId("123", "123")).thenReturn(1);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateProcessInstanceId("123", "123");

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).updateProcessInstanceId("123", "123");

    Assert.assertTrue(isUpdated);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateParametersThrowsExceptionWhenParameterMapIsNull() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateParameters("id", null);
  }

  @Test(expected = BpmRepositoryException.class)
  public void updateParametersThrowsExceptionWhenProcessRequestIdIsBlank() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.updateParameters("", new HashMap<>());
  }

  @Test
  public void updateParametersWhenParameterTypeIsNull() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("parameter name", null);
    parameters.put("parameter name 2", null);

    defaultJdbcProcessRequestRepository.updateParameters("1", parameters);
    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(0)).updateParameter(Mockito.any(), Mockito.any(), Mockito.any());
    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(0)).insert(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
  }

  @Test
  public void updateParametersWhenParameterTypeIsBigDecimal() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("BigDecimal 1 Exists", new BigDecimal(12));
    parameters.put("BigDecimal 2 Doesn't Exist", new BigDecimal(123));

    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "BigDecimal 1 Exists")).thenReturn(new JdbcProcessRequestParameter());
    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "BigDecimal 2 Doesn't Exist")).thenReturn(null);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateParameters("1", parameters);

    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).updateParameter("1", "BigDecimal 1 Exists", "12");
    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).insert("1", "BigDecimal 2 Doesn't Exist", "123", "BigDecimal");

    Assert.assertTrue(isUpdated);
  }

  @Test
  public void updateParameterWhenDoesntExist() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("String Doesn't Exist", "New String");

    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "String Doesn't Exist")).thenReturn(null);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateParameters("1", parameters);

    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).insert("1", "String Doesn't Exist", "New String", "String");

    Assert.assertTrue(isUpdated);
  }

  @Test
  public void updateParameterWhenExists() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("String Exists", "New String");

    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "String Exists")).thenReturn(new JdbcProcessRequestParameter());

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateParameters("1", parameters);

    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).updateParameter("1", "String Exists", "New String");

    Assert.assertTrue(isUpdated);
  }

  @Test
  public void updateMultipleParametersWhenExistsAndDoesntExist() throws BpmRepositoryException
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("BigDecimal 1 Exists", new BigDecimal(12));
    parameters.put("BigDecimal 2 Doesn't Exist", new BigDecimal(123));
    parameters.put("String 1 Exists", "New Value");
    parameters.put("Integer 1 Doesn't Exist", "Inserted New Value");

    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "BigDecimal 1 Exists")).thenReturn(new JdbcProcessRequestParameter());
    when(mockJdbcProcessRequestParameterRepository.getByParameterName("1", "BigDecimal 2 Doesn't Exist")).thenReturn(null);

    boolean isUpdated = defaultJdbcProcessRequestRepository.updateParameters("1", parameters);

    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).updateParameter("1", "BigDecimal 1 Exists", "12");
    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1)).insert("1", "BigDecimal 2 Doesn't Exist", "123", "BigDecimal");

    Assert.assertTrue(isUpdated);
  }

  @Test
  public void findByIdReturnEmptyOptional()
  {
    when(mockJdbcProcessRequestRepository.findById("123")).thenReturn(Optional.empty());

    ProcessRequest result = defaultJdbcProcessRequestRepository.findById(new ProcessRequestId("123"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findById("123");

    Assert.assertNull(result);
  }

  @Test
  public void findByIdReturnNonEmptyOptionalNoParameters()
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);

    when(mockJdbcProcessRequestRepository.findById("1")).thenReturn(Optional.of(jdbcProcessRequest));
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());

    ProcessRequest result = defaultJdbcProcessRequestRepository.findById(new ProcessRequestId("1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findById("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertNotNull(result);
    assertProcessRequestNoParameters(result, "1", "1", createdTime);
  }

  @Test
  public void findByIdReturnNonEmptyOptionalWithParameters()
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);

    List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = createJoinedJdbcProcessRequests("1");

    when(mockJdbcProcessRequestRepository.findById("1")).thenReturn(Optional.of(jdbcProcessRequest));
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcProcessRequestJoinedList);

    ProcessRequest result = defaultJdbcProcessRequestRepository.findById(new ProcessRequestId("1"));

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findById("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertNotNull(result);
    assertProcessRequestWithParameters(result, "1", "1", createdTime);
  }

  @Test
  public void findAllQueryFindAllReturnsEmpty() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.findAll()).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAll();

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findAll();

    Assert.assertEquals(0, result.size());
  }

  @Test
  public void findAllProcessRequestsNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.findAll()).thenReturn(jdbcProcessRequestList);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAll();

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findAll();
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> resultProcessRequestsIterator = result.iterator();
    ProcessRequest actualProcessRequest1 = resultProcessRequestsIterator.next();
    ProcessRequest actualProcessRequest2 = resultProcessRequestsIterator.next();

    assertProcessRequestNoParameters(actualProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(actualProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllJoinedProcessRequestsWithParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.findAll()).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");
    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests2 = createJoinedJdbcProcessRequests("2");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(jdbcJoinedProcessRequests2);

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAll();

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findAll();
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestWithParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Test
  public void findAllProcessRequestsWithParametersAndNoParameters() throws BpmRepositoryException
  {
    List<JdbcProcessRequest> jdbcProcessRequestList = new ArrayList<>();
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    jdbcProcessRequestList.add(createJdbcProcessRequest("1", createdTime));
    jdbcProcessRequestList.add(createJdbcProcessRequest("2", createdTime));

    when(mockJdbcProcessRequestRepository.findAll()).thenReturn(jdbcProcessRequestList);

    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests1 = createJoinedJdbcProcessRequests("1");

    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcJoinedProcessRequests1);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("2")).thenReturn(new ArrayList<>());

    Collection<ProcessRequest> result = defaultJdbcProcessRequestRepository.findAll();

    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).findAll();
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("2");

    Assert.assertEquals(2, result.size());

    Iterator<ProcessRequest> returnedProcessRequestsIterator = result.iterator();
    ProcessRequest returnedProcessRequest1 = returnedProcessRequestsIterator.next();
    ProcessRequest returnedProcessRequest2 = returnedProcessRequestsIterator.next();

    assertProcessRequestWithParameters(returnedProcessRequest1, "1", "1", createdTime);
    assertProcessRequestNoParameters(returnedProcessRequest2, "2", "1", createdTime);
  }

  @Test(expected = BpmRepositoryException.class)
  public void getByProcessInstanceIdThrowsExceptionWhenIdIsBlank() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.getByProcessInstanceId("");
  }

  @Test(expected = BpmRepositoryException.class)
  public void getByProcessInstanceIdThrowsExceptionWhenIdIsNull() throws BpmRepositoryException
  {
    defaultJdbcProcessRequestRepository.getByProcessInstanceId(null);
  }

  @Test
  public void getByProcessInstanceIdWhenDoesntExist() throws BpmRepositoryException
  {
    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestByProcessInstanceId("instance id")).thenReturn(null);
    ProcessRequest result = defaultJdbcProcessRequestRepository.getByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(0)).getJoinedProcessRequestsByProcessRequestId(Mockito.any());

    Assert.assertNull(result);
  }

  @Test
  public void getByProcessInstanceIdMultipleParameters() throws BpmRepositoryException
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);

    List<JdbcProcessRequestJoined> jdbcProcessRequestJoinedList = createJoinedJdbcProcessRequests("1");

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestByProcessInstanceId("instance id")).thenReturn(jdbcProcessRequest);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(jdbcProcessRequestJoinedList);
    ProcessRequest result = defaultJdbcProcessRequestRepository.getByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertNotNull(result);
    assertProcessRequestWithParameters(result, "1", "1", createdTime);
  }

  @Test
  public void getByProcessInstanceIdNoParameters() throws BpmRepositoryException
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    JdbcProcessRequest jdbcProcessRequest = createJdbcProcessRequest("1", createdTime);

    when(mockJdbcProcessRequestRepository.getJdbcProcessRequestByProcessInstanceId("instance id")).thenReturn(jdbcProcessRequest);
    when(mockJdbcProcessRequestRepository.getJoinedProcessRequestsByProcessRequestId("1")).thenReturn(new ArrayList<>());
    ProcessRequest result = defaultJdbcProcessRequestRepository.getByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJdbcProcessRequestByProcessInstanceId("instance id");
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1)).getJoinedProcessRequestsByProcessRequestId("1");

    Assert.assertNotNull(result);
    assertProcessRequestNoParameters(result, "1", "1", createdTime);
  }

  private void checkJdbcProcessRequestRepositoryInsertInvoked(ProcessRequest processRequest)
  {
    Mockito.verify(mockJdbcProcessRequestRepository, Mockito.times(1))
        .insert("101000001", processRequest.getProcessTypeId().getId(), processRequest.getGroupId().getId(),
            processRequest.getRequestedUserId(), processRequest.getState().toString(), processRequest.getCreatedTime());
  }

  private void checkJdbcProcessRequestParameterRepositoryInsertInvoked(String requestId, String parameterName, Serializable parameterValue,
      String parameterType)
  {
    Mockito.verify(mockJdbcProcessRequestParameterRepository, Mockito.times(1))
        .insert(requestId, parameterName, parameterValue, parameterType);
  }

  private JdbcProcessRequestJoined getProcessRequestJoined(String processRequestId)
  {
    JdbcProcessRequestJoined jdbcProcessRequestJoined = new JdbcProcessRequestJoined();

    jdbcProcessRequestJoined.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoined.setProcessRequestState("NEW");
    jdbcProcessRequestJoined.setGroupId("GR1");
    jdbcProcessRequestJoined.setProcessTypeId("123");
    jdbcProcessRequestJoined.setCreatedTime(LocalDateTime.now());
    jdbcProcessRequestJoined.setRequestedUserId("1");
    jdbcProcessRequestJoined.setParameterName("amount");
    jdbcProcessRequestJoined.setParameterValue("1234");
    jdbcProcessRequestJoined.setParameterType("Integer");

    return jdbcProcessRequestJoined;
  }

  private List<JdbcProcessRequestJoined> createJoinedJdbcProcessRequests(String processRequestId)
  {
    LocalDateTime createdTime = LocalDateTime.of(LocalDate.ofYearDay(2020, 100), LocalTime.NOON);
    List<JdbcProcessRequestJoined> jdbcJoinedProcessRequests = new ArrayList<>();

    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd1 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd1.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoinedToAdd1.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd1.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd1.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd1.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd1.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd1.setParameterName("amount");
    jdbcProcessRequestJoinedToAdd1.setParameterValue("1234");
    jdbcProcessRequestJoinedToAdd1.setParameterType("Integer");
    jdbcJoinedProcessRequests.add(jdbcProcessRequestJoinedToAdd1);

    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd2 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd2.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoinedToAdd2.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd2.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd2.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd2.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd2.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd2.setParameterName("User");
    jdbcProcessRequestJoinedToAdd2.setParameterValue("Zorig");
    jdbcProcessRequestJoinedToAdd2.setParameterType("String");
    jdbcJoinedProcessRequests.add(jdbcProcessRequestJoinedToAdd2);

    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd3 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd3.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoinedToAdd3.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd3.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd3.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd3.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd3.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd3.setParameterName("isAccepted");
    jdbcProcessRequestJoinedToAdd3.setParameterValue("true");
    jdbcProcessRequestJoinedToAdd3.setParameterType("Boolean");
    jdbcJoinedProcessRequests.add(jdbcProcessRequestJoinedToAdd3);

    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd4 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd4.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoinedToAdd4.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd4.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd4.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd4.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd4.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd4.setParameterName("LongName");
    jdbcProcessRequestJoinedToAdd4.setParameterValue("1234");
    jdbcProcessRequestJoinedToAdd4.setParameterType("Long");
    jdbcJoinedProcessRequests.add(jdbcProcessRequestJoinedToAdd4);

    JdbcProcessRequestJoined jdbcProcessRequestJoinedToAdd5 = new JdbcProcessRequestJoined();
    jdbcProcessRequestJoinedToAdd5.setProcessRequestId(processRequestId);
    jdbcProcessRequestJoinedToAdd5.setProcessRequestState("NEW");
    jdbcProcessRequestJoinedToAdd5.setGroupId("GR1");
    jdbcProcessRequestJoinedToAdd5.setProcessTypeId("123");
    jdbcProcessRequestJoinedToAdd5.setCreatedTime(createdTime);
    jdbcProcessRequestJoinedToAdd5.setRequestedUserId("1");
    jdbcProcessRequestJoinedToAdd5.setParameterName("BigDecimalName");
    jdbcProcessRequestJoinedToAdd5.setParameterValue("1.234");
    jdbcProcessRequestJoinedToAdd5.setParameterType("BigDecimal");
    jdbcJoinedProcessRequests.add(jdbcProcessRequestJoinedToAdd5);

    return jdbcJoinedProcessRequests;
  }

  private JdbcProcessRequest createJdbcProcessRequest(String processRequestId, LocalDateTime createdTime)
  {
    JdbcProcessRequest jdbcProcessRequest = new JdbcProcessRequest();
    jdbcProcessRequest.setProcessRequestState("NEW");
    jdbcProcessRequest.setGroupId("GR1");
    jdbcProcessRequest.setProcessTypeId("123");
    jdbcProcessRequest.setCreatedTime(createdTime);
    jdbcProcessRequest.setProcessRequestId(processRequestId);
    jdbcProcessRequest.setRequestedUserId("1");
    return jdbcProcessRequest;
  }

  private void assertProcessRequestNoParameters(ProcessRequest processRequest, String processRequestId, String requestedUserId, LocalDateTime createdTime)
  {
    Assert.assertEquals(processRequestId, processRequest.getId().getId());
    Assert.assertEquals("123", processRequest.getProcessTypeId().getId());
    Assert.assertEquals("GR1", processRequest.getGroupId().getId());
    Assert.assertEquals(requestedUserId, processRequest.getRequestedUserId());
    Assert.assertEquals(createdTime, processRequest.getCreatedTime());
    Assert.assertEquals(ProcessRequestState.NEW, processRequest.getState());
    Assert.assertTrue(processRequest.getParameters().isEmpty());
    Assert.assertNull(processRequest.getAssignedUserId());
    Assert.assertNull(processRequest.getAssignedTime());
    Assert.assertNull(processRequest.getProcessInstanceId());
  }

  private void assertProcessRequestWithParameters(ProcessRequest processRequest, String processRequestId, String requestedUserId, LocalDateTime createdTime)
  {
    Assert.assertEquals(processRequestId, processRequest.getId().getId());
    Assert.assertEquals("123", processRequest.getProcessTypeId().getId());
    Assert.assertEquals("GR1", processRequest.getGroupId().getId());
    Assert.assertEquals(requestedUserId, processRequest.getRequestedUserId());
    Assert.assertEquals(createdTime, processRequest.getCreatedTime());
    Assert.assertEquals(ProcessRequestState.NEW, processRequest.getState());
    Assert.assertFalse(processRequest.getParameters().isEmpty());
    Assert.assertNull(processRequest.getAssignedUserId());
    Assert.assertNull(processRequest.getAssignedTime());
    Assert.assertNull(processRequest.getProcessInstanceId());

    Map<String, Serializable> parameters = processRequest.getParameters();

    Assert.assertEquals(true, parameters.containsKey("amount"));
    Assert.assertEquals(1234, parameters.get("amount"));
    Assert.assertEquals(true, parameters.get("amount") instanceof Integer);

    Assert.assertEquals(true, parameters.containsKey("User"));
    Assert.assertEquals("Zorig", parameters.get("User"));
    Assert.assertEquals(true, parameters.get("User") instanceof String);

    Assert.assertEquals(true, parameters.containsKey("isAccepted"));
    Assert.assertEquals(true, parameters.get("isAccepted"));
    Assert.assertEquals(true, parameters.get("isAccepted") instanceof Boolean);

    Assert.assertEquals(true, parameters.containsKey("LongName"));
    Assert.assertEquals(new Long("1234"), parameters.get("LongName"));
    Assert.assertEquals(true, parameters.get("LongName") instanceof Long);

    Assert.assertEquals(true, parameters.containsKey("BigDecimalName"));
    Assert.assertEquals(new BigDecimal(1.234, new MathContext(4)), parameters.get("BigDecimalName"));
    Assert.assertEquals(true, parameters.get("BigDecimalName") instanceof BigDecimal);
  }

  private void assertParameters(Map<String, Serializable> parameters)
  {
    Assert.assertEquals(true, parameters.containsKey("amount"));
    Assert.assertEquals(1234, parameters.get("amount"));
    Assert.assertEquals(true, parameters.get("amount") instanceof Integer);

    Assert.assertEquals(true, parameters.containsKey("User"));
    Assert.assertEquals("Zorig", parameters.get("User"));
    Assert.assertEquals(true, parameters.get("User") instanceof String);

    Assert.assertEquals(true, parameters.containsKey("isAccepted"));
    Assert.assertEquals(true, parameters.get("isAccepted"));
    Assert.assertEquals(true, parameters.get("isAccepted") instanceof Boolean);

    Assert.assertEquals(true, parameters.containsKey("LongName"));
    Assert.assertEquals(new Long("1234"), parameters.get("LongName"));
    Assert.assertEquals(true, parameters.get("LongName") instanceof Long);

    Assert.assertEquals(true, parameters.containsKey("BigDecimalName"));
    Assert.assertEquals(new BigDecimal(1.234, new MathContext(4)), parameters.get("BigDecimalName"));
    Assert.assertEquals(true, parameters.get("BigDecimalName") instanceof BigDecimal);
  }
}
