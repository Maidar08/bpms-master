/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class CalculateLoanAmountMicroTask implements JavaDelegate
{
  private final Environment environment;

  private final AuthenticationService authenticationService;

  private final ProductRepository productRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateLoanAmountMicroTask.class);


  public CalculateLoanAmountMicroTask(Environment environment, AuthenticationService authenticationService,
      ProductRepository productRepository)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication Service is required!");
    this.productRepository = productRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean isLoanAmountCalculate = (boolean)execution.getVariable("calculateLoanAmount");
    if (!isLoanAmountCalculate)
    {
      if (execution.getVariable("grantLoanAmount") == null)
      {
        String errorCode = "BPMS054";
        throw new ProcessTaskException(errorCode, "Must calculate loan amount!");
      }

      Object grantLoanAmountObj = execution.getVariable("grantLoanAmount");
      Object acceptedLoanAmountObj = execution.getVariable("acceptedLoanAmount");

      BigDecimal grantLoanAmountBD;
      BigDecimal acceptedLoanAmountBD;

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

      long acceptedLoanAmountLong = acceptedLoanAmountBD.setScale(2, RoundingMode.HALF_UP).longValue();
      execution.setVariable("acceptedLoanAmount", acceptedLoanAmountLong);

      String acceptedLoanAmountStr = NumberUtils.getThousandSeparatedString(acceptedLoanAmountLong);
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, acceptedLoanAmountStr);
    }

    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Calculating Loan Amount (Micro Task).. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    //----------- set variables from properties
    BigDecimal bichilZeeliinHemjee = new BigDecimal(Objects.requireNonNull(environment.getProperty("bichilZeeliinHemjee")));//30,000,000
    BigDecimal urHurunguHaritsaa = new BigDecimal(Objects.requireNonNull(environment.getProperty("urHurunguHaritsaa")));//2.333
    BigDecimal urHurunguHaritsaa2 = new BigDecimal(Objects.requireNonNull(environment.getProperty("urHurunguHaritsaa2")));//3.33
    BigDecimal bichilZeeliinHemjee2 = new BigDecimal(Objects.requireNonNull(environment.getProperty("bichilZeeliinHemjee2")));//30,000,001
    BigDecimal urOrlogoHaritsaa = new BigDecimal(Objects.requireNonNull(environment.getProperty("urOrlogoHaritsaa")));//.45

    //----------- set variables needed from previous task
    BigDecimal borluulalt = getExecutionVariableToBD(execution,"salesIncome");//D15
    BigDecimal nonOpIncome = getExecutionVariableToBD(execution, "otherIncome");//D43,
    BigDecimal costOfGoods = getExecutionVariableToBD(execution, "costOfGoods");//D20, bburtug
    BigDecimal operatingExpenses = getExecutionVariableToBD(execution, "operatingExpenses");//D41, niit zardal
    BigDecimal rentalExpenses = getExecutionVariableToBD(execution, "rentalExpenses");
    BigDecimal taxFee = getExecutionVariableToBD(execution, "taxCosts");//D47
    BigDecimal nonOpCost = getExecutionVariableToBD(execution, "otherExpense");//D44
    BigDecimal interestPayment = getExecutionVariableToBD(execution, "interestPayment");//D45
    BigDecimal reportingPeriodCash = getExecutionVariableToBD(execution, "reportingPeriodCash");//D53
    BigDecimal fixedAssets = getExecutionVariableToBD(execution, "fixedAssets");//D61
    BigDecimal supplierPay = getExecutionVariableToBD(execution, "supplierPay");//D73
    BigDecimal shortTermPayment = getExecutionVariableToBD(execution, "shortTermPayment");//
    BigDecimal longTermPayment = getExecutionVariableToBD(execution, "longTermPayment");//D77
    BigDecimal currentAssets = getExecutionVariableToBD(execution, "currentAssets"); //D52
    BigDecimal reportPeriod = getExecutionVariableToBD(execution, "reportPeriod");
    BigDecimal netProfit = getExecutionVariableToBD(execution, "netProfit");

    Map<String, BigDecimal> previousCalculationValues = new HashMap<>();

    previousCalculationValues.put("salesIncome", borluulalt);
    previousCalculationValues.put("otherIncome", nonOpIncome);
    previousCalculationValues.put("costOfGoods", costOfGoods);
    previousCalculationValues.put("operatingExpenses", operatingExpenses);
    previousCalculationValues.put("rentalExpenses", rentalExpenses);
    previousCalculationValues.put("taxCosts", taxFee.divide(reportPeriod, 16, RoundingMode.HALF_UP));
    previousCalculationValues.put("otherExpenses", nonOpCost);
    previousCalculationValues.put("interestPayment", interestPayment);
    previousCalculationValues.put("reportingPeriodCash", reportingPeriodCash);
    previousCalculationValues.put("reportPeriod", reportPeriod);
    previousCalculationValues.put("fixedAssets", fixedAssets);
    previousCalculationValues.put("supplierPay", supplierPay);
    previousCalculationValues.put("shortTermPayment", shortTermPayment);
    previousCalculationValues.put("longTermPayment", longTermPayment);
    previousCalculationValues.put("currentAssets", currentAssets);
    previousCalculationValues.put("bichilZeeliinHemjee", bichilZeeliinHemjee);
    previousCalculationValues.put("urHurunguHaritsaa", urHurunguHaritsaa);
    previousCalculationValues.put("urHurunguHaritsaa2", urHurunguHaritsaa2);
    previousCalculationValues.put("bichilZeeliinHemjee2", bichilZeeliinHemjee2);
    previousCalculationValues.put("urOrlogoHaritsaa", urOrlogoHaritsaa);
    previousCalculationValues.put("netProfit", netProfit);


    //----------- calculation

    String lastCalculationType = (String) execution.getVariable("lastCalculationType");

    BigDecimal requestedLoanAmount = BigDecimal.valueOf((long) execution.getVariable("amount"));
    previousCalculationValues.put("requestedLoanAmount", requestedLoanAmount);
    List<BigDecimal> calculationValues = new ArrayList<>();

    BigDecimal urOrlogiinHaritsaa = lastCalculationType.equals("Balance") ? calculateUrOrlogiinHaritsaa(previousCalculationValues, execution) : calculateUrOrlogiinHaritsaaSimple(previousCalculationValues, execution);
    calculationValues.add(urOrlogiinHaritsaa);
    execution.setVariable("debtToIncomeRatio", urOrlogiinHaritsaa.setScale(2, RoundingMode.HALF_UP).longValue());
    String debtToIncomeRatioString = urOrlogiinHaritsaa.compareTo(BigDecimal.valueOf(-999999999)) == 0 ? "Тооцохгүй" : NumberUtils.getThousandSeparatedString(urOrlogiinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    execution.setVariable("debtToIncomeRatioString", debtToIncomeRatioString);
    //new impl
//    String debtToIncomeRatioString = BpmUtils.getValidString(execution.getVariable("debtToIncomeRatioString"));
//
//    String debtToIncomeRatioNew = "";
//    if(BpmUtils.getValidString(debtToIncomeRatioString).equals("Тооцохгүй")){
//      debtToIncomeRatioNew = "Шаардлагагүй";
//    }
//    else
//    {
//      Double netProfitString = BpmUtils.convertObjectToDouble(execution.getVariable("netBenefit"));
//      Double taxCosts = BpmUtils.convertObjectToDouble(execution.getVariable("taxCosts"));
//      Double businessLoanRepayment = BpmUtils.convertObjectToDouble(execution.getVariable("businessLoanRepayment"));
//      Double consumerLoanRepayment = BpmUtils.convertObjectToDouble(execution.getVariable("consumerLoanRepayment"));
//      Double loanTerm = BpmUtils.convertObjectToDouble(execution.getVariable("loanTerm"));
//      Double interestRate = BpmUtils.convertObjectToDouble(execution.getVariable("interestRate"));
//      Double debtToIncomeRatioNewDouble = Double.valueOf(debtToIncomeRatioString);
//      Double debtToIncomeRatio = 0.0;
//      if(debtToIncomeRatioNewDouble == 45){
//        debtToIncomeRatio = (netProfitString + taxCosts - businessLoanRepayment - consumerLoanRepayment / (debtToIncomeRatioNewDouble / 100))
//            / (1 / loanTerm + interestRate / 1200);
//      } else
//      if(debtToIncomeRatioNewDouble == 60){
//        debtToIncomeRatio = (netProfitString - businessLoanRepayment - consumerLoanRepayment / (debtToIncomeRatioNewDouble / 100))
//            / (1 / loanTerm + interestRate / 1200);
//      }
//      debtToIncomeRatioNew = BpmUtils.convertDoubleToString(String.format("%.2f", debtToIncomeRatio), 2);
//    }
//    execution.setVariable("debtToIncomeRatioNew", debtToIncomeRatioNew);

    BigDecimal urTuluhChadvariinHaritsaa = lastCalculationType.equals("Balance") ? calculateUrTuluhChadvariinHaritsaa(previousCalculationValues, execution) : calculateUrTuluhChadvariinHaritsaaSimple(previousCalculationValues, execution);
    calculationValues.add(urTuluhChadvariinHaritsaa);
    execution.setVariable("debtToSolvencyRatio", urTuluhChadvariinHaritsaa.setScale(2, RoundingMode.HALF_UP).longValue());
    String debtToSolvencyRatioString = NumberUtils.getThousandSeparatedString(urTuluhChadvariinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    execution.setVariable("debtToSolvencyRatioString", debtToSolvencyRatioString);

    BigDecimal urHurungiinHaritsaa = lastCalculationType.equals("Balance") ? calculateUrHurungiinHaritsaa(previousCalculationValues, execution) : calculateUrHurungiinHaritsaaSimple(previousCalculationValues, execution);
    //    calculationValues.add(urHurungiinHaritsaa);
    execution.setVariable("debtToAssetsRatio", urHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).longValue());
    //    String debtToAssetsRatioString = urHurungiinHaritsaa.compareTo(BigDecimal.valueOf(0)) <= 0 ? "Тооцохгүй" : NumberUtils.getThousandSeparatedString(urHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    String debtToAssetsRatioString = NumberUtils.getThousandSeparatedString(urHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    execution.setVariable("debtToAssetsRatioString", debtToAssetsRatioString);

    BigDecimal ergeltiinHurungiinHaritsaa = lastCalculationType.equals("Balance") ? calculateErgeltiinHurungiinHaritsaa(previousCalculationValues, execution) : calculateErgeltiinHurungiinHaritsaaSimple(previousCalculationValues, execution);
    //    calculationValues.add(ergeltiinHurungiinHaritsaa);
    execution.setVariable("currentAssetsRatio", ergeltiinHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).longValue());
    //    String currentAssetsRatioString = ergeltiinHurungiinHaritsaa.compareTo(BigDecimal.valueOf(0)) <= 0 ? "Тооцохгүй" : NumberUtils.getThousandSeparatedString(ergeltiinHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    String currentAssetsRatioString = NumberUtils.getThousandSeparatedString(ergeltiinHurungiinHaritsaa.setScale(2, RoundingMode.HALF_UP).doubleValue());
    execution.setVariable("currentAssetsRatioString", currentAssetsRatioString);

    BigDecimal baritsaagaarHangagdahDun = calculateBaritsaagaarHangagdahDun(execution);
    calculationValues.add(baritsaagaarHangagdahDun);
    execution.setVariable("collateralProvidedAmount", baritsaagaarHangagdahDun.setScale(2, RoundingMode.HALF_UP).longValue());
    execution.setVariable("collateralProvidedAmountString", NumberUtils.getThousandSeparatedString(baritsaagaarHangagdahDun.setScale(2, RoundingMode.HALF_UP).doubleValue()));


    BigDecimal min1 = minBigDecimal(calculationValues);
    List<BigDecimal> secondCalculationValuesList = new ArrayList<>();
    secondCalculationValuesList.add(min1);
    secondCalculationValuesList.add(urTuluhChadvariinHaritsaa);
    secondCalculationValuesList.add(urOrlogiinHaritsaa);
    secondCalculationValuesList.add(baritsaagaarHangagdahDun);
    BigDecimal availableLoanAmount = minBigDecimalRequirement(secondCalculationValuesList);
    execution.setVariable("grantLoanAmount", availableLoanAmount.setScale(2, RoundingMode.HALF_UP).longValue());

    BigDecimal acceptedLoanAmount = availableLoanAmount.compareTo(requestedLoanAmount) == 1 ? requestedLoanAmount : availableLoanAmount;
    execution.setVariable("acceptedLoanAmount", acceptedLoanAmount.setScale(2, RoundingMode.HALF_UP).longValue());

    String loanProduct = (String) execution.getVariable("loanProduct");
    String productCode = loanProduct.substring(0, 4);
    CaseService caseService = execution.getProcessEngineServices().getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    setRepaymentType(productCode, execution, caseService, caseInstanceId);

    LOGGER.info("######### Successful calculated loan amount(micro).");
  }

  private BigDecimal getExecutionVariableToBD(DelegateExecution execution, String variableId)
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
      String variableStr = (String)variable;
      //remove commas
      return new BigDecimal(variableStr.replace(",", ""));
    }
    else if (variable instanceof Double)
    {
      return new BigDecimal((double) variable);
    }
    else if (variable instanceof Integer)
    {
      return new BigDecimal((int) variable);
    }
    else
    {
      return BigDecimal.ZERO;
    }
  }

  private BigDecimal minBigDecimal(List<BigDecimal> bigDecimalList)
  {
    BigDecimal min = BigDecimal.valueOf(Long.MAX_VALUE);
    boolean flag = false;

    for (BigDecimal bigDecimal : bigDecimalList)
    {
      if (bigDecimal.compareTo(min) == -1 && bigDecimal.compareTo(BigDecimal.ZERO) > 0)
      {
        min = bigDecimal;
        flag = true;
      }
    }

    return flag ? min : BigDecimal.ZERO;
  }

  private BigDecimal minBigDecimalRequirement(List<BigDecimal> bigDecimalList)
  {
    BigDecimal min = BigDecimal.valueOf(Long.MAX_VALUE);
    boolean flag = false;

    for (BigDecimal bigDecimal : bigDecimalList)
    {
      if (bigDecimal.compareTo(min) == -1 && bigDecimal.compareTo(BigDecimal.valueOf(-999999999)) != 0)
      {
        min = bigDecimal;
        flag = true;
      }
    }

    return flag ? min : BigDecimal.ZERO;
  }

  private BigDecimal calculateUrTuluhChadvariinHaritsaa(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal urTuluhChadvariinHaritsaa;

    BigDecimal bichilZeeliinHemjee = values.get("bichilZeeliinHemjee");

    String loanProductType = (String) execution.getVariable("loanProductType");

    BigDecimal loanPaymentAbilityRatio = BigDecimal.valueOf(1.2); //G24

    BigDecimal borluulalt = values.get("salesIncome");//C37, D15
    BigDecimal bburtug = values.get("costOfGoods");//C39, D20

    BigDecimal operationalExpenses = values.get("operatingExpenses");//C40
    BigDecimal nonOperationalExpenses = values.get("otherExpenses"); //C42

    BigDecimal taxExpenses = values.get("taxCosts"); //C41, D47
    BigDecimal nonOperationalIncome = values.get(OTHER_INCOME);

    BigDecimal previousBusinessLoanPaymentAmount = getExecutionVariableToBD(execution, "businessLoanRepayment");//C33
    BigDecimal previousOtherLoanPaymentAmount = getExecutionVariableToBD(execution, "consumerLoanRepayment");//C32

    BigDecimal interestRate = new BigDecimal((String) execution.getVariable("interestRate"));//C10
    BigDecimal loanTerm = getExecutionVariableToBD(execution, "loanTerm");//C11

    BigDecimal monthlyPersonalExpenses = getExecutionVariableToBD(execution, "householdExpenses");//C45

    BigDecimal requestedLoanAmount = values.get("requestedLoanAmount");//C9

    BigDecimal multiplier = requestedLoanAmount.compareTo(bichilZeeliinHemjee) == 1 ? BigDecimal.ZERO : BigDecimal.ONE;

    BigDecimal beforeTaxNetProfit = borluulalt.subtract(bburtug).subtract(operationalExpenses).add(nonOperationalIncome);
    BigDecimal afterTaxNetProfit = beforeTaxNetProfit.subtract(taxExpenses).subtract(nonOperationalExpenses);

    if (loanProductType.equals("Худалдан авалт"))
    {
      loanPaymentAbilityRatio = BigDecimal.valueOf(.6);
    }

    if (loanProductType.equals("Худалдан авалт"))
    {
      BigDecimal innerAmount2 = interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(loanTerm, 16, RoundingMode.HALF_UP));
      urTuluhChadvariinHaritsaa = loanPaymentAbilityRatio.multiply(afterTaxNetProfit.subtract(previousBusinessLoanPaymentAmount)).subtract(previousOtherLoanPaymentAmount).divide(innerAmount2, 16, RoundingMode.HALF_UP);

    }
    else
    {
      BigDecimal firstValue = afterTaxNetProfit.subtract(monthlyPersonalExpenses.multiply(multiplier));
      BigDecimal secondValue = loanPaymentAbilityRatio.multiply(interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(loanTerm, 16, RoundingMode.HALF_UP)));
      BigDecimal thirdValue = previousBusinessLoanPaymentAmount.add(previousOtherLoanPaymentAmount).divide(interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(loanTerm, 16, RoundingMode.HALF_UP)), 16, RoundingMode.HALF_UP);
      urTuluhChadvariinHaritsaa = firstValue.divide(secondValue, 16, RoundingMode.HALF_UP).subtract(thirdValue);
    }

    return urTuluhChadvariinHaritsaa;
  }

  private BigDecimal calculateUrTuluhChadvariinHaritsaaSimple(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal urTuluhChadvariinHaritsaa;

    BigDecimal bichilZeeliinHemjee = values.get("bichilZeeliinHemjee");

    String loanProductType = (String) execution.getVariable("loanProductType");

    BigDecimal loanPaymentAbilityRatio = BigDecimal.valueOf(1.2); //G24

    BigDecimal previousBusinessLoanPaymentAmount = getExecutionVariableToBD(execution, "businessLoanRepayment");//C33
    BigDecimal previousOtherLoanPaymentAmount = getExecutionVariableToBD(execution, "consumerLoanRepayment");//C32

    BigDecimal interestRate = new BigDecimal((String) execution.getVariable("interestRate"));//C10
    BigDecimal loanTerm = getExecutionVariableToBD(execution, "loanTerm");//C11

    BigDecimal monthlyPersonalExpenses = getExecutionVariableToBD(execution, "householdExpenses");//C45

    BigDecimal requestedLoanAmount = values.get("requestedLoanAmount");//C9

    BigDecimal profit = values.get("netProfit");

    BigDecimal personalExpenseFlag = loanProductType.equals("Жижиг бизнесийн зээл") ? BigDecimal.ZERO : monthlyPersonalExpenses;

    if (loanProductType.equals("Худалдан авалт"))
    {
      loanPaymentAbilityRatio = BigDecimal.valueOf(.6);
    }

    if (loanProductType.equals("Худалдан авалт"))
    {
      BigDecimal value1 = BigDecimal.ONE.divide(loanTerm,16, RoundingMode.HALF_UP).add(interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP));
      urTuluhChadvariinHaritsaa = loanPaymentAbilityRatio.multiply(profit.subtract(previousBusinessLoanPaymentAmount)).subtract(previousOtherLoanPaymentAmount).divide(value1, 16, RoundingMode.HALF_UP);
      //((J20*(K18-Тооцоолол!C33)-Тооцоолол!C32)/(1/Тооцоолол!C11+Тооцоолол!C10/1200))
    }
    else
    {
      BigDecimal value1 = BigDecimal.ONE.divide(loanTerm,16, RoundingMode.HALF_UP).add(interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP));
      BigDecimal value2 = previousBusinessLoanPaymentAmount.add(previousOtherLoanPaymentAmount).divide(value1, 16, RoundingMode.HALF_UP);
      BigDecimal value = profit.subtract(personalExpenseFlag).divide(loanPaymentAbilityRatio.multiply(value1), 16, RoundingMode.HALF_UP).subtract(value2);


      urTuluhChadvariinHaritsaa = requestedLoanAmount.compareTo(value) == -1 ? requestedLoanAmount : value;
    }

    return urTuluhChadvariinHaritsaa;
  }
