package mn.erin.bpms.loan.consumption.utils;

import java.math.BigDecimal;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import mn.erin.common.utils.NumberUtils;

/**
 * @author Tamir
 */
public final class CalculationUtils
{
  private CalculationUtils()
  {

  }

  public static final String AFTER_AVERAGE_SALARY_TAX = "averageSalaryAfterTax";
  public static final String BEFORE_AVERAGE_SALARY_TAX = "averageSalaryBeforeTax";

  private static final String SALARY_AMOUNT = "salaryAmount";
  private static final String SALARY_AMOUNT_STRING = "salaryAmountString";

  private static final String DEBT_INCOME_BALANCE = "debtIncomeBalance";
  private static final String DEBT_INCOME_BALANCE_STRING = "debtIncomeBalanceString";

  public static void setVariablesHasMortgage(DelegateExecution execution)
  {
    setAmountWithValidation(execution, BEFORE_AVERAGE_SALARY_TAX);
    setBeforeTaxAverageSalaryDoubleAmount(execution, BEFORE_AVERAGE_SALARY_TAX);
    setAfterTaxAverageSalaryDoubleAmount(execution, AFTER_AVERAGE_SALARY_TAX);

    // TODO : get debt income balance from file or repository.
    execution.setVariable(DEBT_INCOME_BALANCE, new BigDecimal(.45000000000000000));
    execution.setVariable(DEBT_INCOME_BALANCE_STRING, "45%");
  }

  public static void setVariablesWhenNoMortgage(DelegateExecution execution)
  {
    setAmountWithValidation(execution, AFTER_AVERAGE_SALARY_TAX);
    setBeforeTaxAverageSalaryDoubleAmount(execution, BEFORE_AVERAGE_SALARY_TAX);
    setAfterTaxAverageSalaryDoubleAmount(execution, AFTER_AVERAGE_SALARY_TAX);

    execution.setVariable(DEBT_INCOME_BALANCE, new BigDecimal(.600000000000000000));
    execution.setVariable(DEBT_INCOME_BALANCE_STRING, "60%");
  }

  private static void setBeforeTaxAverageSalaryDoubleAmount(DelegateExecution execution, String variableName)
  {
    double averageSalaryBeforeTax;

    if (execution.getVariable(variableName) instanceof Long)
    {
      averageSalaryBeforeTax = ((Long) execution.getVariable(variableName)).doubleValue();
    }
    else if (execution.getVariable(variableName) instanceof Double)
    {
      averageSalaryBeforeTax = (double) execution.getVariable(variableName);
    }
    else if (execution.getVariable(variableName) instanceof BigDecimal)
    {
      BigDecimal averageSalaryBeforeTaxBD = (BigDecimal) execution.getVariable(variableName);
      averageSalaryBeforeTax = averageSalaryBeforeTaxBD.doubleValue();
    }
    else {
      averageSalaryBeforeTax = Double.valueOf((int) execution.getVariable(variableName));
    }
    execution.setVariable(variableName, averageSalaryBeforeTax);
  }

  private static void setAfterTaxAverageSalaryDoubleAmount(DelegateExecution execution, String variableName)
  {
    double averageSalaryAfterTax;

    if (execution.getVariable(variableName) instanceof Long)
    {
      averageSalaryAfterTax = ((Long) execution.getVariable(variableName)).doubleValue();
    }
    else if (execution.getVariable(variableName) instanceof Double)
    {
      averageSalaryAfterTax = (double) execution.getVariable(variableName);
    }
    else if (execution.getVariable(variableName) instanceof BigDecimal)
    {
      BigDecimal averageSalaryAfterTaxBD = (BigDecimal) execution.getVariable(variableName);
      averageSalaryAfterTax = averageSalaryAfterTaxBD.doubleValue();
    }
    else {
      averageSalaryAfterTax = Double.valueOf((int) execution.getVariable(variableName));
    }
    execution.setVariable(variableName, averageSalaryAfterTax);
  }

  private static void setAmountWithValidation(DelegateExecution execution, String variableName)
  {
    double averageSalaryTax;

    if (execution.getVariable(variableName) instanceof Long)
    {
      averageSalaryTax = ((Long) execution.getVariable(variableName)).doubleValue();
    }
    else if (execution.getVariable(variableName) instanceof Double)
    {
      averageSalaryTax = (double) execution.getVariable(variableName);
    }
    else if (execution.getVariable(variableName) instanceof BigDecimal)
    {
      BigDecimal averageSalaryTaxBigDecimal = (BigDecimal) execution.getVariable(variableName);
      averageSalaryTax = averageSalaryTaxBigDecimal.doubleValue();
    }
    else {
      averageSalaryTax = Double.valueOf((int) execution.getVariable(variableName));
    }
    execution.setVariable(SALARY_AMOUNT, averageSalaryTax);
    execution.setVariable(SALARY_AMOUNT_STRING, NumberUtils.doubleToString(averageSalaryTax));
  }
}
