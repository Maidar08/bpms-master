/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation.mortgage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_MAX_LOAN_AMOUNT_MORTGAGE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_MAX_LOAN_AMOUNT_MORTGAGE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_MAX_LOAN_TERM_MORTGAGE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_MAX_LOAN_TERM_MORTGAGE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_AUTHORIZE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_AUTHORIZE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_CALCULATE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_CALCULATE_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CONDITION_MET;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;

/**
 * @author Zorig
 */
public class MortgageLoanAmountValidation implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Object checker = execution.getVariable(GRANTED_LOAN_AMOUNT);
    Object authorizeCheck = execution.getVariable("authorize");

    if (checker == null)
    {
      throw new UseCaseException(MUST_CALCULATE_CODE, MUST_CALCULATE_MESSAGE);
    }

    if (authorizeCheck == null)
    {
      throw new UseCaseException(MUST_AUTHORIZE_CODE, MUST_AUTHORIZE_MESSAGE);
    }

    //This is set in previous calculation.
    int maxLoanAmount = (int) execution.getVariable(MAX_LOAN_AMOUNT);
    int maxLoanTerm = (int) execution.getVariable(MAX_LOAN_TERM);

    // Validate
    long acceptedLoanAmount = executionVariableToLong(execution, ACCEPTED_LOAN_AMOUNT);
    long loanTerm = executionVariableToLong(execution, LOAN_TERM);
    long grantedLoanAmount = executionVariableToLong(execution, GRANTED_LOAN_AMOUNT);
    long requestedLoanAmount = executionVariableToLong(execution, LOAN_AMOUNT);

    String conditionMet = String.valueOf(execution.getVariable(CONDITION_MET));

    if (!conditionMet.equals(NO_MN_VALUE))
    {
      if (loanTerm > maxLoanTerm)
      {
        throw new UseCaseException(INVALID_MAX_LOAN_TERM_MORTGAGE_CODE, INVALID_MAX_LOAN_TERM_MORTGAGE_MESSAGE);
      }

      if (acceptedLoanAmount > maxLoanAmount)
      {
        throw new UseCaseException(INVALID_MAX_LOAN_AMOUNT_MORTGAGE_CODE, INVALID_MAX_LOAN_AMOUNT_MORTGAGE_MESSAGE);
      }
    }

    if (acceptedLoanAmount > grantedLoanAmount)
    {
      throw new UseCaseException(CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE, CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE);
    }

    if (acceptedLoanAmount > requestedLoanAmount)
    {
      throw new UseCaseException(CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_CODE, CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_MESSAGE);
    }
  }

  private long executionVariableToLong(DelegateExecution execution, String variableId)
  {
    Object variable = execution.getVariable(variableId);

    if (variable == null)
    {
      return 0;
    }

    if (variable instanceof Long)
    {
      return (long) variable;
    }
    else if (variable instanceof Integer)
    {
      return Long.valueOf((int) variable);
    }
    else
    {
      return 0;
    }
  }
}
