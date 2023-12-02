package mn.erin.domain.bpm.usecase.calculations.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_CONSTANT_TAX;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_HHOAT_DISCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_HHOAT_TAX;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_HHOAT_TAX_NEW;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_MAX_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_TAX_ADDED;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION_TAX_PERCENT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultParameters;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Zorig
 */
public final class HhoatCalculation
{

  private HhoatCalculation()
  {
  }

  private static final Map<Integer, BigDecimal> INCOME_TAX_BEFORE_2023 = new HashMap<>();
  private static final Map<Integer, Map<String, Map<String, Object>>> INCOME_TAX_AFTER_2023 = new HashMap<>();

  public static BigDecimal determineHhoat(BigDecimal ndsh, BigDecimal singleMonthSalary, boolean isExcludedNiigmiinDaatgal, boolean isExcludedHealthInsurance,
      int year, DefaultParameterRepository defaultParameterRepository, int index, List<Map<String, BigDecimal>> previousMonthsSalaryList,
      List<Map<String, BigDecimal>> previousMonthsSalaryAfterNdshList,
      List<Map<String, BigDecimal>> previousMonthsHhoatAmountList) throws UseCaseException
  {
    if (year > 2029 || year < 2017)
    {
      throw new UnsupportedOperationException("Year not supported for the calculation for NDSH. (Supported years: 2017-2029)");
    }
    Map<String, Object> minSalary = (Map<String, Object>) getDefaultParameters(defaultParameterRepository, SALARY_CALCULATION, "MinSalaryForDiscount").get(
        "MinSalaryForDiscount");

    BigDecimal minSalaryForDiscount = new BigDecimal(getValidString(minSalary.get("minAmount")));
    BigDecimal hhoatCalculation;
    BigDecimal salaryIncreasedAmount = new BigDecimal(0);
    Map<String, BigDecimal> singleMonthSalaryAfterNdsMap = new HashMap<>();

    if (year > 2022)
    {
      singleMonthSalaryAfterNdsMap.put(getValidString(year), singleMonthSalary.subtract(ndsh));
      previousMonthsSalaryAfterNdshList.add(index, singleMonthSalaryAfterNdsMap);
    }
    for (Map<String, BigDecimal> amount : previousMonthsSalaryAfterNdshList)
    {
      salaryIncreasedAmount = salaryIncreasedAmount.add(amount.entrySet().iterator().next().getValue());
    }

    salaryIncreasedAmount = year > 2022 ? salaryIncreasedAmount : singleMonthSalary.multiply(BigDecimal.valueOf(12));

    BigDecimal discount = salaryIncreasedAmount.compareTo(minSalaryForDiscount) <= 0 ?
        getDiscount(year, salaryIncreasedAmount, defaultParameterRepository) :
        BigDecimal.valueOf(0);

    if (year < 2023)
    {
      BigDecimal incomeTax = getHhoatTaxBefore2023(defaultParameterRepository, INCOME_TAX_BEFORE_2023).get(year).divide(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN);
      hhoatCalculation = calculateHhoatBefore2023(singleMonthSalary, ndsh, incomeTax, discount);
    }
    else
    {
      Map<Integer, Map<String, Map<String, Object>>> incomeTaxAfter2023 = getHhoatTaxAfter2023(defaultParameterRepository, INCOME_TAX_AFTER_2023);
      Map<String, Map<String, Object>> taxInfo = incomeTaxAfter2023.get(year);
      hhoatCalculation = calculateHhoatAfter2023(singleMonthSalary, ndsh, taxInfo, discount, year, index, previousMonthsSalaryList,
          previousMonthsSalaryAfterNdshList,
          previousMonthsHhoatAmountList);
    }

    if ((isExcludedHealthInsurance && isExcludedNiigmiinDaatgal) || hhoatCalculation.compareTo(new BigDecimal(0)) < 0)
    {
      return new BigDecimal(0);
    }
    else
    {
      return hhoatCalculation.setScale(16, RoundingMode.FLOOR);
    }
  }

