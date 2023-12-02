package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static consumption.util.CamundaUtils.getDisburseTransactionParam;
import static consumption.util.CamundaUtils.toLoanDisbursement;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DEFAULT_ACCOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TOTAL_CLOSING_AMOUNT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;

public class DisburseToDefaultAccountTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DisburseToDefaultAccountTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final Environment environment;

  public DisburseToDefaultAccountTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, Environment environment)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    try
    {
      String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      String defaultAccount = String.valueOf(execution.getVariable(DEFAULT_ACCOUNT));
      String totalClosingLoanAmountString = String.valueOf(execution.getVariable(TOTAL_CLOSING_AMOUNT));
      BigDecimal totalClosingLoanAmount = (removeCommaAndGetBigDecimal(totalClosingLoanAmountString));
      String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
      String acceptedAmountString = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
      BigDecimal requestedLoanAmount= (BigDecimal) execution.getVariable("requestedLoanAmount");
      BigDecimal acceptedAmount =  processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE) && requestedLoanAmount != null ?
          requestedLoanAmount : removeCommaAndGetBigDecimal(acceptedAmountString);
      BigDecimal totalAmount = totalClosingLoanAmount.add(acceptedAmount);
      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      String disburseCurrency = String.valueOf(execution.getVariable("disbursementSttlCcy"));
      String accCurrency = String.valueOf(execution.getVariable("accCurrency"));
      String branchId = String.valueOf(execution.getVariable(BRANCH_NUMBER));
      if (branchId.equals(CHO_BRANCH))
      {
        branchId = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER), "Could not get CHO branch number from config file!");
      }

      final Map<String, Object> partTransactions = getDisburseTransactionParam(defaultAccount, accCurrency, totalAmount);
      Map<String, Object> requestBody = toLoanDisbursement(loanAccountNumber, branchId, totalAmount, disburseCurrency, partTransactions);

      final Map<String, Object> response = directOnlineCoreBankingService.createLoanDisbursement(requestBody);
      final String status = String.valueOf(response.get("status"));
      if (status.equals("SUCCESS"))
      {
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "SUCCESSFULLY DISBURSED TO DEFAULT ACCOUNT WITH TOTAL CLOSING AMOUNT. CIF NUMBER = [{}]  REQUEST ID = [{}] LOAN ACCOUNT NUMBER = [{}] DEFAULT ACCOUNT NUMBER = [{}] TOTAL CLOSING AMOUNT = [{}] ", cif,
            requestId, loanAccountNumber, defaultAccount, totalClosingLoanAmount);
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "FAILED TO DISBURSE TO DEFAULT ACCOUNT!! CIF NUMBER = [{}] REQUEST ID = [{}]  LOAN ACCOUNT NUMBER = [{}]",
            cif, requestId, loanAccountNumber);
        throw new BpmnError("Disburse To Default Account", errorMessage);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      throw new BpmnError("Disburse To Default Account", e.getMessage());
    }
  }
}
