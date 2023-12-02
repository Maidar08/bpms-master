package service_task.account_reference.account_reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.model.branch_banking.MakeNoCashAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.MakeNoCashAccountFeeTransaction;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.checkAccountNumberValue;

public class MakeNoCashAccountFeeTransactionTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MakeNoCashAccountFeeTransactionTask.class);
  private final BranchBankingService branchBankingService;

  public MakeNoCashAccountFeeTransactionTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {

    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    checkRequiredVariable(caseService, instanceId);

    String accountId = String.valueOf(execution.getVariable(ACCOUNT_ID_ENTER));
    String toAccountNo = String.valueOf(execution.getVariable("toAccountNo"));

    String transactionCcy = String.valueOf(caseService.getVariable(instanceId, "accountRefTranCcy"));
    String accountCurrency = String.valueOf(caseService.getVariable(instanceId, "accountCurrency"));
    String accountNumber = String.valueOf(caseService.getVariable(instanceId, "accountNumber"));
    String toAccountCurrency = String.valueOf(caseService.getVariable(instanceId, "toAccountCurrency"));
    String feesAmount = String.valueOf(caseService.getVariable(instanceId, "feesAmount"));

    List<Map<String, Object>> transactionList = new ArrayList<>();
    Map<String, Object> transactionParameters = new HashMap<>();
    transactionParameters.put("AcctId", accountNumber);
    transactionParameters.put("CrDr", "D");
    transactionParameters.put("Amount", feesAmount);
    transactionParameters.put("Currency", transactionCcy);
    transactionParameters.put("AccCurrency", accountCurrency);
    transactionParameters.put("Eventid", "");
    transactionParameters.put("Eventtype", "");
    transactionParameters.put("Rate", "");

    Map<String, Object> transactionParameters1 = new HashMap<>();
    transactionParameters1.put("AcctId", toAccountNo);
    transactionParameters1.put("CrDr", "C");
    transactionParameters1.put("Amount", feesAmount);
    transactionParameters1.put("Currency", transactionCcy);
    transactionParameters1.put("AccCurrency", toAccountCurrency);
    transactionParameters1.put("Eventid", "");
    transactionParameters1.put("Eventtype", "");
    transactionParameters1.put("Rate", "");
    transactionList.add(transactionParameters);
    transactionList.add(transactionParameters1);

    MakeNoCashAccountFeeTransactionInput input = new MakeNoCashAccountFeeTransactionInput(transactionList, "T", "CI", "", "",
        accountId + " - Тодорхойлолтын хураамж ", "", "N", "", instanceId);

    MakeNoCashAccountFeeTransaction makeNoCashAccountFeeTransaction = new MakeNoCashAccountFeeTransaction(branchBankingService);
    Map<String, String> outputParam = makeNoCashAccountFeeTransaction.execute(input);

    String bankTransactionNo = outputParam.get(TRANSACTION_ID);
    String transactionDate = outputParam.get(TRAN_DATE);

    execution.setVariable(TRANSACTION_NUMBER, bankTransactionNo);
    caseService.setVariable(instanceId, TRANSACTION_NUMBER, bankTransactionNo);

    execution.setVariable(PAID_DATE, transactionDate);
    caseService.setVariable(instanceId, PAID_DATE, transactionDate);

    LOG.info("########### Add No Cash Transaction is successfully made with transaction id = [{}] \n  WITH PROCESS INSTANCE ID = [{}]", bankTransactionNo,
        instanceId);
  }

  private void checkRequiredVariable(CaseService caseService, String instanceId) throws BpmServiceException
  {
    String transactionType = String.valueOf(caseService.getVariable(instanceId, TRANSACTION_TYPE));
    String accountNumber = String.valueOf(caseService.getVariable(instanceId, BpmBranchBankingConstants.ACCOUNT_NUMBER));

    if (checkAccountNumberValue(accountNumber, transactionType)){
      throw new BpmServiceException(BB_ACCOUNT_NUMBER_NULL_CODE, BB_ACCOUNT_NUMBER_NULL_MESSAGE);
    }
  }
}
