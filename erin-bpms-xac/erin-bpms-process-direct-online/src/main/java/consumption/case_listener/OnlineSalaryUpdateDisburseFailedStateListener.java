package consumption.case_listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSED;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryUpdateDisburseFailedStateListener implements ExecutionListener
{
  private final ProcessRequestRepository processRequestRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryUpdateDisburseFailedStateListener.class);

  public OnlineSalaryUpdateDisburseFailedStateListener(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    final String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    ProcessRequestState state = DISBURSED;
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, state);
    updateRequestState.execute(input);
    LOGGER.info(ONLINE_SALARY_LOG_HASH + "Process state is updated to {}. REQUEST ID = {}, CIF = {}", state, requestId, execution.getVariable(CIF_NUMBER));
  }
}
