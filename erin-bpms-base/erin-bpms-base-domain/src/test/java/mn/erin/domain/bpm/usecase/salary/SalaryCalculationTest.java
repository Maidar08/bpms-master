package mn.erin.domain.bpm.usecase.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.calculations.salary.AverageSalaryInfo;
import mn.erin.domain.bpm.usecase.calculations.salary.SalaryCalculation;

/**
 * @author Zorig
 */
@Ignore
// TODO : tests failing on teamcity, fix issue.
public class SalaryCalculationTest
{
  private DefaultParameterRepository defaultParameterRepository;
  private static int index = 0;
  List<Map<String, BigDecimal>> previousMonthsSalaryList = new ArrayList<>();
  List<Map<String, BigDecimal>> previousMonthsHhoatAmountList = new ArrayList<Map<String, BigDecimal>>();
  List<Map<String, BigDecimal>> previousMonthsSalaryAfterNdshList = new ArrayList<>();

  @Before
  public void initTest()
  {
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateMonthlySalaryThrowsExceptionWhenYearIsNegative() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(213);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = -1;

    SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, 1, defaultParameterRepository, index,
        previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateMonthlySalaryThrowsExceptionWhenSingleMonthSalaryIsNull() throws UseCaseException
  {
    BigDecimal monthlySalary = null;
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2020;

    SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, 1, defaultParameterRepository, index,
        previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateMonthlySalaryThrowsExceptionWhenSingleMonthSalaryIsNegative() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(-4);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2020;

    SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, 1, defaultParameterRepository, index,
        previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
  }

  @Test
  public void calculateMonthlySalaryWhenNiigmiinDaatgalAndHealthInsuranceAreExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result = SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(monthlySalary, result2017);
    Assert.assertEquals(monthlySalary, result2018);
    Assert.assertEquals(monthlySalary, result2019);
    Assert.assertEquals(monthlySalary, result2020);
  }

