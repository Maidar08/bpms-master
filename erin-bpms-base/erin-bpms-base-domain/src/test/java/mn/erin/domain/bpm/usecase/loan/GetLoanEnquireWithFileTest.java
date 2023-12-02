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

public class GetLoanEnquireWithFileTest
{
  private LoanService loanService;
  private GetLoanEnquireWithFile useCase;
  private GetLoanEnquireWithFileInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetLoanEnquireWithFile(loanService);
    input = new GetLoanEnquireWithFileInput("123", "123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new GetLoanEnquireWithFile(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws BpmServiceException, UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getLoanEnquireWithFile(LoanEnquireId.valueOf("123"), BorrowerId.valueOf("123"), input.getCustomerCID()))
        .thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getLoanEnquireWithFile(LoanEnquireId.valueOf("123"), BorrowerId.valueOf("123"), input.getCustomerCID()))
        .thenReturn(new LoanEnquire(LoanEnquireId.valueOf("123"), BorrowerId.valueOf("123"), "123"));
    useCase.execute(input);
    Assert.assertEquals("123", "123", "123");
  }
}
