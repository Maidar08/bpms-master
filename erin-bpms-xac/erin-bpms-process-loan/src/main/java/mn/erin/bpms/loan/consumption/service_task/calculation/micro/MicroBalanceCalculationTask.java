package mn.erin.bpms.loan.consumption.service_task.calculation.micro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.bpms.loan.consumption.utils.NumberUtils.roundWithDecimalPlace;
import static mn.erin.domain.bpm.BpmMessagesConstants.BALANCE_TOTAL_SALE_AMOUNT_ZERO_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BALANCE_TOTAL_SALE_AMOUNT_ZERO_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTALS_JSON;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATION;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_COVERAGE_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALE;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.AMOUNT1;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.AMOUNT2;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.CURRENT_ASSET_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.CURRENT_ASSET_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.EROSION_COST;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.INTEREST_PAYMENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.MAIN_ASSET;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.MAIN_ASSET_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.MAIN_ASSET_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.MN_ACCUMULATED_DEPRECIATION;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.MN_TOTAL_ASSET_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.NON_OP_COST;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.NON_OP_INCOME;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.OPERATION_COST;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.OPERATION_PROFIT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.PERCENT1;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.PERCENT2;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.RENTAL_COST;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.SHORT_TERM_DEBT_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.SHORT_TERM_DEBT_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TAX_FEE;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_DEBT_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_DEBT_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_OPERATION_COST_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_OPERATION_COST_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_OPERATION_INCOME_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_OPERATION_INCOME_PERCENT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_SALE_AMOUNT;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_SALE_COST;
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.TOTAL_SALE_COST_PERCENT;

/**
 * @author Lkhagvadorj.A
 **/

public class MicroBalanceCalculationTask implements JavaDelegate
{
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {

//    if (execution.hasVariable(CALCULATE_BALANCE_CALCULATION) && (boolean) execution.getVariable(CALCULATE_BALANCE_CALCULATION))
//    {
//      if (null == execution.getVariable(REPORT_COVERAGE_PERIOD))
//      {
//        throw new UseCaseException(REPORT_COVERAGE_PERIOD_NULL_CODE, REPORT_COVERAGE_PERIOD_NULL_MESSAGE);
//      }
//
//      try
//      {
//        ProcessEngineServices processEngineServices = execution.getProcessEngineServices();
//        CaseService caseService = processEngineServices.getCaseService();
//        TaskService taskService = processEngineServices.getTaskService();
//
//        RuntimeService runtimeService = processEngineServices.getRuntimeService();
//
//        String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
//
//        String microLoanAmountRootProcessInstanceId = null;
//
//        List<Task> activeTasks = taskService.createTaskQuery()
//            .caseInstanceId(caseInstanceId)
//            .list();
//
//        for (Task activeTask : activeTasks)
//        {
//          String taskDefinitionKey = activeTask.getTaskDefinitionKey();
//
//          if ("user_task_calculate_micro_loan_amount".equalsIgnoreCase(taskDefinitionKey))
//          {
//            microLoanAmountRootProcessInstanceId = activeTask.getProcessInstanceId();
//          }
//        }
//
//        calculateSale(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);
//        calculateOperation(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);
//        calculateAsset(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);
//        calculateDebt(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);
//
//        execution.setVariable("reportPeriod", Long.valueOf((String) execution.getVariable("reportCoveragePeriod")));
//        execution.setVariable("lastCalculationType", "Balance");
//
//        if (null != microLoanAmountRootProcessInstanceId)
//        {
//          runtimeService
//              .setVariable(microLoanAmountRootProcessInstanceId, "reportPeriod", Long.valueOf((String) execution.getVariable("reportCoveragePeriod")));
//          runtimeService.setVariable(microLoanAmountRootProcessInstanceId, "lastCalculationType", "Balance");
//        }
//      }
//      catch (JsonProcessingException exception)
//      {
//        throw new UseCaseException(BALANCE_JSON_EXTRACT_EXCEPTION_CODE, BALANCE_JSON_EXTRACT_EXCEPTION_MESSAGE);
//      }
//    }
  }

