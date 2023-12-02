package consumption.case_listener;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.MB_HAS_SESSION;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class MbSessionStateListener implements CaseExecutionListener
{
  private final ProcessRequestRepository processRequestRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(MbSessionStateListener.class);

  public MbSessionStateListener(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void notify(DelegateCaseExecution execution) throws Exception
  {
    if (execution.hasVariable(MB_HAS_SESSION) && (boolean) execution.getVariable(MB_HAS_SESSION))
    {
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      UpdateRequestStateInput updateRequestStateInput = new UpdateRequestStateInput(requestId, ProcessRequestState.STARTED);
      UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
      updateRequestState.execute(updateRequestStateInput);
      LOGGER.info(ONLINE_SALARY_LOG_HASH + " Updated process request state to STARTED after successfully completed MONGOL BANK task, process request id [{}]", requestId);
    }
  }
}
