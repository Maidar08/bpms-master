package mn.erin.domain.bpm.usecase.calculations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Zorig
 */
public class GetAverageSalaryOutput
{
  private final Map<Date, Map<String, BigDecimal>> afterTaxSalaries;
  private final BigDecimal averageSalaryAfterTax;
  private final BigDecimal averageSalaryBeforeTax;

  public GetAverageSalaryOutput(Map<Date, Map<String, BigDecimal>> afterTaxSalaries, BigDecimal averageSalaryAfterTax,
      BigDecimal averageSalaryBeforeTax)
  {
    this.afterTaxSalaries = afterTaxSalaries;
    this.averageSalaryAfterTax = averageSalaryAfterTax;
    this.averageSalaryBeforeTax = averageSalaryBeforeTax;
  }

  public Map<Date, Map<String, BigDecimal>> getAfterTaxSalaries()
  {
    return afterTaxSalaries;
  }

  public BigDecimal getAverageSalaryAfterTax()
  {
    return averageSalaryAfterTax;
  }

  public BigDecimal getAverageSalaryBeforeTax()
  {
    return averageSalaryBeforeTax;
  }
}
