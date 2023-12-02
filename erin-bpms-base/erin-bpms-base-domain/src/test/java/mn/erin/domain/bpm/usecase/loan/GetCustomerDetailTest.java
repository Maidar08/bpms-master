package mn.erin.domain.bpm.usecase.loan;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.LoanService;

/**
 * @author Lkhagvadorj.A
 **/

public class GetCustomerDetailTest
{
  private LoanService loanService;
  private GetCustomerDetail useCase;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    useCase = new GetCustomerDetail(loanService);
  }

  @Test(expected = UseCaseException.class)
  public void whenUseCaseException() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void whenSuccessfulGetCustomerDetail() throws BpmServiceException, UseCaseException
  {
    GetCustomerRelatedInfoInput input = new GetCustomerRelatedInfoInput("123", "123");
    useCase.execute(input);
    Mockito.verify(loanService, Mockito.atLeast(1)).getCustomerDetail("123");
  }
}
