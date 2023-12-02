/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.update_request_state;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

/**
 * @author Tamir
 */
public class UpdateRequestStateTest
{
  private static final String PROCESS_REQUEST_ID = "1";
  private ProcessRequestRepository requestRepository;
  private UpdateRequestState useCase;

  @Before
  public void setUp()
  {
    requestRepository = Mockito.mock(ProcessRequestRepository.class);
    useCase = new UpdateRequestState(requestRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new UpdateRequestState(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_empty() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_process_request_not_exist() throws UseCaseException
  {
    Mockito.when(requestRepository.findById(ProcessRequestId.valueOf(PROCESS_REQUEST_ID))).thenReturn(null);
    UpdateRequestStateInput input = new UpdateRequestStateInput(PROCESS_REQUEST_ID, ProcessRequestState.CONFIRMED);

    useCase.execute(input);
  }

  @Test
  public void when_successful_process_request() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.findById(ProcessRequestId.valueOf(PROCESS_REQUEST_ID))).thenReturn(getDefault());
    Mockito.when(requestRepository.updateState(PROCESS_REQUEST_ID, ProcessRequestState.CONFIRMED)).thenReturn(true);
    UpdateRequestStateInput input = new UpdateRequestStateInput(PROCESS_REQUEST_ID, ProcessRequestState.CONFIRMED);

    Assert.assertTrue(useCase.execute(input).isUpdated());
  }

  @Test
  public void when_successful_process_request_not_updated() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(requestRepository.findById(ProcessRequestId.valueOf(PROCESS_REQUEST_ID))).thenReturn(getDefault());
    Mockito.when(requestRepository.updateState(PROCESS_REQUEST_ID, ProcessRequestState.CONFIRMED)).thenReturn(false);
    UpdateRequestStateInput input = new UpdateRequestStateInput(PROCESS_REQUEST_ID, ProcessRequestState.CONFIRMED);

    Assert.assertFalse(useCase.execute(input).isUpdated());
  }

  private ProcessRequest getDefault()
  {
    Map<String, Serializable> parameters = new HashMap<>();

    parameters.put("registerNumber", "ЧП57010310");

    return new ProcessRequest(ProcessRequestId.valueOf(PROCESS_REQUEST_ID),
        ProcessTypeId.valueOf("CONSUMPTION_LOAN"), GroupId.valueOf("1"), "user",
        LocalDateTime.now(),
        ProcessRequestState.NEW,
        parameters);
  }
}
