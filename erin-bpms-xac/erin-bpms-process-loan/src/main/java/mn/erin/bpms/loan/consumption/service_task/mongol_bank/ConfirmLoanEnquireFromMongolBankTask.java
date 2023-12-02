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
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquireInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.IS_CONFIRMED_LOAN_ENQUIRE;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;

/**
 * @author Tamir
 */
public class ConfirmLoanEnquireFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmLoanEnquireFromMongolBankTask.class);
  private final LoanService loanService;

  public ConfirmLoanEnquireFromMongolBankTask(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("############# Confirms MONGOL BANK 3nd service..............");
    String enquireId = (String) execution.getVariable(ENQUIRE_ID);

    if (enquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
    {
      return;
    }

    String borrowerId = (String) execution.getVariable(BORROWER_ID);
    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    ConfirmLoanEnquireInput input = new ConfirmLoanEnquireInput(enquireId, borrowerId, customerCID);
    ConfirmLoanEnquire confirmLoanEnquire = new ConfirmLoanEnquire(loanService);

    Boolean isConfirmed = confirmLoanEnquire.execute(input);

    if (null != isConfirmed && !isConfirmed)
    {
      throw new CamundaServiceException("MONGOL BANK : Could not confirm mongol bank services.");
    }

    execution.setVariable(IS_CONFIRMED_LOAN_ENQUIRE, true);

    LOGGER.info("############# Successful confirmed MONGOL BANK 3nd service.");
  }
}
