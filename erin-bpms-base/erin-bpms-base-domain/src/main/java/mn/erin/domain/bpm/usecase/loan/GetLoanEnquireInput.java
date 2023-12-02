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
public class GetLoanEnquireInput
{
  private String customerCID;
  private boolean isSearchByCoBorrower;

  public GetLoanEnquireInput(String customerCID, boolean isSearchByCoBorrower)
  {
    this.customerCID = customerCID;
    this.isSearchByCoBorrower = isSearchByCoBorrower;
  }

  public String getCustomerCID()
  {
    return customerCID;
  }

  public void setCustomerCID(String customerCID)
  {
    this.customerCID = customerCID;
  }

  public boolean isSearchByCoBorrower()
  {
    return isSearchByCoBorrower;
  }

  public void setSearchByCoBorrower(boolean searchByCoBorrower)
  {
    isSearchByCoBorrower = searchByCoBorrower;
  }
}
