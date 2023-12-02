package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LEGAL_STATUS;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class CleanMicroCoBorrowerMongolBankFieldsTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanMicroCoBorrowerMongolBankFieldsTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("########### Cleaning MICRO co-borrower mongol bank fields.");

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    execution.removeVariable(REGISTER_NUMBER_CO_BORROWER);
    execution.removeVariable(LEGAL_STATUS);

    CaseService caseService = execution.getProcessEngine().getCaseService();

    if (null != caseInstanceId)
    {
      caseService.removeVariable(caseInstanceId, REGISTER_NUMBER_CO_BORROWER);
      caseService.removeVariable(caseInstanceId, LEGAL_STATUS);
    }

    LOGGER.info("########### Successful cleaned up MICRO co-borrower mongol bank fields.");
  }
}
