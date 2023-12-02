package mn.erin.bpms.loan.consumption.case_listener;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.manuallyStartExecution;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRM_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;

public class TriggerCollateralListListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(TriggerCollateralListListener.class);

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    ProcessEngine processEngine = caseExecution.getProcessEngine();
    Map<String, Object> executionVariables = caseExecution.getVariables();

    String caseInstanceId = caseExecution.getCaseInstanceId();

    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String confirmedValue = (String) caseExecution.getVariable(CONFIRM_LOAN_AMOUNT);

    if (null == confirmedValue)
    {
      return;
    }

    String hasCollateralValue = (String) caseExecution.getVariable(HAS_COLLATERAL);

    if (null == hasCollateralValue)
    {
      return;
    }

    if (hasCollateralValue.equals(YES_MN_VALUE))
    {
      LOG.info("######### MANUALLY STARTS COLLATERAL LIST STAGE WITH REQUEST ID = [{}]", requestId);
      manuallyStartExecution(caseInstanceId, ACTIVITY_ID_STAGE_COLLATERAL_LIST, processEngine, executionVariables);
    }
    else
    {
      LOG.info("######### TERMINATES COLLATERAL LIST STAGE WITH REQUEST ID = [{}]", requestId);
      CaseExecutionUtils.terminateExecutionByActivityId(caseInstanceId, processEngine,
          ACTIVITY_ID_STAGE_COLLATERAL_LIST, executionVariables);
    }
  }
}
