package consumption.service_task_bnpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_ISSUANCE_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplDisburseDebtIncomeRatioTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplDisburseDebtIncomeRatioTask.class);
  private final Environment environment;

  public BnplDisburseDebtIncomeRatioTask(Environment environment)
  {
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    boolean hasMortgage = (boolean) execution.getVariable("hasMortgage");
    BigDecimal invoiceAmount75 = (BigDecimal) execution.getVariable(INVOICE_AMOUNT_75);
    BigDecimal monthPaymentActiveLoan = new BigDecimal(getValidString(execution.getVariable("monthPaymentActiveLoan")));
    BigDecimal averageMonthlySalary = hasMortgage ?
        (BigDecimal) execution.getVariable("averageSalaryBeforeTax") :
        (BigDecimal) execution.getVariable("averageSalaryAfterTax");

    BigDecimal calConstants = new BigDecimal(Objects.requireNonNull(environment.getProperty("bnpl.loan.amount.calculation.constant")));//1.5

    BigDecimal olgohUrOrlogiinHaritsaa = ((invoiceAmount75.divide(calConstants).add(monthPaymentActiveLoan)).multiply(
        BigDecimal.valueOf(100))).divide(averageMonthlySalary, 2, RoundingMode.HALF_UP);

    execution.setVariable(DEBT_INCOME_ISSUANCE_PERCENT, olgohUrOrlogiinHaritsaa);

    LOGGER.info(BNPL_LOG + "Successfully calculated disburse debt income ratio : {} with requestId = {}", olgohUrOrlogiinHaritsaa, requestId);
  }
}
