package mn.erin.domain.bpm.model.holidays;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lkhagvadorj.A
 */
public class LoanCyclePlan
{
  private final Date firstPaymentDate;
  private final Date loanStartDate;
  private final BigDecimal requestedLoanAmount;
  private final int loanLength;
  private final BigDecimal yearlyInterestRate;
  private final Map<Date, Boolean> holidays;
  private List<MonthlyLoanCycle> monthlyLoanCycles;
  private BigDecimal equalTotalPaymentPercentageSum;

  public LoanCyclePlan(Date firstPaymentDate, Date loanStartDate, BigDecimal requestedLoanAmount, int loanLength, BigDecimal yearlyInterestRate)
  {
    this.firstPaymentDate = firstPaymentDate;
    this.loanStartDate = loanStartDate;
    this.requestedLoanAmount = requestedLoanAmount;
    this.loanLength = loanLength;
    this.yearlyInterestRate = yearlyInterestRate;
    this.holidays = new HashMap<>();
    this.monthlyLoanCycles = new ArrayList<>();
  }

  public void addHoliday(Date holiday)
  {
    holidays.put(holiday, true);
  }

  public Date getFirstPaymentDate()
  {
    return firstPaymentDate;
  }

  public Date getLoanStartDate()
  {
    return loanStartDate;
  }

  public BigDecimal getRequestedLoanAmount()
  {
    return requestedLoanAmount;
  }

  public int getLoanLength()
  {
    return loanLength;
  }

  public BigDecimal getYearlyInterestRate()
  {
    return yearlyInterestRate;
  }

  public List<MonthlyLoanCycle> getMonthlyLoanCycles()
  {
    return Collections.unmodifiableList(monthlyLoanCycles);
  }

  public Map<Date, Boolean> getHolidays()
  {
    return Collections.unmodifiableMap(holidays);
  }

  public BigDecimal getEqualTotalPaymentPercentageSum()
  {
    return equalTotalPaymentPercentageSum;
  }

  public void setEqualTotalPaymentPercentageSum(BigDecimal equalTotalPaymentPercentageSum)
  {
    this.equalTotalPaymentPercentageSum = equalTotalPaymentPercentageSum;
  }

  public void addMonthlyLoanCycle(MonthlyLoanCycle monthlyLoanCycle)
  {
    monthlyLoanCycles.add(monthlyLoanCycle);
  }

