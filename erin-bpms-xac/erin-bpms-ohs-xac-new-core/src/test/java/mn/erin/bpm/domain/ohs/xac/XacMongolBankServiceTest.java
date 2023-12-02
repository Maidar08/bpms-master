/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.XacHttpConstants.LOAN_NOT_FOUND;

/**
 * @author Tamir
 */
public class XacMongolBankServiceTest
{
  private static final String SEARCH_REGISTER_NUMBER = "c.registerno";
  private static final String TEST_REG_NUM_VALUE = "TA97123184";
  private static final String BRANCH_NUMBER = "101";
  private static final String USER_ID = "test";
  private static final String USER_NAME = "test";

  private static final String SEARCH_TYPE_MONGOLIAN_CITIZEN = "01";
  private static final String SERVICE_URL = "http://wso2app-test.xac0000.mn:80/services/";
  public static final String SOURCE = "CAMUNDA";
  public static final String REQUEST_TYPE = "R";
  public static final String REQUEST_ID = "132";

  private Environment cidEnvironment;
  private Environment cidByCoBorrower;

  private Environment loanEnquireEnvironment;

  private Environment confirmEnvironment;
  private Environment loanInfoEnvironment;
  private Environment loanInfoPDFEnvironment;

  private AuthenticationService authenticationService;

