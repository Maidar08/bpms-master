package mn.erin.domain.bpm.usecase.customer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Bilguunbor
 */
public class GetCustomerIDCardInfoTest
{
  private static final String REG_1 = "УП94051512";
  private static final String REG_2 = "УП94051513";
  private static final String FINGER_PRINT_1 = "finger1";
  private static final String FINGER_PRINT_2 = "finger2";
  private static final String CIF_NUMBER = "12345678";

  private CustomerService customerService;
  private GetCustomerIDCardInfo useCase;
  private GetCustomerIDCardInfoInput input;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);
    useCase = new GetCustomerIDCardInfo(customerService);
    input = new GetCustomerIDCardInfoInput(REG_1, REG_2);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerIDCardInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerInfo(REG_1, REG_2))
        .thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_customer_info_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerInfo(REG_1, REG_2))
        .thenReturn((new Customer(PersonId.valueOf(FINGER_PRINT_1), CIF_NUMBER)));
    Customer output = useCase.execute(input);

    Assert.assertEquals(FINGER_PRINT_1, output.getId().getId());
  }
}
