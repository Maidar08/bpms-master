/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation.micro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.loan.GetAccountsList;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.NUMBER_OF_PAYMENTS;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Zorig
 */
public class SetAccountCreationFieldsTaskMicro implements JavaDelegate
{
  private final NewCoreBankingService newCoreBankingService;
  private final AuthenticationService authenticationService;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetAccountCreationFieldsTaskMicro.class);

  public SetAccountCreationFieldsTaskMicro(NewCoreBankingService newCoreBankingService,
      AuthenticationService authenticationService)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Setting Account Creation Fields.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    BigDecimal yearlyInterestRate = null;
    BigDecimal depositInterestRate = null;

    if (execution.getVariable("interestRate") != null)
    {
      yearlyInterestRate = getInterestRate(execution, "interestRate");
      yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
    }
    else if (execution.getVariable("yearlyInterestRate") == null)
    {
      yearlyInterestRate = BigDecimal.ZERO;
    }
    else if (execution.getVariable("yearlyInterestRateString") != null)
    {
      yearlyInterestRate = getInterestRate(execution, "yearlyInterestRateString");
      yearlyInterestRate = yearlyInterestRate.setScale(2, RoundingMode.HALF_UP);
    }

    depositInterestRate = yearlyInterestRate.multiply(BigDecimal.valueOf(.20));
    depositInterestRate = depositInterestRate.setScale(2, RoundingMode.HALF_UP);

    execution.setVariable("yearlyInterestRateString", yearlyInterestRate.toString());
    execution.setVariable("yearlyInterestRate", yearlyInterestRate);
    execution.setVariable("yearlyInterestRateStringPercentage", yearlyInterestRate.toString() + "%");

    execution.setVariable("depositInterestRateString", depositInterestRate.toString() + "%");
    execution.setVariable("depositInterestRate", depositInterestRate);

    //---------------------------------------------------------------------------------

    Object acceptedLoanAmountObj = execution.getVariable("acceptedLoanAmount");
    Double acceptedLoanAmount = (double) 0;

    if (acceptedLoanAmountObj instanceof Integer)
    {
      Integer acceptedLoanAmountInt = (int) acceptedLoanAmountObj;
      acceptedLoanAmount = acceptedLoanAmountInt.doubleValue();
    }
    else if (acceptedLoanAmountObj instanceof Double)
    {
      acceptedLoanAmount = (double) acceptedLoanAmountObj;
    }
    else if (acceptedLoanAmountObj instanceof Long)
    {
      Long acceptedLoanAmountInt = (long) acceptedLoanAmountObj;
      acceptedLoanAmount = acceptedLoanAmountInt.doubleValue();
    }
    String acceptedLoanAmountStr = NumberUtils.getThousandSeparatedString(acceptedLoanAmount.doubleValue());
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);

    //---------------------------------------------------------------------------------

    if (execution.getVariable("currentAccountNumber") != null)
    {
      //set account Branch Number

      String currentAccountNumber = (String) execution.getVariable(CURRENT_ACCOUNT_NUMBER);

      String regNo = (String) execution.getVariable(REGISTER_NUMBER);
      String customerNumber = (String) execution.getVariable(CIF_NUMBER);
      Map<String, String> input = new HashMap<>();
      input.put(REGISTER_NUMBER, regNo);
      input.put(CIF_NUMBER, customerNumber);
      input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      input.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
      String accountBranchNumber = getAccountBranchNumber(input);
      execution.setVariable("accountBranchNumber", accountBranchNumber);
      //and dayOfPayment

      Date firstPaymentDate = (Date) execution.getVariable("firstPaymentDate");

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(firstPaymentDate);
      Integer dayOfPayment = calendar.get(Calendar.DAY_OF_MONTH);

      execution.setVariable("dayOfPayment", dayOfPayment.toString());
    }

    Object loanTerm = execution.getVariable(LOAN_TERM);

    if (null == loanTerm)
    {
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(0));
    }
    else if (loanTerm instanceof Long)
    {
      long microLoanTerm = (long) loanTerm;
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
    }
    else if (loanTerm instanceof Integer)
    {
      Integer microLoanTerm = (Integer) loanTerm;
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
    }
    else if (loanTerm instanceof Double)
    {
      double microLoanTerm = (Double) loanTerm;
      execution.setVariable(NUMBER_OF_PAYMENTS, String.valueOf(microLoanTerm));
    }
    else if (loanTerm instanceof String)
    {
      execution.setVariable(NUMBER_OF_PAYMENTS, loanTerm);
    }

    LOGGER.info("######### Finished Setting Account Creation Fields for MICRO with REQUEST ID = [{}]", requestId);
  }

  private String getAccountBranchNumber(Map<String, String> input) throws UseCaseException
  {
    GetAccountsList getAccountsList = new GetAccountsList(newCoreBankingService);

    List<XacAccount> xacAccounts = getAccountsList.execute(input).getAccountList();

    for (XacAccount xacAccount : xacAccounts)
    {
      if (xacAccount.getId().getId().equalsIgnoreCase(input.get(CURRENT_ACCOUNT_NUMBER)))
      {
        return xacAccount.getBranchId();
      }
    }
    return null;
  }

  private BigDecimal getInterestRate(DelegateExecution execution, String id) throws ProcessTaskException
  {
    String interestRateString = (String) execution.getVariable(id);

    try
    {
      return new BigDecimal(interestRateString);
    }
    catch (Exception e)
    {
      throw new ProcessTaskException("BPMS076", "Invalid percentage!");
    }
  }
}
