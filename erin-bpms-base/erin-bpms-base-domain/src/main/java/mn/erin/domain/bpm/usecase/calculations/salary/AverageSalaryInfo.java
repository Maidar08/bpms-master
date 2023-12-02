package mn.erin.domain.bpm.usecase.calculations.salary;

import java.math.BigDecimal;

/**
 * @author Zorig
 */
public class AverageSalaryInfo
{
  private final BigDecimal monthlySalary;
  private final boolean isExcludedNiigmiinDaatgal;
  private final boolean isExcludedHealthInsurance;
  private final int year;
  private final int month;

  public AverageSalaryInfo(BigDecimal monthlySalary, boolean isExcludedNiigmiinDaatgal, boolean isExcludedHealthInsurance, int year, int month)
  {
    this.monthlySalary = monthlySalary;
    this.isExcludedNiigmiinDaatgal = isExcludedNiigmiinDaatgal;
    this.isExcludedHealthInsurance = isExcludedHealthInsurance;
    this.year = year;
    this.month = month;
  }

  public BigDecimal getMonthlySalary()
  {
    return monthlySalary;
  }

  public boolean isExcludedNiigmiinDaatgal()
  {
    return isExcludedNiigmiinDaatgal;
  }

  public boolean isExcludedHealthInsurance()
  {
    return isExcludedHealthInsurance;
  }

  public int getYear()
  {
    return year;
  }

  public int getMonth()
  {
    return month;
  }
}
