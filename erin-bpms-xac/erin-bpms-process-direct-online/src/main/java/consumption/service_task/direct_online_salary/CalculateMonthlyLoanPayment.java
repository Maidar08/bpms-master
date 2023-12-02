package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.bnpl.GetRepaymentSchedule;
import mn.erin.domain.bpm.usecase.loan.GetMongolBankInfoNew;

import static consumption.constant.CamundaVariableConstants.HAS_MORTGAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LINE_LOAN_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.MONTH_PAYMENT_ACTIVE_LOAN;
import static mn.erin.domain.bpm.BpmModuleConstants.MORTGAGE_LOAN_MONTH;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_1_APPLICATION_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_2_APPLICATION_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_SCHEDULE_PROJECT;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.stringToDateByFormat;

public class CalculateMonthlyLoanPayment implements JavaDelegate
{
  private final LoanService loanService;
  private final Environment environment;
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProductRepository productRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateMonthlyLoanPayment.class);

  public CalculateMonthlyLoanPayment(LoanService loanService, Environment environment, DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProductRepository productRepository)
  {
    this.loanService = loanService;
    this.environment = environment;
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.productRepository = productRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    Map<String, Loan> xacLoans = (Map<String, Loan>) execution.getVariable(LOAN_INFO);
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

    Map<String, String> input = (Map<String, String>) execution.getVariable("mbRequestParameter");

    try {
      GetMongolBankInfoNew getMongolBankInfoNew = new GetMongolBankInfoNew(loanService);
      Map<String, Object> mbLoanInfo = getMongolBankInfoNew.execute(input);

      List<Loan> loanInfoList = (List<Loan>) mbLoanInfo.get("mbCustomerLoanList");
      List<Loan> coLoanInfoList = (List<Loan>) mbLoanInfo.get("mbCoBorrowerLoans");
      loanInfoList.addAll(coLoanInfoList);
      JSONArray loanInfoJson =  new JSONArray(Arrays.asList(loanInfoList));
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("######## Response from XAC MONGOL BANK service with requestId = {}, registerNumber = {}, trackNumber = {}, [{}]", processRequestId, input.get(REGISTER_NUMBER), trackNumber, loanInfoJson);
      }
      else
      {
        LOGGER.info("######## Response from XAC MONGOL BANK service with requestId = {}, registerNumber = {}, [{}]", processRequestId, input.get(REGISTER_NUMBER), loanInfoJson);
      }

      Map<String, Object> calculationResponseMap = calculateActiveLoansMonthlyPayment(loanInfoList, processRequestId, xacLoans, processTypeId, trackNumber);
      BigDecimal activeLoanMonthlyPayment = (BigDecimal) calculationResponseMap.get(MONTH_PAYMENT_ACTIVE_LOAN);

      xacLoans = filterByProduct(xacLoans, "DEPOSIT_BACKED_LOAN");
      BigDecimal xacLoanRepayment = getXacLoanRepayment(xacLoans, input, trackNumber);
      activeLoanMonthlyPayment = activeLoanMonthlyPayment.add(xacLoanRepayment);
      boolean hasMortgage = (boolean) calculationResponseMap.get(HAS_MORTGAGE);

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("############# CALCULATED ACTIVE  LOAN MONTHLY PAYMENT AMOUNT  WITH PROCESS REQUEST ID [{}], ACTIVE LOAN PAYMENT = [{}], HAS MORTGAGE = [{}], TRACKNUMBER = [{}]",
          processRequestId, activeLoanMonthlyPayment, hasMortgage, trackNumber);
    }
    else
    {
      LOGGER.info("############# CALCULATED ACTIVE  LOAN MONTHLY PAYMENT AMOUNT  WITH PROCESS REQUEST ID [{}], ACTIVE LOAN PAYMENT = [{}], HAS MORTGAGE = [{}]",
          processRequestId, activeLoanMonthlyPayment, hasMortgage);
    }

      execution.setVariable(MONTH_PAYMENT_ACTIVE_LOAN, activeLoanMonthlyPayment.longValue());
      execution.setVariable(HAS_MORTGAGE, hasMortgage);
    } catch (UseCaseException e) {
      throw new ProcessTaskException(e.getCode(), "CIB_FAILED " + e.getMessage());
    }
  }

  private boolean hasLoanAtXacBank(Loan loan, Map<String, Loan> xacLoans)
  {
    String startedDate = loan.getStartDate().contains("/") ? loan.getStartDate().replace("/", "-") : loan.getStartDate();
    String expireDate = loan.getExpireDate().contains("/") ? loan.getExpireDate().replace("/", "-") : loan.getExpireDate();

    BigDecimal amount = loan.getAmount().setScale(0, RoundingMode.HALF_EVEN);
    if (!xacLoans.isEmpty())
    {
      return xacLoans.values().stream().anyMatch(
          loan1 -> loan1.getStartDate().equals(startedDate) && loan1.getExpireDate().equals(expireDate) && loan1.getAmount().setScale(0, RoundingMode.HALF_EVEN)
              .equals(amount));
    }
    else
      return false;
  }

  private Map<String, Object> calculateActiveLoansMonthlyPayment(List<Loan> loanList, String processRequestId, Map<String, Loan> xacLoans, String processTypeId, String trackNumber) throws BpmRepositoryException
  {
    Map<String, Object> responseMap = new HashMap<>();
    BigDecimal totalRepaymentAmountMonthly = new BigDecimal(0);
    BigDecimal monthlyRepaymentAmount;
    boolean hasMortgage;
    Map<String, Boolean> loanTypes = new HashMap<>();

    if (!loanList.isEmpty())
    {
      for (Loan loan : loanList)
      {
        double loanTerm = calculateLoanTerm(loan);
        String mortgageLoanMonthString = Objects.requireNonNull(environment.getProperty(MORTGAGE_LOAN_MONTH), "Cannot get mortgage loan month from property file. ");
        boolean isMortgageLoan = loanTerm >= Integer.parseInt(mortgageLoanMonthString);

        if (!hasLoanAtXacBank(loan, xacLoans))
        {
          if (!(loan.getBalance().equals(BigDecimal.ZERO)))
          {
            double monthlyInterestRate = calculateMonthlyInterestRate(isMortgageLoan);
            monthlyRepaymentAmount = loan.getType().contains(LINE_LOAN_TYPE) ?
                calculateLineLoanMonthlyPayment(loan) :
                calculateSimpleLoanMonthlyPayment(loan, monthlyInterestRate, loanTerm);
            totalRepaymentAmountMonthly = totalRepaymentAmountMonthly.add(monthlyRepaymentAmount);
          }
        }
        loanTypes.put(loan.getId().getId(), isMortgageLoan);
      }
    }
    hasMortgage = checkHasMortgageLoan(loanTypes, xacLoans, processTypeId);
    responseMap.put(MONTH_PAYMENT_ACTIVE_LOAN, totalRepaymentAmountMonthly);
    responseMap.put(HAS_MORTGAGE, hasMortgage);

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("############# CALCULATED ACTIVE LOANS MONTHLY PAYMENT AMOUNT WITH PROCESS REQUEST ID [{}] WITH TRACKNUMBER = [{}] AND RESPONSE [{}]", processRequestId, trackNumber, responseMap);
    }
    else
    {
      LOGGER.info("############# CALCULATED ACTIVE LOANS MONTHLY PAYMENT AMOUNT WITH PROCESS REQUEST ID [{}] AND RESPONSE [{}]", processRequestId, responseMap);
    }

    return responseMap;
  }

  private BigDecimal calculateSimpleLoanMonthlyPayment(Loan loan, double monthlyInterestRate, double loanTerm)
  {
    BigDecimal loanAmount = loan.getAmount();
    BigDecimal powerOfTwoValue = BigDecimal.valueOf(Math.pow(BigDecimal.valueOf(1 + monthlyInterestRate).doubleValue(), loanTerm));
    return (loanAmount.multiply(BigDecimal.valueOf(monthlyInterestRate).multiply(powerOfTwoValue)))
        .divide(powerOfTwoValue.subtract(BigDecimal.ONE), RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
  }

  private BigDecimal calculateLineLoanMonthlyPayment(Loan lineLoan)
  {
    return lineLoan.getAmount().multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_EVEN);
  }

  private double calculateLoanTerm(Loan loanInfo)
  {
    LocalDate startDate = LocalDate.parse(loanInfo.getStartDate(), DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
    LocalDate endDate = LocalDate.parse(loanInfo.getExpireDate(), DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
    int days = (int) ChronoUnit.DAYS.between(startDate, endDate);
    return (double) days / 365 * 12;
  }

  private boolean checkHasMortgageLoan(Map<String, Boolean> loanTypeMap, Map<String, Loan> xacLoans, String processTypeId) throws BpmRepositoryException
  {
    boolean hasMortgageLoan = loanTypeMap.containsValue(true);
    if(!hasMortgageLoan){
      String filterApplicationCatergory = null;
      switch (processTypeId) {
        case ONLINE_SALARY_PROCESS_TYPE:
          filterApplicationCatergory = PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
          break;
        case BNPL_PROCESS_TYPE_ID:
          filterApplicationCatergory = BNPL;
          break;
        case INSTANT_LOAN_PROCESS_TYPE_ID:
          filterApplicationCatergory = INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
          break;
        case ONLINE_LEASING_PROCESS_TYPE_ID:
          filterApplicationCatergory = ONLINE_LEASING_1_APPLICATION_CATEGORY;
          break;
        default:
          throw new NullPointerException("Need process type id");
      }
      xacLoans = filterByProduct(xacLoans, filterApplicationCatergory);
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID)) {
        xacLoans = filterByProduct(xacLoans, ONLINE_LEASING_2_APPLICATION_CATEGORY);
      }
      String mortgageLoanMonthString = Objects.requireNonNull(environment.getProperty(MORTGAGE_LOAN_MONTH), "Cannot get mortgage loan month from property file. ");
      hasMortgageLoan = xacLoans.values().stream().anyMatch(loan -> Double.valueOf(calculateLoanTerm(loan)) >= Integer.parseInt(mortgageLoanMonthString));
    }
    return hasMortgageLoan;
  }

  private double calculateMonthlyInterestRate(boolean isMortgageLoan)
  {
    double mortgageLoanInterest = Double.parseDouble(environment.getProperty("mortgage.loan.interest"));
    double simpleLoanInterest = Double.parseDouble(environment.getProperty("simple.loan.interest"));

    double yearInterest = isMortgageLoan ? mortgageLoanInterest : simpleLoanInterest;
    return yearInterest / 12 / 100;
  }

  private BigDecimal getXacLoanRepayment(Map<String, Loan> xacLoans, Map<String, String> input, String trackNumber)
      throws UseCaseException, BpmRepositoryException, ParseException
  {
    String processTypeId = input.get(PROCESS_TYPE_ID);
    BigDecimal loanSummary = new BigDecimal("0");
    if (processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      xacLoans = filterByProduct(xacLoans, PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY);
    }
    if ( processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      xacLoans = filterByProduct(xacLoans, INSTANT_LOAN_PROCESS_TYPE_CATEGORY);
    }
    for (Loan xacLoan : xacLoans.values())
    {
      Map<String, Object> repaymentSchedule;
      String project = Objects.requireNonNull(environment.getProperty(REPAYMENT_SCHEDULE_PROJECT));
//      GetRepaymentScheduleInput getRepaymentScheduleInput = new GetRepaymentScheduleInput(xacLoan.getId().getId(), project);
      input.put("acid", xacLoan.getId().getId());
      input.put("project", project);
      GetRepaymentSchedule getRepaymentSchedule = new GetRepaymentSchedule(directOnlineCoreBankingService);
      repaymentSchedule = getRepaymentSchedule.execute(input);
      BigDecimal maxPayment = new BigDecimal(0);
      if (!repaymentSchedule.isEmpty())
      {
        List<Map<String, Object>> schedule = (List<Map<String, Object>>) repaymentSchedule.get("schedule");
        maxPayment = getMaxPayment(schedule);
      }
      loanSummary = loanSummary.add(maxPayment);
    }

    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("############# MAX PAYMENT OF SCHEDULED REPAYMENT - [{}], TRACKNUMBER = [{}]",loanSummary, trackNumber);
    }
    else
    {
      LOGGER.info("############# MAX PAYMENT OF SCHEDULED REPAYMENT - [{}]", loanSummary);
    }
    return loanSummary;
  }

  private BigDecimal getMaxPayment(List<Map<String, Object>> repayments) throws ParseException
  {
    BigDecimal maxDecimal = new BigDecimal(0);
    for (Map<String, Object> repayment : repayments)
    {
      LocalDate dueDate = stringToDateByFormat(ISO_DATE_FORMAT, getStringValue(repayment.get("dueDate")));
      if (dueDate.compareTo(LocalDate.now()) > -1)
      {
        maxDecimal = maxDecimal.max((BigDecimal) repayment.get("installmentAmount"));
      }
    }
    return maxDecimal;
  }

  private Map<String, Loan> filterByProduct(Map<String, Loan> xacLoans, String productApplicationCategory) throws BpmRepositoryException
  {
    Map<String, Loan> tempXacLoans = new HashMap<>();
    List<Product> products = productRepository.findByAppCategory(productApplicationCategory);
    List<String> productId = products.stream().map(product -> product.getId().getId()).collect(Collectors.toList());
    for (Map.Entry<String, Loan> entry : xacLoans.entrySet())
    {
      if (!productId.contains(entry.getValue().getType()))
      {
        tempXacLoans.put(entry.getKey(), entry.getValue());
      }
    }
    return tempXacLoans;
  }
}
