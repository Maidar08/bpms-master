package consumption.case_listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SCORING_REJECTED;

/**
 * @author Odgavaa
 */
public class BnplScoringRejectedListener implements ExecutionListener
{
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplScoringRejectedListener.class);

  public BnplScoringRejectedListener(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, SCORING_REJECTED);
    updateRequestState.execute(input);

    Object scoringLevel = execution.getVariable("scoring_level");

    LOGGER.info("#############  SCORING FAILED. Updated process state to {}, with process request id = [{}], SCORING = [{}]",
        ProcessRequestState.fromEnumToString(SCORING_REJECTED), requestId, scoringLevel);
  }
}
