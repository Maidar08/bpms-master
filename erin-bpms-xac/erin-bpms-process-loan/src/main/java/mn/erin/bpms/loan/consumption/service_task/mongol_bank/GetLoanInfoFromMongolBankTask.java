/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.List;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfo;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoOutput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.BORROWER_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.ENQUIRE_ID;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;
import static mn.erin.bpms.loan.consumption.utils.MongolBankServiceUtils.getLowestLoanClass;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_CID;

/**
 * @author Tamir
 */
public class GetLoanInfoFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetLoanInfoFromMongolBankTask.class);

  private final LoanService loanService;

  public GetLoanInfoFromMongolBankTask(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("############# Downloading loan infos from MONGOL BANK 4th service ..............");

    String enquireId = (String) execution.getVariable(ENQUIRE_ID);

    if (enquireId.equalsIgnoreCase(LOAN_NOT_FOUND))
    {
      return;
    }

    String customerCID = (String) execution.getVariable(CUSTOMER_CID);
    String borrowerId = (String) execution.getVariable(BORROWER_ID);

    GetLoanInfoInput input = new GetLoanInfoInput(borrowerId, customerCID);
    GetLoanInfo getLoanInfo = new GetLoanInfo(loanService);

    GetLoanInfoOutput output = getLoanInfo.execute(input);

    List<Loan> loanList = output.getLoanList();
    LoanClass lowestLoanClass = getLowestLoanClass(loanList);

    Integer rank = lowestLoanClass.getRank();
    String loanClassName = lowestLoanClass.getName();

    execution.setVariable(LOAN_CLASS_RANK, rank);
    execution.setVariable(LOAN_CLASS_NAME, loanClassName);

    LOGGER.info("############# Successful downloaded loan infos from MONGOL BANK  4th service.");
  }
}
