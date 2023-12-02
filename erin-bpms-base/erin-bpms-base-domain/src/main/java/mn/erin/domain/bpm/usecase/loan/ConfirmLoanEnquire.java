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
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Tamir
 */
public class ConfirmLoanEnquire extends AbstractUseCase<ConfirmLoanEnquireInput, Boolean>
{
  private final LoanService loanService;

  public ConfirmLoanEnquire(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public Boolean execute(ConfirmLoanEnquireInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS068";
      throw new UseCaseException(errorCode, "Confirm loan enquire input cannot be null!");
    }

    String enquireId = input.getLoanEnquireId();
    String borrowerId = input.getBorrowerId();
    String customerCID = input.getCustomerCID();

    try
    {
      return loanService.confirmLoanEnquire(LoanEnquireId.valueOf(enquireId), BorrowerId.valueOf(borrowerId), customerCID);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
