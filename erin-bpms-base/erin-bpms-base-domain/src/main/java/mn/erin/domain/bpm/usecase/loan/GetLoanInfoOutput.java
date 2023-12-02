/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.loan;

import java.util.List;

import mn.erin.domain.bpm.model.loan.Loan;

/**
 * @author Tamir
 */
public class GetLoanInfoOutput
{
  private List<Loan> loanList;

  public GetLoanInfoOutput(List<Loan> loanList)
  {
    this.loanList = loanList;
  }

  public List<Loan> getLoanList()
  {
    return loanList;
  }

  public void setLoanList(List<Loan> loanList)
  {
    this.loanList = loanList;
  }
}
