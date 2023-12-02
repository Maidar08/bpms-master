/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.case_listener.micro;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.camunda.bpm.engine.task.Task;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DEF_ID_LOAN_AMOUNT_AFTER_COLL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.DEF_ID_LOAN_AMOUNT_AFTER_ROOT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEF_ID_ROOT_LOAN_AMOUNT;

/**
 * @author Zorig
 */
public class CheckIfAmountCalculationIsActiveListener implements CaseExecutionListener
{
  private static final String IS_LOAN_AMOUNT_CALCULATION_ACTIVE = "isLoanAmountCalculationActive";
  
  @Override
  public void notify(DelegateCaseExecution execution) throws Exception
  {
    execution.setVariable(IS_LOAN_AMOUNT_CALCULATION_ACTIVE, true);

    ProcessEngineServices processEngineServices = execution.getProcessEngineServices();
    CaseService caseService = processEngineServices.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String loanAmountRootProcessInstanceId = getActiveTaskProcessInstanceIdByTaskDefKeyCaseExecution(DEF_ID_ROOT_LOAN_AMOUNT,
        caseInstanceId, execution);
    String loanAmountOtherProcessInstanceId = getActiveTaskProcessInstanceIdByTaskDefKeyCaseExecution(DEF_ID_LOAN_AMOUNT_AFTER_ROOT,
        caseInstanceId, execution);
    String loanAmountOther2ProcessInstanceId = getActiveTaskProcessInstanceIdByTaskDefKeyCaseExecution(DEF_ID_LOAN_AMOUNT_AFTER_COLL_LIST,
        caseInstanceId, execution);

    if (null != loanAmountRootProcessInstanceId && null != loanAmountOtherProcessInstanceId && null != loanAmountOther2ProcessInstanceId)
    {
      caseService.setVariable(caseInstanceId, IS_LOAN_AMOUNT_CALCULATION_ACTIVE, false);
      execution.setVariable(IS_LOAN_AMOUNT_CALCULATION_ACTIVE, false);
    }
  }

  String getActiveTaskProcessInstanceIdByTaskDefKeyCaseExecution(String taskDefKey, String caseInstanceId, DelegateCaseExecution execution)
  {
    if (StringUtils.isBlank(taskDefKey) || StringUtils.isBlank(caseInstanceId) || null == execution)
    {
      return null;
    }
    TaskService taskService = execution.getProcessEngine().getTaskService();
    List<Task> activeTasks = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .list();
    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getTaskDefinitionKey();

      if (taskDefinitionKey.equalsIgnoreCase(taskDefKey))
      {
        return activeTask.getProcessInstanceId();
      }
    }

    return null;
  }
}
