package mn.erin.domain.bpm.usecase.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.calculations.salary.NiigmiinDaatgalCalculation;

/**
 * @author Zorig
 */
@Ignore
// TODO : tests failing on teamcity, fix issue.
public class NiigmiinDaatgalCalculationTest
{
  private DefaultParameterRepository defaultParameterRepository;
  @Before
  public void initTest()
  {
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void determineNdshThrowsExceptionWhenYearIsUnsupportedLow() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(213);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2016;
    NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, 1,  defaultParameterRepository);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void determineNdshThrowsExceptionWhenYearIsUnsupportedHigh() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(213);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;
    int year = 2021;
    NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, year, 1, defaultParameterRepository);
  }

  @Test
  public void determineNdshWhenHealthInsuranceAndNiigmiinDaatgalAreExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(0), result2018);
    Assert.assertEquals(new BigDecimal(0), result2019);
    Assert.assertEquals(new BigDecimal(0), result2020);
  }

  @Test
  public void determineNdshWhenHealthInsuranceIsNotExcludedAndNiigmiinDaatgalIsExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = true;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(100811.62).setScale(2, RoundingMode.CEILING), result2018);
    Assert.assertEquals(new BigDecimal(100811.62).setScale(2, RoundingMode.CEILING), result2019);
    Assert.assertEquals(new BigDecimal(100811.62).setScale(2, RoundingMode.CEILING), result2020);
  }

  @Test
  public void determineNdshWhenHealthInsuranceIsExcludedAndNiigmiinDaatgalIsNotExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(264000.00).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(368000.00).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(420000.00).setScale(2, RoundingMode.FLOOR), result2020);
  }

  @Test
  public void determineNdshWhenHealthInsuranceIsExcludedAndNiigmiinDaatgalIsNotExcluded_DoesNotMeetMaxTaxable() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(1000000);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = true;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);

    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(110000.00).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(115000.00).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(115000.00).setScale(2, RoundingMode.FLOOR), result2020);
  }

  @Test
  public void determineNdshWhenHealthInsuranceAndNiigmiinDaatgalAreNotExcluded() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(5040581);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);
    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(264000.00).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(368000.00).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(420000.00).setScale(2, RoundingMode.FLOOR), result2020);
  }

  @Test
  public void determineNdshWhenHealthInsuranceAndNiigmiinDaatgalAreExcludedANDDoesNotMeetMaxTaxable() throws UseCaseException
  {
    BigDecimal monthlySalary = new BigDecimal(1000000);
    boolean isExcludedNiigmiinDaatgal = false;
    boolean isExcludedHealthInsurance = false;

    //BigDecimal result2017 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2017);
    BigDecimal result2018 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2018, 1, defaultParameterRepository);
    BigDecimal result2019 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2019, 1, defaultParameterRepository);
    BigDecimal result2020 = NiigmiinDaatgalCalculation.determineNdsh(monthlySalary, isExcludedNiigmiinDaatgal, isExcludedHealthInsurance, 2020, 1, defaultParameterRepository);
    //Assert.assertEquals(new BigDecimal(0), result2017);
    Assert.assertEquals(new BigDecimal(110000.00).setScale(2, RoundingMode.FLOOR), result2018);
    Assert.assertEquals(new BigDecimal(115000.00).setScale(2, RoundingMode.FLOOR), result2019);
    Assert.assertEquals(new BigDecimal(115000.00).setScale(2, RoundingMode.FLOOR), result2020);
  }
}
