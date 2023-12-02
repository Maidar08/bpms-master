/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation.mortgage;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getActiveTaskProcessInstanceIdByTaskDefKey;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

/**
 * @author Zorig
 */
public class SetLastCalculationTypeBusinessTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    execution.setVariable("mortgageLastCalculationType", "Business");
    long netProfit = executionVariableToLong(execution, "netProfit");

    // set variable
    ProcessEngineServices processEngineServices = execution.getProcessEngineServices();
    CaseService caseService = processEngineServices.getCaseService();
    RuntimeService runtimeService = processEngineServices.getRuntimeService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String mortgageLoanAmountRootProcessInstanceId = getActiveTaskProcessInstanceIdByTaskDefKey("user_task_calculate_mortgage_loan_amount",
        caseInstanceId, execution);
    caseService.setVariable(caseInstanceId, "mortgageLastCalculationType", "Business");
    caseService.setVariable(caseInstanceId, "netProfit", netProfit);
    if (null != mortgageLoanAmountRootProcessInstanceId)
    {
      runtimeService.setVariable(mortgageLoanAmountRootProcessInstanceId, "mortgageLastCalculationType", "Business");
      runtimeService.setVariable(mortgageLoanAmountRootProcessInstanceId, "netProfit", netProfit);
    }
  }

  private long executionVariableToLong(DelegateExecution execution, String variableId)
  {
    Object executionVariableObj = execution.getVariable(variableId);

    if (executionVariableObj == null)
    {
      return 0;
    }

    if (executionVariableObj instanceof Integer)
    {
      int executionVariableInt = (int) executionVariableObj;
      return (long) executionVariableInt;
    }
    if (executionVariableObj instanceof Double)
    {
      double executionVariableDouble = (double) executionVariableObj;
      return (long) executionVariableDouble;
    }
    if (executionVariableObj instanceof Long)
    {
      return (long) executionVariableObj;
    }

    return 0;
  }
}
