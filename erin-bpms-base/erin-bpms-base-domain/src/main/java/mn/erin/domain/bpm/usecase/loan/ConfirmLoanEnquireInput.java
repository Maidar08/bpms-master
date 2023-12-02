/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.loan;

/**
 * @author Tamir
 */
public class ConfirmLoanEnquireInput
{
  private String loanEnquireId;
  private String borrowerId;
  private String customerCID;

  public ConfirmLoanEnquireInput(String loanEnquireId, String borrowerId, String customerCID)
  {
    this.loanEnquireId = loanEnquireId;
    this.borrowerId = borrowerId;
    this.customerCID = customerCID;
  }

  public String getLoanEnquireId()
  {
    return loanEnquireId;
  }

  public void setLoanEnquireId(String loanEnquireId)
  {
    this.loanEnquireId = loanEnquireId;
  }

  public String getBorrowerId()
  {
    return borrowerId;
  }

  public void setBorrowerId(String borrowerId)
  {
    this.borrowerId = borrowerId;
  }

  public String getCustomerCID()
  {
    return customerCID;
  }

  public void setCustomerCID(String customerCID)
  {
    this.customerCID = customerCID;
  }
}
