/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CREATE_LOAN_ACCOUNT;
import static mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils.getExecutionParameterLongValue;
import static mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils.getExecutionParameterStringValue;
import static mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils.setVariablesOnAllActiveTasks;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DEPOSIT_INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEPOSIT_INTEREST_RATE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.YEARLY_INTEREST_RATE_STRING_PERCENTAGE;

/**
 * @author Zorig
 */
public class SetFieldsAfterLoanAmountCalculationTask implements JavaDelegate
{
  private final AuthenticationService authenticationService;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetFieldsAfterLoanAmountCalculationTask.class);

  public SetFieldsAfterLoanAmountCalculationTask(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    ProcessEngine processEngine = execution.getProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    CaseService caseService = processEngine.getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String accountTaskProcessId = getActiveAccountTaskProcessId(execution, processEngine);

    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info(
        "#########  Setting Fields After Loan Amount Calculation.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: "
            + userId);

    BigDecimal yearlyInterestRate = null;
    BigDecimal depositInterestRate = null;

    Double interestRate = (Double) execution.getVariable("interest_rate");

    yearlyInterestRate = BigDecimal.valueOf(interestRate).multiply(BigDecimal.valueOf(12));
    yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);

    depositInterestRate = yearlyInterestRate.multiply(BigDecimal.valueOf(.20));
    depositInterestRate = depositInterestRate.setScale(2, RoundingMode.HALF_UP);

    execution.setVariable("interest_rate", interestRate);

    execution.setVariable("yearlyInterestRateString", yearlyInterestRate.toString());
    execution.setVariable("yearlyInterestRate", yearlyInterestRate);
    execution.setVariable("yearlyInterestRateStringPercentage", yearlyInterestRate.toString() + "%");

    execution.setVariable("depositInterestRateString", depositInterestRate.toString() + "%");
    execution.setVariable("depositInterestRate", depositInterestRate);

    // SETs variable to account creation execution.
    if (null != accountTaskProcessId)
    {
      caseService.setVariable(caseInstanceId, "interest_rate", interestRate);
      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE, yearlyInterestRate);
      caseService.setVariable(caseInstanceId, YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");
      caseService.setVariable(caseInstanceId, DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
      caseService.setVariable(caseInstanceId, DEPOSIT_INTEREST_RATE, depositInterestRate);

      runtimeService.setVariable(accountTaskProcessId, "interest_rate", interestRate);
      runtimeService.setVariable(accountTaskProcessId, YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
      runtimeService.setVariable(accountTaskProcessId, YEARLY_INTEREST_RATE, yearlyInterestRate);
      runtimeService.setVariable(accountTaskProcessId, YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");
      runtimeService.setVariable(accountTaskProcessId, DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
      runtimeService.setVariable(accountTaskProcessId, DEPOSIT_INTEREST_RATE, depositInterestRate);
    }

    //----------------------------------------------------------------------------------

    int term = 0;
    if (execution.getVariable("term") instanceof Long)
    {
      long termLong = (long) execution.getVariable("term");
      term = (int) termLong;
    }
    else
    {
      term = (int) execution.getVariable("term");
    }

    execution.setVariable("numberOfPayments", String.valueOf(term));

    Long fixedAcceptedLoanAmount = getExecutionParameterLongValue(execution, FIXED_ACCEPTED_LOAN_AMOUNT);
    Long oldFixedAcceptedLoanAmount = getExecutionParameterLongValue(execution, OLD_FIXED_ACCEPTED_LOAN_AMOUNT);
    String fixedAcceptedLoanAmountString = getExecutionParameterStringValue(execution, FIXED_ACCEPTED_LOAN_AMOUNT_STRING);
    String grantLoanAmountString = getExecutionParameterStringValue(execution, GRANT_LOAN_AMOUNT_STRING);
    String loanProduct = getExecutionParameterStringValue(execution, LOAN_PRODUCT);

    if (null != accountTaskProcessId)
    {
      caseService.setVariable(caseInstanceId, "numberOfPayments", String.valueOf(term));
      runtimeService.setVariable(accountTaskProcessId, "numberOfPayments", String.valueOf(term));
    }

    Map<String, Object> runtimeVariables = new HashMap<>();
    runtimeVariables.put(FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
    runtimeVariables.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, fixedAcceptedLoanAmountString);
    runtimeVariables.put(OLD_FIXED_ACCEPTED_LOAN_AMOUNT, oldFixedAcceptedLoanAmount);
    runtimeVariables.put(YEARLY_INTEREST_RATE_STRING, yearlyInterestRate.toString());
    runtimeVariables.put(YEARLY_INTEREST_RATE, yearlyInterestRate);
    runtimeVariables.put(YEARLY_INTEREST_RATE_STRING_PERCENTAGE, yearlyInterestRate.toString() + "%");
    runtimeVariables.put(DEPOSIT_INTEREST_RATE_STRING, depositInterestRate.toString() + "%");
    runtimeVariables.put(DEPOSIT_INTEREST_RATE, depositInterestRate);

    setVariablesOnAllActiveTasks(execution, runtimeVariables);

    LOGGER.info("######### Finished Setting Field After Loan Amount Calculation...");
  }

  private String getActiveAccountTaskProcessId(DelegateExecution execution, ProcessEngine processEngine)
  {

    TaskService taskService = processEngine.getTaskService();
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    List<Task> activeTasks = taskService.createTaskQuery()
        .caseInstanceId(caseInstanceId)
        .active()
        .list();

    for (Task task : activeTasks)
    {
      String taskDefinitionKey = task.getTaskDefinitionKey();

      if (taskDefinitionKey.equalsIgnoreCase(TASK_DEF_KEY_CREATE_LOAN_ACCOUNT))
      {
        return task.getProcessInstanceId();
      }
    }
    return null;
  }
}
