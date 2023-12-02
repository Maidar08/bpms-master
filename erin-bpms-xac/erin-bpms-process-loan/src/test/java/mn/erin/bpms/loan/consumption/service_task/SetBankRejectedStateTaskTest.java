/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpms.loan.consumption.service_task.bpms.SetBankRejectedStateTask;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class SetBankRejectedStateTaskTest
{
  private static final String VALUE_REQUEST_ID = "request1";
  private static final String STATE_BANK_REJECTED = "stateBankRejected";

  private DelegateExecution delegateExecution;
  private ProcessRequestRepository processRequestRepository;

  private AuthenticationService authenticationService;

  private SetBankRejectedStateTask setBankRejectedStateTask;
  @Before
  public void setUp()
  {
    delegateExecution = Mockito.mock(DelegateExecution.class);
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    authenticationService = Mockito.mock(AuthenticationService.class);

    setBankRejectedStateTask = new SetBankRejectedStateTask(processRequestRepository, authenticationService);
  }

  @Test
  public void when_updated_state() throws Exception
  {
    initialMock();

    when(processRequestRepository.updateState(VALUE_REQUEST_ID, ProcessRequestState.ORG_REJECTED)).thenReturn(true);

    setBankRejectedStateTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(STATE_BANK_REJECTED, true);
  }

  @Test(expected = UseCaseException.class)
  public void throw_use_case_exception() throws Exception
  {
    initialMock();

    when(processRequestRepository.updateState(VALUE_REQUEST_ID, ProcessRequestState.ORG_REJECTED)).thenThrow(BpmRepositoryException.class);

    setBankRejectedStateTask.execute(delegateExecution);
  }

  private void initialMock()
  {
    when(delegateExecution.getVariables()).thenReturn(getProperties());
    when(delegateExecution.getVariable(PROCESS_REQUEST_ID)).thenReturn(VALUE_REQUEST_ID);
    when(processRequestRepository.findById(ProcessRequestId.valueOf(VALUE_REQUEST_ID))).thenReturn(getDefault());
  }
  private Map<String, Object> getProperties()
  {
    Map<String, Object> properties = new HashMap<>();
    properties.put(PROCESS_REQUEST_ID, VALUE_REQUEST_ID);
    return properties;
  }

  private ProcessRequest getDefault()
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("registerNumber", "ЧП57010310");

    return new ProcessRequest(ProcessRequestId.valueOf(VALUE_REQUEST_ID),
        ProcessTypeId.valueOf("CONSUMPTION_LOAN"), GroupId.valueOf("GR1"), "admin",
        LocalDateTime.now(),
        ProcessRequestState.NEW,
        parameters);
  }
}
