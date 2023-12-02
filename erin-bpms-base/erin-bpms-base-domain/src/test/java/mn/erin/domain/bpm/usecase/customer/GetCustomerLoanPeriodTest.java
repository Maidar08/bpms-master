package mn.erin.domain.bpm.usecase.customer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;


/**
 * @author Bilguunbor
 */
public class GetCustomerLoanPeriodTest
{
  private static final String CIF_NUMBER = "12345678";

  private CoreBankingService coreBankingService;
  private GetCustomerLoanPeriod useCase;
  private GetCustomerLoanPeriodInput input;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetCustomerLoanPeriod(coreBankingService);
    input = new GetCustomerLoanPeriodInput(CIF_NUMBER);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerLoanPeriod(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_cif_number_is_blank() throws UseCaseException
  {
    useCase.execute(new GetCustomerLoanPeriodInput(""));
  }

  @Test(expected = UseCaseException.class)
  public void when_cif_number_is_null() throws UseCaseException
  {
    useCase.execute(new GetCustomerLoanPeriodInput(null));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getCustomerLoanPeriodInformation(CIF_NUMBER)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_loan_period_info_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getCustomerLoanPeriodInformation(CIF_NUMBER)).thenReturn(123);
    int output = useCase.execute(input);

    Assert.assertEquals(123, output);
  }
}
