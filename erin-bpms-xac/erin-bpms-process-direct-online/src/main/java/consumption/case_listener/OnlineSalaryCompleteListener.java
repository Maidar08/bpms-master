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
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryCompleteListener implements CaseExecutionListener
{
  private final ProcessRequestRepository processRequestRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryCompleteListener.class);

  public OnlineSalaryCompleteListener(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void notify(DelegateCaseExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String cif = String.valueOf(execution.getVariable(CIF_NUMBER));

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, ProcessRequestState.COMPLETED);
    updateRequestState.execute(input);

    LOGGER.info(ONLINE_SALARY_LOG_HASH + "{} request is completed. cif number = {}", requestId, cif);
    execution.removeVariables();
  }
}
