/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.service_task.mongol_bank;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.consumption.exception.CamundaServiceException;
import mn.erin.bpms.loan.consumption.utils.MongolBankServiceUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanEnquire;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.ConfirmLoanEnquireInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquire;
import mn.erin.domain.bpm.usecase.loan.GetLoanEnquireInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfo;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoInput;
import mn.erin.domain.bpm.usecase.loan.GetLoanInfoOutput;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.CUSTOMER_CID_BY_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_RANK;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_NOT_FOUND;

/**
 * @author Tamir
 */
public class GetEnquireByCoBorrowerFromMongolBankTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetEnquireByCoBorrowerFromMongolBankTask.class);

  private final LoanService loanService;

  public GetEnquireByCoBorrowerFromMongolBankTask(LoanService loanService)
  {
    this.loanService = Objects.requireNonNull(loanService, "Loan service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    LOGGER.info("############# Downloads customer CID by Co-borrower from MONGOL BANK 5th service..............");

    String customerCidByCoBorrower = (String) execution.getVariable(CUSTOMER_CID_BY_CO_BORROWER);

    if (null == customerCidByCoBorrower)
    {
      return;
    }

    GetLoanEnquireInput getLoanEnquireInput = new GetLoanEnquireInput(customerCidByCoBorrower, true);
    GetLoanEnquire getLoanEnquire = new GetLoanEnquire(loanService);

    LoanEnquire loanEnquire = getLoanEnquire.execute(getLoanEnquireInput);

    String enquireId = loanEnquire.getId().getId();
    String borrowerId = loanEnquire.getBorrowerId().getId();

    if (LOAN_NOT_FOUND.equalsIgnoreCase(enquireId))
    {
      return;
    }

    ConfirmLoanEnquireInput input = new ConfirmLoanEnquireInput(enquireId, borrowerId, customerCidByCoBorrower);
    ConfirmLoanEnquire confirmLoanEnquire = new ConfirmLoanEnquire(loanService);

    Boolean isConfirmed = confirmLoanEnquire.execute(input);

    if (null != isConfirmed && !isConfirmed)
    {
      throw new CamundaServiceException("MONGOL BANK : Could not confirm mongol bank services.");
    }
    setLowestClassRank(execution, borrowerId, customerCidByCoBorrower);

    LOGGER.info("############# Successful downloaded customer CID by Co-borrower from MONGOL BANK 5th service.");
  }

  private void setLowestClassRank(DelegateExecution execution, String borrowerId, String customerCid) throws UseCaseException
  {
    List<Loan> loanList = getLoanList(borrowerId, customerCid);
    LoanClass loanClassByCoBorrower = MongolBankServiceUtils.getLowestLoanClass(loanList);
    Map<String, Object> variables = execution.getVariables();

    if (variables.containsKey(LOAN_CLASS_RANK))
    {
      Integer rank = (Integer) variables.get(LOAN_CLASS_RANK);
      Integer rankByCoBorrower = loanClassByCoBorrower.getRank();

      if (rank > rankByCoBorrower)
      {
        execution.removeVariable(LOAN_CLASS_RANK);
        execution.removeVariable(LOAN_CLASS_NAME);

        execution.setVariable(LOAN_CLASS_RANK, rankByCoBorrower);
        execution.setVariable(LOAN_CLASS_NAME, loanClassByCoBorrower.getName());
      }
    }
    else
    {
      execution.setVariable(LOAN_CLASS_RANK, loanClassByCoBorrower.getRank());
      execution.setVariable(LOAN_CLASS_RANK, loanClassByCoBorrower.getName());
    }
  }

  private List<Loan> getLoanList(String borrowerId, String customerCID) throws UseCaseException
  {
    GetLoanInfoInput getLoanInfoInput = new GetLoanInfoInput(borrowerId, customerCID);
    GetLoanInfo getLoanInfo = new GetLoanInfo(loanService);

    GetLoanInfoOutput output = getLoanInfo.execute(getLoanInfoInput);
    return output.getLoanList();
  }
}
