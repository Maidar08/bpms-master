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
public class GetCustomerResourceTest
{
  private static final String CIF_NUMBER = "12345678";

  private CoreBankingService coreBankingService;
  private GetCustomerResource useCase;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetCustomerResource(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerResource(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_blank() throws UseCaseException
  {
    useCase.execute(" ");
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getCustomerResource(CIF_NUMBER)).thenThrow(BpmServiceException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Test
  public void when_customer_resource_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.getCustomerResource(CIF_NUMBER)).thenReturn("123");
    Double output = useCase.execute(CIF_NUMBER);

    Assert.assertEquals(Double.valueOf(123), output);
  }
}
