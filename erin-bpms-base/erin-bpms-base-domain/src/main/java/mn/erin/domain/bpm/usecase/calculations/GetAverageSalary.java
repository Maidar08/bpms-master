package mn.erin.domain.bpm.usecase.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.calculations.salary.AverageSalaryInfo;
import mn.erin.domain.bpm.usecase.calculations.salary.HhoatCalculation;
import mn.erin.domain.bpm.usecase.calculations.salary.NiigmiinDaatgalCalculation;
import mn.erin.domain.bpm.usecase.calculations.salary.SalaryCalculation;

/**
 * @author Zorig
 */
public class GetAverageSalary implements UseCase<GetAverageSalaryInput, GetAverageSalaryOutput>
{
  private final DefaultParameterRepository defaultParameterRepository;

  public GetAverageSalary(DefaultParameterRepository defaultParameterRepository)
  {
    this.defaultParameterRepository = defaultParameterRepository;
  }

  @Override
  public GetAverageSalaryOutput execute(GetAverageSalaryInput input) throws UseCaseException
  {
    if (input == null)
    {
      String errorCode = "BPMS027";
      throw new UseCaseException(errorCode, "Use case input must not be null");
    }
    Map<Date, BigDecimal> dateToSalariesMap = validateDateToSalariesMap(input.getYearToSalariesMap());
    boolean isNiigmiinDaatgalExcluded = input.isExcludedNiigmiinDaatgal();
    boolean isHealthInsuranceExcluded = input.isExcludedHealthInsurance();

    try
    {
      Map<Date, Map<String, BigDecimal>> afterTaxSalaries = new HashMap<>();

      int index = 0;
      List<Map<String, BigDecimal>> previousMonthsSalaryList = new ArrayList<>();
      List<Map<String, BigDecimal>> previousMonthsHhoatAmountList = new ArrayList<>();
      List<Map<String, BigDecimal>> previousMonthsSalaryListAfterNdsh = new ArrayList<>();
      List<AverageSalaryInfo> averageSalaryInfoList = new ArrayList<>();

      BigDecimal monthlySalarySum = BigDecimal.valueOf(0);

      for (Map.Entry<Date, BigDecimal> monthToSalary : dateToSalariesMap.entrySet())
      {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthToSalary.getKey());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (year > 2022 && (!previousMonthsSalaryList.isEmpty() && (year > Integer.parseInt(
            previousMonthsSalaryList.get(index - 1).entrySet().iterator().next().getKey()))))
        {
          previousMonthsSalaryList = new ArrayList<>();
          previousMonthsSalaryListAfterNdsh = new ArrayList<>();
          previousMonthsHhoatAmountList = new ArrayList<>();
          index = 0;
        }
        BigDecimal ndsh = NiigmiinDaatgalCalculation
            .determineNdsh(monthToSalary.getValue(), isNiigmiinDaatgalExcluded, isHealthInsuranceExcluded, year, month, defaultParameterRepository);
        BigDecimal hhoat = HhoatCalculation
            .determineHhoat(ndsh, monthToSalary.getValue(), isNiigmiinDaatgalExcluded, isHealthInsuranceExcluded, year, defaultParameterRepository, index,
                previousMonthsSalaryList, previousMonthsSalaryListAfterNdsh, previousMonthsHhoatAmountList);
        BigDecimal monthSalaryAfterTax = monthToSalary.getValue().subtract(ndsh).subtract(hhoat);
        if (year > 2022)
        {
          index++;
        }
        Map<String, BigDecimal> afterTaxSalaryInfo = new HashMap<>();
        afterTaxSalaryInfo.put("Ndsh", ndsh);
        afterTaxSalaryInfo.put("Hhoat", hhoat);
        afterTaxSalaryInfo.put("MonthSalaryAfterTax", monthSalaryAfterTax);
        afterTaxSalaries.put(monthToSalary.getKey(), afterTaxSalaryInfo);
        averageSalaryInfoList.add(new AverageSalaryInfo(monthSalaryAfterTax, isNiigmiinDaatgalExcluded, isHealthInsuranceExcluded, year, month));
        monthlySalarySum = monthlySalarySum.add(monthSalaryAfterTax);
      }
      BigDecimal averageMonthlySalaryAfterTax = monthlySalarySum.divide(new BigDecimal(12), 16, RoundingMode.FLOOR)
          .setScale(16, RoundingMode.FLOOR);
      BigDecimal averageMonthlySalaryBeforeTax = SalaryCalculation.calculateAverageSalaryNoTax(toListOfSalaries(input.getYearToSalariesMap()));

      return new GetAverageSalaryOutput(afterTaxSalaries, averageMonthlySalaryAfterTax, averageMonthlySalaryBeforeTax);
    }
    catch (IllegalArgumentException | UnsupportedOperationException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private List<BigDecimal> toListOfSalaries(Map<Date, BigDecimal> dateToSalaryMap)
  {
    List<BigDecimal> salariesListToReturn = new ArrayList<>();

    for (Map.Entry<Date, BigDecimal> monthlySalary : dateToSalaryMap.entrySet())
    {
      salariesListToReturn.add(monthlySalary.getValue());
    }

    return salariesListToReturn;
  }

  private Map<Date, BigDecimal> validateDateToSalariesMap(Map<Date, BigDecimal> dateToSalariesMap) throws UseCaseException
  {
    if (dateToSalariesMap == null)
    {
      String errorCode = "BPMS028";
      throw new UseCaseException(errorCode, "Null salaries list is not allowed!");
    }

    if (dateToSalariesMap.isEmpty())
    {
      String errorCode = "BPMS028";
      throw new UseCaseException(errorCode, "Empty salaries list is not allowed!");
    }

    for (Map.Entry<Date, BigDecimal> monthSalary : dateToSalariesMap.entrySet())
    {
      //TODO: make sure date has year and month
      if (monthSalary.getKey() == null || monthSalary.getValue() == null || monthSalary.getValue().compareTo(new BigDecimal(0)) < 0)
      {
        String errorCode = "BPMS029";
        throw new UseCaseException(errorCode, "Invalid salary list input!");
      }
    }
    Map<Date, BigDecimal> sortedMap = new LinkedHashMap<>();
    dateToSalariesMap.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
    return sortedMap;
  }

  private List<AverageSalaryInfo> mapToSalaryInfoList(Map<Date, BigDecimal> yearToSalariesMap, boolean isNiigmiinDaatgalExcluded,
      boolean isHealthInsuranceExcluded)
  {
    List<AverageSalaryInfo> listToReturn = new ArrayList<>();

    for (Map.Entry<Date, BigDecimal> entry : yearToSalariesMap.entrySet())
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(entry.getKey());
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);

      AverageSalaryInfo averageSalaryInfoToAdd = new AverageSalaryInfo(entry.getValue(), isNiigmiinDaatgalExcluded, isHealthInsuranceExcluded, year, month);
      listToReturn.add(averageSalaryInfoToAdd);
    }

    return listToReturn;
  }
}