  private static BigDecimal getDiscount(int year, BigDecimal salaryIncreasedAmountAfterNdsh, DefaultParameterRepository defaultParameterRepository)
      throws UseCaseException
  {
    return getHhoatDiscount(year, salaryIncreasedAmountAfterNdsh, defaultParameterRepository).divide(new BigDecimal(12), 16, RoundingMode.FLOOR);
  }

  public static BigDecimal getHhoatDiscount(int year, BigDecimal increasedSalaryAfterNdsh, DefaultParameterRepository defaultParameterRepository)
      throws UseCaseException
  {
    BigDecimal discount = new BigDecimal(0);

    Map<String, Object> parameters = getDefaultParameters(defaultParameterRepository, SALARY_CALCULATION, SALARY_CALCULATION_HHOAT_DISCOUNT);
    Map<String, Map<String, String>> paramValueMap = (Map<String, Map<String, String>>) parameters.get(SALARY_CALCULATION_HHOAT_DISCOUNT);
    Map<String, String> discountIntervalMap = paramValueMap.get(String.valueOf(year));

    Map<BigDecimal, BigDecimal> sortedMap = new TreeMap<>();
    discountIntervalMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .forEachOrdered(entry -> sortedMap.put(new BigDecimal(getValidString(entry.getKey())), new BigDecimal(getValidString(entry.getValue()))));

    for (Map.Entry<BigDecimal, BigDecimal> entry : sortedMap.entrySet())
    {
      if (increasedSalaryAfterNdsh.compareTo(entry.getKey()) <= 0)
      {
        discount = entry.getValue();
        break;
      }
    }
    return discount;
  }

  public static Map<Integer, BigDecimal> getHhoatTaxBefore2023(DefaultParameterRepository defaultParameterRepository,
      Map<Integer, BigDecimal> INCOME_TAX_BEFORE_2023)
      throws UseCaseException
  {
    Map<String, Object> parameters = getDefaultParameters(defaultParameterRepository, SALARY_CALCULATION, SALARY_CALCULATION_HHOAT_TAX);
    Map<String, String> paramValueMap = (Map<String, String>) parameters.get(SALARY_CALCULATION_HHOAT_TAX);
    paramValueMap.forEach((k, v) -> {
      if (Integer.parseInt(k) < 2023)
      {
        INCOME_TAX_BEFORE_2023.put(Integer.parseInt(k), new BigDecimal(v));
      }
    });
    return INCOME_TAX_BEFORE_2023;
  }

  public static Map<Integer, Map<String, Map<String, Object>>> getHhoatTaxAfter2023(DefaultParameterRepository defaultParameterRepository,
      Map<Integer, Map<String, Map<String, Object>>> INCOME_TAX_AFTER_2023)
      throws UseCaseException
  {
    Map<String, Object> parameters = getDefaultParameters(defaultParameterRepository, SALARY_CALCULATION, SALARY_CALCULATION_HHOAT_TAX_NEW);
    Map<String, Map<String, Map<String, Object>>> paramValueMap = (Map<String, Map<String, Map<String, Object>>>) parameters.get(
        SALARY_CALCULATION_HHOAT_TAX_NEW);
    paramValueMap.forEach((k, v) -> {
      if (Integer.parseInt(k) > 2022)
      {
        INCOME_TAX_AFTER_2023.put(Integer.parseInt(k), v);
      }
    });
    return INCOME_TAX_AFTER_2023;
  }

  private static BigDecimal calculateHhoatBefore2023(BigDecimal singleMonthSalary, BigDecimal ndsh, BigDecimal incomeTax, BigDecimal discount)
  {
    return singleMonthSalary.subtract(ndsh).multiply(incomeTax).subtract(discount);
  }

