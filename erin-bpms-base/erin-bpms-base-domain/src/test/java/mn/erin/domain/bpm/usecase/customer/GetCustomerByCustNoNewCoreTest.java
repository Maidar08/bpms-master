package mn.erin.domain.bpm.usecase.customer;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

public class GetCustomerByCustNoNewCoreTest
{
  private static final String CIF_NUMBER = "123123";
  private static final String phoneNumber = "87654321";
  private static final String REG_NUMBER = "УУ00280232";

  private NewCoreBankingService newCoreBankingService;
  private GetCustomerByCustNoNewCore useCase;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetCustomerByCustNoNewCore(newCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerByCustNoNewCore(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(new HashMap<>());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_bpm_service_exception() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(BpmModuleConstants.CIF_NUMBER, CIF_NUMBER);
    input.put(PHONE_NUMBER, phoneNumber);
    Mockito.when(newCoreBankingService.findCustomerByCifNumber(input)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_customer_is_found() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(BpmModuleConstants.CIF_NUMBER, CIF_NUMBER);
    input.put(PHONE_NUMBER, phoneNumber);
    Mockito.when(newCoreBankingService.findCustomerByCifNumber(input)).thenReturn(getCustomerInfoMApResponse());
    Customer customer = useCase.execute(input);

    Assert.assertNotNull(customer);
    Assert.assertEquals(CIF_NUMBER, customer.getCustomerNumber());
  }

  private Map<String, Object> getCustomerInfoMApResponse()
  {
    Map<String, Object> customerMap = new HashMap<>();
    Customer customer = new Customer(PersonId.valueOf("123"));
    customer.setCustomerNumber(CIF_NUMBER);
    customerMap.put(CUSTOMER, customer);
    return customerMap;
  }
}
