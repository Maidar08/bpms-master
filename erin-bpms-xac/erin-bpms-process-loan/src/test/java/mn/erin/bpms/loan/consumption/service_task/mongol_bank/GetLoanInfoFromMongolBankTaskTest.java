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
import mn.erin.domain.bpm.model.loan.LoanId;
import mn.erin.domain.bpm.service.LoanService;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tamir
 */
public class GetLoanInfoFromMongolBankTaskTest
{

  private static final String VALUE_CUST_CID = "321456";
  private LoanService loanService;
  private DelegateExecution delegateExecution;

  private GetLoanInfoFromMongolBankTask getLoanInfoFromMongolBankTask;

  @Before
  public void setUp()
  {
    loanService = Mockito.mock(LoanService.class);
    delegateExecution = Mockito.mock(DelegateExecution.class);

    getLoanInfoFromMongolBankTask = new GetLoanInfoFromMongolBankTask(loanService);
  }

  @Test
  public void when_successful_get_loan_info() throws Exception
  {
    Map<String, Object> properties = getProperties("e1", "b1", VALUE_CUST_CID);

    Mockito.when(delegateExecution.getVariables()).thenReturn(properties);

    when(delegateExecution.getVariable(ENQUIRE_ID)).thenReturn("e1");
    when(delegateExecution.getVariable(BORROWER_ID)).thenReturn("b1");
    when(delegateExecution.getVariable(CUSTOMER_CID)).thenReturn(VALUE_CUST_CID);

    when(loanService.getLoanList(VALUE_CUST_CID, BorrowerId.valueOf("b1"))).thenReturn(getLoanList());

    getLoanInfoFromMongolBankTask.execute(delegateExecution);

    verify(delegateExecution, times(1)).setVariable(LOAN_CLASS_RANK, 1);
    verify(delegateExecution, times(1)).setVariable(LOAN_CLASS_NAME, "ABNORMAL");
  }

  private List<Loan> getLoanList()
  {
    List<Loan> loanList = new ArrayList<>();

    loanList.add(new Loan(LoanId.valueOf("l1"), new LoanClass(1, "ABNORMAL")));
    loanList.add(new Loan(LoanId.valueOf("l2"), new LoanClass(2, "NORMAL")));

    return loanList;
  }

  private Map<String, Object> getProperties(String enquireId, String borrowerId, String custCID)
  {
    Map<String, Object> properties = new HashMap<>();

    properties.put(ENQUIRE_ID, enquireId);
    properties.put(BORROWER_ID, borrowerId);
    properties.put(CUSTOMER_CID, custCID);

    return properties;
  }
}
