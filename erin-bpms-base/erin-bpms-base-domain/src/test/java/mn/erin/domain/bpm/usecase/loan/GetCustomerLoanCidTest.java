package mn.erin.domain.bpm.usecase.loan;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

public class GetCustomerLoanCidTest
{
  private LoanService loanService;
  private GetCustomerLoanCid useCase;
  private GetCustomerLoanCidInput input;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetCustomerLoanCid(loanService);
    input = new GetCustomerLoanCidInput("123", "123", "123", false, "123", "123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_service_is_null()
  {
    new GetCustomerLoanCid(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws BpmServiceException, UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(loanService.getCustomerCID("123", "123", "123", false, "123", "123", "123")).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws UseCaseException, BpmServiceException
  {
    Mockito.when(loanService.getCustomerCID("123", "123", "123", true, "123", "123", "123")).thenReturn("123", "123");
    useCase.execute(input);
    Assert.assertEquals("123", "123", "123");
  }
}