  public void setUpLoanCyclePlanEqualBasicPayment()
  {
    //first monthlyLoanCycle
    MonthlyLoanCycle firstMonthlyLoanCycle = new MonthlyLoanCycle();
    BigDecimal basicMonthlyPayment = calculateBasicMonthlyPayment();

    //add a checker for valid payment first scheduled day
    Date firstScheduledPaymentDay = calculateScheduledDay(firstPaymentDate);
    int firstCycleDaysInBetweenPayments = calculateDaysDifferenceInDates(loanStartDate, firstScheduledPaymentDay);
    BigDecimal firstMonthInterest = calculateMonthlyInterest(requestedLoanAmount, firstCycleDaysInBetweenPayments);
    BigDecimal firstMonthPayment = basicMonthlyPayment.add(firstMonthInterest);
    BigDecimal firstLeftOverLoanTotal = requestedLoanAmount.subtract(basicMonthlyPayment);

    firstMonthlyLoanCycle.setScheduledDay(firstScheduledPaymentDay);
    firstMonthlyLoanCycle.setDaysInBetweenPayments(firstCycleDaysInBetweenPayments);
    firstMonthlyLoanCycle.setBasicMonthlyPayment(basicMonthlyPayment);
    firstMonthlyLoanCycle.setMonthlyInterest(firstMonthInterest);
    firstMonthlyLoanCycle.setTotalMonthlyPayment(firstMonthPayment);
    firstMonthlyLoanCycle.setLeftOverLoanTotal(firstLeftOverLoanTotal);

    monthlyLoanCycles.add(firstMonthlyLoanCycle);

    for (int i = 1; i < loanLength; i++)
    {
      MonthlyLoanCycle monthlyLoanCycleToInsert = new MonthlyLoanCycle();
      MonthlyLoanCycle previousMonthLoanCycle = monthlyLoanCycles.get(i-1);

      Date nextMonthCycle = getNextCycleDate(firstPaymentDate, i);
      Date scheduledPaymentDay = calculateScheduledDay(nextMonthCycle);
      Date previousCycleScheduledDate = previousMonthLoanCycle.getScheduledDay();
      int daysInBetweenPayments = calculateDaysDifferenceInDates(previousCycleScheduledDate, scheduledPaymentDay);
      BigDecimal monthlyInterest = calculateMonthlyInterest(previousMonthLoanCycle.getLeftOverLoanTotal(), daysInBetweenPayments);
      BigDecimal totalMonthlyPayment = basicMonthlyPayment.add(monthlyInterest);
      BigDecimal leftOverLoanTotal = previousMonthLoanCycle.getLeftOverLoanTotal().subtract(basicMonthlyPayment);

      monthlyLoanCycleToInsert.setScheduledDay(scheduledPaymentDay);
      monthlyLoanCycleToInsert.setDaysInBetweenPayments(daysInBetweenPayments);
      monthlyLoanCycleToInsert.setBasicMonthlyPayment(basicMonthlyPayment);
      monthlyLoanCycleToInsert.setMonthlyInterest(monthlyInterest);
      monthlyLoanCycleToInsert.setTotalMonthlyPayment(totalMonthlyPayment);
      monthlyLoanCycleToInsert.setLeftOverLoanTotal(leftOverLoanTotal);

      monthlyLoanCycles.add(monthlyLoanCycleToInsert);
    }
  }

  public void setUpLoanCyclePlanEqualTotalPayment()
  {
    //first monthlyLoanCycle
    MonthlyLoanCycle firstMonthlyLoanCycle = new MonthlyLoanCycle();

    //add a checker for valid payment first scheduled day
    Date firstScheduledPaymentDay = calculateScheduledDay(firstPaymentDate);
    int firstCycleDaysInBetweenPayments = calculateDaysDifferenceInDates(loanStartDate, firstScheduledPaymentDay);
    BigDecimal firstEqualPaymentPercentage = calculateEqualPaymentPercentage(BigDecimal.ONE, firstCycleDaysInBetweenPayments);
    firstMonthlyLoanCycle.setScheduledDay(firstScheduledPaymentDay);
    firstMonthlyLoanCycle.setDaysInBetweenPayments(firstCycleDaysInBetweenPayments);
    firstMonthlyLoanCycle.setEqualPaymentPercentage(firstEqualPaymentPercentage);

    monthlyLoanCycles.add(firstMonthlyLoanCycle);

    BigDecimal equalTotalPaymentPercentageSum = firstEqualPaymentPercentage;

    for (int i = 1; i < loanLength; i++)
    {
      MonthlyLoanCycle monthlyLoanCycleToInsert = new MonthlyLoanCycle();
      MonthlyLoanCycle previousMonthLoanCycle = monthlyLoanCycles.get(i-1);

      Date nextMonthCycle = getNextCycleDate(firstPaymentDate, i);
      Date scheduledPaymentDay = calculateScheduledDay(nextMonthCycle);
      Date previousCycleScheduledDate = previousMonthLoanCycle.getScheduledDay();
      int daysInBetweenPayments = calculateDaysDifferenceInDates(previousCycleScheduledDate, scheduledPaymentDay);
      BigDecimal equalPaymentPercentage = calculateEqualPaymentPercentage(previousMonthLoanCycle.getEqualPaymentPercentage(), daysInBetweenPayments);

      monthlyLoanCycleToInsert.setScheduledDay(scheduledPaymentDay);
      monthlyLoanCycleToInsert.setDaysInBetweenPayments(daysInBetweenPayments);
      monthlyLoanCycleToInsert.setEqualPaymentPercentage(equalPaymentPercentage);

      monthlyLoanCycles.add(monthlyLoanCycleToInsert);

      equalTotalPaymentPercentageSum = equalTotalPaymentPercentageSum.add(equalPaymentPercentage);
    }

    this.equalTotalPaymentPercentageSum = equalTotalPaymentPercentageSum;

    for (MonthlyLoanCycle monthlyLoanCycle : monthlyLoanCycles)
    {
      monthlyLoanCycle.setTotalMonthlyPayment(requestedLoanAmount.divide(equalTotalPaymentPercentageSum, 16, RoundingMode.FLOOR));
    }
  }

