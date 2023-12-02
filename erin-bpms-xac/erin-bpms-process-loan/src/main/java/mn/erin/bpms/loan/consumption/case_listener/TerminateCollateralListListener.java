package mn.erin.bpms.loan.consumption.case_listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_CREATE_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVITY_ID_STAGE_COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

public class TerminateCollateralListListener implements ExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(TerminateCollateralListListener.class);

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    ProcessEngine processEngine = execution.getProcessEngine();
    Map<String, Object> executionVariables = execution.getVariables();

    LOG.info("########## TERMINATES COLLATERAL LIST STAGE AFTER COLLATERAL LOAN ACCOUNT"
        + " TASK WITH REQUEST ID = [{}]", requestId);
    CaseExecutionUtils.terminateExecutionByActivityId(caseInstanceId, processEngine,
        ACTIVITY_ID_STAGE_COLLATERAL_LIST, executionVariables);

    List<String> taskDefinitionKeys = new ArrayList<>();

    taskDefinitionKeys.add(TASK_DEF_COLLATERAL_LIST);
    taskDefinitionKeys.add(TASK_DEF_CREATE_COLLATERAL);

    CaseExecutionUtils.suspendActiveProcessInstancesByDefKey(caseInstanceId, execution, taskDefinitionKeys, true);
  }
}
