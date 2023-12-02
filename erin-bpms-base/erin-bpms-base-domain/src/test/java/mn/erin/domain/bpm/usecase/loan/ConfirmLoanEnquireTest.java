package mn.erin.domain.bpm.usecase.loan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Sukhbat
 */

public class ConfirmLoanEnquireTest
{
  private LoanService loanService;
  private ConfirmLoanEnquire useCase;
  private ConfirmLoanEnquireInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new ConfirmLoanEnquire(loanService);
    input = new ConfirmLoanEnquireInput("loanEnquireId", "borrowerId", "customerCID");
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new ConfirmLoanEnquire(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.confirmLoanEnquire(LoanEnquireId.valueOf("loanEnquireId"), BorrowerId.valueOf("borrowerId"), input.getCustomerCID()))
        .thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(loanService.confirmLoanEnquire(LoanEnquireId.valueOf("loanEnquireId"), BorrowerId.valueOf("borrowerId"), input.getCustomerCID()))
        .thenReturn(true);
    Boolean output = useCase.execute(input);
    Assert.assertEquals(output, true);
  }
}
