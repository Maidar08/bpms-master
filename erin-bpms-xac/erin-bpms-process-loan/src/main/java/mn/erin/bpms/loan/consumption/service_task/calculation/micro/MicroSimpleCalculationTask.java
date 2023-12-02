package mn.erin.bpms.loan.consumption.service_task.calculation.micro;

import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;

import mn.erin.bpms.loan.consumption.utils.NumberUtils;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmModuleConstants.CALCULATE_SIMPLE_CALCULATION;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COST_OF_GOODS;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ASSETS;
import static mn.erin.domain.bpm.BpmModuleConstants.LAST_CALCULATION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.LONG_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_BENEFIT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_PROFIT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_PROFIT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATING_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.RENTAL_EXPENSES;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORTING_PERIOD_CASH;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALES_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.SHORT_TERM_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.SIMPLE;
import static mn.erin.domain.bpm.BpmModuleConstants.SUPPLIER_PAY;
import static mn.erin.domain.bpm.BpmModuleConstants.TAX_COSTS;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_ASSET;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_ASSET_STRING;

/**
 * @author Lkhagvadorj.A
 **/

public class MicroSimpleCalculationTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Object calculateSimpleCalculation = execution.getVariable(CALCULATE_SIMPLE_CALCULATION);
    if (null != calculateSimpleCalculation && (boolean) calculateSimpleCalculation)
    {
      try
      {
        ProcessEngineServices processEngineServices = execution.getProcessEngineServices();
        CaseService caseService = processEngineServices.getCaseService();
        TaskService taskService = processEngineServices.getTaskService();

        RuntimeService runtimeService = processEngineServices.getRuntimeService();

        String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

        String microLoanAmountRootProcessInstanceId = null;

        List<Task> activeTasks = taskService.createTaskQuery()
            .caseInstanceId(caseInstanceId)
            .list();

        for (Task activeTask : activeTasks)
        {
          String taskDefinitionKey = activeTask.getTaskDefinitionKey();

          if ("user_task_calculate_micro_loan_amount".equalsIgnoreCase(taskDefinitionKey))
          {
            microLoanAmountRootProcessInstanceId = activeTask.getProcessInstanceId();
          }
        }

        calculateTotalAsset(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);
        calculateNetProfit(execution, caseService, caseInstanceId, microLoanAmountRootProcessInstanceId);

        execution.setVariable("reportPeriod", (long) execution.getVariable("reportPeriod"));
        execution.setVariable("lastCalculationType", "Simple");

        if (null != microLoanAmountRootProcessInstanceId)
        {
          runtimeService
              .setVariable(microLoanAmountRootProcessInstanceId, "reportPeriod", (long) execution.getVariable("reportPeriod"));
          runtimeService.setVariable(microLoanAmountRootProcessInstanceId, "lastCalculationType", "Simple");
        }
      }
      catch (Exception exception)
      {
        throw new UseCaseException("Error in Calculation!");
      }
    }
  }

  private void calculateTotalAsset(DelegateExecution execution, CaseService caseService, String caseInstanceId, String microLoanAmountProcessInstanceId)
  {
    long reportingPeriodCash = getVariableByType(execution, REPORTING_PERIOD_CASH);
    long currentAssets = getVariableByType(execution, CURRENT_ASSETS);
    long fixedAssets = getVariableByType(execution, FIXED_ASSETS);

    long supplierPay = getVariableByType(execution, SUPPLIER_PAY);
    long shortTermPayment = getVariableByType(execution, SHORT_TERM_PAYMENT);
    long longTermPayment = getVariableByType(execution, LONG_TERM_PAYMENT);

    long totalAsset = (reportingPeriodCash + currentAssets + fixedAssets) -
        (supplierPay + shortTermPayment + longTermPayment);

    setVariable(execution, caseService, REPORTING_PERIOD_CASH, reportingPeriodCash, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, CURRENT_ASSETS, currentAssets, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, FIXED_ASSETS, fixedAssets, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, SUPPLIER_PAY, supplierPay, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, SHORT_TERM_PAYMENT, shortTermPayment, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, LONG_TERM_PAYMENT, longTermPayment, caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, TOTAL_ASSET_STRING, NumberUtils.getThousandSeparatedString(String.valueOf(totalAsset)), caseInstanceId, microLoanAmountProcessInstanceId);
    setVariable(execution, caseService, TOTAL_ASSET, totalAsset, caseInstanceId, microLoanAmountProcessInstanceId);
  }

  private void calculateNetProfit(DelegateExecution execution, CaseService caseService, String caseInstanceId, String microLoanAmountProcessInstanceId)
  {
    long reportPeriod = getVariableByType(execution, REPORT_PERIOD);
    if (reportPeriod > 0)
    {
      double salesIncome = getVariableByType(execution, SALES_INCOME);
      double otherIncome = getVariableByType(execution, OTHER_INCOME);
      double costOfGoods = getVariableByType(execution, COST_OF_GOODS);
      double operatingExpenses = getVariableByType(execution, OPERATING_EXPENSES);
      double taxCosts = getVariableByType(execution, TAX_COSTS);
      double rentalExpenses = getVariableByType(execution, RENTAL_EXPENSES);
      double otherExpense = getVariableByType(execution, OTHER_EXPENSES);
      double total = (salesIncome + otherIncome) - (costOfGoods + operatingExpenses + taxCosts + rentalExpenses + otherExpense);
      double netProfit = NumberUtils.roundWithDecimalPlace( (total / reportPeriod), 2 );

      setVariable(execution, caseService, SALES_INCOME, (Double.valueOf(salesIncome)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, OTHER_INCOME, (Double.valueOf(otherIncome)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, COST_OF_GOODS, (Double.valueOf(costOfGoods)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, OPERATING_EXPENSES, (Double.valueOf(operatingExpenses)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, TAX_COSTS, (Double.valueOf(taxCosts)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, RENTAL_EXPENSES, (Double.valueOf(rentalExpenses)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, OTHER_EXPENSES, (Double.valueOf(otherExpense)).longValue(), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, NET_PROFIT_STRING, NumberUtils.getThousandSeparatedString(String.valueOf(netProfit)), caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, NET_PROFIT, netProfit, caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, LAST_CALCULATION_TYPE, SIMPLE, caseInstanceId, microLoanAmountProcessInstanceId);
      setVariable(execution, caseService, NET_BENEFIT, netProfit, caseInstanceId, microLoanAmountProcessInstanceId);
    }
  }

  private long getVariableByType(DelegateExecution execution, String id)
  {
    Object variable = execution.getVariable(id);
    if (null != variable)
    {
      return Long.parseLong(String.valueOf(variable));
    }
    return 0;
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
}
