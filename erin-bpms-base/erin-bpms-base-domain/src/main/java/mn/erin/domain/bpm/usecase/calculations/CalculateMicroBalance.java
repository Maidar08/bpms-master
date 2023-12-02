package mn.erin.domain.bpm.usecase.calculations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.util.process.BpmNumberUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.BALANCE_TOTAL_SALE_AMOUNT_ZERO_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BALANCE_TOTAL_SALE_AMOUNT_ZERO_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPORT_COVERAGE_PERIOD_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPORT_COVERAGE_PERIOD_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BALANCE_TOTAL_INCOME_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COST_OF_GOODS;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.LAST_CALCULATION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.LONG_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATING_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATION;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.RENTAL_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORTING_PERIOD_CASH;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALE;
import static mn.erin.domain.bpm.BpmModuleConstants.SALES_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.SHORT_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.SUPPLIER_PAY;
import static mn.erin.domain.bpm.BpmModuleConstants.TAX_COSTS;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_BENEFIT;
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
import static mn.erin.domain.bpm.constants.MicroBalanceCalculationConstants.SHORT_TERM_DEBT;
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
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getDoubleAndRemoveComma;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.roundWithDecimalPlace;

/**
 * @author Lkhagvadorj.A
 **/

public class CalculateMicroBalance extends AbstractUseCase<CalculateMicroBalanceInput, CalculateMicroBalanceOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculateMicroBalance.class);
  private static final String USER_TASK_MICRO_BALANCE_CALCULATION = "user_task_micro_balance_calculation";

  private final CaseService caseService;
  private final RuntimeService runtimeService;
  private final TaskService taskService;


  public CalculateMicroBalance(CaseService caseService, RuntimeService runtimeService, TaskService taskService)
  {
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.runtimeService = Objects.requireNonNull(runtimeService, "Runtime service is required!");
    this.taskService = Objects.requireNonNull(taskService, "Task service is required!");
  }

  @Override
  public CalculateMicroBalanceOutput execute(CalculateMicroBalanceInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String instanceId = input.getInstanceId();
    if (StringUtils.isBlank(instanceId))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    int reportPeriod = input.getReportPeriod();
    if (reportPeriod == 0)
    {
      throw new UseCaseException(REPORT_COVERAGE_PERIOD_NULL_CODE, REPORT_COVERAGE_PERIOD_NULL_MESSAGE);
    }

    String microLoanAmountRootProcessInstanceId = getProcessInstanceIdFromTaskService(instanceId);
    String balanceProcessId = getBalanceProcessId(instanceId);

    double totalIncomeAmount = 0;
    double totalIncomePercent = 0;
    Map<String, Map<String, Object>> colHeader = new HashMap<>();

    List<Double> totalIncome = calculateSale(instanceId, microLoanAmountRootProcessInstanceId, input.getSale(), reportPeriod, totalIncomeAmount,
        totalIncomePercent, colHeader, balanceProcessId);
    if (totalIncome.isEmpty())
    {
      throw new UseCaseException(BALANCE_TOTAL_SALE_AMOUNT_ZERO_CODE, BALANCE_TOTAL_SALE_AMOUNT_ZERO_MESSAGE);
    }
    totalIncomeAmount = totalIncome.get(0);
    totalIncomePercent = totalIncome.get(1);

    calculateOperation(instanceId, microLoanAmountRootProcessInstanceId, input.getOperation(), reportPeriod, totalIncomeAmount, colHeader,
        balanceProcessId);
    calculateAsset(instanceId, microLoanAmountRootProcessInstanceId, input.getAsset(), colHeader, balanceProcessId);
    calculateDebt(instanceId, microLoanAmountRootProcessInstanceId, input.getDebt(), colHeader, balanceProcessId);

    caseService.setCaseVariableById(instanceId, REPORT_PERIOD, reportPeriod);
    caseService.setCaseVariableById(instanceId, LAST_CALCULATION_TYPE, BALANCE);

    if (null != balanceProcessId)
    {
      runtimeService.setVariable(balanceProcessId, REPORT_PERIOD, (long) (reportPeriod));
      runtimeService.setVariable(balanceProcessId, LAST_CALCULATION_TYPE, BALANCE);
    }

    if (null != microLoanAmountRootProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountRootProcessInstanceId, REPORT_PERIOD, (long) (reportPeriod));
      runtimeService.setVariable(microLoanAmountRootProcessInstanceId, LAST_CALCULATION_TYPE, BALANCE);
    }

    return new CalculateMicroBalanceOutput(reportPeriod, input.getSale(), input.getOperation(), input.getAsset(), input.getDebt(), colHeader, totalIncomeAmount,
        totalIncomePercent);
  }

  private String getBalanceProcessId(String instanceId)
  {
    LOGGER.info("################## GETS BALANCE ACTIVE PROCESS ID ... with CASE INSTANCE ID = [{}]", instanceId);
    List<Task> activeTasks = taskService.getActiveByCaseInstanceId(instanceId);

    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getDefinitionKey();

      if (USER_TASK_MICRO_BALANCE_CALCULATION.equalsIgnoreCase(taskDefinitionKey))
      {
        LOGGER.info("################## FOUND ACTIVE BALANCE PROCESS ID  = [{}] with CASE INSTANCE ID = [{}]", activeTask.getProcessInstanceId(), instanceId);
        return activeTask.getProcessInstanceId();
      }
    }

    LOGGER.info("############ NOT FOUND ACTIVE BALANCE PROCESS ID! with  CASE INSTANCE ID = [{}]", instanceId);
    return null;
  }

  private String getProcessInstanceIdFromTaskService(String instanceId)
  {
    List<Task> activeTasks = taskService.getActiveByCaseInstanceId(instanceId);
    for (Task activeTask : activeTasks)
    {
      String taskDefinitionKey = activeTask.getDefinitionKey();

      if ("user_task_calculate_micro_loan_amount".equalsIgnoreCase(taskDefinitionKey))
      {
        return activeTask.getProcessInstanceId();
      }
    }
    return null;
  }

  private List<Double> calculateSale(String instanceId, String microLoanAmountProcessInstanceId, Map<Integer, Map<String, Object>> sale, int reportPeriod,
      double totalIncomeAmount, double totalIncomePercent,
      Map<String, Map<String, Object>> colHeader, String balanceProcessId)
      throws UseCaseException
  {
    List<Double> results = calculateTotalSaleAmount(sale, reportPeriod);
    double totalSalesAmount = results.get(0);
    double totalGoodsAmount = results.get(1);
    double totalGoodsPercent = results.get(2);

    // set percents
    // AMOUNT 1 is sale amount
    // PERCENT 1 is sale amount percent
    // AMOUNT 2 is ББӨртөг amount
    // Percent is ББӨртөг amount percent
    for (Map.Entry<Integer, Map<String, Object>> row : sale.entrySet())
    {
      Map<String, Object> saleRow = row.getValue();
      double saleAmount = getDoubleValue(saleRow, AMOUNT1);

      double percent1 = findPercent(saleAmount, totalSalesAmount);
      percent1 = percent1 / reportPeriod;
      saleRow.put(PERCENT1, roundWithDecimalPlace(percent1, 1));

      double goodAmount = getDoubleValue(saleRow, AMOUNT2);
      double percent2 = findPercent(goodAmount, totalGoodsAmount);
      percent2 = percent2 / reportPeriod;
      saleRow.put(PERCENT2, roundWithDecimalPlace(percent2, 1));
    }

    // Set results
    Map<String, Object> saleColHeader = new HashMap<>();
    saleColHeader.put(TOTAL_SALE_AMOUNT, getThousandSeparatedString(totalSalesAmount));
    saleColHeader.put(TOTAL_SALE_COST, getThousandSeparatedString(totalGoodsAmount));
    saleColHeader.put(TOTAL_SALE_COST_PERCENT, roundWithDecimalPlace(totalGoodsPercent, 1));
    colHeader.put(SALE, saleColHeader);

    totalIncomeAmount = totalSalesAmount - totalGoodsAmount;
    totalIncomePercent = roundWithDecimalPlace(100 - totalGoodsPercent, 1);

    caseService.setCaseVariableById(instanceId, SALES_INCOME, (long) totalSalesAmount);
    caseService.setCaseVariableById(instanceId, COST_OF_GOODS, (long) totalGoodsAmount);
    caseService.setCaseVariableById(instanceId, BALANCE_TOTAL_INCOME_AMOUNT, totalIncomeAmount);
    caseService.setCaseVariableById(instanceId, BALANCE_TOTAL_INCOME_PERCENT, totalIncomePercent);

    if (null != balanceProcessId)
    {
      runtimeService.setVariable(balanceProcessId, SALES_INCOME, (long) totalSalesAmount);
      runtimeService.setVariable(balanceProcessId, COST_OF_GOODS, (long) totalGoodsAmount);

      runtimeService.setVariable(balanceProcessId, BALANCE_TOTAL_INCOME_AMOUNT, totalIncomeAmount);
      runtimeService.setVariable(balanceProcessId, BALANCE_TOTAL_INCOME_PERCENT, totalIncomePercent);
    }

    if (null != microLoanAmountProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountProcessInstanceId, SALES_INCOME, (long) totalSalesAmount);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, COST_OF_GOODS, (long) totalGoodsAmount);

      runtimeService.setVariable(microLoanAmountProcessInstanceId, BALANCE_TOTAL_INCOME_AMOUNT, totalIncomeAmount);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, BALANCE_TOTAL_INCOME_PERCENT, totalIncomePercent);
    }

    return Arrays.asList(totalIncomeAmount, totalIncomePercent);
  }

  private void calculateOperation(String instanceId, String microLoanAmountProcessInstanceId, Map<Integer, Map<String, Object>> operation, int reportPeriod,
      double totalIncomeAmount,
      Map<String, Map<String, Object>> colHeader, String balanceProcessId)
  {
    double totalSaleAmount = getTotalSaleAmount(colHeader);
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

    for (Map.Entry<Integer, Map<String, Object>> row : operation.entrySet())
    {
      Map<String, Object> operationRow = row.getValue();
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

    sumOfCost = round(sumOfCost / reportPeriod);
    totalOperationIncomeAmount = round(totalIncomeAmount - sumOfCost);

    incomeBeforeTax = totalOperationIncomeAmount + nonOpIncome - nonOpCost - interestPayment;
    netBenefit = incomeBeforeTax - roundWithDecimalPlace(taxFee / reportPeriod, 8);
    totalIncome = round(incomeBeforeTax + interestPayment + erosionCost);

    // set the results
    operation.get(3).put(AMOUNT2, getThousandSeparatedString(incomeBeforeTax));
    operation.get(3).put(PERCENT2, roundWithDecimalPlace(findPercent(incomeBeforeTax, totalSaleAmount), 1));
    operation.get(5).put(AMOUNT2, getThousandSeparatedString(netBenefit));
    operation.get(5).put(PERCENT2, roundWithDecimalPlace(findPercent(netBenefit, totalSaleAmount), 1));
    operation.get(6).put(AMOUNT2, getThousandSeparatedString(totalIncome));
    operation.get(6).put(PERCENT2, roundWithDecimalPlace(findPercent(totalIncome, totalSaleAmount), 1));

    Map<String, Object> operationColHeader = new HashMap<>();
    operationColHeader.put(TOTAL_OPERATION_COST_AMOUNT, getThousandSeparatedString(sumOfCost));
    operationColHeader.put(TOTAL_OPERATION_COST_PERCENT, roundWithDecimalPlace(findPercent(sumOfCost, totalSaleAmount), 1));
    operationColHeader.put(TOTAL_OPERATION_INCOME_AMOUNT, getThousandSeparatedString(totalOperationIncomeAmount));
    operationColHeader.put(TOTAL_OPERATION_INCOME_PERCENT, roundWithDecimalPlace(findPercent(totalOperationIncomeAmount, totalSaleAmount), 1));

    colHeader.put(OPERATION, operationColHeader);

    caseService.setCaseVariableById(instanceId, OTHER_INCOME, (long) nonOpIncome);
    caseService.setCaseVariableById(instanceId, OPERATING_EXPENSES, (long) sumOfCost);
    //    caseService.setCaseVariableById(instanceId, TAX_COSTS, (long) roundWithDecimalPlace(taxFee/reportPeriod, 8));
    caseService.setCaseVariableById(instanceId, TAX_COSTS, (long) taxFee);
    caseService.setCaseVariableById(instanceId, OTHER_EXPENSES, (long) nonOpCost);
    caseService.setCaseVariableById(instanceId, INTEREST_PAYMENT, interestPayment);
    caseService.setCaseVariableById(instanceId, RENTAL_EXPENSES, (long) rentalExpenses);
    caseService.setCaseVariableById(instanceId, NET_BENEFIT, netBenefit);

    if (null != balanceProcessId)
    {
      runtimeService.setVariable(balanceProcessId, OTHER_INCOME, (long) nonOpIncome);
      runtimeService.setVariable(balanceProcessId, OPERATING_EXPENSES, (long) sumOfCost);
      runtimeService.setVariable(balanceProcessId, TAX_COSTS, (long) taxFee);
      runtimeService.setVariable(balanceProcessId, OTHER_EXPENSES, (long) nonOpCost);
      runtimeService.setVariable(balanceProcessId, INTEREST_PAYMENT, interestPayment);
      runtimeService.setVariable(balanceProcessId, RENTAL_EXPENSES, (long) rentalExpenses);
      runtimeService.setVariable(balanceProcessId, NET_BENEFIT, netBenefit);
    }

    if (null != microLoanAmountProcessInstanceId)
    {
      runtimeService.setVariable(microLoanAmountProcessInstanceId, OTHER_INCOME, (long) nonOpIncome);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, OPERATING_EXPENSES, (long) sumOfCost);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, TAX_COSTS, (long) taxFee);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, OTHER_EXPENSES, (long) nonOpCost);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, INTEREST_PAYMENT, interestPayment);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, RENTAL_EXPENSES, (long) rentalExpenses);
      runtimeService.setVariable(microLoanAmountProcessInstanceId, NET_BENEFIT, netBenefit);
    }
  }

  private void calculateAsset(String instanceId, String microLoanAmountExecId, Map<Integer, Map<String, Object>> asset,
      Map<String, Map<String, Object>> colHeader, String balanceProcessId)
  {
    double currentAsset = 0;
    double mainAsset = 0;
    double reportingPeriodCash = 0;

    // calculate current asset and main asset
    // AMOUNT 1 is current asset amount
    // AMOUNT 2 is main asset amount

    for (Map.Entry<Integer, Map<String, Object>> row : asset.entrySet())
    {
      Map<String, Object> assetRow = row.getValue();

      currentAsset += getDoubleValue(assetRow, AMOUNT1);

      if (String.valueOf(assetRow.get(CURRENT_ASSETS)).equals("Бэлэн мөнгө"))
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
    asset.get(asset.size() - 1).put(AMOUNT2, totalAsset);
    asset.get(asset.size() - 1).put(PERCENT2, 100);

    // calculate the other amount's percentage and set it
    // PERCENT 1 is current asset amount percent
    // Percent 2 is main asset amount percent
    for (Map.Entry<Integer, Map<String, Object>> row : asset.entrySet())
    {
      Map<String, Object> assetRow = row.getValue();
      double percent1 = findPercent(getDoubleValue(assetRow, AMOUNT1), totalAsset);
      assetRow.put(PERCENT1, roundWithDecimalPlace(percent1, 1));
      if (!String.valueOf(assetRow.get(MAIN_ASSET)).equals(MN_TOTAL_ASSET_AMOUNT))
      {
        double percent2 = findPercent(getDoubleValue(assetRow, AMOUNT2), totalAsset);
        assetRow.put(PERCENT2, roundWithDecimalPlace(percent2, 1));
      }
    }

    // set results
    Map<String, Object> assetColHeader = new HashMap<>();
    assetColHeader.put(CURRENT_ASSET_AMOUNT, getThousandSeparatedString(currentAsset));
    assetColHeader.put(CURRENT_ASSET_PERCENT, roundWithDecimalPlace(findPercent(currentAsset, totalAsset), 1));
    assetColHeader.put(MAIN_ASSET_AMOUNT, getThousandSeparatedString(mainAsset));
    assetColHeader.put(MAIN_ASSET_PERCENT, roundWithDecimalPlace(findPercent(mainAsset, totalAsset), 1));
    colHeader.put(ASSET, assetColHeader);

    caseService.setCaseVariableById(instanceId, CURRENT_ASSETS, (long) currentAsset);
    caseService.setCaseVariableById(instanceId, FIXED_ASSETS, (long) mainAsset);
    caseService.setCaseVariableById(instanceId, REPORTING_PERIOD_CASH, (long) reportingPeriodCash);

    if (null != balanceProcessId)
    {
      runtimeService.setVariable(balanceProcessId, CURRENT_ASSETS, (long) currentAsset);
      runtimeService.setVariable(balanceProcessId, FIXED_ASSETS, (long) mainAsset);
      runtimeService.setVariable(balanceProcessId, REPORTING_PERIOD_CASH, (long) reportingPeriodCash);
    }

    if (null != microLoanAmountExecId)
    {
      runtimeService.setVariable(microLoanAmountExecId, CURRENT_ASSETS, (long) currentAsset);
      runtimeService.setVariable(microLoanAmountExecId, FIXED_ASSETS, (long) mainAsset);
      runtimeService.setVariable(microLoanAmountExecId, REPORTING_PERIOD_CASH, (long) reportingPeriodCash);
    }
  }

  private void calculateDebt(String instanceId, String microLoanAmountExecId, Map<Integer, Map<String, Object>> debt,
      Map<String, Map<String, Object>> colHeader, String balanceProcessId)
  {
    double supplierPay = 0;
    // calculate short term debt
    double shortTermDebt = getDoubleValue(debt.get(0), AMOUNT1) + getDoubleValue(debt.get(1), AMOUNT1)
        + getDoubleValue(debt.get(2), AMOUNT1) + getDoubleValue(debt.get(3), AMOUNT1);
    //set long term debt
    double longTermDebt = getDoubleValue(debt.get(5), AMOUNT1);
    debt.get(4).put(AMOUNT1, longTermDebt);

    double totalDebt = shortTermDebt + longTermDebt;

    debt.get(2).put(AMOUNT2, (getDoubleValue(debt.get(3), AMOUNT2) + getDoubleValue(debt.get(4), AMOUNT2)));
    double ownersProperty = getDoubleValue(debt.get(1), AMOUNT2) + getDoubleValue(debt.get(2), AMOUNT2);

    //set owner's Property
    debt.get(0).put(AMOUNT2, ownersProperty);

    double totalSource = totalDebt + ownersProperty;
    debt.get(5).put(AMOUNT2, totalSource);
    debt.get(5).put(PERCENT2, 100);

    // set the other percents
    for (Map.Entry<Integer, Map<String, Object>> row : debt.entrySet())
    {
      Map<String, Object> debtRow = row.getValue();
      double value = getDoubleValue(debtRow, AMOUNT1);
      debtRow.put(PERCENT1, roundWithDecimalPlace(findPercent(value, totalSource), 1));
      if (String.valueOf(debtRow.get(SHORT_TERM_DEBT)).equals("Бэлт/нийлүүлэгчийн өглөг"))
      {
        supplierPay = value;
      }

      if (!String.valueOf(debtRow.get(SHORT_TERM_DEBT)).equals(MN_TOTAL_ASSET_AMOUNT))
      {
        value = getDoubleValue(debtRow, AMOUNT2);
        debtRow.put(PERCENT2, roundWithDecimalPlace(findPercent(value, totalSource), 1));
      }
    }

    Map<String, Object> debtColHeader = new HashMap<>();
    debtColHeader.put(SHORT_TERM_DEBT_AMOUNT, getThousandSeparatedString(shortTermDebt));
    debtColHeader.put(SHORT_TERM_DEBT_PERCENT, roundWithDecimalPlace(findPercent(shortTermDebt, totalSource), 1));
    debtColHeader.put(TOTAL_DEBT_AMOUNT, getThousandSeparatedString(totalDebt));
    debtColHeader.put(TOTAL_DEBT_PERCENT, roundWithDecimalPlace(findPercent(totalDebt, totalSource), 1));
    colHeader.put(DEBT, debtColHeader);

    caseService.setCaseVariableById(instanceId, LONG_TERM_PAYMENT, (long) longTermDebt);
    caseService.setCaseVariableById(instanceId, SHORT_TERM_PAYMENT, (long) shortTermDebt);
    caseService.setCaseVariableById(instanceId, SUPPLIER_PAY, (long) supplierPay);

    if (null != balanceProcessId)
    {
      runtimeService.setVariable(balanceProcessId, LONG_TERM_PAYMENT, (long) longTermDebt);
      runtimeService.setVariable(balanceProcessId, SHORT_TERM_PAYMENT, (long) shortTermDebt);
      runtimeService.setVariable(balanceProcessId, SUPPLIER_PAY, (long) supplierPay);
    }

    if (null != microLoanAmountExecId)
    {
      runtimeService.setVariable(microLoanAmountExecId, LONG_TERM_PAYMENT, (long) longTermDebt);
      runtimeService.setVariable(microLoanAmountExecId, SHORT_TERM_PAYMENT, (long) shortTermDebt);
      runtimeService.setVariable(microLoanAmountExecId, SUPPLIER_PAY, (long) supplierPay);
    }
  }

  private double getTotalSaleAmount(Map<String, Map<String, Object>> colHeader)
  {
    Map<String, Object> saleMap = colHeader.get(SALE);
    return getDoubleValue(saleMap, TOTAL_SALE_AMOUNT);
  }

  private List<Double> calculateTotalSaleAmount(Map<Integer, Map<String, Object>> sale, int reportPeriod) throws UseCaseException
  {
    List<Double> values = new ArrayList<>();
    double amount1 = 0;
    double amount2 = 0;
    for (Map.Entry<Integer, Map<String, Object>> row : sale.entrySet())
    {
      Map<String, Object> saleRow = row.getValue();
      amount1 += getDoubleValue(saleRow, AMOUNT1);
      amount2 += getDoubleValue(saleRow, AMOUNT2);
    }
    double totalGoodsAmount = round(amount2 / reportPeriod);
    double totalSalesAmount = round(amount1 / reportPeriod);
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

  private double getDoubleValue(Map<String, Object> map, String key)
  {
    return getDoubleAndRemoveComma(String.valueOf(map.get(key)));
  }

  private double round(double value)
  {
    return BpmNumberUtils.roundDouble(value, 2);
  }
}
