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
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Tamir
 */
public class GetLoanEnquireWithFile extends AbstractUseCase<GetLoanEnquireWithFileInput, LoanEnquire>
{
  private final LoanService loanService;

  public GetLoanEnquireWithFile(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public LoanEnquire execute(GetLoanEnquireWithFileInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS067";
      throw new UseCaseException(errorCode, "Get loan enquire with file input is required!");
    }

    String loanEnquireId = input.getLoanEnquireId();
    String borrowerId = input.getBorrowerId();
    String customerCID = input.getCustomerCID();

    try
    {
      return loanService.getLoanEnquireWithFile(LoanEnquireId.valueOf(loanEnquireId), BorrowerId.valueOf(borrowerId), customerCID);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
