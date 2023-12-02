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
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplCalculateLoanAmountTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplCalculateLoanAmountTask.class);
  private final Environment environment;
  private BigDecimal loanAmount;

  public BnplCalculateLoanAmountTask(Environment environment)
  {
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    BigDecimal averageSalaryBeforeTax = (BigDecimal) execution.getVariable("averageSalaryBeforeTax");
    BigDecimal averageSalaryAfterTax = (BigDecimal) execution.getVariable("averageSalaryAfterTax");
    BigDecimal monthPaymentActiveLoan = new BigDecimal(getValidString(execution.getVariable("monthPaymentActiveLoan")));

    BigDecimal urOrlogoHaritsaa45 = new BigDecimal(Objects.requireNonNull(environment.getProperty("urOrlogoHaritsaa")));//.45
    BigDecimal urOrlogoHaritsaa60 = new BigDecimal(Objects.requireNonNull(environment.getProperty("urOrlogoHaritsaa60")));//.60
    BigDecimal calConstants = new BigDecimal(Objects.requireNonNull(environment.getProperty("bnpl.loan.amount.calculation.constant")));//1.5
    BigDecimal loanAmount;

    boolean hasMortgage = (boolean) execution.getVariable("hasMortgage");
    if (hasMortgage)
    {
      loanAmount = (averageSalaryBeforeTax.multiply(urOrlogoHaritsaa45).subtract(monthPaymentActiveLoan)).multiply(calConstants);
    }
    else
    {
      loanAmount = (averageSalaryAfterTax.multiply(urOrlogoHaritsaa60).subtract(monthPaymentActiveLoan)).multiply(calConstants);
    }

    execution.setVariable(BNPL_LOAN_AMOUNT, loanAmount.setScale(2, RoundingMode.HALF_UP));
    LOGGER.info(BNPL_LOG + "Successfully calculated loan amount: {}, with request ID:{}", loanAmount, requestId);
  }
}
