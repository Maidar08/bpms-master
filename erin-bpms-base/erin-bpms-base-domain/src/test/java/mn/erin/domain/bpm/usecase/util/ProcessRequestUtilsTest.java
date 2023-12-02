package mn.erin.domain.bpm.usecase.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;

import static mn.erin.domain.bpm.util.process.ProcessRequestUtils.filterProcessRequestByState;
import static mn.erin.domain.bpm.util.process.ProcessRequestUtils.findBySearchKey;

/**
 * @author Bilguunbor
 */
@RunWith(JUnitParamsRunner.class)
public class ProcessRequestUtilsTest
{
  @Test
  public void when_process_request_state_filter_returns_null()
  {
    ProcessRequest processRequest = createProcessRequest("processRequestId", "processTypeId", "groupId", "userId", ProcessRequestState.DELETED, generateMap());
    Collection<ProcessRequest> output = filterProcessRequestByState(addToCollectionAndReturn(processRequest));
    Assert.assertEquals(0, output.size());
  }

  @Test
  public void when_process_request_state_filter_returns_collection()
  {
    ProcessRequest processRequest = createProcessRequest("processRequestId", "processTypeId", "groupId", "userId", ProcessRequestState.NEW, generateMap());
    Collection<ProcessRequest> output = filterProcessRequestByState(addToCollectionAndReturn(processRequest));
    Assert.assertEquals(1, output.size());
  }

  //Process request with same value as the search key does not exist.
  @Test
  public void when_find_by_search_key_returns_null()
  {
    ProcessRequest processRequest = createProcessRequest("processRequestId", "processTypeId", "groupId", "userId", ProcessRequestState.NEW, generateMap());
    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "searchKey");
    Assert.assertEquals(0, output.size());
  }

  @Test
  public void when_find_by_search_key_returns_filtered_collection()
  {
    ProcessRequest processRequest = createProcessRequest("123", "processTypeId", "groupId", "userId", ProcessRequestState.NEW, generateMap());
    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "123");
    Assert.assertEquals(1, output.size());
  }

  @Test
  public void verifying_process_types()
  {
    ProcessRequest processRequest = createProcessRequest("processRequestId", "consumptionLoan", "groupId", "userId", ProcessRequestState.NEW, generateMap());

    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "Хэрэглээний зээл");
    Assert.assertEquals(1, output.size());
  }

  //Combined two tests into one. It was 4 before but due to progress in the test we needed some other methods.
  @Test
  @Parameters({
      "processRequestId, processTypeId, 123, userId",
      "processRequestId, processTypeId, groupId, 123",
  })
  public void verifying_ids(String processRequestId, String processTypeId, String groupId, String userId)
  {
    ProcessRequest processRequest = new ProcessRequest(ProcessRequestId.valueOf(processRequestId), ProcessTypeId.valueOf(processTypeId),
        GroupId.valueOf(groupId), userId, LocalDateTime.now(), ProcessRequestState.NEW, generateMap());
    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "123");
    Assert.assertEquals(1, output.size());
  }

  @Test
  public void verifying_assigned_user_id()
  {
    ProcessRequest processRequest = createInvalidProcessRequest(generateMap());
    processRequest.setAssignedUserId(UserId.valueOf("123"));

    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "123");
    Assert.assertEquals(1, output.size());
  }

  @Test
  public void verifying_process_instance_id()
  {
    ProcessRequest processRequest = createInvalidProcessRequest(generateMap());
    processRequest.setProcessInstanceId("123");

    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "123");
    Assert.assertEquals(1, output.size());
  }

  @Test
  public void verifying_process_request_state()
  {
    ProcessRequest processRequest = createInvalidProcessRequest(generateMap());
    processRequest.setProcessInstanceId("123");

    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "ШИНЭ");
    Assert.assertEquals(1, output.size());
  }

  @Test
  public void verifying_parameter()
  {
    ProcessRequest processRequest = createInvalidProcessRequest(generateMap());

    Collection<ProcessRequest> output = findBySearchKey(addToCollectionAndReturn(processRequest), "value");
    Assert.assertEquals(1, output.size());
  }

  private Collection<ProcessRequest> addToCollectionAndReturn(ProcessRequest processRequest)
  {
    Collection<ProcessRequest> processRequests = new ArrayList<>();
    processRequests.add(processRequest);

    return processRequests;
  }

  private ProcessRequest createProcessRequest(String processRequestId, String processTypeId, String groupId, String userId, ProcessRequestState state,
      Map<String, Serializable> map)
  {
    return new ProcessRequest(ProcessRequestId.valueOf(processRequestId), ProcessTypeId.valueOf(processTypeId),
        GroupId.valueOf(groupId), userId, LocalDateTime.now(), state, map);
  }

  private ProcessRequest createInvalidProcessRequest(Map<String, Serializable> map)
  {
    return createProcessRequest("invalid", "invalid", "invalid", "invalid", ProcessRequestState.NEW, map);
  }

  private Map<String, Serializable> generateMap()
  {
    Map<String, Serializable> map = new HashMap<>();
    map.put("key", "value");

    return map;
  }
}
