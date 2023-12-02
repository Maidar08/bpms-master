package mn.erin.domain.bpm.usecase.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.calculations.salary.HhoatCalculation;

/**
 * @author Zorig
 */
@Ignore
// TODO : tests failing on teamcity, fix issue.
public class HhoatCalculationTest
{
  private DefaultParameterRepository defaultParameterRepository;
  private final int index = 0;
  List<Map<String, BigDecimal>> previousMonthsSalaryList = new ArrayList<>();
  List<Map<String, BigDecimal>> previousMonthsHhoatAmountList = new ArrayList<Map<String, BigDecimal>>();
  List<Map<String, BigDecimal>> previousMonthsSalaryAfterNdshList = new ArrayList<>();

  @Before
  public void initTest()
  {
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void determineHhoatThrowsExceptionWhenYearIsUnsupportedLow() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(420000);
    BigDecimal singleMonthSalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2016;
    HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, defaultParameterRepository, index,
        previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void determineHhoatThrowsExceptionWhenYearIsUnsupportedHigh() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(420000);
    BigDecimal singleMonthSalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2021;
    HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, defaultParameterRepository, index,
        previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
  }

  @Test
  public void determineHhoatWhenNiigmiinDaatgalAndHealthInsuranceAreExcluded() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(420000);
    BigDecimal singleMonthSalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result2017 = HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(0), result2018);
    Assert.assertEquals(new BigDecimal(0), result2019);
    Assert.assertEquals(new BigDecimal(0), result2020);
  }

  @Test
  public void determineHhoatWhenNiigmiinDaatgalIsExcludedAndHealthInsuranceIsNotExcluded() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(100811.62);
    BigDecimal singleMonthSalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result2017 = HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(493976.93).setScale(2, RoundingMode.CEILING), result2018);
    Assert.assertEquals(new BigDecimal(493976.93).setScale(2, RoundingMode.CEILING), result2019);
    Assert.assertEquals(new BigDecimal(493976.93).setScale(2, RoundingMode.CEILING), result2020);
  }

  @Test
  public void determineHhoatWhenNiigmiinDaatgalIsNotExcludedAndHealthInsuranceIsExcluded() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(230000);
    BigDecimal ndsh2018 = new BigDecimal(220000);
    BigDecimal singleMonthSalary = new BigDecimal(2000000);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result2017 = HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = HhoatCalculation
        .determineHhoat(ndsh2018, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(163000.00).setScale(2, RoundingMode.CEILING), result2018);
    Assert.assertEquals(new BigDecimal(162000.00).setScale(2, RoundingMode.CEILING), result2019);
    Assert.assertEquals(new BigDecimal(162000.00).setScale(2, RoundingMode.CEILING), result2020);
  }

  @Test
  public void determineHhoatWhenNiigmiinDaatgalAndHealthInsuranceAreNotExcluded() throws UseCaseException
  {
    BigDecimal ndsh = new BigDecimal(230000);
    BigDecimal ndsh2018 = new BigDecimal(220000);
    BigDecimal singleMonthSalary = new BigDecimal(2000000);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result2017 = HhoatCalculation.determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = HhoatCalculation
        .determineHhoat(ndsh2018, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2019 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);
    BigDecimal result2020 = HhoatCalculation
        .determineHhoat(ndsh, singleMonthSalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, defaultParameterRepository, index,
            previousMonthsSalaryList, previousMonthsSalaryAfterNdshList, previousMonthsHhoatAmountList);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(163000.00).setScale(2, RoundingMode.CEILING), result2018);
    Assert.assertEquals(new BigDecimal(162000.00).setScale(2, RoundingMode.CEILING), result2019);
    Assert.assertEquals(new BigDecimal(162000.00).setScale(2, RoundingMode.CEILING), result2020);
  }
}
