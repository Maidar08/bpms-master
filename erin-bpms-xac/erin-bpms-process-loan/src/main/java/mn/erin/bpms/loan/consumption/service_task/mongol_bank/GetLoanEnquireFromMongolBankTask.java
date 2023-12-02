/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.exception.CamundaServiceException;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;

/**
 * @author Tamir
 */
public class GetLoanEnquireFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetLoanEnquireFromMongolBankTask.class);

  private final LoanService loanService;

  public GetLoanEnquireFromMongolBankTask(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("############# Downloading loan enquire from MONGOL BANK 2nd service..............");

    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    GetLoanEnquireInput input = new GetLoanEnquireInput(customerCID, false);
    GetLoanEnquire getLoanEnquire = new GetLoanEnquire(loanService);

    LoanEnquire loanEnquire = getLoanEnquire.execute(input);

    if (null == loanEnquire)
    {
      throw new CamundaServiceException("Customer loan enquire is null!");
    }

    String borrowerId = loanEnquire.getBorrowerId().getId();
    String enquireId = loanEnquire.getId().getId();

    execution.setVariable(ENQUIRE_ID, enquireId);
    execution.setVariable(BORROWER_ID, borrowerId);

    LOGGER.info("############# Successful downloaded loan enquire from MONGOL BANK 2nd service.");
  }
}
