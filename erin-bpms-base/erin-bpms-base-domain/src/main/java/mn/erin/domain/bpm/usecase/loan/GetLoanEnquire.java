/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.loan;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Tamir
 */
public class GetLoanEnquire extends AbstractUseCase<GetLoanEnquireInput, LoanEnquire>
{
  private final LoanService loanService;

  public GetLoanEnquire(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public LoanEnquire execute(GetLoanEnquireInput input) throws UseCaseException
  {
    if(null == input)
    {
      String errorCode = "BPMS067";
      throw new UseCaseException(errorCode, "Get loan enquire input is required!");
    }
    String customerCID = input.getCustomerCID();
    boolean searchByCoBorrower = input.isSearchByCoBorrower();

    if (StringUtils.isBlank(customerCID))
    {
      String errorCode = "BPMS061";
      throw new UseCaseException(errorCode, "Customer CID cannot be null!");
    }

    if(searchByCoBorrower)
    {
      try
      {
        return loanService.getLoanEnquireByCoBorrower(customerCID);
      }
      catch (BpmServiceException e)
      {
        throw new UseCaseException(e.getCode(), e.getMessage(), e);
      }
    }

    try
    {
      return loanService.getLoanEnquire(customerCID);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
