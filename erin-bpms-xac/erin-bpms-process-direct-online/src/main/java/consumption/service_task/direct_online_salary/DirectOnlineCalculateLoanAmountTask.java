package consumption.service_task.direct_online_salary;

import static consumption.constant.CamundaVariableConstants.AMOUNT;
import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.bpm.domain.ohs.xac.util.XypServiceUtil.SALARY_AMOUNT;
import static mn.erin.common.utils.NumberUtils.bigDecimalToString;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.AFTER_AVERAGE_SALARY_TAX;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_BALANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_ACTIVE_LOAN_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LAST_CALCULATED_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_GRANT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.START;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import consumption.exception.ProcessTaskException;
import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.holidays.LoanCyclePlan;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoOutput;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;
import mn.erin.domain.bpm.util.process.BpmHolidayUtils;

/**
 * @author Lkhagvadorj.A
 **/

public class DirectOnlineCalculateLoanAmountTask implements JavaDelegate
{
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final ProductRepository productRepository;
  private final DefaultParameterRepository defaultParameterRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineCalculateLoanAmountTask.class);

  public DirectOnlineCalculateLoanAmountTask(BpmsServiceRegistry bpmsServiceRegistry, ProductRepository productRepository,
      DefaultParameterRepository defaultParameterRepository)
  {
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.productRepository = productRepository;
    this.defaultParameterRepository = defaultParameterRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String logPrefix = getLogPrefix(processTypeId);
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

    boolean isInstantLoan = processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID);

    Map<String, Object> variables = execution.getVariables();
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("{} Register Number: {}, TrackNumber: {}, Request ID: {}. {}", logPrefix, registrationNumber, trackNumber, requestId,
          (isInstantLoan ? ", actionType :" + execution.getVariable(ACTION_TYPE) : ""));
    }
    else
    {
      LOGGER.info("{} Register Number: {}, Request ID: {}. {}", logPrefix, registrationNumber, requestId,
          (isInstantLoan ? ", actionType :" + execution.getVariable(ACTION_TYPE) : ""));
    }

    long activeMonthlyLoanPayment = Long.parseLong(String.valueOf(variables.get("monthPaymentActiveLoan")));

    BigDecimal debtToIncomeRatio = ((BigDecimal) variables.get(DEBT_INCOME_BALANCE)).setScale(16, RoundingMode.FLOOR);

    long termLong = Long.parseLong(String.valueOf(variables.get(TERM)));
    int term = (int) termLong;

    BigDecimal interestRate = getInterestRate(variables);
    BigDecimal yearlyInterestRate = getYearlyInterestRate(interestRate);
    BigDecimal requestedLoanAmount = getRequestedLoanAmount(execution, variables);
    if (requestedLoanAmount != null){
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, bigDecimalToString(requestedLoanAmount));
    }
    Date loanGrantDate = getLoanGrantDate();
    Date firstPaymentDate = (Date) execution.getVariable(FIRST_PAYMENT_DATE);
    BigDecimal salaryAmount = BigDecimal.valueOf((double) variables.get(SALARY_AMOUNT));
    String loanProduct = (String) variables.get("loanProduct");
    String productCode = loanProduct.substring(0, 4);
    BigDecimal value0;
    BigDecimal totalMonthlyPayment;
    BigDecimal value2;
    BigDecimal acceptedLoanAmount;
    String repaymentType = setRepaymentType(productCode, execution, isInstantLoan);
    Map<String, BigDecimal> totalSalaryMonth = (Map<String, BigDecimal>) variables.get("salary");

    if (repaymentType.equals("Нийт төлбөр тэнцүү"))
    {
      totalMonthlyPayment = getTotalPaymentEqualMax(firstPaymentDate, loanGrantDate, requestedLoanAmount, term, yearlyInterestRate);
    }
    else
    {
      totalMonthlyPayment = getBasicPaymentEqualMax(firstPaymentDate, loanGrantDate, requestedLoanAmount, term, yearlyInterestRate);
    }

    value2 = salaryAmount.multiply(debtToIncomeRatio).setScale(16, RoundingMode.FLOOR)
        .subtract(BigDecimal.valueOf(activeMonthlyLoanPayment).setScale(16, RoundingMode.FLOOR));

    BigDecimal anotherValue = totalMonthlyPayment.divide(value2, 16, RoundingMode.FLOOR);

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
    Double interestRateDouble = interestRate.multiply(BigDecimal.valueOf(100)).doubleValue();
    String interestRateString = interestRateDouble.toString() + "%";

    execution.setVariable(FIRST_PAYMENT_DATE, firstPaymentDate);
    execution.setVariable("interest_rate_string", interestRateString);
    execution.setVariable("interest_rate", interestRateDouble);
    execution.setVariable("totalMonthlyPaymentAmount", totalMonthlyPayment);

    String action = getValidString(execution.getVariable(ACTION_TYPE));
    if(execution.getVariable(GRANT_LOAN_AMOUNT) == null && (StringUtils.isBlank(action) || START.equals(action)))
    {
      execution.setVariable(GRANT_LOAN_AMOUNT_STRING, NumberUtils.bigDecimalToString(acceptedLoanAmount));
      execution.setVariable(GRANT_LOAN_AMOUNT, acceptedLoanAmount);
    }
    execution.setVariable("totalIncomeAmount", salaryAmount.setScale(3, RoundingMode.FLOOR));
    execution.setVariable("totalIncomeAmountString", NumberUtils.bigDecimalToString(salaryAmount));
    execution.setVariable("repaymentType", repaymentType);
    execution.setVariable(LAST_CALCULATED_PRODUCT, execution.getVariable(LOAN_PRODUCT_DESCRIPTION));
    execution.setVariable(LOAN_GRANT_DATE, loanGrantDate);

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("{} Successful calculated loan amount. {}, with tracknumber {}", logPrefix,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) : "."), trackNumber);
    }
    else
    {
      LOGGER.info("{} Successful calculated loan amount. {}", logPrefix, (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) : "."));
    }

    long grantMinimumAmount = (long) execution.getVariable("grantMinimumAmount");
    String processRequestState;
    if (acceptedLoanAmount.compareTo(BigDecimal.valueOf(grantMinimumAmount)) == -1 || totalSalaryMonth.size() < 6)
    {
      processRequestState = fromEnumToString(AMOUNT_REJECTED);
      execution.setVariable(STATE, processRequestState);
      return;
    }
    else
    {
      processRequestState = fromEnumToString(CONFIRMED);
      execution.setVariable(STATE, processRequestState);
    }

    // Confirm Loan Amount
    Object previousAcceptedLoanAmount = execution.getVariable(GRANT_LOAN_AMOUNT);
    if (null == previousAcceptedLoanAmount || Double.parseDouble(String.valueOf(previousAcceptedLoanAmount)) == 0)
    {
      previousAcceptedLoanAmount = acceptedLoanAmount;
    }

    if (previousAcceptedLoanAmount instanceof Integer || ((BigDecimal) previousAcceptedLoanAmount).compareTo(acceptedLoanAmount) != 0)
    {
      if (!isInstantLoan && !processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE) && !processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        setZero(execution);
      }
    }
    long fixedAcceptedLoanAmount = Double.valueOf(String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT))).longValue();

    if (BigDecimal.valueOf(fixedAcceptedLoanAmount).compareTo(acceptedLoanAmount) == 1)
    {
      throw new ProcessTaskException(BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_CODE,
          BpmMessagesConstants.CALCULATION_TOO_BIG_ACCEPTED_AMOUNT_ERROR_MESSAGE);
    }
    else
    {
      BigDecimal averageSalaryAfterTax = BigDecimal.valueOf((double) execution.getVariable(AFTER_AVERAGE_SALARY_TAX));
      BigDecimal totalPayment = totalMonthlyPayment.add(BigDecimal.valueOf(activeMonthlyLoanPayment));
      BigDecimal totalIncome = averageSalaryAfterTax;

      BigDecimal debtIncomeIssuance = totalPayment.divide(totalIncome, 16, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);

      if ((debtIncomeIssuance.compareTo(debtToIncomeRatio) != -1))
      {
        debtIncomeIssuance = debtToIncomeRatio;
      }

      execution.setVariable(DEBT_INCOME_INSURANCE, debtIncomeIssuance);
      execution.setVariable(DEBT_INCOME_INSURANCE_STRING,
          debtIncomeIssuance.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%");
      execution.setVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, fixedAcceptedLoanAmount);
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, NumberUtils.longToString(fixedAcceptedLoanAmount));
      execution.setVariable(DEBT_INCOME_INSURANCE_PERCENT,
          debtIncomeIssuance.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).doubleValue());

      if (debtToIncomeRatio.compareTo(BigDecimal.valueOf(60)) > 0)
      {
        execution.setVariable(STATE, fromEnumToString(REJECTED));
      }
    }
  }

  private String setRepaymentType(String productCode, DelegateExecution execution, boolean isInstantLoan) throws UseCaseException
  {
    GetProduct getProduct = new GetProduct(productRepository);
    String productCategory;
    if (execution.getVariable(PRODUCT_CATEGORY) == null)
    {
      productCategory = isInstantLoan ? INSTANT_LOAN_PROCESS_TYPE_CATEGORY : PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
    }
    else
      productCategory = getValidString(execution.getVariable(PRODUCT_CATEGORY));
    Product product = getProduct.execute(new UniqueProductInput(productCode, productCategory));

    String repaymentType = product.getCategoryDescription();

    if (repaymentType.equals("Үндсэн төлбөр тэнцүү"))
    {
      execution.setVariable("repaymentTypeId", "equalPrinciplePayment");
    }
    else
    {
      execution.setVariable("repaymentTypeId", "equatedMonthlyInstallment");
    }

    execution.setVariable("repaymentType", repaymentType);

    return repaymentType;
  }

  private BigDecimal getTotalPaymentEqualMax(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength,
      BigDecimal yearlyInterestRate) throws UseCaseException, ParseException
  {
    LoanCyclePlan loanCyclePlan = new LoanCyclePlan(firstPaymentDate, loanStartDate, requestedLoanAmount, loanLength, yearlyInterestRate);
    loanCyclePlan = BpmHolidayUtils.addHolidays(loanCyclePlan, defaultParameterRepository);
    loanCyclePlan.setUpLoanCyclePlanEqualTotalPayment();

    return loanCyclePlan.getMaxTotalMonthlyPayment();
  }

  private BigDecimal getBasicPaymentEqualMax(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength,
      BigDecimal yearlyInterestRate) throws UseCaseException, ParseException
  {
    LoanCyclePlan loanCyclePlan = new LoanCyclePlan(firstPaymentDate, loanStartDate, requestedLoanAmount, loanLength, yearlyInterestRate);
    loanCyclePlan = BpmHolidayUtils.addHolidays(loanCyclePlan, defaultParameterRepository);
    loanCyclePlan.setUpLoanCyclePlanEqualBasicPayment();

    return loanCyclePlan.getMaxTotalMonthlyPayment();
  }

  private BigDecimal getYearlyInterestRate(BigDecimal interestRate)
  {
    return interestRate.multiply(new BigDecimal(12)).setScale(16, RoundingMode.FLOOR);
  }

  private BigDecimal getInterestRate(Map<String, Object> variables) throws ProcessTaskException
  {
    String interestRateString = String.valueOf(variables.get(INTEREST_RATE));
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

  private void setZero(DelegateExecution execution)
  {
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, Long.valueOf("0"));
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, "0");

    execution.setVariable(DEBT_INCOME_INSURANCE_STRING, "0%");
    execution.setVariable(DEBT_INCOME_INSURANCE, BigDecimal.ZERO);
  }

  private Date getLoanGrantDate()
  {
    LocalDate systemDate = LocalDate.now();
    return Date.from(systemDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  private BigDecimal getRequestedLoanAmount(DelegateExecution execution, Map<String, Object> variables) throws UseCaseException
  {
    if (String.valueOf(variables.get(FIXED_ACCEPTED_LOAN_AMOUNT)).equals("0"))
    {
      return new BigDecimal(String.valueOf(variables.get(AMOUNT)));
    }
    else
    {
      boolean hasActiveLoan = false;
      if(variables.get(HAS_ACTIVE_LOAN_ACCOUNT) !=null){
         hasActiveLoan = (boolean) variables.get(HAS_ACTIVE_LOAN_ACCOUNT);
      }
      if (hasActiveLoan && getValidString(variables.get(PROCESS_TYPE_ID)).equals(ONLINE_SALARY_PROCESS_TYPE))
      {
        Map<String, String> input = new HashMap<>();
        input.put(REGISTER_NUMBER, getValidString(variables.get(REGISTER_NUMBER)));
        input.put(CIF_NUMBER, getValidString(variables.get(CIF_NUMBER)));
        input.put(PRODUCT_CODE, getValidString(variables.get(PROCESS_TYPE_ID)));
        input.put(PROCESS_TYPE_ID, ONLINE_SALARY_PROCESS_TYPE);
        input.put(PHONE_NUMBER, getValidString(execution.getVariable(PHONE_NUMBER)));
        BigDecimal totalBalance = getTotalBalance(input);
        BigDecimal fixedAcceptedLoanAmount = new BigDecimal(String.valueOf(variables.get(FIXED_ACCEPTED_LOAN_AMOUNT)));
        execution.setVariable("requestedLoanAmount", fixedAcceptedLoanAmount);
        execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, totalBalance.add(fixedAcceptedLoanAmount));
        return totalBalance.add(fixedAcceptedLoanAmount);
      }
    }
    return new BigDecimal(String.valueOf(variables.get(FIXED_ACCEPTED_LOAN_AMOUNT)));
  }
  private BigDecimal getTotalBalance(Map<String, String> input) throws UseCaseException
  {
    GetLoanInfo getLoanInfo = new GetLoanInfo(bpmsServiceRegistry.getNewCoreBankingService(), bpmsServiceRegistry.getDirectOnlineCoreBankingService(),
      productRepository);
    final GetLoanInfoOutput output = getLoanInfo.execute(input);
    if (output == null)
    {
      throw new UseCaseException("Failed to download loan account info for cif = " + input.get(CIF_NUMBER));
    }
    return output.getTotalBalance();
  }
}
