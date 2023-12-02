package mn.erin.domain.bpm.usecase.customer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetCustomerByPersonIdAndTypeTest
{
  private static final String CUST_TYPE = "Retail";
  private static final String REG_NUM = "УУ00280232";

  private NewCoreBankingService newCoreBankingService;
  private GetCustomerByPersonIdAndType useCase;
  private GetCustomerByPersonIdAndTypeInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetCustomerByPersonIdAndType(newCoreBankingService);
    input = new GetCustomerByPersonIdAndTypeInput("", CUST_TYPE, REG_NUM);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerByPersonIdAndType(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_register_number_is_null() throws UseCaseException
  {
    useCase.execute(new GetCustomerByPersonIdAndTypeInput("", CUST_TYPE, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_type_is_null() throws UseCaseException
  {
    useCase.execute(new GetCustomerByPersonIdAndTypeInput("", null, REG_NUM));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_bpm_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.findCustomerByPersonIdAndType(REG_NUM, CUST_TYPE)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_customer_is_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.findCustomerByPersonIdAndType(REG_NUM, CUST_TYPE)).thenReturn(new Customer(PersonId.valueOf(REG_NUM),""));
    Customer customer = useCase.execute(input);

    Assert.assertNotNull(customer);
    Assert.assertEquals(REG_NUM, customer.getId().getId());
  }

  @Test(expected = UseCaseException.class)
  public void when_service_returns_null() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.findCustomerByPersonIdAndType(REG_NUM, CUST_TYPE)).thenReturn(null);
    useCase.execute(input);
  }
}
