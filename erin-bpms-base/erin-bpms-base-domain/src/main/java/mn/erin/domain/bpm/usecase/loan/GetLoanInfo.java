/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.loan;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Tamir
 */
public class GetLoanInfo extends AbstractUseCase<GetLoanInfoInput, GetLoanInfoOutput>
{
  private final LoanService loanService;

  public GetLoanInfo(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public GetLoanInfoOutput execute(GetLoanInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    String customerCID = input.getCustomerCID();
    String borrowerId = input.getBorrowerId();

    if (StringUtils.isBlank(borrowerId) || StringUtils.isBlank(customerCID))
    {
      String errorCode = "BPMS070";
      throw new UseCaseException(errorCode, "Borrower id or Customer CID cannot be null when get loan info list!");
    }

    try
    {
      List<Loan> loanList = loanService.getLoanList(customerCID, BorrowerId.valueOf(borrowerId));
      return new GetLoanInfoOutput(loanList);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
