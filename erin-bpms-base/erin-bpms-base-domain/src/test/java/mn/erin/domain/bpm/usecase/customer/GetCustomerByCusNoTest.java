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

/**
 * @author Tamir
 */
public class GetCustomerByCusNoTest
{
  public static final String CIF_NUMBER = "12345678";

  private CoreBankingService coreBankingService;
  private GetCustomerByCustNo useCase;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(CoreBankingService.class);
    useCase = new GetCustomerByCustNo(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetCustomerByCustNo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throw_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.findCustomerByCifNumber(CIF_NUMBER)).thenThrow(BpmServiceException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Test(expected = UseCaseException.class)
  public void when_customer_not_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.findCustomerByCifNumber(CIF_NUMBER)).thenReturn(null);
    useCase.execute(CIF_NUMBER);
  }

  @Test
  public void when_successful_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(coreBankingService.findCustomerByCifNumber(CIF_NUMBER)).thenReturn(new Customer(PersonId.valueOf("regNo")));
    Customer customer = useCase.execute(CIF_NUMBER);
    Assert.assertEquals("regNo", customer.getId().getId());
  }
}
