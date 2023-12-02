/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.loan_amount_calculation.LoanCyclePlan;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.SALARY_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.bpms.loan.consumption.utils.CalculationUtils.AFTER_AVERAGE_SALARY_TAX;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableAllExecutions;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getCaseVariables;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getDisabledExecutions;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendAllActiveProcesses;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LAST_CALCULATED_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_APPROVAL_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class CalculateLoanAmountTask implements JavaDelegate
{
  private final AuthenticationService authenticationService;

  private final ProductRepository productRepository;

  private DefaultParameterRepository defaultParameterRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateLoanAmountTask.class);

  private static final String HAS_MORTGAGE = "hasMortgage";
  private static final String SALARY_AMOUNT_STRING = "salaryAmountString";

  private static final String DEBT_INCOME_BALANCE_STRING = "debtIncomeBalanceString";
  private static final String DEBT_INCOME_BALANCE = "debtIncomeBalance";

  public static final String REJECTED_STATE = "Зөвшөөрөөгүй";

  public CalculateLoanAmountTask(AuthenticationService authenticationService, ProductRepository productRepository,
      DefaultParameterRepository defaultParameterRepository)
  {
    this.authenticationService = authenticationService;
    this.productRepository = productRepository;
    this.defaultParameterRepository = defaultParameterRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngineServices().getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Calculating Loan Amount.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    execution.setVariable(HAS_MORTGAGE, caseVariables.get(HAS_MORTGAGE));
    execution.setVariable(SALARY_AMOUNT, caseVariables.get(SALARY_AMOUNT));
    execution.setVariable(SALARY_AMOUNT_STRING, caseVariables.get(SALARY_AMOUNT_STRING));

    execution.setVariable(DEBT_INCOME_BALANCE_STRING, caseVariables.get(DEBT_INCOME_BALANCE_STRING));
    execution.setVariable(DEBT_INCOME_BALANCE, caseVariables.get(DEBT_INCOME_BALANCE));

    if (caseVariables.containsKey(COLLATERAL_AMOUNT))
    {
      execution.setVariable(COLLATERAL_AMOUNT, caseVariables.get(COLLATERAL_AMOUNT));
      caseService.setVariable(caseInstanceId, COLLATERAL_AMOUNT, caseVariables.get(COLLATERAL_AMOUNT));
    }

    if (caseVariables.containsKey(LOAN_APPROVAL_AMOUNT))
    {
      execution.setVariable(LOAN_APPROVAL_AMOUNT, caseVariables.get(LOAN_APPROVAL_AMOUNT));
    }

    Map<String, Object> variables = execution.getVariables();

    long activeMonthlyLoanPayment = (long) variables.get("monthPaymentActiveLoan");

    BigDecimal debtToIncomeRatio = ((BigDecimal) variables.get(DEBT_INCOME_BALANCE)).setScale(16, RoundingMode.FLOOR);

    long termLong = (long) variables.get("term");
    int term = (int) termLong;

    BigDecimal interestRate = getInterestRate(variables);
    BigDecimal yearlyInterestRate = getYearlyInterestRate(interestRate);

    BigDecimal requestedLoanAmount = BigDecimal.valueOf((long) variables.get("amount"));

    Date firstPaymentDate = (Date) variables.get("firstPaymentDate");
    Date loanGrantDate = (Date) variables.get("loanGrantDate");
    BigDecimal salaryAmount = BigDecimal.valueOf((double) variables.get(SALARY_AMOUNT));
    BigDecimal salaryAmountCoBorrower = BigDecimal.valueOf((long) variables.get("incomeAmountCoBorrower"));
    BigDecimal totalIncomeAmount = salaryAmount.add(salaryAmountCoBorrower);

    String loanProduct = (String) variables.get("loanProduct");
    String productCode = loanProduct.substring(0, 4);

    BigDecimal value0;
    BigDecimal value1;
    BigDecimal value2;
    BigDecimal acceptedLoanAmount;

    String repaymentType = setRepaymentType(productCode, execution, caseService, caseInstanceId);

    if (repaymentType.equals("Нийт төлбөр тэнцүү"))
    {
      value1 = getTentsuuTulburtMax(firstPaymentDate, loanGrantDate, requestedLoanAmount, term, yearlyInterestRate);
    }
    else
    {
      value1 = getUndsenTulburtMax(firstPaymentDate, loanGrantDate, requestedLoanAmount, term, yearlyInterestRate);
    }

    value2 = totalIncomeAmount.multiply(debtToIncomeRatio).setScale(16, RoundingMode.FLOOR)
        .subtract(BigDecimal.valueOf(activeMonthlyLoanPayment).setScale(16, RoundingMode.FLOOR));

    BigDecimal anotherValue = value1.divide(value2, 16, RoundingMode.FLOOR);

    if (anotherValue.compareTo(BigDecimal.ZERO) == 0)
    {
      value0 = requestedLoanAmount;
    }
    else
    {
      value0 = requestedLoanAmount.divide(anotherValue, 16, RoundingMode.FLOOR);
    }

    if (value0.compareTo(BigDecimal.ZERO) == 0 || value0.compareTo(BigDecimal.ZERO) == -1)
    {
      acceptedLoanAmount = BigDecimal.ZERO;//bolomjgui
    }
    else
    {
      acceptedLoanAmount = value0.min(requestedLoanAmount);
    }

    if (term > 30)
    {
      String errorCode = "BPMS056";
      throw new ProcessTaskException(errorCode, "Loan term must not exceed 30!");
    }

    //rounding before setting
    acceptedLoanAmount = acceptedLoanAmount.setScale(0, RoundingMode.FLOOR);
    //set variables
    execution.setVariable(GRANT_LOAN_AMOUNT_STRING, NumberUtils.bigDecimalToString(acceptedLoanAmount));
    execution.setVariable(GRANT_LOAN_AMOUNT, acceptedLoanAmount);
    execution.setVariable("totalIncomeAmount", totalIncomeAmount.setScale(3, RoundingMode.FLOOR));
    execution.setVariable("totalIncomeAmountString", NumberUtils.bigDecimalToString(totalIncomeAmount));
    execution.setVariable("repaymentType", repaymentType);
    execution.setVariable(LAST_CALCULATED_PRODUCT, execution.getVariable(LOAN_PRODUCT_DESCRIPTION));

    caseService.setVariable(caseInstanceId, GRANT_LOAN_AMOUNT_STRING, NumberUtils.bigDecimalToString(acceptedLoanAmount));
    caseService.setVariable(caseInstanceId, GRANT_LOAN_AMOUNT, acceptedLoanAmount);
    caseService.setVariable(caseInstanceId, "totalIncomeAmount", totalIncomeAmount.setScale(3, RoundingMode.FLOOR));
    caseService.setVariable(caseInstanceId, "totalIncomeAmountString", NumberUtils.bigDecimalToString(totalIncomeAmount));
    caseService.setVariable(caseInstanceId, "repaymentType", repaymentType);
    caseService.setVariable(caseInstanceId, LAST_CALCULATED_PRODUCT, LOAN_PRODUCT_DESCRIPTION);

    Object previousAcceptedLoanAmount = execution.getVariable(GRANT_LOAN_AMOUNT);
    if (null == previousAcceptedLoanAmount || Double.parseDouble(String.valueOf(previousAcceptedLoanAmount)) == 0)
    {
      previousAcceptedLoanAmount = acceptedLoanAmount;
    }

    //set interest rate for next task and case folder

    Double interestRateDouble = interestRate.multiply(BigDecimal.valueOf(100)).doubleValue();
    String interestRateString = interestRateDouble.toString() + "%";

    execution.setVariable("interest_rate_string", interestRateString);
    execution.setVariable("interest_rate", interestRateDouble);

    caseService.setVariable(caseInstanceId, "interest_rate_string", interestRateString);
    caseService.setVariable(caseInstanceId, "interest_rate", interestRateDouble);

    if (previousAcceptedLoanAmount instanceof Integer)
    {
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

      execution.setVariable("debtIncomeIssuanceString", "0%");
      execution.setVariable("debtIncomeIssuance", BigDecimal.ZERO);

      caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
      caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

      caseService.setVariable(caseInstanceId, "debtIncomeIssuanceString", "0%");
      caseService.setVariable(caseInstanceId, "debtIncomeIssuance", BigDecimal.ZERO);

      return;
    }
    else if (((BigDecimal) previousAcceptedLoanAmount).compareTo(acceptedLoanAmount) != 0)
    {
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

      execution.setVariable("debtIncomeIssuanceString", "0%");
      execution.setVariable("debtIncomeIssuance", BigDecimal.ZERO);

      caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
      caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

      caseService.setVariable(caseInstanceId, "debtIncomeIssuanceString", "0%");
      caseService.setVariable(caseInstanceId, "debtIncomeIssuance", BigDecimal.ZERO);
      return;
    }
    else
    {
      long fixedAcceptedLoanAmount = (Long) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT);

      if (BigDecimal.valueOf(fixedAcceptedLoanAmount).compareTo(acceptedLoanAmount) == 1)
      {
        throw new ProcessTaskException(BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE,
            BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE);
      }
      if (fixedAcceptedLoanAmount == 0)
      {
        execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
        execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

        execution.setVariable("debtIncomeIssuanceString", "0%");
        execution.setVariable("debtIncomeIssuance", BigDecimal.ZERO);

        caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
        caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

        caseService.setVariable(caseInstanceId, "debtIncomeIssuanceString", "0%");
        caseService.setVariable(caseInstanceId, "debtIncomeIssuance", BigDecimal.ZERO);
      }
      else
      {
        if (repaymentType.equals("Нийт төлбөр тэнцүү"))
        {
          value1 = getTentsuuTulburtMax(firstPaymentDate, loanGrantDate, BigDecimal.valueOf(fixedAcceptedLoanAmount), term, yearlyInterestRate);
        }
        else
        {
          value1 = getUndsenTulburtMax(firstPaymentDate, loanGrantDate, BigDecimal.valueOf(fixedAcceptedLoanAmount), term, yearlyInterestRate);
        }

        BigDecimal averageSalaryAfterTax = BigDecimal.valueOf((double) execution.getVariable(AFTER_AVERAGE_SALARY_TAX));
        BigDecimal totalPayment = value1.add(BigDecimal.valueOf(activeMonthlyLoanPayment));
        BigDecimal totalIncome = averageSalaryAfterTax.add(salaryAmountCoBorrower);

        BigDecimal debtIncomeIssuance = totalPayment.divide(totalIncome, 16, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);

        if (!(debtIncomeIssuance.compareTo(debtToIncomeRatio) == -1))
        {
          debtIncomeIssuance = debtToIncomeRatio;
        }

        execution.setVariable("debtIncomeIssuance", debtIncomeIssuance);
        execution
            .setVariable("debtIncomeIssuanceString", debtIncomeIssuance.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%");
        execution.setVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
        execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
        execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, NumberUtils.longToString(fixedAcceptedLoanAmount));

        caseService.setVariable(caseInstanceId, "debtIncomeIssuance", debtIncomeIssuance);
        caseService.setVariable(caseInstanceId, "debtIncomeIssuanceString",
            debtIncomeIssuance.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%");
        caseService.setVariable(caseInstanceId, OLD_FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);

        caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
        caseService.setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, NumberUtils.longToString(fixedAcceptedLoanAmount));
      }
    }

    LOGGER.info("######### Successful calculated loan amount.");
  }

  private void reEnableSendLoanDecision(String caseInstanceId, DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    List<CaseExecution> disabledExecutions = getDisabledExecutions(caseInstanceId, execution);
    Map<String, Object> caseVariables = getCaseVariables(caseInstanceId, execution.getProcessEngine());

    if (!disabledExecutions.isEmpty())
    {
      for (CaseExecution disabledExecution : disabledExecutions)
      {
        String activityId = disabledExecution.getActivityId();

        if (activityId.equalsIgnoreCase(ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION) && disabledExecution.isDisabled())
        {
          caseService.reenableCaseExecution(disabledExecution.getId(), caseVariables);
          LOGGER.info("######## Re-enables create loan account execution with variable count = {}", caseVariables.size());
        }
      }
    }
  }

  private String setRepaymentType(String productCode, DelegateExecution execution, CaseService caseService, String caseInstanceId) throws
      UseCaseException
  {
    GetProduct getProduct = new GetProduct(productRepository);
    Product product = getProduct.execute(new UniqueProductInput(productCode, "CONSUMER"));

    String repaymentType = product.getCategoryDescription();

    if (repaymentType.equals("Үндсэн төлбөр тэнцүү"))
    {
      execution.setVariable("repaymentTypeId", "equalPrinciplePayment");
      caseService.setVariable(caseInstanceId, "repaymentTypeId", "equalPrinciplePayment");
    }
    else
    {
      execution.setVariable("repaymentTypeId", "equatedMonthlyInstallment");
      caseService.setVariable(caseInstanceId, "repaymentTypeId", "equatedMonthlyInstallment");
    }

    execution.setVariable("repaymentType", repaymentType);

    return repaymentType;
  }

  private BigDecimal getTentsuuTulburtMax(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength,
      BigDecimal yearlyInterestRate) throws UseCaseException, ParseException
  {
    LoanCyclePlan loanCyclePlan = new LoanCyclePlan(firstPaymentDate, loanStartDate, requestedLoanAmount, loanLength, yearlyInterestRate);
    loanCyclePlan = addHolidays(loanCyclePlan);
    loanCyclePlan.setUpLoanCyclePlanEqualTotalPayment();

    return loanCyclePlan.getMaxTotalMonthlyPayment();
  }

  private BigDecimal getUndsenTulburtMax(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength,
      BigDecimal yearlyInterestRate) throws UseCaseException, ParseException
  {
    LoanCyclePlan loanCyclePlan = new LoanCyclePlan(firstPaymentDate, loanStartDate, requestedLoanAmount, loanLength, yearlyInterestRate);
    loanCyclePlan = addHolidays(loanCyclePlan);
    loanCyclePlan.setUpLoanCyclePlanEqualBasicPayment();

    return loanCyclePlan.getMaxTotalMonthlyPayment();
  }

  private BigDecimal getYearlyInterestRate(BigDecimal interestRate)
  {
    return interestRate.multiply(new BigDecimal(12)).setScale(16, RoundingMode.FLOOR);
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

  private LoanCyclePlan addHolidays(LoanCyclePlan loanCyclePlan) throws UseCaseException, ParseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput("LoanCalculation", "Holidays");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);

    Map<String, List<String>> holidays = (Map<String, List<String>>) defaultParameters.get("Holidays");
    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
    for (Map.Entry<String, List<String>> entry : holidays.entrySet())
    {
      for (String holiday : entry.getValue())
      {
        calendar.setTime(dateFormat.parse(holiday));
        loanCyclePlan.addHoliday(calendar.getTime());
      }
    }

    return loanCyclePlan;
  }

  private void disableAndSuspendTasks(String caseInstanceId, DelegateExecution execution)
  {
    suspendAllActiveProcesses(caseInstanceId, execution);
    disableAllExecutions(caseInstanceId, execution);
  }
}

/*
=MIN(D9,IF(Hugatsaa>30,"Зээл олгох боломжгүй",
    IF(D9/(IF(D12="Нийт төлбөр сар бүр тэнцүү",'Тэнцүү төлбөрт'!G6,'Үндсэн төлбөр тэнцүү'!C6)/
    IF(D17="Тийм",D19*H11-D18,D16*H11-D18))<=0,"Зээл олгох боломжгүй",D9/
    (IF(D12="Нийт төлбөр сар бүр тэнцүү",'Тэнцүү төлбөрт'!G6,'Үндсэн төлбөр тэнцүү'!C6)/
    IF(D17="Тийм",D19*H11-D18,D16*H11-D18)))))

If year > 30 --> no loan
else
    {
      if (wantedLoan / someValue1) /  ==
    }

someValue1 = G6 from schedule payments*/
