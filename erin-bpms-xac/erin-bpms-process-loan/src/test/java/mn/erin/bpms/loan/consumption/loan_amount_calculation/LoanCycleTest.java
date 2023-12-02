package mn.erin.bpms.loan.consumption.loan_amount_calculation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Zorig
 */
public class LoanCycleTest
{
  private LoanCyclePlan loanCyclePlan;

  @Before
  public void init()
  {
    Calendar loanStartDate = Calendar.getInstance();
    loanStartDate.set(2020, 3, 18);
    Date loanStart = loanStartDate.getTime();

    Calendar firstPaymentDate = Calendar.getInstance();
    firstPaymentDate.set(2020, 4, 18);
    Date firstPayment = firstPaymentDate.getTime();

    BigDecimal requestedLoanAmount = new BigDecimal(50000000);
    int loanLength = 30;
    BigDecimal yearlyInterestRate = new BigDecimal(.168);

    loanCyclePlan = new LoanCyclePlan(firstPaymentDate.getTime(), loanStartDate.getTime(), requestedLoanAmount, loanLength, yearlyInterestRate);

    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, 0, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 5, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 2, 8);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 6, 11);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 6, 12);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 6, 13);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 6, 14);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 6, 15);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 10, 26);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2020, 11, 29);
    loanCyclePlan.addHoliday(calendar.getTime());

    calendar.set(2021, 0, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 5, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 2, 8);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 6, 11);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 6, 12);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 6, 13);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 6, 14);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 6, 15);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 10, 26);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2021, 11, 29);
    loanCyclePlan.addHoliday(calendar.getTime());

    calendar.set(2022, 0, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 5, 1);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 2, 8);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 6, 11);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 6, 12);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 6, 13);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 6, 14);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 6, 15);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 10, 26);
    loanCyclePlan.addHoliday(calendar.getTime());
    calendar.set(2022, 11, 29);
    loanCyclePlan.addHoliday(calendar.getTime());
  }

  @Test
  public void calculateScheduledDayNonWeekendNonHoliday()
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, 4, 11);

    Calendar expected = Calendar.getInstance();
    expected.set(2020, 4, 11);

    Date date = loanCyclePlan.calculateScheduledDay(calendar.getTime());

    Assert.assertEquals(date.toString(), expected.getTime().toString());
  }

  @Test
  public void calculateScheduledDayWeekendAndHoliday()
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, 4, 30);

    Calendar expected = Calendar.getInstance();
    expected.set(2020, 5, 2);

    Date date = loanCyclePlan.calculateScheduledDay(calendar.getTime());

    Assert.assertEquals(date.toString(), expected.getTime().toString());
  }

  @Test
  public void calculateScheduledDayWeekendNonHoliday()
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, 4, 9);

    Calendar expected = Calendar.getInstance();
    expected.set(2020, 4, 11);

    Date date = loanCyclePlan.calculateScheduledDay(calendar.getTime());

    Assert.assertEquals(date.toString(), expected.getTime().toString());
  }

  @Test
  public void calculateScheduledDayNonWeekendHoliday()
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, 5, 1);

    Calendar expected = Calendar.getInstance();
    expected.set(2020, 5, 2);

    Date date = loanCyclePlan.calculateScheduledDay(calendar.getTime());

    Assert.assertEquals(date.toString(), expected.getTime().toString());
  }

  @Test
  public void setUpLoanCyclePlanEqualBasicPayment()
  {
    loanCyclePlan.setUpLoanCyclePlanEqualBasicPayment();
    List<MonthlyLoanCycle> monthlyLoanCycles = loanCyclePlan.getMonthlyLoanCycles();

    Assert.assertEquals(monthlyLoanCycles.size(), 30);

    BigDecimal basicMonthlyPayment = new BigDecimal(1666666.67);

    assertMonthlyLoanCycle(monthlyLoanCycles, 0, 30, basicMonthlyPayment, new BigDecimal(690410.96), new BigDecimal(2357077.63), new BigDecimal(48333333.33));
    assertMonthlyLoanCycle(monthlyLoanCycles, 29, 29, basicMonthlyPayment, new BigDecimal(690410.96), new BigDecimal(2357077.63), new BigDecimal(48333333.33));
  }

  // FIXME urgently
//  @Test
//  public void getMaxTotalMonthlyPaymentEqualBasicPayment()
//  {
//    loanCyclePlan.setUpLoanCyclePlanEqualBasicPayment();
//    loanCyclePlan.getMonthlyLoanCycles();
//    loanCyclePlan.getMaxTotalMonthlyPayment();
//  }

  @Test
  public void setUpLoanCyclePlanEqualTotalPayment()
  {
    loanCyclePlan.setUpLoanCyclePlanEqualTotalPayment();
    List<MonthlyLoanCycle> monthlyLoanCycles = loanCyclePlan.getMonthlyLoanCycles();

    Assert.assertEquals(monthlyLoanCycles.size(), 30);

    BigDecimal basicMonthlyPayment = new BigDecimal(1666666.67);

    //Assert.assertEquals(monthlyLoanCycles.get(0).getEqualPaymentPercentage(), 1);

    //Assert.assertEquals(loanCyclePlan.getEqualTotalPaymentPercentageSum(), 1);
    //Assert.assertEquals(loanCyclePlan.getMaxTotalMonthlyPayment(), 1);
    //Assert.assertEquals(monthlyLoanCycles.get(0).getTotalMonthlyPayment(), 1);

    assertMonthlyLoanCycle(monthlyLoanCycles, 0, 30, basicMonthlyPayment, new BigDecimal(690410.96), new BigDecimal(2357077.63), new BigDecimal(48333333.33));
    assertMonthlyLoanCycle(monthlyLoanCycles, 29, 29, basicMonthlyPayment, new BigDecimal(690410.96), new BigDecimal(2357077.63), new BigDecimal(48333333.33));
  }

  // FIXME urgently
//  @Test
//  public void getMaxTotalMonthlyPaymentEqualTotalPayment()
//  {
//    loanCyclePlan.setUpLoanCyclePlanEqualTotalPayment();
//    loanCyclePlan.getMonthlyLoanCycles();
//    loanCyclePlan.getMaxTotalMonthlyPayment();
//  }

  private void assertMonthlyLoanCycle(List<MonthlyLoanCycle> monthlyLoanCycles, int index, int daysInBetweenPayments, BigDecimal basicMonthlyPayment,
      BigDecimal monthlyInterest, BigDecimal totalMonthlyPayment, BigDecimal leftOverLoanTotal)
  {
    MonthlyLoanCycle monthlyLoanCycle = monthlyLoanCycles.get(index);
    Assert.assertEquals(monthlyLoanCycle.getDaysInBetweenPayments(), daysInBetweenPayments);
    //Assert.assertEquals(monthlyLoanCycle.getBasicMonthlyPayment(), basicMonthlyPayment.setScale(3, RoundingMode.FLOOR));
    //Assert.assertEquals(monthlyLoanCycle.getMonthlyInterest(), monthlyInterest.setScale(4, RoundingMode.FLOOR));
    //Assert.assertEquals(monthlyLoanCycle.getTotalMonthlyPayment(), totalMonthlyPayment);
    //Assert.assertEquals(monthlyLoanCycle.getLeftOverLoanTotal(), leftOverLoanTotal);
  }
}
