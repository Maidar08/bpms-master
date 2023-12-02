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

import mn.erin.bpms.loan.consumption.exception.CamundaServiceException;
import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetLoanEnquireFromMongolBankTaskTest
{
  private static final String VALUE_CUSTOMER_CID = "32456";

  private LoanService loanService;
  private DelegateExecution delegateExecution;

  private GetLoanEnquireFromMongolBankTask getLoanEnquireFromMongolBankTask;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    delegateExecution = Mockito.mock(DelegateExecution.class);

    getLoanEnquireFromMongolBankTask = new GetLoanEnquireFromMongolBankTask(loanService);
  }

  @Test(expected = CamundaServiceException.class)
  public void when_null_loan_enquire() throws Exception
  {
    when(delegateExecution.getVariables()).thenReturn(getProperties(VALUE_CUSTOMER_CID));
    when(delegateExecution.getVariable(CUSTOMER_CID)).thenReturn(VALUE_CUSTOMER_CID);

    when(loanService.getLoanEnquire(VALUE_CUSTOMER_CID)).thenReturn(null);

    getLoanEnquireFromMongolBankTask.execute(delegateExecution);
  }

  @Test
  public void when_successful_download_loan_enquire() throws Exception
  {
    when(delegateExecution.getVariables()).thenReturn(getProperties(VALUE_CUSTOMER_CID));
    when(delegateExecution.getVariable(CUSTOMER_CID)).thenReturn(VALUE_CUSTOMER_CID);

    when(loanService.getLoanEnquire(VALUE_CUSTOMER_CID)).thenReturn(getLoanEnquire("e1", "b1"));

    getLoanEnquireFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(ENQUIRE_ID, "e1");
    verify(delegateExecution, times(1)).setVariable(BORROWER_ID, "b1");
  }

  private Map<String, Object> getProperties(String customerCID)
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put(CUSTOMER_CID, customerCID);

    return properties;
  }

  private LoanEnquire getLoanEnquire(String enquireId, String borrowerId)
  {
    return new LoanEnquire(LoanEnquireId.valueOf(enquireId), BorrowerId.valueOf(borrowerId));
  }
}
