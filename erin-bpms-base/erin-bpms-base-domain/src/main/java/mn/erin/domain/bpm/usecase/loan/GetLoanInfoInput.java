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
public class GetLoanInfoInput
{
  private String borrowerId;
  private String customerCID;

  public GetLoanInfoInput(String borrowerId, String customerCID)
  {
    this.borrowerId = borrowerId;
    this.customerCID = customerCID;
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