  private static BigDecimal calculateHhoatAfter2023(BigDecimal singleMonthSalary, BigDecimal ndsh, Map<String, Map<String, Object>> incomeTaxAfter2023,
      BigDecimal discount, int year, int index, List<Map<String, BigDecimal>> previousMonthsSalaryList,
      List<Map<String, BigDecimal>> previousMonthsSalaryAfterNdshList,
      List<Map<String, BigDecimal>> previousMonthsHhoatAmountList)
  {
    BigDecimal singleMonthSalaryAfterNds = singleMonthSalary.subtract(ndsh);
    BigDecimal hhoatTaxPercent;
    BigDecimal hhoatTaxPercentAdded;
    BigDecimal hhoatAdditionalIncomeTax;

    BigDecimal hhoatDefaultTax;

    BigDecimal hhoatTotalAmount = BigDecimal.valueOf(0);

    BigDecimal salaryIncreasedAmount = BigDecimal.valueOf(0); // included all previous months salary
    BigDecimal salaryIncreasedAmountAfterNdsh = BigDecimal.valueOf(0);//included all previous months salary after ndsh
    BigDecimal allPreviousMonthTax = BigDecimal.valueOf(0); //included all previous months tax

    String getter1;
    String getter2;

    Map<String, BigDecimal> singleMonthSalaryMap = new HashMap<>();

    singleMonthSalaryMap.put(getValidString(year), singleMonthSalary);
    previousMonthsSalaryList.add(index, singleMonthSalaryMap);

    if (index > 0)
    {
      for (Map<String, BigDecimal> amount : previousMonthsSalaryList)
      {
        salaryIncreasedAmount = salaryIncreasedAmount.add(amount.entrySet().iterator().next().getValue());
      }
      for (Map<String, BigDecimal> amount : previousMonthsSalaryAfterNdshList)
      {
          salaryIncreasedAmountAfterNdsh = salaryIncreasedAmountAfterNdsh.add(amount.entrySet().iterator().next().getValue());
      }
      for (Map<String, BigDecimal> amount : previousMonthsHhoatAmountList)
      {
        allPreviousMonthTax = allPreviousMonthTax.add(amount.entrySet().iterator().next().getValue());
      }
    }
    else if (index == 0)
    {
      salaryIncreasedAmount = singleMonthSalary;
      salaryIncreasedAmountAfterNdsh = singleMonthSalaryAfterNds;
      allPreviousMonthTax = BigDecimal.valueOf(0);
    }

    if (salaryIncreasedAmount.compareTo(new BigDecimal(getValidString(incomeTaxAfter2023.get("0").get(SALARY_CALCULATION_MAX_INCOME)))) <= 0)
    {
      hhoatTaxPercent = new BigDecimal(getValidString(incomeTaxAfter2023.get("0").get(SALARY_CALCULATION_TAX_PERCENT))).divide(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN);
      hhoatDefaultTax = new BigDecimal(getValidString(incomeTaxAfter2023.get("0").get(SALARY_CALCULATION_CONSTANT_TAX)));
      hhoatTotalAmount = singleMonthSalaryAfterNds.multiply(hhoatTaxPercent).subtract(discount).add(hhoatDefaultTax);
    }
    else if (salaryIncreasedAmount.compareTo(new BigDecimal(getValidString(incomeTaxAfter2023.get("0").get(SALARY_CALCULATION_MAX_INCOME)))) > 0)
    {
      if (salaryIncreasedAmount.compareTo(new BigDecimal(getValidString(incomeTaxAfter2023.get("1").get(SALARY_CALCULATION_MAX_INCOME)))) <= 0)
      {
        getter1 = "0";
        getter2 = "1";
      }
      else
      {
        getter1 = "1";
        getter2 = "2";
      }
      hhoatDefaultTax = new BigDecimal(getValidString(incomeTaxAfter2023.get(getter2).get(SALARY_CALCULATION_CONSTANT_TAX)));
      hhoatTaxPercentAdded = new BigDecimal(getValidString(incomeTaxAfter2023.get(getter2).get(SALARY_CALCULATION_TAX_ADDED))).divide(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN);
      hhoatAdditionalIncomeTax = salaryIncreasedAmountAfterNdsh.subtract(
          new BigDecimal(getValidString(incomeTaxAfter2023.get(getter1).get(SALARY_CALCULATION_MAX_INCOME)))).multiply(hhoatTaxPercentAdded);
      hhoatTotalAmount = hhoatDefaultTax.add(hhoatAdditionalIncomeTax).subtract(allPreviousMonthTax).subtract(discount);
    }

    Map<String, BigDecimal> hhoatAmount = new HashMap<>();
    hhoatAmount.put(getValidString(year), hhoatTotalAmount);
    previousMonthsHhoatAmountList.add(index, hhoatAmount);
    return hhoatTotalAmount;
  }
}
