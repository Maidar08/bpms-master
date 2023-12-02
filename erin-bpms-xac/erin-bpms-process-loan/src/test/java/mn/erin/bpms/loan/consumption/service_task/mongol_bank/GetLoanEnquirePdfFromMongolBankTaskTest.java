/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_BORROWER_ID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_CUSTOMER_CID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_ENQUIRE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetLoanEnquirePdfFromMongolBankTaskTest
{
  private LoanService loanService;

  private GetLoanEnquirePdfFromMongolBankTask getLoanEnquirePdfFromMongolBankTask;
  private DelegateExecution delegateExecution;

  @Before
  public void setUp() throws Exception
  {
    loanService = Mockito.mock(LoanService.class);
    delegateExecution = Mockito.mock(DelegateExecution.class);

    getLoanEnquirePdfFromMongolBankTask = new GetLoanEnquirePdfFromMongolBankTask(loanService);
  }

  @Test
  public void when_loan_not_found() throws Exception
  {
    Map<String, Object> properties = new HashMap<>();
    properties.put(ENQUIRE_ID, LOAN_NOT_FOUND);

    when(delegateExecution.getVariables()).thenReturn(properties);
    when(delegateExecution.getVariable(ENQUIRE_ID)).thenReturn(LOAN_NOT_FOUND);

    getLoanEnquirePdfFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(0)).setVariable(LOAN_ENQUIRE_PDF_CO_BORROWER, getByteArray());
  }

  @Test
  public void verify_successful_download_pdf_as_base64() throws Exception
  {
    mockGetVariables();

    LoanEnquireId loanEnquireId = LoanEnquireId.valueOf(VALUE_ENQUIRE_ID);
    BorrowerId borrowerId = BorrowerId.valueOf(VALUE_BORROWER_ID);

    when(loanService.getLoanEnquireWithFile(loanEnquireId, borrowerId, VALUE_CUSTOMER_CID)).thenReturn(getLoanEnquireWithFile());

    getLoanEnquirePdfFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(LOAN_ENQUIRE_PDF_CO_BORROWER, getByteArray());
  }

  private void mockGetVariables()
  {
    when(delegateExecution.getVariables()).thenReturn(getProperties(VALUE_ENQUIRE_ID, VALUE_BORROWER_ID, VALUE_CUSTOMER_CID));
    when(delegateExecution.getVariable(ENQUIRE_ID)).thenReturn(VALUE_ENQUIRE_ID);
    when(delegateExecution.getVariable(BORROWER_ID)).thenReturn(VALUE_BORROWER_ID);
    when(delegateExecution.getVariable(CUSTOMER_CID)).thenReturn(VALUE_CUSTOMER_CID);
  }

  private Map<String, Object> getProperties(String enquireId, String borrowerId, String custCID)
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put(ENQUIRE_ID, enquireId);
    properties.put(BORROWER_ID, borrowerId);
    properties.put(CUSTOMER_CID, custCID);

    return properties;
  }

  private LoanEnquire getLoanEnquireWithFile()
  {
    LoanEnquire loanEnquire = new LoanEnquire(LoanEnquireId.valueOf(VALUE_ENQUIRE_ID), BorrowerId.valueOf(VALUE_BORROWER_ID));

    loanEnquire.setEnquireAsFile(getByteArray());

    return loanEnquire;
  }

  private byte[] getByteArray()
  {
    return "base64String".getBytes();
  }
}
