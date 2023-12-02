/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CoreBankingService;

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetCustomerByPersonIdTest
{
  private static final String REG_NO = "УП94051512";
  private static final String CIF_NUMBER = "12345678";

  private CoreBankingService coreBankingService;
  private GetCustomerByPersonId useCase;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetCustomerByPersonId(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new GetCustomerByPersonId(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_regNo_empty() throws UseCaseException
  {
    useCase.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_not_found() throws BpmServiceException, UseCaseException
  {
    when(coreBankingService.findCustomerByPersonId(REG_NO)).thenReturn(null);
    useCase.execute(REG_NO);
  }

  @Test(expected = UseCaseException.class)
  public void when_throw_service_exception() throws BpmServiceException, UseCaseException
  {
    when(coreBankingService.findCustomerByPersonId(REG_NO)).thenThrow(BpmServiceException.class);
    useCase.execute(REG_NO);
  }

  @Test
  public void when_successful_found_customer() throws BpmServiceException, UseCaseException
  {
    when(coreBankingService.findCustomerByPersonId(REG_NO)).thenReturn(new Customer(PersonId.valueOf(REG_NO), CIF_NUMBER));

    Customer output = useCase.execute(REG_NO);

    Assert.assertEquals(CIF_NUMBER, output.getCustomerNumber());
  }
}
