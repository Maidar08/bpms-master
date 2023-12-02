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
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.IS_CONFIRMED_LOAN_ENQUIRE;
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
public class ConfirmLoanEnquireFromMongolBankTaskTest
{
  private LoanService loanService;
  private DelegateExecution delegateExecution;
  private ConfirmLoanEnquireFromMongolBankTask confirmLoanEnquireFromMongolBankTask;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    delegateExecution = Mockito.mock(DelegateExecution.class);

    confirmLoanEnquireFromMongolBankTask = new ConfirmLoanEnquireFromMongolBankTask(loanService);
  }

  @Test
  public void when_enquire_id_is_not_found_loan() throws Exception
  {
    Map<String, Object> properties = new HashMap<>();
    properties.put(ENQUIRE_ID, LOAN_NOT_FOUND);

    when(delegateExecution.getVariables()).thenReturn(properties);
    when(delegateExecution.getVariable(ENQUIRE_ID)).thenReturn(LOAN_NOT_FOUND);
    confirmLoanEnquireFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(0)).setVariable(IS_CONFIRMED_LOAN_ENQUIRE, true);
  }

  @Test(expected = CamundaServiceException.class)
  public void when_confirmed_false() throws Exception
  {
    mockGetVariables();

    LoanEnquireId loanEnquireId = LoanEnquireId.valueOf(VALUE_ENQUIRE_ID);
    BorrowerId borrowerId = BorrowerId.valueOf(VALUE_BORROWER_ID);

    when(loanService.confirmLoanEnquire(loanEnquireId, borrowerId, VALUE_CUSTOMER_CID)).thenReturn(Boolean.FALSE);
    confirmLoanEnquireFromMongolBankTask.execute(delegateExecution);
  }

  @Test
  public void when_confirmed_true() throws Exception
  {
    mockGetVariables();

    LoanEnquireId loanEnquireId = LoanEnquireId.valueOf(VALUE_ENQUIRE_ID);
    BorrowerId borrowerId = BorrowerId.valueOf(VALUE_BORROWER_ID);

    when(loanService.confirmLoanEnquire(loanEnquireId, borrowerId, VALUE_CUSTOMER_CID)).thenReturn(Boolean.TRUE);
    confirmLoanEnquireFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(IS_CONFIRMED_LOAN_ENQUIRE, true);
  }

  private void mockGetVariables()
  {
    when(delegateExecution.getVariables()).thenReturn(getValidProperties(VALUE_ENQUIRE_ID, VALUE_BORROWER_ID, VALUE_CUSTOMER_CID));
    when(delegateExecution.getVariable(ENQUIRE_ID)).thenReturn(VALUE_ENQUIRE_ID);
    when(delegateExecution.getVariable(BORROWER_ID)).thenReturn(VALUE_BORROWER_ID);
    when(delegateExecution.getVariable(CUSTOMER_CID)).thenReturn(VALUE_CUSTOMER_CID);
  }

  private Map<String, Object> getValidProperties(String enquireId, String borrowerId, String custCID)
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put(ENQUIRE_ID, enquireId);
    properties.put(BORROWER_ID, borrowerId);
    properties.put(CUSTOMER_CID, custCID);

    return properties;
  }
}
