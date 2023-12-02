package mn.erin.bpms.loan.consumption.service_task.calculation.mortgage;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getActiveTaskProcessInstanceIdByTaskDefKey;
import static mn.erin.bpms.loan.consumption.utils.NumberUtils.convertToInt;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVE_LOAN_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.BUSINESS_CALCULATION;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_PROFIT;
import static mn.erin.domain.bpm.BpmModuleConstants.NET_PROFIT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.OPERATION_COST;
import static mn.erin.domain.bpm.BpmModuleConstants.OTHER_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.REPORT_PERIOD;
import static mn.erin.domain.bpm.BpmModuleConstants.SALES_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.SOLD_GOODS;

/**
 * @author Lkhagvadorj.A
 **/

public class BusinessCalculationMortgageTask implements JavaDelegate
{
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean businessCalculation = (boolean) execution.getVariable(BUSINESS_CALCULATION);
    if (businessCalculation)
    {
      long reportPeriod = (long) execution.getVariable(REPORT_PERIOD);
      long salesIncome = (long) execution.getVariable(SALES_INCOME);
      long otherIncome = (long) execution.getVariable(OTHER_INCOME);
      long soldGoods = (long) execution.getVariable(SOLD_GOODS);
      long operationCost = (long) execution.getVariable(OPERATION_COST);
      long activeLoanPayment = (long) execution.getVariable(ACTIVE_LOAN_PAYMENT);

      // Цэвэр ашиг = (Борлуулалтын орлого + Бусад орлого - Борлуулсан барааны өртөг - Үйл ажиллагааны өртөг)
      // Тайлан хамрах хугацаа - Идэвхтэй  зээлийн сарын төлбөр
      long netProfit =  ((salesIncome + otherIncome - soldGoods - operationCost) / reportPeriod) - activeLoanPayment;
      execution.setVariable(NET_PROFIT, netProfit);
      execution.setVariable(NET_PROFIT_STRING, convertToInt(netProfit));

      // set variable
      ProcessEngineServices processEngineServices = execution.getProcessEngineServices();
      CaseService caseService = processEngineServices.getCaseService();
      RuntimeService runtimeService = processEngineServices.getRuntimeService();

      String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
      String mortgageLoanAmountRootProcessInstanceId = getActiveTaskProcessInstanceIdByTaskDefKey("user_task_calculate_mortgage_loan_amount",
          caseInstanceId, execution);
      caseService.setVariable(caseInstanceId, NET_PROFIT, netProfit);
      caseService.setVariable(caseInstanceId, NET_PROFIT_STRING, convertToInt(netProfit));
      if (null != mortgageLoanAmountRootProcessInstanceId)
      {
        runtimeService.setVariable(mortgageLoanAmountRootProcessInstanceId, NET_PROFIT, netProfit);
        runtimeService.setVariable(mortgageLoanAmountRootProcessInstanceId, NET_PROFIT_STRING, convertToInt(netProfit));
      }
    }
  }
}
