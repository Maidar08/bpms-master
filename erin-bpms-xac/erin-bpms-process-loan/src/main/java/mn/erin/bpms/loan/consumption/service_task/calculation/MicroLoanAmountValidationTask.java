/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.bpm.BpmMessagesConstants;

import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;

/**
 * @author Zorig
 */
public class MicroLoanAmountValidationTask implements JavaDelegate
{

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (execution.getVariable("grantLoanAmount") == null)
    {
      String errorCode = "BPMS054";
      throw new ProcessTaskException(errorCode, "Must calculate loan amount!");
    }

    Object grantLoanAmountObj = execution.getVariable("grantLoanAmount");
    Object acceptedLoanAmountObj = execution.getVariable("acceptedLoanAmount");
    Object requestedLoanAmountObj = execution.getVariable("amount");

    BigDecimal grantLoanAmountBD;
    BigDecimal acceptedLoanAmountBD;
    BigDecimal requestedLoanAmount;

    if (requestedLoanAmountObj instanceof Long)
    {
      requestedLoanAmount = new BigDecimal((long) requestedLoanAmountObj);
    }
    else if (requestedLoanAmountObj instanceof Integer)
    {
      requestedLoanAmount = new BigDecimal((int) requestedLoanAmountObj);
    }
    else
    {
      requestedLoanAmount = new BigDecimal((double) requestedLoanAmountObj);
    }

    if (grantLoanAmountObj instanceof Long)
    {
      grantLoanAmountBD = new BigDecimal((long) execution.getVariable("grantLoanAmount"));
    }
    else if (grantLoanAmountObj instanceof Integer)
    {
      grantLoanAmountBD = new BigDecimal((int) execution.getVariable("grantLoanAmount"));
    }
    else
    {
      grantLoanAmountBD = new BigDecimal((double) execution.getVariable("grantLoanAmount"));
    }

    if (acceptedLoanAmountObj instanceof Long)
    {
      acceptedLoanAmountBD = new BigDecimal((long) execution.getVariable("acceptedLoanAmount"));
    }
    else if (acceptedLoanAmountObj instanceof Integer)
    {
      acceptedLoanAmountBD = new BigDecimal((int) execution.getVariable("acceptedLoanAmount"));
    }
    else
    {
      acceptedLoanAmountBD = new BigDecimal((double) execution.getVariable("acceptedLoanAmount"));
    }

    if (acceptedLoanAmountBD.compareTo(grantLoanAmountBD) == 1)
    {
      throw new ProcessTaskException(BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE,
          BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE);
    }

    if (requestedLoanAmount.compareTo(acceptedLoanAmountBD) < 0)
    {
      throw new ProcessTaskException(BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_CODE,
          BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_REQUESTED_ERROR_MESSAGE);
    }

    long acceptedLoanAmountLong = acceptedLoanAmountBD.setScale(2, RoundingMode.HALF_UP).longValue();
    execution.setVariable("acceptedLoanAmount", acceptedLoanAmountLong);
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, acceptedLoanAmountLong);

    String acceptedLoanAmountStr = NumberUtils.getThousandSeparatedString(acceptedLoanAmountLong);
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);
  }
}
