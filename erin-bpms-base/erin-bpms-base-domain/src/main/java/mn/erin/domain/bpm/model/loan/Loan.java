/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.loan;

import java.io.Serializable;
import java.math.BigDecimal;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class Loan implements Entity<Loan>, Serializable
{
  private final LoanId id;
  private BorrowerId borrowerId;

  private LoanClass loanClass;
  private String customerCID;

  private String type;
  private String status;

  private String startDate;
  private String expireDate;

  private String currencyCode;

  private BigDecimal remaining;
  private BigDecimal amount;
  private BigDecimal balance;

  public Loan(LoanId id, LoanClass loanClass)
  {
    this.id = id;
    this.loanClass = loanClass;
  }

  public Loan(LoanId loanId, LoanClass loanClass, String type,  String status, String startDate, String expireDate, String currencyCode, BigDecimal amount, BigDecimal balance){
    this.id = loanId;
    this.loanClass = loanClass;
    this.type = type;
    this.status = status;
    this.startDate = startDate;
    this.expireDate = expireDate;
    this.currencyCode = currencyCode;
    this.amount = amount;
    this.balance = balance;
  }

  public LoanId getId()
  {
    return id;
  }

  public BigDecimal getRemaining()
  {
    return remaining;
  }

  public void setRemaining(BigDecimal remaining)
  {
    this.remaining = remaining;
  }

  public LoanClass getLoanClass()
  {
    return loanClass;
  }

  public void setLoanClass(LoanClass loanClass)
  {
    this.loanClass = loanClass;
  }

  public String getCustomerCID()
  {
    return customerCID;
  }

  public void setCustomerCID(String customerCID)
  {
    this.customerCID = customerCID;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getStartDate()
  {
    return startDate;
  }

  public void setStartDate(String startDate)
  {
    this.startDate = startDate;
  }

  public String getExpireDate()
  {
    return expireDate;
  }

  public void setExpireDate(String expireDate)
  {
    this.expireDate = expireDate;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public void setAmount(BigDecimal amount)
  {
    this.amount = amount;
  }

  public String getStatus()
  {
    return status;
  }

  public BigDecimal getBalance()
  {
    return balance;
  }

  public void setBalance(BigDecimal balance)
  {
    this.balance = balance;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getCurrencyCode()
  {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode)
  {
    this.currencyCode = currencyCode;
  }

  public BorrowerId getBorrowerId()
  {
    return borrowerId;
  }

  public void setBorrowerId(BorrowerId borrowerId)
  {
    this.borrowerId = borrowerId;
  }

  @Override
  public boolean sameIdentityAs(Loan other)
  {
    return other != null && other.id.equals(this.id);
  }
}