  private void calculateSale(DelegateExecution execution, CaseService caseService, String caseInstanceId, String microLoanAmountProcessInstanceId)
      throws JsonProcessingException, UseCaseException
  {
    List<Map<String, Object>> sales = extractJsonList(SALE, execution);
    // this is result json
    Map<String, Object> balanceTotalsJson = extractSingleJson(BALANCE_TOTALS_JSON, execution);
    int reportCoveragePeriod = Integer.parseInt((String) execution.getVariable(REPORT_COVERAGE_PERIOD));
    List<Double> results = calculateTotalSaleAmount(sales, reportCoveragePeriod);
    double totalSalesAmount = results.get(0);
    double totalGoodsAmount = results.get(1);
    double totalGoodsPercent = results.get(2);

    // set percents
    // AMOUNT 1 is sale amount
    // PERCENT 1 is sale amount percent
    // AMOUNT 2 is ББӨртөг amount
    // Percent is ББӨртөг amount percent

    for (Map<String, Object> saleRow : sales)
    {
      double saleAmount = getDoubleValue(saleRow, AMOUNT1);

      double percent1 = findPercent(saleAmount, totalSalesAmount);
      percent1 = percent1 / reportCoveragePeriod;
      saleRow.put(PERCENT1, roundWithDecimalPlace(percent1, 1));

      double goodAmount = getDoubleValue(saleRow, AMOUNT2);
      double percent2 = findPercent(goodAmount, totalGoodsAmount);
      percent2 = percent2 / reportCoveragePeriod;
      saleRow.put(PERCENT2, roundWithDecimalPlace(percent2, 1));
    }

    // Set results
    @SuppressWarnings("unchecked")
    Map<String, Object> saleColHeader = (Map<String, Object>) balanceTotalsJson.get(SALE);

    saleColHeader.put(TOTAL_SALE_AMOUNT, NumberUtils.getThousandSeparatedString(totalSalesAmount));
    execution.setVariable("salesIncome", (long) totalSalesAmount);

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
    if (null != microLoanAmountProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "salesIncome", (long) totalSalesAmount);
    }

    saleColHeader.put(TOTAL_SALE_COST, NumberUtils.getThousandSeparatedString(totalGoodsAmount));
    execution.setVariable("costOfGoods", (long) totalGoodsAmount);

