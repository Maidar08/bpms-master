package mn.erin.domain.bpm.usecase.calculations.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

/**
 * @author Zorig
 */
public final class SalaryCalculation
{
  private SalaryCalculation()
  {
  }

  public static BigDecimal calculateAverageSalary(List<AverageSalaryInfo> monthlyAverageSalaryInfoList, DefaultParameterRepository defaultParameterRepository)
      throws UseCaseException
  {
    if (monthlyAverageSalaryInfoList == null || monthlyAverageSalaryInfoList.isEmpty())
    {
      throw new IllegalArgumentException("Monthly Average Salary Information is invalid! (empty or null)");
    }

    BigDecimal monthlySalarySum = new BigDecimal(0);
    Iterator<AverageSalaryInfo> monthlyAverageSalaryInfoListIterator = monthlyAverageSalaryInfoList.iterator();
    List<Map<String, BigDecimal>> previousMonthsSalaryList = new ArrayList<>(0);
    List<Map<String, BigDecimal>> previousMonthsHhoatAmountList = new ArrayList<>(0);
    List<Map<String, BigDecimal>> previousMonthsSalaryListAfterNdsh = new ArrayList<>(0);
    while (monthlyAverageSalaryInfoListIterator.hasNext())
    {
      AverageSalaryInfo currentAverageSalaryInfo = monthlyAverageSalaryInfoListIterator.next();
      BigDecimal afterTaxMonthlySalary = calculateMonthlySalary(currentAverageSalaryInfo.getMonthlySalary(),
          currentAverageSalaryInfo.isExcludedNiigmiinDaatgal(), currentAverageSalaryInfo.isExcludedHealthInsurance(), currentAverageSalaryInfo.getYear(),
          currentAverageSalaryInfo.getMonth(), defaultParameterRepository, 0, previousMonthsSalaryList, previousMonthsSalaryListAfterNdsh,
          previousMonthsHhoatAmountList);
      monthlySalarySum = monthlySalarySum.add(afterTaxMonthlySalary);
    }

    return monthlySalarySum.divide(new BigDecimal(monthlyAverageSalaryInfoList.size()), 16, RoundingMode.FLOOR).setScale(16, RoundingMode.FLOOR);
  }

  public static BigDecimal calculateMonthlySalary(BigDecimal singleMonthSalary, boolean isExcludedNiigmiinDaatgal, boolean isExcludedHealthInsurance, int year,
      int month, DefaultParameterRepository defaultParameterRepository, int index, List<Map<String, BigDecimal>> previousMonthsSalaryList,
      List<Map<String, BigDecimal>> previousMonthsSalaryListAfterNdsh,
      List<Map<String, BigDecimal>> previousMonthsHhoatAmountList)
      throws UseCaseException
  {
    if (year < 0)
    {
      throw new IllegalArgumentException("Year must not be negative");
    }
    if (singleMonthSalary == null || singleMonthSalary.compareTo(new BigDecimal(0)) == -1)
    {
      throw new IllegalArgumentException("Invalid salary input! (negative or null)");
    }

    BigDecimal ndsh = NiigmiinDaatgalCalculation
        .determineNdsh(singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, month, defaultParameterRepository);
    BigDecimal hhoat = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryListAfterNdsh, previousMonthsHhoatAmountList);
    BigDecimal remainderMonthlySalary = singleMonthSalary.subtract(ndsh).subtract(hhoat);
    return remainderMonthlySalary;
  }

  public static BigDecimal calculateAverageSalaryNoTax(List<BigDecimal> monthlySalaryList)
  {
    if (monthlySalaryList == null || monthlySalaryList.isEmpty())
    {
      throw new IllegalArgumentException("Monthly Salary Information is invalid! (empty or null)");
    }

    BigDecimal monthlySalarySum = new BigDecimal(0);
    Iterator<BigDecimal> monthlySalaryListIterator = monthlySalaryList.iterator();
    while (monthlySalaryListIterator.hasNext())
    {
      BigDecimal currentMonthlySalary = monthlySalaryListIterator.next();
      monthlySalarySum = monthlySalarySum.add(currentMonthlySalary);
    }
    return monthlySalarySum.divide(new BigDecimal(12), 16, RoundingMode.FLOOR).setScale(16, RoundingMode.FLOOR);
  }
}
