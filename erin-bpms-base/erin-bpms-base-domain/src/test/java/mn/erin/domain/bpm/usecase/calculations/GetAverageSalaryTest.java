package mn.erin.domain.bpm.usecase.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

/**
 * @author Zorig
 */
@Ignore
// TODO : fix tests.
public class GetAverageSalaryTest
{
  private static GetAverageSalary getAverageSalary;
  private static DefaultParameterRepository defaultParameterRepository;

  @Before
  public void setUp()
  {
    getAverageSalary = new GetAverageSalary(defaultParameterRepository);
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
  }

  @Test(expected = UseCaseException.class)
  public void getAverageSalaryThrowsExceptionWhenInputIsNull() throws UseCaseException
  {
    getAverageSalary.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void getAverageSalaryThrowsExceptionWhenMapIsEmpty() throws UseCaseException
  {
    Map<Date, BigDecimal> dateToSalariesMap = new HashMap<>();
    GetAverageSalaryInput input = new GetAverageSalaryInput(dateToSalariesMap, true, true);
    getAverageSalary.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void getAverageSalaryThrowsExceptionWhenMapKeyContainsNull() throws UseCaseException
  {
    Map<Date, BigDecimal> dateToSalariesMap = new HashMap<>();
    dateToSalariesMap.put(null, new BigDecimal(11));
    GetAverageSalaryInput input = new GetAverageSalaryInput(dateToSalariesMap, true, true);
    getAverageSalary.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void getAverageSalaryThrowsExceptionWhenMapSalaryIsNegative() throws UseCaseException
  {
    Map<Date, BigDecimal> dateToSalariesMap = new HashMap<>();
    dateToSalariesMap.put(new Date(), new BigDecimal(-1));
    GetAverageSalaryInput input = new GetAverageSalaryInput(dateToSalariesMap, true, true);
    getAverageSalary.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void getAverageSalaryThrowsExceptionWhenMapSalaryIsNull() throws UseCaseException
  {
    Map<Date, BigDecimal> dateToSalariesMap = new HashMap<>();
    dateToSalariesMap.put(new Date(), null);
    GetAverageSalaryInput input = new GetAverageSalaryInput(dateToSalariesMap, true, true);
    getAverageSalary.execute(input);
  }

  @Test
  public void getAverageSalaryWhenNiigmiinDaatgalIsExcludedAndHealthInsuranceIsNotExcluded() throws UseCaseException
  {
    GetAverageSalaryInput input = new GetAverageSalaryInput(createDateToSalariesMap(), true, false);
    GetAverageSalaryOutput output = getAverageSalary.execute(input);

    Assert.assertEquals(new BigDecimal(1000000.00).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryBeforeTax());
    Assert.assertEquals(new BigDecimal(900333.331).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryAfterTax());

/*
    Map<Date, Map<String, BigDecimal>> dateToSalaries = output.getAfterTaxSalaries();

    Map<String, BigDecimal> salariesInfo = dateToSalaries.get(new Date());
    Assert.assertNotNull(salariesInfo);
    Assert.assertEquals(1, salariesInfo.get("Hhoat"));
    salariesInfo.get("Ndsh");
    salariesInfo.get("MonthSalaryAfterTax");

    Map<String, BigDecimal> salariesInfo = dateToSalaries.get(new Date());
    salariesInfo.get("Hhoat");
    salariesInfo.get("Ndsh");
    salariesInfo.get("MonthSalaryAfterTax");

    Map<String, BigDecimal> salariesInfo = dateToSalaries.get(new Date());
    salariesInfo.get("Hhoat");
    salariesInfo.get("Ndsh");
    salariesInfo.get("MonthSalaryAfterTax");
     */

  }

  @Test
  public void getAverageSalaryWhenNiigmiinDaatgalIsNotExcludedAndHealthInsuranceIsExcluded() throws UseCaseException
  {
    GetAverageSalaryInput input = new GetAverageSalaryInput(createDateToSalariesMap(), false, true);
    GetAverageSalaryOutput output = getAverageSalary.execute(input);

    Assert.assertEquals(new BigDecimal(1000000.00).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryBeforeTax());
    Assert.assertEquals(new BigDecimal(815958.331).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryAfterTax());
  }

  @Test
  public void getAverageSalaryWhenNiigmiinDaatgalAndHealthInsuranceAreExcluded() throws UseCaseException
  {
    GetAverageSalaryInput input = new GetAverageSalaryInput(createDateToSalariesMap(), true, true);
    GetAverageSalaryOutput output = getAverageSalary.execute(input);

    Assert.assertEquals(new BigDecimal(1000000.00).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryBeforeTax());
    Assert.assertEquals(new BigDecimal(1000000.00).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryAfterTax());
  }

  @Test
  public void getAverageSalaryWhenNiigmiinDaatgalAndHealthInsuranceAreNotExcluded() throws UseCaseException
  {
    GetAverageSalaryInput input = new GetAverageSalaryInput(createDateToSalariesMap(), false, false);
    GetAverageSalaryOutput output = getAverageSalary.execute(input);

    Assert.assertEquals(new BigDecimal(1000000.00).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryBeforeTax());
    Assert.assertEquals(new BigDecimal(815958.331).setScale(2, RoundingMode.FLOOR), output.getAverageSalaryAfterTax());
  }

  private Map<Date, BigDecimal> createDateToSalariesMap()
  {
    Map<Date, BigDecimal> dateToSalariesMap = new HashMap<>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2018);
    calendar.set(Calendar.MONTH, 3);
    calendar.set(Calendar.DAY_OF_MONTH, 1);

    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 4);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));

    calendar.set(Calendar.YEAR, 2019);
    calendar.set(Calendar.MONTH, 5);

    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 6);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 7);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 8);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));

    calendar.set(Calendar.YEAR, 2020);
    calendar.set(Calendar.MONTH, 9);

    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 10);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));

    /*calendar.set(Calendar.YEAR, 2017);
    calendar.set(Calendar.MONTH, 9);

    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));
    calendar.set(Calendar.MONTH, 10);
    dateToSalariesMap.put(calendar.getTime(), new BigDecimal(1000000));*/

    return dateToSalariesMap;
  }
}
