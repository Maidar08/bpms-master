package mn.erin.bpms.loan.consumption.case_listener;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants;

/**
 * @author Tamir
 */
public class SetCollAccountStageStartedListener implements CaseExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetCollAccountStageStartedListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String caseInstanceId = caseExecution.getCaseInstanceId();

    caseExecution.setVariable(CamundaVariableConstants.IS_STARTED_COLL_ACCOUNT_STAGE, true);
    CaseService caseService = caseExecution.getProcessEngine().getCaseService();

    LOGGER.info("########### SETS IS STARTED COLL ACCOUNT STAGE VALUE = [{}]", true);
    caseService.setVariable(caseInstanceId, CamundaVariableConstants.IS_STARTED_COLL_ACCOUNT_STAGE, true);
  }
}
