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

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Tamir
 */
public class SetBankRejectedStateTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetBankRejectedStateTask.class);
  private static final String STATE_BANK_REJECTED = "stateBankRejected";

  private final ProcessRequestRepository processRequestRepository;
  private final AuthenticationService authenticationService;

  public SetBankRejectedStateTask(ProcessRequestRepository processRequestRepository, AuthenticationService authenticationService)
  {
    this.processRequestRepository = processRequestRepository;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("*********** Set bank rejected state task with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    String processRequestId = (String) execution.getVariable(BpmModuleConstants.PROCESS_REQUEST_ID);

    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.ORG_REJECTED);
    UpdateRequestStateOutput output = updateRequestState.execute(input);

    boolean isUpdated = output.isUpdated();

    if (isUpdated)
    {
      execution.setVariable(STATE_BANK_REJECTED, true);
      LOGGER.info("##### Successful set bank rejected state.");
    }
  }
}
