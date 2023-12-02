package mn.erin.domain.bpm.usecase.customer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Bilguunbor
 */
public class GetCustomerAddressInfoTest
{
  private CustomerService customerService;
  private GetCustomerAddressInfo useCase;
  private GetCustomerIDCardInfoInput input;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);
    useCase = new GetCustomerAddressInfo(customerService);
    input = new GetCustomerIDCardInfoInput("reg", "reg1");
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerAddressInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerAddress("reg", "reg1")).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }
}
