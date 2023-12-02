/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.request.webapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpms.loan.request.webapp.controller.RestLoanRequest;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;

/**
 * @author Tamir
 */
public class LoanRequestRestUtilTest
{
  private static final String TENANT_ID = "xac";
  private static final String AUTHENTICATION_STRING = "Basic ZWJhbms6c2VjcmV0";

  private static final String EBANK = "ebank";
  private static final String PASS_WORD = "secret";
  private static final String TOKEN = "token";
  private static final String IS_AUTHENTICATED = "isAuthenticated";

  private AuthenticationService authenticationService;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
  }

  @Test
  public void whenIsAuthenticatedTrue()
  {
    Mockito.when(authenticationService.authenticate(TENANT_ID, EBANK, PASS_WORD, false)).thenReturn(TOKEN);
    Map<String, Object> authenticatedUser = LoanRequestRestUtil.isAuthenticatedUser(authenticationService, AUTHENTICATION_STRING);
    boolean isAuthenticated = (boolean) authenticatedUser.get(IS_AUTHENTICATED);
    Assert.assertTrue(isAuthenticated);
  }

  @Test
  public void whenIsAuthenticatedFalse()
  {
    Mockito.when(authenticationService.authenticate(TENANT_ID, EBANK, PASS_WORD)).thenReturn(null);
    Map<String, Object> authenticatedUser = LoanRequestRestUtil.isAuthenticatedUser(authenticationService, AUTHENTICATION_STRING);
    boolean isAuthenticated = (boolean) authenticatedUser.get(IS_AUTHENTICATED);
    Assert.assertFalse(isAuthenticated);
  }

  @Test
  public void whenThrowsExceptionDuringAuth()
  {
    Mockito.when(authenticationService.authenticate(TENANT_ID, EBANK, PASS_WORD)).thenThrow(RuntimeException.class);
    Map<String, Object> authenticatedUser = LoanRequestRestUtil.isAuthenticatedUser(authenticationService, AUTHENTICATION_STRING);
    boolean isAuthenticated = (boolean) authenticatedUser.get(IS_AUTHENTICATED);
    Assert.assertFalse(isAuthenticated);
  }

  @Test
  public void whenRegNumberIsOnlyDigits()
  {
    boolean isDigit0 = LoanRequestRestUtil.isDigits("12345678");
    boolean isDigit1 = LoanRequestRestUtil.isDigits("-12345678");
    boolean isDigit2 = LoanRequestRestUtil.isDigits("СЩ12345678");

    Assert.assertTrue(isDigit0);
    Assert.assertFalse(isDigit1);
    Assert.assertFalse(isDigit2);
  }

  @Test
  public void verifySortDescendingSuccessful() throws BpmInvalidArgumentException
  {
    Collection<RestLoanRequest> sortedRestLoanRequests = LoanRequestRestUtil.descSortByCreatedDate(getTestLoanRequests());
    Assert.assertEquals(3, sortedRestLoanRequests.size());
  }

  @Test(expected = BpmInvalidArgumentException.class)
  public void verifySortDescendingFailed() throws BpmInvalidArgumentException
  {
    LoanRequestRestUtil.descSortByCreatedDate(getLoanRequestsWithInvalidDate());
  }

  private Collection<RestLoanRequest> getTestLoanRequests()
  {
    Collection<RestLoanRequest> restLoanRequests = new ArrayList<>();

    RestLoanRequest restLoanRequest = createRestLoanRequest("1", "2021-06-31");
    RestLoanRequest restLoanRequest2 = createRestLoanRequest("4", "2020-06-11");
    RestLoanRequest restLoanRequest3 = createRestLoanRequest("7", "2021-05-31");

    restLoanRequests.add(restLoanRequest);
    restLoanRequests.add(restLoanRequest2);
    restLoanRequests.add(restLoanRequest3);

    return restLoanRequests;
  }

  private Collection<RestLoanRequest> getLoanRequestsWithInvalidDate()
  {
    Collection<RestLoanRequest> restLoanRequests = new ArrayList<>();

    RestLoanRequest restLoanRequest = createRestLoanRequest("1", "232-06-12");
    RestLoanRequest restLoanRequest1 = createRestLoanRequest("3", "22-JUN-20");

    restLoanRequests.add(restLoanRequest);
    restLoanRequests.add(restLoanRequest1);

    return restLoanRequests;
  }

  private RestLoanRequest createRestLoanRequest(String id, String createdDate)
  {
    RestLoanRequest restLoanRequest = new RestLoanRequest();

    restLoanRequest.setId(id);
    restLoanRequest.setCreatedDate(createdDate);

    return restLoanRequest;
  }

  @Ignore
  @Test(expected = IllegalArgumentException.class)
  public void whenInvalidOrganizationRegNumber()
  {
    LoanRequestRestUtil.validateOrganizationRegNumber("123456789");
  }

  @Test
  public void whenValidOrganizationRegNumber()
  {
    LoanRequestRestUtil.validateOrganizationRegNumber("87654321");
  }
}
