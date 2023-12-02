package mn.erin.domain.bpm.model.holidays;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Lkhagvadorj.A
 */
public class MonthlyLoanCycle
{
  private BigDecimal equalPaymentPercentage;
  private Date scheduledDay;
  private int daysInBetweenPayments;
  private BigDecimal basicMonthlyPayment;
  private BigDecimal monthlyInterest;
  private BigDecimal totalMonthlyPayment;
  private BigDecimal leftOverLoanTotal;

  public MonthlyLoanCycle()
  {
  }

  public BigDecimal getEqualPaymentPercentage()
  {
    return equalPaymentPercentage;
  }

  public void setEqualPaymentPercentage(BigDecimal equalPaymentPercentage)
  {
    this.equalPaymentPercentage = equalPaymentPercentage;
  }

  public Date getScheduledDay()
  {
    return scheduledDay;
  }

  public void setScheduledDay(Date scheduledDay)
  {
    this.scheduledDay = scheduledDay;
  }

  public int getDaysInBetweenPayments()
  {
    return daysInBetweenPayments;
  }

  public void setDaysInBetweenPayments(int daysInBetweenPayments)
  {
    this.daysInBetweenPayments = daysInBetweenPayments;
  }

  public BigDecimal getBasicMonthlyPayment()
  {
    return basicMonthlyPayment;
  }

  public void setBasicMonthlyPayment(BigDecimal basicMonthlyPayment)
  {
    this.basicMonthlyPayment = basicMonthlyPayment;
  }

  public BigDecimal getMonthlyInterest()
  {
    return monthlyInterest;
  }

  public void setMonthlyInterest(BigDecimal monthlyInterest)
  {
    this.monthlyInterest = monthlyInterest;
  }

  public BigDecimal getTotalMonthlyPayment()
  {
    return totalMonthlyPayment;
  }

  public void setTotalMonthlyPayment(BigDecimal totalMonthlyPayment)
  {
    this.totalMonthlyPayment = totalMonthlyPayment;
  }

  public BigDecimal getLeftOverLoanTotal()
  {
    return leftOverLoanTotal;
  }

  public void setLeftOverLoanTotal(BigDecimal leftOverLoanTotal)
  {
    this.leftOverLoanTotal = leftOverLoanTotal;
  }
}