  public BigDecimal getMaxTotalMonthlyPayment()
  {
    BigDecimal currentMax = BigDecimal.ZERO;

    for (MonthlyLoanCycle monthlyLoanCycle: monthlyLoanCycles)
    {
      BigDecimal totalMonthlyPayment = monthlyLoanCycle.getTotalMonthlyPayment();
      if (totalMonthlyPayment.compareTo(currentMax) == 1)
      {
        currentMax = totalMonthlyPayment;
      }
    }

    return currentMax;
  }

  public Date calculateScheduledDay(Date paymentDate)
  {
    while (!isDateAvailable(paymentDate))
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(paymentDate);
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      paymentDate = calendar.getTime();
    }
    return paymentDate;
  }

  private Date getNextCycleDate(Date paymentDate, int nthLoanCycle)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(paymentDate);
    calendar.add(Calendar.MONTH, nthLoanCycle);
    return calendar.getTime();
  }

  private BigDecimal calculateMonthlyInterest(BigDecimal leftOverLoanAmount, int daysInBetweenPayments)
  {
    return leftOverLoanAmount.multiply(yearlyInterestRate).divide(new BigDecimal(365), 16 , RoundingMode.FLOOR).multiply(new BigDecimal(daysInBetweenPayments));//.multiply(, 5, RoundingMode.FLOOR);
  }

  private BigDecimal calculateBasicMonthlyPayment()
  {
    return requestedLoanAmount.divide(new BigDecimal(loanLength), 16, RoundingMode.FLOOR).setScale(16, RoundingMode.FLOOR);
    //maybe add another logic where if it is the last monthly payment then
    //calculation is basicMonthlyPayment = requestedLoanAmount - sum(all previous payments)
    //sanity check needed!
  }

  private BigDecimal calculateEqualPaymentPercentage(BigDecimal previousEqualPaymentPercentage, int daysInBetweenCycles)
  {
    BigDecimal factor = yearlyInterestRate.multiply(new BigDecimal(daysInBetweenCycles)).divide(new BigDecimal(365), 16, RoundingMode.FLOOR).add(BigDecimal.ONE);
    return previousEqualPaymentPercentage.divide(factor, 16, RoundingMode.FLOOR);
  }

  private boolean isDateAvailable(Date dateToCheck)
  {
    if(isWeekend(dateToCheck) || isHoliday(dateToCheck))
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  private boolean isWeekend(Date dateToCheck)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateToCheck);
    int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);

    if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
    {
      return true;
    }
    return false;
  }

  public boolean isHoliday(Date dateToCheck)
  {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(dateToCheck);

    for (Map.Entry<Date, Boolean> dateBooleanEntry: holidays.entrySet())
    {
      Date date = dateBooleanEntry.getKey();
      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(date);

      if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
          cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
          cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
      )
      {
        return true;
      }
    }

    return false;
  }

  public int calculateDaysDifferenceInDates(Date d1, Date d2)
  {
   Long diff = ChronoUnit.DAYS.between(d1.toInstant(), d2.toInstant());
   return diff.intValue();
  }
}
