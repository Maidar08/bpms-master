package consumption.case_listener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import consumption.util.CaseExecutionUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import static consumption.util.CaseExecutionUtils.disableExecutions;
import static mn.erin.domain.bpm.BpmActivityIdConstants.ACTIVITY_ID_ONLINE_SALARY_CLOSE_AND_DISBURSE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FAILED_ACCOUNT_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CLOSING_LOAN_AMOUNT;

/**
 * @author Lkhagvadorj.A
 **/

@SuppressWarnings("unchecked")
public class OnlineSalaryDisburseErrorListener implements CaseExecutionListener
{
  @Override
  public void notify(DelegateCaseExecution execution)
  {
    final String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    ProcessEngine processEngine = execution.getProcessEngine();
    Map<String, Object> executionVariables = execution.getVariables();
    List<String> activityIds = Collections.singletonList(ACTIVITY_ID_ONLINE_SALARY_CLOSE_AND_DISBURSE);
    disableExecutions(instanceId, execution, activityIds);
    CaseExecutionUtils.terminateExecutionByActivityId(instanceId, processEngine,
        "case_bpms_direct_online_salary_loan", executionVariables);

    if (execution.hasVariable(HAS_ACTIVE_LOAN_ACCOUNT) && (boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT))
    {
      if (null != execution.getVariable(FAILED_ACCOUNT_LIST) && !((List<Map<String, Object>>) execution.getVariable(FAILED_ACCOUNT_LIST)).isEmpty())
      {
        List<Map<String, Object>> failedAccountList = (List<Map<String, Object>>) execution.getVariable(FAILED_ACCOUNT_LIST);
        StringBuilder failedAccountListString = new StringBuilder();
        int index = 1;
        for (Map<String, Object> account : failedAccountList)
        {
          failedAccountListString.append(index).append(". ").append(account.get(XAC_ACCOUNT_ID)).append(" : ").append(account.get(XAC_CLOSING_LOAN_AMOUNT));
          if (account.containsKey("error"))
          {
            failedAccountListString.append("  ").append(account.get("error"));
          }
          failedAccountListString.append("\n");
          index++;
        }
        execution.setVariable("errorCause", failedAccountListString);
      }
      else
      {
        final String errorMessage = String.valueOf(execution.getVariable(ERROR_CAUSE));
        execution.setVariable("errorCause", errorMessage);
      }
    }
  }
}
