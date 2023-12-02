/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.loan;

import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Tamir
 */
public class GetCustomerLoanCid extends AbstractUseCase<GetCustomerLoanCidInput, String>
{
  private final LoanService loanService;

  public GetCustomerLoanCid(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public String execute(GetCustomerLoanCidInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS057";
      throw new UseCaseException(errorCode, "Get customer loan CID input cannot be null!");
    }

    String searchValueType = input.getSearchValueType();
    String searchValue = input.getSearchValue();
    String searchType = input.getSearchType();

    String branchNumber = input.getBranchNumber();
    boolean isCoborrower = input.isCoborrower();

    String userId = input.getUserId();
    String userName = input.getUserName();

    try
    {
      return loanService.getCustomerCID(searchValueType, searchValue, searchType, isCoborrower, branchNumber, userId, userName);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
