package mn.erin.domain.bpm.usecase.loan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

public class GetLoanEnquireTest
{
  private LoanService loanService;
  private GetLoanEnquire useCase;
  private GetLoanEnquireInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetLoanEnquire(loanService);
    input = new GetLoanEnquireInput("123", false);
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new GetLoanEnquire(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_cid_is_blank() throws UseCaseException
  {
    input.setCustomerCID("");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getLoanEnquire(input.getCustomerCID())).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(loanService.getLoanEnquire(input.getCustomerCID())).thenReturn(new LoanEnquire(LoanEnquireId.valueOf("123"), BorrowerId.valueOf("123")));
    useCase.execute(input);
    Assert.assertEquals(LoanEnquireId.valueOf("123"), BorrowerId.valueOf("123"));
  }
}
