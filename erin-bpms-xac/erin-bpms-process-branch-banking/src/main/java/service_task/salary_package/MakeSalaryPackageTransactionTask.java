package service_task.salary_package;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.MakeNoCashAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.MakeNoCashAccountFeeTransaction;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT_CAPITAL_A;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_SALARY_ACCOUNT_LIST_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_SALARY_ACCOUNT_LIST_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_SIMPLE_DATE_FORMATTER;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getDoubleAndRemoveComma;
import static mn.erin.domain.bpm.util.process.BpmUtils.getFormattedDateString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;

/**
 * @author Lkhagvadorj.A
 **/

public class MakeSalaryPackageTransactionTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MakeSalaryPackageTransactionTask.class);
  private final BranchBankingService branchBankingService;
  private final AuthenticationService authenticationService;

  public MakeSalaryPackageTransactionTask(BranchBankingService branchBankingService, AuthenticationService authenticationService)
  {
    this.branchBankingService = branchBankingService;
    this.authenticationService = authenticationService;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(DelegateExecution execution) throws BpmServiceException, ParseException
  {
    if (String.valueOf(execution.getVariable("action")).equals("complete"))
    {
      clearTaskVariables(execution);
      return;
    }

    if (!execution.hasVariable("transactionTable"))
    {
      throw new BpmServiceException(BB_SALARY_ACCOUNT_LIST_NULL_CODE, BB_SALARY_ACCOUNT_LIST_NULL_MESSAGE);
    }

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String transactionDescription = getStringValue(execution.getVariable("transactionDescription"));

    List<Map<String, Object>> accountList = (List<Map<String, Object>>) execution.getVariable("transactionTable");

    makeTransaction(execution, accountList, instanceId, transactionDescription);
  }

  private void makeTransaction(DelegateExecution execution, List<Map<String, Object>> accountList, String instanceId, String transactionDescription)
      throws BpmServiceException, ParseException
  {
    MakeNoCashAccountFeeTransactionInput input = setTransactionInfo(execution, accountList, instanceId, transactionDescription);
    MakeNoCashAccountFeeTransaction useCase = new MakeNoCashAccountFeeTransaction(branchBankingService);

    try
    {
      Map<String, String> outputParam = useCase.execute(input);
      String bankTransactionNo = outputParam.get("TrnId");
      String paidDate = outputParam.get("TranDt");
      String formattedDateString = getFormattedDateString(paidDate, ISO_SIMPLE_DATE_FORMATTER);

      CaseService caseService = execution.getProcessEngine().getCaseService();
      execution.setVariable(TRANSACTION_NUMBER, bankTransactionNo);
      execution.setVariable(PAID_DATE, paidDate);
      execution.setVariable(TRANSACTION_DT, formattedDateString);
      caseService.setVariable(instanceId, TRANSACTION_DT, formattedDateString);
      caseService.setVariable(instanceId, TRANSACTION_NUMBER, bankTransactionNo);
      caseService.setVariable(instanceId, PAID_DATE, paidDate);


      LOG.info("######################## Salary package transaction is successfully made with transaction id = [{}], \n process instance id = [{}]",
          bankTransactionNo, instanceId);
    }
    catch (UseCaseException e)
    {
      execution.setVariable("invalidAccounts", e.getMessage());
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }
  }

  private MakeNoCashAccountFeeTransactionInput setTransactionInfo(DelegateExecution execution, List<Map<String, Object>> accountList, String instanceId, String transactionDescription)
  {
    // Set response body for call service
    List<Map<String, Object>> transactionParameters = new ArrayList<>();
    double totalAmount = 0;

    for (Map<String, Object> accountInfo : accountList)
    {
      Map<String, Object> creditAccountInfo = getCreditAccountInfo(accountInfo, execution);
      totalAmount += getDoubleAndRemoveComma(String.valueOf(creditAccountInfo.get(AMOUNT_CAPITAL_A)));
      transactionParameters.add(creditAccountInfo);
    }

    Map<String, Object> debitAccountInfo = getDebitAccountInfo(execution, instanceId);
    debitAccountInfo.put(AMOUNT_CAPITAL_A, String.valueOf(totalAmount));
    debitAccountInfo.put(CURRENCY, transactionParameters.get(0).get(CURRENCY));

    transactionParameters.add(debitAccountInfo);

    String transactionType = "T";
    String transactionSubType = "CI";
    String userId = authenticationService.getCurrentUserId();

    return new MakeNoCashAccountFeeTransactionInput(transactionParameters, transactionType, transactionSubType, "",
        userId, transactionDescription, "", "N", "", instanceId);
  }

  private Map<String, Object> getDebitAccountInfo(DelegateExecution execution, String instanceId)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    Map<String, Object> debitAccountInfo = new HashMap<>();
    String accountNumber = String.valueOf(execution.getVariable(ACCOUNT_ID_ENTER));
    String accountCurrency = getStringValue(caseService.getVariable(instanceId, ACCOUNT_CURRENCY));

    debitAccountInfo.put("AcctId", accountNumber);
    debitAccountInfo.put("CrDr", "D");
    debitAccountInfo.put("AccCurrency", accountCurrency);
    debitAccountInfo.put("Eventid", "");
    debitAccountInfo.put("Eventtype", "TRANF");
    debitAccountInfo.put("Rate", "");

    return debitAccountInfo;
  }

  private Map<String, Object> getCreditAccountInfo(Map<String, Object> transactionInfo, DelegateExecution execution)
  {
    Map<String, Object> creditAccountInfo = new HashMap<>();
    double amountDoubleValue = getDoubleAndRemoveComma(String.valueOf(transactionInfo.get(AMOUNT)));
    String amount = String.valueOf(amountDoubleValue);
    String accountCurrency = getStringValue(transactionInfo.get("accountCcy"));
    boolean hasFee = null != execution.getVariable("hasFee") && (boolean) execution.getVariable("hasFee");
    String eventId = hasFee ? "NONCASH_TRANS_FEE_MNT" : "";

    creditAccountInfo.put("AcctId", String.valueOf(transactionInfo.get(ACCOUNT_ID)));
    creditAccountInfo.put("CrDr", "C");
    creditAccountInfo.put("Amount", amount);
    // TODO: it will be fixed once check account info service has implemented
    creditAccountInfo.put("Currency", String.valueOf(transactionInfo.get("transactionCcy")));
    creditAccountInfo.put("AccCurrency", accountCurrency);
//    creditAccountInfo.put("AccCurrency", "MNT");
    creditAccountInfo.put("Eventid", eventId);
    creditAccountInfo.put("Eventtype", "TRANF");
    creditAccountInfo.put("Rate", "");

    return creditAccountInfo;
  }

  private void clearTaskVariables(DelegateExecution execution) {
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();

    execution.setVariable(ACCOUNT_NUMBER, null);
    caseService.setVariable(instanceId, ACCOUNT_NUMBER, null);
    execution.setVariable(ACCOUNT_ID_ENTER, null);
    caseService.setVariable(instanceId, ACCOUNT_ID_ENTER, null);
    execution.setVariable(ACCOUNT_NAME, "");
    caseService.setVariable(instanceId, ACCOUNT_NAME, "");
    execution.setVariable(ACCOUNT_BALANCE, 0);
    caseService.setVariable(instanceId, ACCOUNT_BALANCE, 0);
    execution.setVariable(ACCOUNT_CURRENCY, "");
    caseService.setVariable(instanceId, ACCOUNT_CURRENCY, "");
    execution.setVariable("transactionCount", 0);
    caseService.setVariable(instanceId, "transactionCount", 0);
    execution.setVariable("transactionTotalAmount", 0);
    caseService.setVariable(instanceId, "transactionTotalAmount", 0);
    execution.setVariable("hasFee", null);
    caseService.setVariable(instanceId, "hasFee", null);
    execution.setVariable("transactionDescription", "");
    caseService.setVariable(instanceId, "transactionDescription", "");
    execution.setVariable("invalidAccounts", "");
    caseService.setVariable(instanceId, "invalidAccounts", "");
    execution.setVariable(TRANSACTION_DT, "");
    caseService.setVariable(instanceId, TRANSACTION_DT, "");
    execution.setVariable("transactionNumber", "");
    caseService.setVariable(instanceId, "transactionNumber", "");
    execution.setVariable("fileNameChips", "");
    caseService.setVariable(instanceId, "fileNameChips", "");
  }
}
