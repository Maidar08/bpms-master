/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.bpms.loan.consumption.utils.CalculationUtils.setVariablesHasMortgage;
import static mn.erin.bpms.loan.consumption.utils.CalculationUtils.setVariablesWhenNoMortgage;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;

/**
 * @author Tamir
 */
public class SetLoanAmountCalculationData implements JavaDelegate
{
  private final AuthenticationService authenticationService;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetLoanAmountCalculationData.class);

  private static final String HAS_MORTGAGE = "hasMortgage";

  public SetLoanAmountCalculationData(AuthenticationService authenticationService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    Object dateObject = execution.getVariable("firstPaymentDate");
    String userId = authenticationService.getCurrentUserId();
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info(
          "#########  Filling Data Before Loan Amount Calculation.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: "
              + userId + " , TrackNumber: " + trackNumber);
    }
    else
    {
      LOGGER.info(
          "#########  Filling Data Before Loan Amount Calculation.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: "
              + userId);
    }

    //if interestRate has not been set, then take from previous interest rate case execution variable
    if (execution.getVariable("interestRate") == null)
    {
      Double interestRate = (Double) execution.getVariable("interest_rate");

      if(null != interestRate)
      {
        execution.setVariable("interestRate", interestRate.toString());
      }
    }

    if (dateObject instanceof String)
    {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      String dateString = (String) dateObject;

      Date firstPaymentDate = dateFormat.parse(dateString);

      execution.setVariable("firstPaymentDate", firstPaymentDate);
    }

    if (execution.getVariable("calculateLoanAmount") == null)
    {
      execution.setVariable("incomeAmountCoBorrower", Long.valueOf(0));
    }

    if (execution.getVariable(HAS_MORTGAGE) instanceof Boolean)
    {
      boolean hasMortgage = (boolean) execution.getVariable(HAS_MORTGAGE);

      if (hasMortgage)
      {
        setVariablesHasMortgage(execution);
      }
      else
      {
        setVariablesWhenNoMortgage(execution);
      }
    }

    else if (execution.getVariable(HAS_MORTGAGE) instanceof String)
    {
      String hasMortgageString = (String) execution.getVariable(HAS_MORTGAGE);

      if (hasMortgageString.equals("true"))
      {
        setVariablesHasMortgage(execution);
      }
      else
      {
        setVariablesWhenNoMortgage(execution);
      }
    }

    LOGGER.info("##### Successful filled required data.");
  }
}
