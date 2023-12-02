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

import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireWithFile;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireWithFileInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_ENQUIRE_PDF_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;

/**
 * @author Tamir
 */
public class GetLoanEnquirePdfFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetLoanEnquirePdfFromMongolBankTask.class);

  private final LoanService loanService;

  public GetLoanEnquirePdfFromMongolBankTask(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.error("############# Downloading loan enquire PDF from Mongol bank 6th service..............");

    String loanEnquireId = (String) execution.getVariable(ENQUIRE_ID);

    if (loanEnquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
    {
      return;
    }

    String borrowerId = (String) execution.getVariable(BORROWER_ID);
    String customerCID = (String) execution.getVariable(CUSTOMER_CID);

    GetLoanEnquireWithFileInput input = new GetLoanEnquireWithFileInput(loanEnquireId, borrowerId, customerCID);
    GetLoanEnquireWithFile getLoanEnquireWithFile = new GetLoanEnquireWithFile(loanService);

    LoanEnquire loanEnquire = getLoanEnquireWithFile.execute(input);
    byte[] enquireAsFile = loanEnquire.getEnquireAsFile();

    execution.setVariable(LOAN_ENQUIRE_PDF_CO_BORROWER, enquireAsFile);

    LOGGER.error("############# Successful downloaded loan enquire PDF from Mongol bank 6th service");
  }
}
