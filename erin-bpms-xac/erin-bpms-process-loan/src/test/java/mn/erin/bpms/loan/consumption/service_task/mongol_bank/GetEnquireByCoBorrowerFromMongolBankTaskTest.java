/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.bpm.model.loan.BorrowerId;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.model.loan.LoanEnquireId;
import mn.erin.domain.bpm.model.loan.LoanId;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_BORROWER_ID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_CUSTOMER_CID;
import static mn.erin.bpms.loan.consumption.service_task.mongol_bank.ServiceTaskTestConstants.VALUE_ENQUIRE_ID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetEnquireByCoBorrowerFromMongolBankTaskTest
{
  private LoanService loanService;
  private DelegateExecution delegateExecution;
  private GetEnquireByCoBorrowerFromMongolBankTask getEnquireByCoBorrowerFromMongolBankTask;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    delegateExecution = Mockito.mock(DelegateExecution.class);

    getEnquireByCoBorrowerFromMongolBankTask = new GetEnquireByCoBorrowerFromMongolBankTask(loanService);
  }

  @Test
  public void when_null_customer_cid_by_co_borrower() throws Exception
  {
    when(delegateExecution.getVariable(CUSTOMER_CID_BY_CO_BORROWER)).thenReturn(null);

    getEnquireByCoBorrowerFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(0)).setVariable(LOAN_CLASS_RANK, 1);
  }

  @Test
  public void when_successful_download_loan_enquire() throws Exception
  {
    when(delegateExecution.getVariable(CUSTOMER_CID_BY_CO_BORROWER)).thenReturn(VALUE_CUSTOMER_CID);

    when(loanService.getLoanEnquireByCoBorrower(VALUE_CUSTOMER_CID)).thenReturn(getLoanEnquire(VALUE_ENQUIRE_ID, VALUE_BORROWER_ID));

    LoanEnquireId loanEnquireId = LoanEnquireId.valueOf(VALUE_ENQUIRE_ID);
    BorrowerId borrowerId = BorrowerId.valueOf(VALUE_BORROWER_ID);

    when(loanService.confirmLoanEnquire(loanEnquireId, borrowerId, VALUE_CUSTOMER_CID)).thenReturn(Boolean.TRUE);
    when(loanService.getLoanList(VALUE_CUSTOMER_CID, borrowerId)).thenReturn(getLoanList());

    Map<String, Object> propWithClassRank = new HashMap<>();

    propWithClassRank.put(LOAN_CLASS_RANK, 5);

    when(delegateExecution.getVariables()).thenReturn(propWithClassRank);

    when(delegateExecution.hasVariable(LOAN_CLASS_RANK)).thenReturn(true);
    when(delegateExecution.getVariable(LOAN_CLASS_RANK)).thenReturn(5);

    getEnquireByCoBorrowerFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).removeVariable(LOAN_CLASS_RANK);
    verify(delegateExecution, times(1)).removeVariable(LOAN_CLASS_NAME);

    verify(delegateExecution, times(1)).setVariable(LOAN_CLASS_RANK, 1);
    verify(delegateExecution, times(1)).setVariable(LOAN_CLASS_NAME, "ABNORMAL");
  }

  private LoanEnquire getLoanEnquire(String enquireId, String borrowerId)
  {
    return new LoanEnquire(LoanEnquireId.valueOf(enquireId), BorrowerId.valueOf(borrowerId));
  }

  private List<Loan> getLoanList()
  {
    List<Loan> loanList = new ArrayList<>();

    loanList.add(new Loan(LoanId.valueOf("l1"), new LoanClass(1, "ABNORMAL")));
    loanList.add(new Loan(LoanId.valueOf("l2"), new LoanClass(2, "NORMAL")));

    return loanList;
  }
}