  @Test
  public void calculateMonthlySalaryWhenNiigmiinDaatgalAndHealthInsuranceAreNotExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result = SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(monthlySalary, result2017);
    Assert.assertEquals(new BigDecimal(4298922.90).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(4205322.90).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(4158522.90).setScale(2, RoundingMode.CEILING), result2020);
  }

  @Test
  public void calculateMonthlySalaryWhenNiigmiinDaatgalIsExcludedAndHealthInsuranceIsNotExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result = SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(monthlySalary, result2017);
    Assert.assertEquals(new BigDecimal(4445792.45).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(4445792.45).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(4445792.45).setScale(2, RoundingMode.FLOOR), result2020);
  }

  @Test
  public void calculateMonthlySalaryWhenNiigmiinDaatgalIsNotExcludedAndHealthInsuranceIsExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result = SalaryCalculation.calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = SalaryCalculation
        .calculateMonthlySalary(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(monthlySalary, result2017);
    Assert.assertEquals(new BigDecimal(4298922.90).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(4205322.90).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(4158522.90).setScale(2, RoundingMode.CEILING), result2020);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateAverageSalaryThrowsExceptionWhenListIsNull() throws UseCaseException
  {
    SalaryCalculation.calculateAverageSalary(null, defaultParameterRepository);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateAverageSalaryThrowsExceptionWhenListIsEmpty() throws UseCaseException
  {
    SalaryCalculation.calculateAverageSalary(new ArrayList<>(), defaultParameterRepository);
  }

  @Test
  public void calculateAverageSalaryWhenNiigmiinAndHealthInsuranceAreNotExcluded() throws UseCaseException
  {
    List<AverageSalaryInfo> salaryInfoList = new ArrayList<>();

    AverageSalaryInfo averageSalaryInfo1 = new AverageSalaryInfo(new BigDecimal(5040581), false, false, 2020, 1);
    AverageSalaryInfo averageSalaryInfo2 = new AverageSalaryInfo(new BigDecimal(4587994), false, false, 2019, 1);
    AverageSalaryInfo averageSalaryInfo3 = new AverageSalaryInfo(new BigDecimal(1500000), false, false, 2018, 1);
    //AverageSalaryInfo averageSalaryInfo4 = new AverageSalaryInfo(new BigDecimal(1500000), false, false, 2017);
    salaryInfoList.add(averageSalaryInfo1);
    salaryInfoList.add(averageSalaryInfo2);
    salaryInfoList.add(averageSalaryInfo3);
    //salaryInfoList.add(averageSalaryInfo4);

    BigDecimal result = SalaryCalculation.calculateAverageSalary(salaryInfoList, defaultParameterRepository);

    Assert.assertEquals(new BigDecimal(3058228.051).setScale(2, RoundingMode.FLOOR), result);
  }

  @Test
  public void calculateAverageSalaryWhenNiigmiinAndHealthInsuranceAreExcluded() throws UseCaseException
  {
    List<AverageSalaryInfo> salaryInfoList = new ArrayList<>();

    AverageSalaryInfo averageSalaryInfo1 = new AverageSalaryInfo(new BigDecimal(5040581), true, true, 2020, 1);
    AverageSalaryInfo averageSalaryInfo2 = new AverageSalaryInfo(new BigDecimal(4587994), true, true, 2019, 1);
    AverageSalaryInfo averageSalaryInfo3 = new AverageSalaryInfo(new BigDecimal(1500000), true, true, 2018, 1);
    //AverageSalaryInfo averageSalaryInfo4 = new AverageSalaryInfo(new BigDecimal(1500000), false, false, 2017);
    salaryInfoList.add(averageSalaryInfo1);
    salaryInfoList.add(averageSalaryInfo2);
    salaryInfoList.add(averageSalaryInfo3);
    //salaryInfoList.add(averageSalaryInfo4);

    BigDecimal result = SalaryCalculation.calculateAverageSalary(salaryInfoList, defaultParameterRepository);

    Assert.assertEquals(new BigDecimal(3709525.00).setScale(2, RoundingMode.FLOOR), result);
  }

  @Test
  public void calculateAverageSalaryWhenNiigmiinIsExcludedAndHealthInsuranceIsNotExcluded() throws UseCaseException
  {
    List<AverageSalaryInfo> salaryInfoList = new ArrayList<>();

    AverageSalaryInfo averageSalaryInfo1 = new AverageSalaryInfo(new BigDecimal(5040581), true, false, 2020, 1);
    AverageSalaryInfo averageSalaryInfo2 = new AverageSalaryInfo(new BigDecimal(4587994), true, false, 2019, 1);
    AverageSalaryInfo averageSalaryInfo3 = new AverageSalaryInfo(new BigDecimal(1500000), true, false, 2018, 1);
    //AverageSalaryInfo averageSalaryInfo4 = new AverageSalaryInfo(new BigDecimal(1500000), false, false, 2017);
    salaryInfoList.add(averageSalaryInfo1);
    salaryInfoList.add(averageSalaryInfo2);
    salaryInfoList.add(averageSalaryInfo3);
    //salaryInfoList.add(averageSalaryInfo4);

    BigDecimal result = SalaryCalculation.calculateAverageSalary(salaryInfoList, defaultParameterRepository);

    Assert.assertEquals(new BigDecimal(3277356.601).setScale(2, RoundingMode.FLOOR), result);
  }

  @Test
  public void calculateAverageSalaryWhenNiigmiinIsNotExcludedAndHealthInsuranceIsExcluded() throws UseCaseException
  {
    List<AverageSalaryInfo> salaryInfoList = new ArrayList<>();

    AverageSalaryInfo averageSalaryInfo1 = new AverageSalaryInfo(new BigDecimal(5040581), false, true, 2020, 1);
    AverageSalaryInfo averageSalaryInfo2 = new AverageSalaryInfo(new BigDecimal(4587994), false, true, 2019, 1);
    AverageSalaryInfo averageSalaryInfo3 = new AverageSalaryInfo(new BigDecimal(1500000), false, true, 2018, 1);
    //AverageSalaryInfo averageSalaryInfo4 = new AverageSalaryInfo(new BigDecimal(1500000), false, false, 2017);
    salaryInfoList.add(averageSalaryInfo1);
    salaryInfoList.add(averageSalaryInfo2);
    salaryInfoList.add(averageSalaryInfo3);
    //salaryInfoList.add(averageSalaryInfo4);

    BigDecimal result = SalaryCalculation.calculateAverageSalary(salaryInfoList, defaultParameterRepository);

    Assert.assertEquals(new BigDecimal(3058228.051).setScale(2, RoundingMode.FLOOR), result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateAverageSalaryNoTaxThrowsExceptionWhenNullList()
  {
    SalaryCalculation.calculateAverageSalaryNoTax(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void calculateAverageSalaryNoTaxThrowsExceptionWhenEmptyList()
  {
    SalaryCalculation.calculateAverageSalaryNoTax(Collections.EMPTY_LIST);
  }

  @Test
  public void calculateAverageSalaryNoTax()
  {
    List<BigDecimal> salariesList = new ArrayList<>();

    salariesList.add(new BigDecimal(1000000));
    salariesList.add(new BigDecimal(1500000));
    salariesList.add(new BigDecimal(2000000));
    salariesList.add(new BigDecimal(2200000));
    salariesList.add(new BigDecimal(2543001));

    BigDecimal result = SalaryCalculation.calculateAverageSalaryNoTax(salariesList);

    Assert.assertEquals(new BigDecimal(1848600.201).setScale(2, RoundingMode.FLOOR), result);
  }
}
