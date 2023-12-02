package consumption.case_listener;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class BnplProcessCompleteListener implements ExecutionListener
{
  private final ProcessRequestRepository processRequestRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplProcessCompleteListener.class);

  public BnplProcessCompleteListener(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    final String cif = String.valueOf(execution.getVariable(CIF_NUMBER));

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, ProcessRequestState.COMPLETED);
    updateRequestState.execute(input);

    LOGGER.info("{} {} request is completed. cif number = {}. {}", getLogPrefix(getValidString(execution.getVariable(PROCESS_TYPE_ID))), requestId, cif,
        (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
    execution.removeVariables();
  }
}
