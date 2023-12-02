/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

/**
 * @author Tamir
 */
public class SetRequestConfirmedStateTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetRequestConfirmedStateTask.class);

  private final ProcessRequestRepository processRequestRepository;
  private final AuthenticationService authenticationService;

  public SetRequestConfirmedStateTask(ProcessRequestRepository processRequestRepository, AuthenticationService authenticationService)
  {
    this.processRequestRepository = processRequestRepository;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processRequestId = (String) execution.getVariable(BpmModuleConstants.PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    String registrationNumber = (String) execution.getVariable(BpmModuleConstants.REGISTER_NUMBER);

    LOGGER.info("#########  Set Process Request State Task.. Register Number: " + registrationNumber + ", Request ID: " + processRequestId + " , User ID: " + userId);

    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.STARTED);
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);

    try
    {
      UpdateRequestStateOutput output = updateRequestState.execute(input);

      if (output.isUpdated())
      {
        LOGGER.info("**************** Successful updated request CONFIRMED state.");
      }
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
