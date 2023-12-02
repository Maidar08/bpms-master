/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

/**
 * @author Tamir
 */
public class SetCustomerRejectedStateTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetCustomerRejectedStateTask.class);
  private static final String STATE_CUSTOMER_REJECTED = "stateCustomerRejected";

  private final ProcessRequestRepository processRequestRepository;

  public SetCustomerRejectedStateTask(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("*********** Sets customer rejected state.");

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    String processRequestId = (String) execution.getVariable(BpmModuleConstants.PROCESS_REQUEST_ID);

    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, ProcessRequestState.CUST_REJECTED);
    UpdateRequestStateOutput output = updateRequestState.execute(input);

    boolean isUpdated = output.isUpdated();

    if (isUpdated)
    {
      execution.setVariable(STATE_CUSTOMER_REJECTED, true);
      LOGGER.info("##### Successful set customer rejected state.");
    }
  }
}