  @Before
  public void setup()
  {
    this.authenticationService = Mockito.mock(AuthenticationService.class);
    Mockito.when(authenticationService.getCurrentUserId()).thenReturn("erin");

    this.cidEnvironment = Mockito.mock(Environment.class);
    Mockito.when(cidEnvironment.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(cidEnvironment.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(cidEnvironment.getProperty("wso2.header.getCustomerCID.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(cidEnvironment.getProperty("wso2.header.getCustomerCID.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(cidEnvironment.getProperty("wso2.header.getCustomerCID.function")).thenReturn("getXBTInfo");
    Mockito.when(cidEnvironment.getProperty("wso2.header.getCustomerCID.securityCode")).thenReturn("0D430ABF0133FC21ECBA0E1175B447DC");

    this.loanEnquireEnvironment = Mockito.mock(Environment.class);
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.header.getCustomerLoanEnquire.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.header.getCustomerLoanEnquire.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.header.getCustomerLoanEnquire.function")).thenReturn("getXBTCustInfo");
    Mockito.when(loanEnquireEnvironment.getProperty("wso2.header.getCustomerLoanEnquire.securityCode")).thenReturn("04D423DC317D2061B129A3BC2274D2CE");

    this.confirmEnvironment = Mockito.mock(Environment.class);
    Mockito.when(confirmEnvironment.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(confirmEnvironment.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(confirmEnvironment.getProperty("wso2.header.getXBTInfoDetailed.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(confirmEnvironment.getProperty("wso2.header.getXBTInfoDetailed.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(confirmEnvironment.getProperty("wso2.header.getXBTInfoDetailed.function")).thenReturn("getXBTInfoDetailed");
    Mockito.when(confirmEnvironment.getProperty("wso2.header.getXBTInfoDetailed.securityCode")).thenReturn("6F53834E648769F07B9433BF7EB6E126");

    this.loanInfoEnvironment = Mockito.mock(Environment.class);
    Mockito.when(loanInfoEnvironment.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(loanInfoEnvironment.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(loanInfoEnvironment.getProperty("wso2.header.getXBTLoanInfo.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(loanInfoEnvironment.getProperty("wso2.header.getXBTLoanInfo.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(loanInfoEnvironment.getProperty("wso2.header.getXBTLoanInfo.function")).thenReturn("getXBTLoanInfo");
    Mockito.when(loanInfoEnvironment.getProperty("wso2.header.getXBTLoanInfo.securityCode")).thenReturn("7C56A4C177FA6FE2D00441145BB35534");

    this.loanInfoPDFEnvironment = Mockito.mock(Environment.class);
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.header.getXBTInfoPdf.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.header.getXBTInfoPdf.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.header.getXBTInfoPdf.function")).thenReturn("getXBTInfoPdf");
    Mockito.when(loanInfoPDFEnvironment.getProperty("wso2.header.getXBTInfoPdf.securityCode")).thenReturn("CC337F74CD0CBBBA710147FBFD33B700");

    this.cidByCoBorrower = Mockito.mock(Environment.class);

    Mockito.when(cidByCoBorrower.getProperty("wso2.endpoint")).thenReturn(SERVICE_URL);
    Mockito.when(cidByCoBorrower.getProperty("wso2.header.source")).thenReturn(SOURCE);
    Mockito.when(cidByCoBorrower.getProperty("wso2.header.getXBTCustInfoSpl.requestType")).thenReturn(REQUEST_TYPE);
    Mockito.when(cidByCoBorrower.getProperty("wso2.header.getXBTCustInfoSpl.requestId")).thenReturn(REQUEST_ID);
    Mockito.when(cidByCoBorrower.getProperty("wso2.header.getXBTCustInfoSpl.function")).thenReturn("getXBTCustInfoSpl");
    Mockito.when(cidByCoBorrower.getProperty("wso2.header.getXBTCustInfoSpl.securityCode")).thenReturn("36C8DC8AA124CBCA7DDE681E08610C91");
  }

  @Ignore
  @Test
  public void verify_customer_loan_enquire() throws BpmServiceException
  {
    String customerCID = customerCIDService().getCustomerCID(SEARCH_REGISTER_NUMBER,
        TEST_REG_NUM_VALUE, SEARCH_TYPE_MONGOLIAN_CITIZEN, false, BRANCH_NUMBER, USER_ID, USER_NAME);

    LoanEnquire loanEnquire = customerLoanEnquireService().getLoanEnquire(customerCID);

    LoanEnquireId enquireId = loanEnquire.getId();
    BorrowerId borrowerId = loanEnquire.getBorrowerId();

    boolean isConfirmed = confirmService().confirmLoanEnquire(enquireId, borrowerId, customerCID);

    if (isConfirmed)
    {
      List<Loan> loanList = loanInfoService().getLoanList(customerCID, borrowerId);

      for (Loan loan : loanList)
      {
        Assert.assertNotNull(loan);
      }
    }

    LoanEnquire loanEnquireWithFile = loanInfoPDFService()
        .getLoanEnquireWithFile(enquireId, borrowerId, customerCID);

    Assert.assertNotNull(loanEnquireWithFile.getEnquireAsFile());
  }

  @Ignore
  @Test
  public void when_loan_info_not_found_by_co_borrower() throws BpmServiceException
  {
    LoanEnquire loanEnquire = customerCIDbyCoBorrowerService().getLoanEnquireByCoBorrower("1");

    Assert.assertNotNull(loanEnquire);
    Assert.assertEquals(LOAN_NOT_FOUND, loanEnquire.getId().getId());
    Assert.assertEquals(LOAN_NOT_FOUND, loanEnquire.getBorrowerId().getId());

  }


  @Ignore
  @Test
  public void when_loan_info_response_null() throws BpmServiceException
  {
    String customerCID = customerCIDService().getCustomerCID(SEARCH_REGISTER_NUMBER,
        TEST_REG_NUM_VALUE, SEARCH_TYPE_MONGOLIAN_CITIZEN, false, BRANCH_NUMBER, USER_ID, USER_NAME);

    LoanEnquire loanEnquire = customerCIDbyCoBorrowerService().getLoanEnquireByCoBorrower(customerCID);

    Assert.assertNotNull(loanEnquire);
    Assert.assertEquals(LOAN_NOT_FOUND, loanEnquire.getId().getId());
    Assert.assertEquals(LOAN_NOT_FOUND, loanEnquire.getBorrowerId().getId());

  }

  private XacMongolBankService customerCIDService()
  {
    return new XacMongolBankService(cidEnvironment, authenticationService);
  }

  private XacMongolBankService customerLoanEnquireService()
  {
    return new XacMongolBankService(loanEnquireEnvironment, authenticationService);
  }

  private XacMongolBankService confirmService()
  {
    return new XacMongolBankService(confirmEnvironment, authenticationService);
  }

  private XacMongolBankService loanInfoService()
  {
    return new XacMongolBankService(loanInfoEnvironment, authenticationService);
  }

  private XacMongolBankService loanInfoPDFService()
  {
    return new XacMongolBankService(loanInfoPDFEnvironment, authenticationService);
  }

  private XacMongolBankService customerCIDbyCoBorrowerService()
  {
    return new XacMongolBankService(cidByCoBorrower, authenticationService);
  }
}
