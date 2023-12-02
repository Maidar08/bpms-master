/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.loan;

import java.io.Serializable;
import java.util.List;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.customer.Customer;

/**
 * @author Tamir
 */
public class LoanEnquire implements Entity<LoanEnquire>, Serializable
{
  private final LoanEnquireId id;
  private BorrowerId borrowerId;

  private String customerCID;
  private Customer customer;

  private List<LoanId> loanIdList;
  private byte[] enquireAsFile;

  public LoanEnquire(LoanEnquireId id, BorrowerId borrowerId)
  {
    this.id = id;
    this.borrowerId = borrowerId;
  }

  public LoanEnquire(LoanEnquireId id, BorrowerId borrowerId, String customerCID)
  {
    this.id = id;
    this.borrowerId = borrowerId;
    this.customerCID = customerCID;
  }

  public LoanEnquire(LoanEnquireId id, BorrowerId borrowerId, String customerCID, Customer customer,
      List<LoanId> loanIdList)
  {
    this.id = id;
    this.borrowerId = borrowerId;
    this.customerCID = customerCID;
    this.customer = customer;
    this.loanIdList = loanIdList;
  }

  public LoanEnquireId getId()
  {
    return id;
  }

  public String getCustomerCID()
  {
    return customerCID;
  }

  public void setCustomerCID(String customerCID)
  {
    this.customerCID = customerCID;
  }

  public Customer getCustomer()
  {
    return customer;
  }

  public void setCustomer(Customer customer)
  {
    this.customer = customer;
  }

  public BorrowerId getBorrowerId()
  {
    return borrowerId;
  }

  public void setBorrowerId(BorrowerId borrowerId)
  {
    this.borrowerId = borrowerId;
  }

  public List<LoanId> getLoanIdList()
  {
    return loanIdList;
  }

  public void setLoanIdList(List<LoanId> loanIdList)
  {
    this.loanIdList = loanIdList;
  }

  public byte[] getEnquireAsFile()
  {
    return enquireAsFile;
  }

  public void setEnquireAsFile(byte[] enquireAsFile)
  {
    this.enquireAsFile = enquireAsFile;
  }

  public void addLoanId(LoanId loanId)
  {
    if (null != loanId)
    {
      this.loanIdList.add(loanId);
    }
  }

  @Override
  public boolean sameIdentityAs(LoanEnquire other)
  {
    return other != null && other.id.equals(this.id);
  }
}
