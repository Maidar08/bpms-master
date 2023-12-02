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
public class GetCustomerLoanCidInput
{
  private String searchValueType;
  private String searchValue;
  private String searchType;

  private boolean isCoborrower;
  private String branchNumber;
  private String userId;
  private String userName;

  public GetCustomerLoanCidInput(String searchValueType, String searchValue, String searchType, boolean isCoborrower, String branchNumber,
      String userId, String userName)
  {
    this.searchValueType = searchValueType;
    this.searchValue = searchValue;
    this.searchType = searchType;
    this.isCoborrower = isCoborrower;
    this.branchNumber = branchNumber;
    this.userId = userId;
    this.userName = userName;
  }

  public String getSearchValueType()
  {
    return searchValueType;
  }

  public void setSearchValueType(String searchValueType)
  {
    this.searchValueType = searchValueType;
  }

  public String getSearchType()
  {
    return searchType;
  }

  public void setSearchType(String searchType)
  {
    this.searchType = searchType;
  }

  public String getSearchValue()
  {
    return searchValue;
  }

  public void setSearchValue(String searchValue)
  {
    this.searchValue = searchValue;
  }

  public boolean isCoborrower()
  {
    return isCoborrower;
  }

  public void setCoborrower(boolean coborrower)
  {
    isCoborrower = coborrower;
  }

  public String getBranchNumber()
  {
    return branchNumber;
  }

  public void setBranchNumber(String branchNumber)
  {
    this.branchNumber = branchNumber;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }
}
