/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation.mortgage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.Finance;
import org.apache.poi.ss.formula.functions.FinanceLib;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROFIT_TYPE_COBORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROFIT_TYPE_REMMITANCE;

/**
 * @author Zorig
 */
public class CalculateMortgageLoanAmountTask implements JavaDelegate
{
  private final Environment environment;
  private final AuthenticationService authenticationService;
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateMortgageLoanAmountTask.class);

  public CalculateMortgageLoanAmountTask(Environment environment, AuthenticationService authenticationService)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication Service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info(
        "#########  Calculating Loan Amount (Mortgage Task).. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    calculateProfitFields(execution);
    calculateTotalFunding(execution);
    calculateMonthlyPayment(execution);
    calculateGrantedLoanAmount(execution);

    //TODO: set case variables and runtime variables for future/active tasks

    LOGGER.info(
        "#########  Finished calculating Loan Amount (Mortgage Task).. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: "
            + userId);
  }

  private void calculateMonthlyPayment(DelegateExecution execution) throws ProcessTaskException
  {
    BigDecimal interestRate = getInterestRate(execution.getVariables());
    BigDecimal yearlyMonthlyDivisor = new BigDecimal(Objects.requireNonNull(environment.getProperty("mortgage.yearlyMonthlyDivisor")));
    BigDecimal loanTerm = executionNumberFieldToBD(execution, "loanTerm");
    BigDecimal requestedLoanAmount = executionNumberFieldToBD(execution, "amount");

    BigDecimal monthlyRate = interestRate.divide(yearlyMonthlyDivisor, 16, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

    BigDecimal pmt = excelPeriodicPaymentFunction(monthlyRate, loanTerm, requestedLoanAmount.multiply(BigDecimal.valueOf(-1)));

    execution.setVariable("loanMonthlyPayment", pmt.setScale(0, RoundingMode.HALF_UP).longValue());
  }

  private void calculateTotalFunding(DelegateExecution execution)
  {
    BigDecimal housingFinancing = executionNumberFieldToBD(execution, "housingFinancing");
    BigDecimal borrowerFinances = executionNumberFieldToBD(execution, "borrowerFinances");
    BigDecimal autoGarage = executionNumberFieldToBD(execution, "autoGarage");
    BigDecimal borrowerFinanceGarage = executionNumberFieldToBD(execution, "borrowerFinanceGarage");

    BigDecimal totalFunding = housingFinancing.subtract(borrowerFinances).add(autoGarage).subtract(borrowerFinanceGarage);
    execution.setVariable("totalFunding", totalFunding);

    execution.setVariable("borrowerFinancesPercent",
        findPercentage(housingFinancing, borrowerFinances).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString() + "%");
    execution.setVariable("borrowerFinanceGaragePercent",
        findPercentage(autoGarage, borrowerFinanceGarage).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString() + "%");
  }

  private void calculateGrantedLoanAmount(DelegateExecution execution) throws ProcessTaskException
  {
    BigDecimal urOrlogiinHaritsaa = new BigDecimal(Objects.requireNonNull(environment.getProperty("mortgage.urOrloginHaritsaa")));

    BigDecimal interestRate = getInterestRate(execution.getVariables());
    BigDecimal yearlyMonthlyDivisor = new BigDecimal(Objects.requireNonNull(environment.getProperty("mortgage.yearlyMonthlyDivisor")));
    BigDecimal loanTerm = executionNumberFieldToBD(execution, "loanTerm");

    BigDecimal monthlyRate = interestRate.divide(yearlyMonthlyDivisor, 16, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

    BigDecimal netIncome = executionNumberFieldToBD(execution, "netIncome");
    BigDecimal activePayment = executionNumberFieldToBD(execution, "monthlyPayment");

    // Updated XAC calculation formula.
    BigDecimal periodicPayment = netIncome.multiply(urOrlogiinHaritsaa)
        .subtract(activePayment)
        .multiply(BigDecimal.valueOf(-1));

    BigDecimal pv = excelPresentValueFunction(monthlyRate, loanTerm, periodicPayment);

    execution.setVariable("loanAmount", pv.setScale(0, RoundingMode.HALF_UP).longValue());
  }

  private void calculateProfitFields(DelegateExecution execution)
  {
    String mortgageLastCalculatonType = (String) execution.getVariable("mortgageLastCalculationType");

    BigDecimal originalBorrowerProfitAmount;
    if (mortgageLastCalculatonType.equals("Salary"))
    {
      //get before tax average salary from salary calculation
      originalBorrowerProfitAmount = executionNumberFieldToBD(execution, "averageSalaryBeforeTax");
    }
    else
    {
      //get tsever ashig field from business calculation
      originalBorrowerProfitAmount = executionNumberFieldToBD(execution, "netProfit");
    }

    BigDecimal coborrowerProfitAmount = BigDecimal.ZERO;
    BigDecimal remmitanceProfitAmount = BigDecimal.ZERO;

    //first row
    String typeOfIncome0 = (String) execution.getVariable("typeOfIncome0");
    BigDecimal seasonOne0 = getProfitField(execution, "seasonOne0");
    BigDecimal seasonTwo0 = getProfitField(execution, "seasonTwo0");
    BigDecimal seasonThree0 = getProfitField(execution, "seasonThree0");
    BigDecimal seasonFour0 = getProfitField(execution, "seasonFour0");
    BigDecimal annualResults0 = seasonOne0.add(seasonTwo0).add(seasonThree0).add(seasonFour0);
    BigDecimal monthlyAverage0 = annualResults0.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_UP);

    execution.setVariable("annualResults0", annualResults0.setScale(0, RoundingMode.HALF_UP).longValue());
    execution.setVariable("monthlyAverage0", monthlyAverage0.setScale(0, RoundingMode.HALF_UP).longValue());

    //second row
    String typeOfIncome1 = (String) execution.getVariable("typeOfIncome1");
    BigDecimal seasonOne1 = getProfitField(execution, "seasonOne1");
    BigDecimal seasonTwo1 = getProfitField(execution, "seasonTwo1");
    BigDecimal seasonThree1 = getProfitField(execution, "seasonThree1");
    BigDecimal seasonFour1 = getProfitField(execution, "seasonFour1");
    BigDecimal annualResults1 = seasonOne1.add(seasonTwo1).add(seasonThree1).add(seasonFour1);
    BigDecimal monthlyAverage1 = annualResults1.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_UP);

    if (annualResults1.compareTo(new BigDecimal(0)) >= 0)
    {
      execution.setVariable("annualResults1", annualResults1.setScale(0, RoundingMode.HALF_UP).longValue());
      execution.setVariable("monthlyAverage1", monthlyAverage1.setScale(0, RoundingMode.HALF_UP).longValue());
    }
    else
    {
      execution.setVariable("annualResults1", -1);
      execution.setVariable("monthlyAverage1", -1);
    }

    //third row
    String typeOfIncome2 = (String) execution.getVariable("typeOfIncome2");
    BigDecimal seasonOne2 = getProfitField(execution, "seasonOne2");
    BigDecimal seasonTwo2 = getProfitField(execution, "seasonTwo2");
    BigDecimal seasonThree2 = getProfitField(execution, "seasonThree2");
    BigDecimal seasonFour2 = getProfitField(execution, "seasonFour2");
    BigDecimal annualResults2 = seasonOne2.add(seasonTwo2).add(seasonThree2).add(seasonFour2);
    BigDecimal monthlyAverage2 = annualResults2.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_UP);

    if (annualResults2.compareTo(new BigDecimal(0)) >= 0)
    {
      execution.setVariable("annualResults2", annualResults2.setScale(0, RoundingMode.HALF_UP).longValue());
      execution.setVariable("monthlyAverage2", monthlyAverage2.setScale(0, RoundingMode.HALF_UP).longValue());
    }
    else {
      execution.setVariable("annualResults2", -1);
      execution.setVariable("monthlyAverage2", -1);
    }

    //total amount additions
    if (typeOfIncome0 != null && !StringUtils.isBlank(typeOfIncome0) && typeOfIncome0.equals(PROFIT_TYPE_COBORROWER))
    {
      coborrowerProfitAmount = coborrowerProfitAmount.add(monthlyAverage0);
    }
    else if (typeOfIncome0 != null && !StringUtils.isBlank(typeOfIncome0) && typeOfIncome0.equals(PROFIT_TYPE_REMMITANCE))
    {
      remmitanceProfitAmount = remmitanceProfitAmount.add(monthlyAverage0);
    }

    if (typeOfIncome1 != null && !StringUtils.isBlank(typeOfIncome1) && typeOfIncome1.equals(PROFIT_TYPE_COBORROWER))
    {
      coborrowerProfitAmount = coborrowerProfitAmount.add(monthlyAverage1);
    }
    else if (typeOfIncome1 != null && !StringUtils.isBlank(typeOfIncome1) && typeOfIncome1.equals(PROFIT_TYPE_REMMITANCE))
    {
      remmitanceProfitAmount = remmitanceProfitAmount.add(monthlyAverage1);
    }

    if (typeOfIncome2 != null && !StringUtils.isBlank(typeOfIncome2) && typeOfIncome2.equals(PROFIT_TYPE_COBORROWER))
    {
      coborrowerProfitAmount = coborrowerProfitAmount.add(monthlyAverage2);
    }
    else if (typeOfIncome2 != null && !StringUtils.isBlank(typeOfIncome2) && typeOfIncome2.equals(PROFIT_TYPE_REMMITANCE))
    {
      remmitanceProfitAmount = remmitanceProfitAmount.add(monthlyAverage2);
    }

    BigDecimal totalProfit = originalBorrowerProfitAmount.add(coborrowerProfitAmount).add(remmitanceProfitAmount);

    //calculation of percentages
    BigDecimal totalProfitPercentage = findPercentage(totalProfit, totalProfit);
    BigDecimal originalBorrowerProfitPercentage = findPercentage(totalProfit, originalBorrowerProfitAmount);
    BigDecimal coborrowerProfitPercentage = findPercentage(totalProfit, coborrowerProfitAmount);
    BigDecimal remmitanceProfitPercentage = findPercentage(totalProfit, remmitanceProfitAmount);

    execution.setVariable("netIncome", totalProfit.setScale(0, RoundingMode.HALF_UP).longValue());
    execution.setVariable("netIncomePercent", totalProfitPercentage.multiply(BigDecimal.valueOf(100)).setScale(0
        , RoundingMode.HALF_UP).toString() + "%");

    execution.setVariable("borrowersIncome", originalBorrowerProfitAmount.setScale(0, RoundingMode.HALF_UP).longValue());
    execution.setVariable("borrowersIncomePercent",
        originalBorrowerProfitPercentage.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString() + "%");

    execution.setVariable("coBorrowersIncome", coborrowerProfitAmount.setScale(0, RoundingMode.HALF_UP).longValue());
    execution.setVariable("coBorrowersIncomePercent",
        coborrowerProfitPercentage.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString() + "%");

    execution.setVariable("transferIncome", remmitanceProfitAmount.setScale(0, RoundingMode.HALF_UP).longValue());
    execution
        .setVariable("transferIncomePercent", remmitanceProfitPercentage.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).toString() + "%");
  }

  private BigDecimal excelPeriodicPaymentFunction(BigDecimal rate, BigDecimal numberOfPayments, BigDecimal presentValue)
  {
    double pmtResult = Finance.pmt(rate.setScale(16, RoundingMode.HALF_UP).doubleValue(), numberOfPayments.setScale(16, RoundingMode.HALF_UP).intValue(),
        presentValue.setScale(16, RoundingMode.HALF_UP).doubleValue());

    return BigDecimal.valueOf(pmtResult);
  }

  private BigDecimal excelPresentValueFunction(BigDecimal rate, BigDecimal numberOfPayments, BigDecimal periodicPayment)
  {
    double pvResult = FinanceLib.pv(rate.setScale(16, RoundingMode.HALF_UP).doubleValue(), numberOfPayments.setScale(16, RoundingMode.HALF_UP).intValue(),
        periodicPayment.setScale(16, RoundingMode.HALF_UP).doubleValue(), 0, false);

    return BigDecimal.valueOf(pvResult);
  }

  private BigDecimal getInterestRate(Map<String, Object> variables) throws ProcessTaskException
  {
    String interestRateString = (String) variables.get("interestRate");

    try
    {
      BigDecimal interestRate = new BigDecimal(interestRateString);

      interestRate = interestRate.divide(new BigDecimal(100), 10, RoundingMode.FLOOR);

      return interestRate;
    }
    catch (Exception e)
    {
      String errorCode = "BPMS076";
      throw new ProcessTaskException(errorCode, "Invalid percentage!");
    }
  }

  private BigDecimal executionNumberFieldToBD(DelegateExecution execution, String variableId)
  {
    Object variable = execution.getVariable(variableId);

    if (variable == null)
    {
      return BigDecimal.ZERO;
    }

    if (variable instanceof Long)
    {
      return new BigDecimal((long) variable);
    }
    else if (variable instanceof String)
    {
      String variableStr = (String) variable;
      //remove commas
      return new BigDecimal(variableStr.replace(",", ""));
    }
    else if (variable instanceof Double)
    {
      return BigDecimal.valueOf((double) variable);
    }
    else if (variable instanceof Integer)
    {
      return BigDecimal.valueOf((int) variable);
    }
    else
    {
      return BigDecimal.ZERO;
    }
  }

  private BigDecimal findPercentage(BigDecimal firstAmount, BigDecimal totalAmount)
  {
    if ((firstAmount.compareTo(BigDecimal.ZERO) != 0))
    {
      return totalAmount.divide(firstAmount, 16, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO;
  }

  private BigDecimal getProfitField(DelegateExecution execution, String variableId)
  {
    Object profitField = execution.getVariable(variableId);

    if (profitField == null)
    {
      return BigDecimal.ZERO;
    }

    return BigDecimal.valueOf((long) profitField);
  }
}
