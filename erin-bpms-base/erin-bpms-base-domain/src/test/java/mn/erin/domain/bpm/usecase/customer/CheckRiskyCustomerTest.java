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

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */

public class CheckRiskyCustomerTest
{
  private static final String REG_NO = "УП96121115";

  private NewCoreBankingService coreBankingService;
  private CheckRiskyCustomer useCase;

  @Before
  public void setUp()
  {
    coreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new CheckRiskyCustomer(coreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new CheckRiskyCustomer(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_person_id_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_person_id_blank() throws UseCaseException
  {
    useCase.execute(" ");
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    when(coreBankingService.checkRiskyCustomer(REG_NO)).thenThrow(BpmServiceException.class);
    useCase.execute(REG_NO);
  }

  @Test
  public void when_found_risky_customer_info() throws BpmServiceException, UseCaseException
  {
    when(coreBankingService.checkRiskyCustomer(REG_NO)).thenReturn(true);

    Boolean isRisky = useCase.execute(REG_NO);

    Assert.assertTrue(isRisky);
  }
}