/*  =IF(H20="Худалдан авалт",((J20*(K18-Тооцоолол!C33)-Тооцоолол!C32)/(1/Тооцоолол!C11+Тооцоолол!C10/1200)),
  MIN(((K18-IF(H20<>"Жижиг бизнесийн зээл",Тооцоолол!C45,0))/(J20*(1/(Тооцоолол!C11)+Тооцоолол!C10/1200))-
    (Тооцоолол!C32+Тооцоолол!C33)/(1/Тооцоолол!C11+Тооцоолол!C10/1200)),
  Тооцоолол!C9))*/

  private BigDecimal calculateUrHurungiinHaritsaa(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal urHurungiinHaritsaa;

    BigDecimal requestedLoanAmount = values.get("requestedLoanAmount");

    BigDecimal urHurungHaritsaaFromProperties = values.get("urHurunguHaritsaa");

    BigDecimal urHurungHaritsaaFromProperties2 = values.get("urHurunguHaritsaa2");

    BigDecimal bichilZeeliinHemjee2 = values.get("bichilZeeliinHemjee2");

    String loanRequesterType = (String) execution.getVariable("borrowerType");

    BigDecimal liquidCash = values.get("reportingPeriodCash");//c23, D53

    BigDecimal ergeltiinHurungu = values.get("currentAssets");//etc. //C24

    BigDecimal undsenHurungu = values.get("fixedAssets"); //c25, D61

    BigDecimal beltgenNiiluulegchUguhOrlogo = values.get("supplierPay"); //c26, D73

    BigDecimal busadBhot = values.get("shortTermPayment"); //c27, D74, D75, D76

    BigDecimal uhot = values.get("longTermPayment"); //c28

    // TODO currently commented because of new request. Delete the commented sections when everything is OK
    //    if (requestedLoanAmount.compareTo(bichilZeeliinHemjee2) == -1 && loanRequesterType.equals("Иргэн"))
    //    {
    //      return BigDecimal.valueOf(-999999999);
    //    }
    //    else
    //    {
    urHurungiinHaritsaa = urHurungHaritsaaFromProperties.multiply(ergeltiinHurungu.add(undsenHurungu)).subtract(urHurungHaritsaaFromProperties2.multiply(busadBhot.add(uhot)));
    //2.33333333333333*(C23+C24+C25)-3.33333333333333*(C26+C27+C28);
    //    }

    return urHurungiinHaritsaa;
  }

  private BigDecimal calculateUrHurungiinHaritsaaSimple(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal urHurungiinHaritsaa;

    BigDecimal urHurungHaritsaaFromProperties = values.get("urHurunguHaritsaa");

    BigDecimal urHurungHaritsaaFromProperties2 = values.get("urHurunguHaritsaa2");


    BigDecimal liquidCash = values.get("reportingPeriodCash");//c23, D53

    BigDecimal ergeltiinHurungu = values.get("currentAssets");//etc. //C24

    BigDecimal undsenHurungu = values.get("fixedAssets"); //c25, D61

    BigDecimal beltgenNiiluulegchUguhOrlogo = values.get("supplierPay"); //c26, D73

    BigDecimal busadBhot = values.get("shortTermPayment"); //c27, D74, D75, D76

    BigDecimal uhot = values.get("longTermPayment"); //c28

    BigDecimal firstValue = urHurungHaritsaaFromProperties.multiply(undsenHurungu.add(ergeltiinHurungu).add(liquidCash));
    BigDecimal secondValue = urHurungHaritsaaFromProperties2.multiply(busadBhot.add(uhot).add(beltgenNiiluulegchUguhOrlogo));

    urHurungiinHaritsaa = firstValue.subtract(secondValue);

    return urHurungiinHaritsaa;
  }

  /*=7/3*(I12+I11+I10)-10/3*(I14+I15+I13)*/

  private BigDecimal calculateErgeltiinHurungiinHaritsaa(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal ergenTulultiinHurungiinHaritsaa;

    String loanReason = (String) execution.getVariable("purposeOfLoan");
    String areasOfActivity = (String) execution.getVariable("areasOfActivity");

    BigDecimal urHurungiinHaritsaa = values.get("urHurunguHaritsaa");

    BigDecimal tailantUgiinBelenMungu = values.get("reportingPeriodCash");//C23, D53

    BigDecimal ergeltiinHurungu = values.get("currentAssets");//etc. //C24

    // TODO currently commented because of new request. Delete the commented sections when everything is OK
    //    if (loanReason.equals("Эргэлтийн хөрөнгө") && areasOfActivity.equals("Худалдаа"))
    //    {
    ergenTulultiinHurungiinHaritsaa = urHurungiinHaritsaa.multiply(ergeltiinHurungu);
    //2.33333333333333*SUM(C23:C24)
    //    }
    //    else
    //    {
    //      return BigDecimal.valueOf(-999999999);
    //    }

    return ergenTulultiinHurungiinHaritsaa;
  }

  private BigDecimal calculateErgeltiinHurungiinHaritsaaSimple(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal ergenTulultiinHurungiinHaritsaa;

    BigDecimal urHurungiinHaritsaa = values.get("urHurunguHaritsaa");

    BigDecimal tailantUgiinBelenMungu = values.get("reportingPeriodCash");//C23, D53

    BigDecimal ergeltiinHurungu = values.get("currentAssets");//etc. //C24

    ergenTulultiinHurungiinHaritsaa = urHurungiinHaritsaa.multiply(ergeltiinHurungu.add(tailantUgiinBelenMungu));

    return ergenTulultiinHurungiinHaritsaa;
  }

  private BigDecimal calculateBaritsaagaarHangagdahDun(DelegateExecution execution)
  {
    BigDecimal totalCollateralAmount = new BigDecimal(0);
    Map<String, Object> variables = execution.getVariables();
    for (Map.Entry<String,Object> entry : variables.entrySet()){
      if(!entry.getKey().equals("collateralAmount") && entry.getKey().contains("collateralAmount")){
        long colAmount = Long.parseLong(String.valueOf(entry.getValue()));
        BigDecimal collateralValue = new BigDecimal(colAmount);
        if(collateralValue.compareTo(BigDecimal.ZERO) > 0){
          totalCollateralAmount = totalCollateralAmount.add(collateralValue);
        }
      }
    }

    //set readonly in execution
    execution.setVariable("loanApprovalAmount0", getExecutionVariableToBD(execution, "collateralAmount0"));
    execution.setVariable("loanApprovalAmount1", getExecutionVariableToBD(execution, "collateralAmount1"));
    execution.setVariable("loanApprovalAmount2", getExecutionVariableToBD(execution, "collateralAmount2"));
    execution.setVariable("loanApprovalAmount3", getExecutionVariableToBD(execution, "collateralAmount3"));
    execution.setVariable("loanApprovalAmount4", getExecutionVariableToBD(execution, "collateralAmount4"));
    execution.setVariable("loanApprovalAmount5", getExecutionVariableToBD(execution, "collateralAmount5"));
    execution.setVariable("loanApprovalAmount6", getExecutionVariableToBD(execution, "collateralAmount6"));
    execution.setVariable("loanApprovalAmount7", getExecutionVariableToBD(execution, "collateralAmount7"));
    execution.setVariable("loanApprovalAmount8", getExecutionVariableToBD(execution, "collateralAmount8"));
    execution.setVariable("loanApprovalAmount9", getExecutionVariableToBD(execution, "collateralAmount9"));
    execution.setVariable("loanApprovalAmount10", getExecutionVariableToBD(execution, "collateralAmount10"));
    execution.setVariable("loanApprovalAmount11", getExecutionVariableToBD(execution, "collateralAmount11"));
    execution.setVariable("loanApprovalAmount12", getExecutionVariableToBD(execution, "collateralAmount12"));
    execution.setVariable("loanApprovalAmount13", getExecutionVariableToBD(execution, "collateralAmount13"));
    execution.setVariable("loanApprovalAmount14", getExecutionVariableToBD(execution, "collateralAmount14"));
    execution.setVariable("loanApprovalAmount15", getExecutionVariableToBD(execution, "collateralAmount15"));
    execution.setVariable("loanApprovalAmount16", getExecutionVariableToBD(execution, "collateralAmount16"));
    execution.setVariable("loanApprovalAmount17", getExecutionVariableToBD(execution, "collateralAmount17"));
    execution.setVariable("loanApprovalAmount18", getExecutionVariableToBD(execution, "collateralAmount18"));
    execution.setVariable("loanApprovalAmount19", getExecutionVariableToBD(execution, "collateralAmount19"));

    return totalCollateralAmount;
  }

  private BigDecimal calculateUrOrlogiinHaritsaa(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    BigDecimal urOrlogiinHaritsaa;

    BigDecimal bichilZeeliinHemjee = values.get("bichilZeeliinHemjee");

    BigDecimal requestedLoanAmount = values.get("requestedLoanAmount");

    BigDecimal sales = values.get("salesIncome");//d15
    BigDecimal nonOpIncome = values.get("otherIncome");//d43
    BigDecimal borluulsanBaraaniiBurtug = values.get("costOfGoods");//d20

    BigDecimal rentalExpenses = values.get("rentalExpenses");//d28
    BigDecimal operationalCost = values.get("operatingExpenses");//c40

    BigDecimal tax = values.get("taxCosts"); //d47

    BigDecimal nonOperationalExpenses = values.get("otherExpenses"); //d44
    BigDecimal taxPayment = values.get("interestPayment");//d45
    BigDecimal otherExpenses = nonOperationalExpenses.add(taxPayment);

    BigDecimal interestRate = new BigDecimal((String) execution.getVariable("interestRate"));//C10
    BigDecimal term = getExecutionVariableToBD(execution, "loanTerm");//C11

    BigDecimal currentMonthlyBusinessLoanPayment = getExecutionVariableToBD(execution, "businessLoanRepayment");

    BigDecimal otherMonthlyLoanPayment = getExecutionVariableToBD(execution, "consumerLoanRepayment");

    BigDecimal beforeTaxNetProfit = sales.subtract(borluulsanBaraaniiBurtug).subtract(operationalCost).subtract(nonOperationalExpenses).add(nonOpIncome);
    BigDecimal afterTaxNetProfit = beforeTaxNetProfit.subtract(tax);

    boolean hasRealEstateLoan  = ((String) execution.getVariable("hasMortgage")).equals("Тийм");
    BigDecimal urOrlogiinHaritsaa2 = null;

    if (hasRealEstateLoan)
    {
      urOrlogiinHaritsaa2 = values.get("urOrlogoHaritsaa");
    }

    String loanProductType = (String) execution.getVariable("loanProductType");

    if (urOrlogiinHaritsaa2 == null)
    {
      return BigDecimal.valueOf(-999999999);
    }
    else
    {
      if (loanProductType.equals("Худалдан авалт"))
      {
        BigDecimal value0 = urOrlogiinHaritsaa2.multiply(beforeTaxNetProfit.subtract(BigDecimal.ZERO).subtract(currentMonthlyBusinessLoanPayment)).subtract(otherMonthlyLoanPayment);
        BigDecimal value1 = interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(term, 16, RoundingMode.HALF_UP));

        urOrlogiinHaritsaa = value0.divide(value1, 16, RoundingMode.HALF_UP);
      }
      else
      {
        if (requestedLoanAmount.compareTo(bichilZeeliinHemjee) == 1)
        {
          BigDecimal value0 = beforeTaxNetProfit.subtract(currentMonthlyBusinessLoanPayment).subtract(otherMonthlyLoanPayment.divide(urOrlogiinHaritsaa2,16, RoundingMode.HALF_UP));
          BigDecimal value1 = interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(term, 16, RoundingMode.HALF_UP));
          urOrlogiinHaritsaa = value0.divide(value1, 16, RoundingMode.HALF_UP);
        }
        else
        {
          BigDecimal value0 = beforeTaxNetProfit.subtract(currentMonthlyBusinessLoanPayment).subtract(otherMonthlyLoanPayment.divide(urOrlogiinHaritsaa2, 16, RoundingMode.HALF_UP));
          BigDecimal value1 = interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(term, 16, RoundingMode.HALF_UP));
          urOrlogiinHaritsaa = value0.divide(value1, 16, RoundingMode.HALF_UP);
        }
      }
    }

    return urOrlogiinHaritsaa;
  }

  private BigDecimal calculateUrOrlogiinHaritsaaSimple(Map<String, BigDecimal> values, DelegateExecution execution)
  {
    boolean hasRealEstateLoan  = ((String) execution.getVariable("hasMortgage")).equals("Үгүй") || ((String) execution.getVariable("hasMortgage")).equals("Банкны нөхцөлтэй орон сууц");
    if (hasRealEstateLoan)
    {
      return BigDecimal.valueOf(-999999999);
    }

    BigDecimal urOrlogiinHaritsaa;

    BigDecimal sales = values.get("salesIncome");//d15
    BigDecimal nonOpIncome = values.get("otherIncome");//d43
    BigDecimal borluulsanBaraaniiBurtug = values.get("costOfGoods");//d20

    BigDecimal rentalExpenses = values.get("rentalExpenses");//d28
    BigDecimal operationalCost = values.get("operatingExpenses");//c40

    BigDecimal tax = values.get("taxCosts"); //d47

    BigDecimal operationalExpenses = values.get("otherExpenses"); //d44
    BigDecimal otherExpenses = operationalExpenses;

    BigDecimal interestRate = new BigDecimal((String) execution.getVariable("interestRate"));//C10
    BigDecimal term = getExecutionVariableToBD(execution, "loanTerm");//C11

    BigDecimal currentMonthlyBusinessLoanPayment = getExecutionVariableToBD(execution, "businessLoanRepayment");

    BigDecimal otherMonthlyLoanPayment = getExecutionVariableToBD(execution, "consumerLoanRepayment");

    BigDecimal urOrlogiinHaritsaa2 = BigDecimal.valueOf(.45);

    BigDecimal reportingPeriod = values.get("reportPeriod");

    String loanProductType = (String) execution.getVariable("loanProductType");

    BigDecimal value1 = BigDecimal.ONE.divide(term,16, RoundingMode.HALF_UP).add(interestRate.divide(BigDecimal.valueOf(1200), 16, RoundingMode.HALF_UP));
    BigDecimal value2 = sales.add(nonOpIncome).subtract(borluulsanBaraaniiBurtug).subtract(operationalCost).subtract(rentalExpenses).subtract(otherExpenses).divide(reportingPeriod, 16, RoundingMode.HALF_UP).subtract(currentMonthlyBusinessLoanPayment);

    if (loanProductType.equals("Худалдан авалт"))
    {
      urOrlogiinHaritsaa = value2.multiply(urOrlogiinHaritsaa2).subtract(otherMonthlyLoanPayment).divide(value1, 16, RoundingMode.HALF_UP);
    }
    else
    {
      urOrlogiinHaritsaa = value2.subtract(otherMonthlyLoanPayment.divide(urOrlogiinHaritsaa2, 16, RoundingMode.HALF_UP)).divide(value1, 16, RoundingMode.HALF_UP);
    }


    return urOrlogiinHaritsaa;
  }

  private String setRepaymentType(String productCode, DelegateExecution execution, CaseService caseService, String caseInstanceId) throws
      UseCaseException
  {
    GetProduct getProduct = new GetProduct(productRepository);
    Product product = getProduct.execute(new UniqueProductInput(productCode, "SMALL_MICRO"));

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

/*  =IF(H20="Худалдан авалт",(((K11+K12-K13-K130-K14-K16-K17)/K10-Тооцоолол!C33)*0.45-Тооцоолол!C32)/(1/Тооцоолол!C11+Тооцоолол!C10/1200),
    (((K11+K12-K13-K130-K14-K16-K17)/K10-Тооцоолол!C33)-Тооцоолол!C32/0.45)/(1/Тооцоолол!C11+Тооцоолол!C10/1200))*/
}