    if (null != microLoanAmountProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "costOfGoods", (long) totalGoodsAmount);
    }

    saleColHeader.put(TOTAL_SALE_COST_PERCENT, roundWithDecimalPlace(totalGoodsPercent, 1));

    setMapVariable(execution, caseService, SALE, sales, caseInstanceId, true, microLoanAmountProcessInstanceId);
    setMapVariable(execution, caseService, BALANCE_TOTALS_JSON, balanceTotalsJson, caseInstanceId, false, microLoanAmountProcessInstanceId);

    setVariable(execution, caseService, BALANCE_TOTAL_INCOME_AMOUNT_STRING, totalSalesAmount - totalGoodsAmount, caseInstanceId,
        microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, BALANCE_TOTAL_INCOME_AMOUNT, totalSalesAmount - totalGoodsAmount, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, BALANCE_TOTAL_INCOME_PERCENT_STRING, roundWithDecimalPlace(100 - totalGoodsPercent, 1), caseInstanceId,
        microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, BALANCE_TOTAL_INCOME_PERCENT, roundWithDecimalPlace(100 - totalGoodsPercent, 1), caseInstanceId,
        microLoanAmountProcessInstanceId);
  }

  private void calculateOperation(DelegateExecution execution, CaseService caseService, String processInstanceId, String microLoanAmountProcessInstanceId)
      throws JsonProcessingException
  {
    List<Map<String, Object>> operationMap = extractJsonList(OPERATION, execution);
    double totalSaleAmount = getTotalSaleAmount(execution);
    int reportCoveragePeriod = Integer.parseInt((String) execution.getVariable(REPORT_COVERAGE_PERIOD));
    double totalIncomeAmount = Double.parseDouble(String.valueOf(execution.getVariable(BALANCE_TOTAL_INCOME_AMOUNT)));
    double sumOfCost = 0;

    // variables
    double erosionCost = 0;
    double totalOperationIncomeAmount;
    double nonOpIncome = 0;
    double nonOpCost = 0;
    double interestPayment = 0;
    double incomeBeforeTax;
    double taxFee = 0;
    double netBenefit;
    double totalIncome;
    double rentalExpenses = 0;

    // calculate sum of cost
    // AMOUNT 1 is operation cost amount
    // PERCENT 1 is operation cost amount percent
    // AMOUNT 2 is operation income amount
    // Percent is operation income amount percent

    for (Map<String, Object> operationRow : operationMap)
    {
      double opCost = getDoubleValue(operationRow, AMOUNT1);

      sumOfCost += opCost;
      double percent1 = findPercent(opCost, totalSaleAmount);
      operationRow.put(PERCENT1, roundWithDecimalPlace(percent1, 1));

      if (String.valueOf(operationRow.get(OPERATION_COST)).equals(EROSION_COST))
      {
        erosionCost = opCost;
      }

      if (String.valueOf(operationRow.get(OPERATION_COST)).equals(RENTAL_COST))
      {
        rentalExpenses = opCost;
      }

      if (operationRow.containsKey(AMOUNT2))
      {
        double opIncome = getDoubleValue(operationRow, AMOUNT2);
        double percent2 = findPercent(opIncome, totalSaleAmount);
        operationRow.put(PERCENT2, roundWithDecimalPlace(percent2, 1));
        switch (String.valueOf(operationRow.get(OPERATION_PROFIT)))
        {
        case NON_OP_INCOME:
          nonOpIncome = opIncome;
          break;
        case NON_OP_COST:
          nonOpCost = opIncome;
          break;
        case INTEREST_PAYMENT:
          interestPayment = opIncome;
          break;
        case TAX_FEE:
          taxFee = opIncome;
          operationRow.put(AMOUNT2, taxFee);
          break;
        default:
          break;
        }
      }
    }

    sumOfCost = round(sumOfCost / reportCoveragePeriod);
    totalOperationIncomeAmount = round(totalIncomeAmount - sumOfCost);

    incomeBeforeTax = totalOperationIncomeAmount + nonOpIncome - nonOpCost - interestPayment;
    netBenefit = incomeBeforeTax - roundWithDecimalPlace(taxFee/reportCoveragePeriod, 8);
    totalIncome = round(incomeBeforeTax + interestPayment + erosionCost);

    // set the results
    operationMap.get(3).put(AMOUNT2, NumberUtils.getThousandSeparatedString(incomeBeforeTax));
    operationMap.get(3).put(PERCENT2, roundWithDecimalPlace(findPercent(incomeBeforeTax, totalSaleAmount), 1));

    operationMap.get(5).put(AMOUNT2, NumberUtils.getThousandSeparatedString(netBenefit));
    operationMap.get(5).put(PERCENT2, roundWithDecimalPlace(findPercent(netBenefit, totalSaleAmount), 1));

    operationMap.get(6).put(AMOUNT2, NumberUtils.getThousandSeparatedString(totalIncome));
    operationMap.get(6).put(PERCENT2, roundWithDecimalPlace(findPercent(totalIncome, totalSaleAmount), 1));

    Map<String, Object> balanceTotalsJson = extractSingleJson(BALANCE_TOTALS_JSON, execution);

    @SuppressWarnings("unchecked")
    Map<String, Object> operationColHeader = (Map<String, Object>) balanceTotalsJson.get(OPERATION);

    operationColHeader.put(TOTAL_OPERATION_COST_AMOUNT, NumberUtils.getThousandSeparatedString(sumOfCost));
    operationColHeader.put(TOTAL_OPERATION_COST_PERCENT, roundWithDecimalPlace(findPercent(sumOfCost, totalSaleAmount), 1));
    operationColHeader.put(TOTAL_OPERATION_INCOME_AMOUNT, NumberUtils.getThousandSeparatedString(totalOperationIncomeAmount));
    operationColHeader.put(TOTAL_OPERATION_INCOME_PERCENT, roundWithDecimalPlace(findPercent(totalOperationIncomeAmount, totalSaleAmount), 1));

    // set variables to execution
    setMapVariable(execution, caseService, OPERATION, operationMap, processInstanceId, true, microLoanAmountProcessInstanceId);
    setMapVariable(execution, caseService, BALANCE_TOTALS_JSON, balanceTotalsJson, processInstanceId, false, microLoanAmountProcessInstanceId);

    //set variables for loan amount calculation
    execution.setVariable("otherIncome", (long) nonOpIncome);
    execution.setVariable("operatingExpenses", (long) sumOfCost);
    execution.setVariable("taxCosts", (long) taxFee);
    execution.setVariable("otherExpense", (long) nonOpCost);
    execution.setVariable("interestPayment", interestPayment);
    execution.setVariable("rentalExpenses", (long) rentalExpenses);

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
    if (null != microLoanAmountProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "otherIncome", (long) nonOpIncome);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "operatingExpenses", (long) sumOfCost);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "taxCosts", (long) taxFee);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "otherExpense", (long) nonOpCost);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "interestPayment", interestPayment);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, "rentalExpenses", (long) rentalExpenses);
    }
  }

  private void calculateAsset(DelegateExecution execution, CaseService caseService, String processInstanceId, String microLoanAmountExecId)
      throws JsonProcessingException
  {
    List<Map<String, Object>> assetMap = extractJsonList(ASSET, execution);

    double currentAsset = 0;
    double mainAsset = 0;
    double reportingPeriodCash = 0;

    // calculate current asset and main asset
    // AMOUNT 1 is current asset amount
    // AMOUNT 2 is main asset amount

    for (Map<String, Object> assetRow : assetMap)
    {
      currentAsset += getDoubleValue(assetRow, AMOUNT1);

      if (String.valueOf(assetRow.get("currentAsset")).equals("Бэлэн мөнгө"))
      {
        reportingPeriodCash = currentAsset;
      }

      if (!String.valueOf(assetRow.get(MAIN_ASSET)).equals(MN_ACCUMULATED_DEPRECIATION) && !String.valueOf(assetRow.get(MAIN_ASSET))
          .equals(MN_TOTAL_ASSET_AMOUNT))
      {
        mainAsset += getDoubleValue(assetRow, AMOUNT2);
      }
      else if (String.valueOf(assetRow.get(MAIN_ASSET)).equals(MN_ACCUMULATED_DEPRECIATION))
      {
        mainAsset -= getDoubleValue(assetRow, AMOUNT2);
      }
    }

    // find total asset amount from json(map) and set
    double totalAsset = currentAsset + mainAsset;
    assetMap.get(assetMap.size() - 1).put(AMOUNT2, totalAsset);
    assetMap.get(assetMap.size() - 1).put(PERCENT2, 100);

    // calculate the other amount's percentage and set it
    // PERCENT 1 is current asset amount percent
    // Percent 2 is main asset amount percent

    for (Map<String, Object> assetRow : assetMap)
    {
      double percent1 = findPercent(getDoubleValue(assetRow, AMOUNT1), totalAsset);
      assetRow.put(PERCENT1, roundWithDecimalPlace(percent1, 1));
      if (!String.valueOf(assetRow.get(MAIN_ASSET)).equals(MN_TOTAL_ASSET_AMOUNT))
      {
        double percent2 = findPercent(getDoubleValue(assetRow, AMOUNT2), totalAsset);
        assetRow.put(PERCENT2, roundWithDecimalPlace(percent2, 1));
      }
    }

    Map<String, Object> balanceTotalsJson = extractSingleJson(BALANCE_TOTALS_JSON, execution);

    @SuppressWarnings("unchecked")
    Map<String, Object> assetColHeader = (Map<String, Object>) balanceTotalsJson.get(ASSET);

    assetColHeader.put(CURRENT_ASSET_AMOUNT, NumberUtils.getThousandSeparatedString(currentAsset));
    assetColHeader.put(CURRENT_ASSET_PERCENT, roundWithDecimalPlace(findPercent(currentAsset, totalAsset), 1));
    assetColHeader.put(MAIN_ASSET_AMOUNT, NumberUtils.getThousandSeparatedString(mainAsset));
    assetColHeader.put(MAIN_ASSET_PERCENT, roundWithDecimalPlace(findPercent(mainAsset, totalAsset), 1));

    execution.setVariable("currentAssets", (long) currentAsset);
    execution.setVariable("fixedAssets", (long) mainAsset);
    execution.setVariable("reportingPeriodCash", (long) reportingPeriodCash);

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
    if (null != microLoanAmountExecId)
    {
      runtimeService.setVariable(microLoanAmountExecId, "currentAssets", (long) currentAsset);
      runtimeService.setVariable(microLoanAmountExecId, "fixedAssets", (long) mainAsset);
      runtimeService.setVariable(microLoanAmountExecId, "reportingPeriodCash", (long) reportingPeriodCash);
    }

    // set variables to execution
    setMapVariable(execution, caseService, ASSET, assetMap, processInstanceId, true, microLoanAmountExecId);
    setMapVariable(execution, caseService, BALANCE_TOTALS_JSON, balanceTotalsJson, processInstanceId, false, microLoanAmountExecId);
  }

  private void calculateDebt(DelegateExecution execution, CaseService caseService, String processInstanceId, String microLoanAmountExecId)
      throws JsonProcessingException
  {
    List<Map<String, Object>> debtMap = extractJsonList(DEBT, execution);

    double supplierPay = 0;

    // calculate short term debt
    double shortTermDebt = getDoubleValue(debtMap.get(0), AMOUNT1) + getDoubleValue(debtMap.get(1), AMOUNT1)
        + getDoubleValue(debtMap.get(2), AMOUNT1) + getDoubleValue(debtMap.get(3), AMOUNT1);
    //set long term debt
    double longTermDebt = getDoubleValue(debtMap.get(5), AMOUNT1);
    debtMap.get(4).put(AMOUNT1, longTermDebt);

    double totalDebt = shortTermDebt + longTermDebt;

    debtMap.get(2).put(AMOUNT2, (getDoubleValue(debtMap.get(3), AMOUNT2) + getDoubleValue(debtMap.get(4), AMOUNT2)));
    double ownersProperty = getDoubleValue(debtMap.get(1), AMOUNT2) + getDoubleValue(debtMap.get(2), AMOUNT2);

    //set owner's Property
    debtMap.get(0).put(AMOUNT2, ownersProperty);

    double totalSource = totalDebt + ownersProperty;
    debtMap.get(5).put(AMOUNT2, totalSource);
    debtMap.get(5).put(PERCENT2, 100);

    // set the other percents
    for (Map<String, Object> debtRow : debtMap)
    {
      double value = getDoubleValue(debtRow, AMOUNT1);
      debtRow.put(PERCENT1, roundWithDecimalPlace(findPercent(value, totalSource), 1));
      if (String.valueOf(debtRow.get(MAIN_ASSET)).equals("Бэлт/нийлүүлэгчийн өглөг"))
      {
        supplierPay = value;
      }

      if (!String.valueOf(debtRow.get(MAIN_ASSET)).equals(MN_TOTAL_ASSET_AMOUNT))
      {
        value = getDoubleValue(debtRow, AMOUNT2);
        debtRow.put(PERCENT2, roundWithDecimalPlace(findPercent(value, totalSource), 1));
      }
    }

    Map<String, Object> balanceTotalsJson = extractSingleJson(BALANCE_TOTALS_JSON, execution);

    @SuppressWarnings("unchecked")
    Map<String, Object> debtColHeader = (Map<String, Object>) balanceTotalsJson.get(DEBT);

    debtColHeader.put(SHORT_TERM_DEBT_AMOUNT, NumberUtils.getThousandSeparatedString(shortTermDebt));
    debtColHeader.put(SHORT_TERM_DEBT_PERCENT, roundWithDecimalPlace(findPercent(shortTermDebt, totalSource), 1));
    debtColHeader.put(TOTAL_DEBT_AMOUNT, NumberUtils.getThousandSeparatedString(totalDebt));
    debtColHeader.put(TOTAL_DEBT_PERCENT, roundWithDecimalPlace(findPercent(totalDebt, totalSource), 1));

    execution.setVariable("longTermPayment", (long) longTermDebt);
    execution.setVariable("shortTermPayment", (long) shortTermDebt);
    execution.setVariable("supplierPay", (long) supplierPay);

    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
    if (null != microLoanAmountExecId)
    {
      runtimeService.setVariable(microLoanAmountExecId, "longTermPayment", (long) longTermDebt);
      runtimeService.setVariable(microLoanAmountExecId, "shortTermPayment", (long) shortTermDebt);
      runtimeService.setVariable(microLoanAmountExecId, "supplierPay", (long) supplierPay);
    }

    // set variables to execution
    setMapVariable(execution, caseService, DEBT, debtMap, processInstanceId, true, microLoanAmountExecId);
    setMapVariable(execution, caseService, BALANCE_TOTALS_JSON, balanceTotalsJson, processInstanceId, false, microLoanAmountExecId);
  }

  // calculate sales amount
  private List<Double> calculateTotalSaleAmount(List<Map<String, Object>> sales, int reportCoveragePeriod) throws UseCaseException
  {
    List<Double> values = new ArrayList<>();
    double amount1 = 0;
    double amount2 = 0;
    for (Map<String, Object> saleRow : sales)
    {
      amount1 += getDoubleValue(saleRow, AMOUNT1);
      amount2 += getDoubleValue(saleRow, AMOUNT2);
    }

    double totalGoodsAmount = round(amount2 / reportCoveragePeriod);
    double totalSalesAmount = round(amount1 / reportCoveragePeriod);
    values.add(totalSalesAmount);
    values.add(totalGoodsAmount);
    if (totalSalesAmount == 0)
    {
      throw new UseCaseException(BALANCE_TOTAL_SALE_AMOUNT_ZERO_CODE, BALANCE_TOTAL_SALE_AMOUNT_ZERO_MESSAGE);
    }
    values.add(round(totalGoodsAmount / totalSalesAmount * 100));
    return values;
  }

  // common methods
  private double findPercent(double value, double total)
  {
    if (value == 0)
    {
      return 0;
    }
    return round((value / total) * 100);
  }

  private double getTotalSaleAmount(DelegateExecution execution) throws JsonProcessingException
  {
    Map<String, Object> balanceTotalsJson = extractSingleJson(BALANCE_TOTALS_JSON, execution);
    @SuppressWarnings("unchecked")
    Map<String, Object> saleMap = (Map<String, Object>) balanceTotalsJson.get(SALE);

    return getDoubleValue(saleMap, TOTAL_SALE_AMOUNT);
  }

  private List<Map<String, Object>> extractJsonList(String key, DelegateExecution execution) throws JsonProcessingException
  {
    Map<String, Object> saleMap = extractSingleJson(key, execution);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> returnMapList = (List<Map<String, Object>>) saleMap.get(key);

    return returnMapList;
  }

  private Map<String, Object> extractSingleJson(String key, DelegateExecution execution) throws JsonProcessingException
  {
    String jsonString = String.valueOf(execution.getVariable(key));

    @SuppressWarnings("unchecked")
    Map<String, Object> returnMap = objectMapper.readValue(jsonString, Map.class);

    return returnMap;
  }

  private double getDoubleValue(Map<String, Object> map, String key)
  {
    return NumberUtils.getDoubleAndRemoveComma(String.valueOf(map.get(key)));
  }

  private void setMapVariable(DelegateExecution execution, CaseService caseService, String key, Object mapJson,
      String caseInstanceId, boolean convertToJson, String activeProcessInstanceId)
      throws JsonProcessingException
  {
    String jsonString = objectMapper.writeValueAsString(mapJson);
    if (convertToJson)
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(key, jsonString);

      setVariable(execution, caseService, key, jsonObject.toString(), caseInstanceId, activeProcessInstanceId);
    }
    else
    {
      setVariable(execution, caseService, key, jsonString, caseInstanceId, activeProcessInstanceId);
    }
  }

  private void setVariable(DelegateExecution execution, CaseService caseService, String key, Object value, String caseInstanceId,
      String activeProcessInstanceId)
  {
    RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();

    execution.setVariable(key, value);
    caseService.setVariable(caseInstanceId, key, value);
    if (activeProcessInstanceId != null)
    {
      runtimeService.setVariable(activeProcessInstanceId, key, value);
    }
  }

  private double round(double value)
  {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
