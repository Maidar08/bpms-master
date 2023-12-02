package consumption.service_task.direct_online_salary;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static consumption.constant.CamundaMongolBankConstants.NORMAL;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.ORG_REJECTED;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

public class ElementaryCriteriaTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ElementaryCriteriaTask.class);
  private static final String MONGOL_BANK_APPROVE = "mbApprove";

  private final ProcessRequestRepository processRequestRepository;

  public ElementaryCriteriaTask(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String loanClassification = String.valueOf(execution.getVariable(LOAN_CLASS_NAME));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    if (loanClassification.equals(NORMAL))
    {
      execution.setVariable(MONGOL_BANK_APPROVE, true);
    }
    else
    {
      boolean isStateUpdated = updateRequestState(processRequestRepository, processRequestId, ORG_REJECTED);
      if (isStateUpdated)
      {
        execution.setVariable(MONGOL_BANK_APPROVE, false);
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          LOGGER.info("##### PROCESS REQUEST WITH ID [{}] STATE UPDATED AS ORG_REJECTED. TRACKNUMBER = [{}]", processRequestId, trackNumber);
        }
        else
        {
          LOGGER.info("##### PROCESS REQUEST WITH ID [{}] STATE UPDATED AS ORG_REJECTED.", processRequestId);
        }
      }
    }
  }
}
