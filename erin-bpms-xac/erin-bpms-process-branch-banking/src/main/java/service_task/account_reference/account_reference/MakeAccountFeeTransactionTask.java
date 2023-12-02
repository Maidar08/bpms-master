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

import mn.erin.domain.bpm.model.branch_banking.MakeAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.MakeAccountFeeTransaction;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRAN_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class MakeAccountFeeTransactionTask  implements JavaDelegate

{
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MakeAccountFeeTransactionTask.class);
    private final BranchBankingService branchBankingService;

    public MakeAccountFeeTransactionTask(BranchBankingService branchBankingService)
    {
        this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
    }
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        CaseService caseService = execution.getProcessEngine().getCaseService();
        String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

        String accountId = String.valueOf(execution.getVariable(ACCOUNT_ID_ENTER));
        String transactionCcy = String.valueOf(caseService.getVariable(instanceId,"accountRefTranCcy"));
        String accountNumber = String.valueOf(execution.getVariable("toAccountNo"));
        String accountCurrency = String.valueOf(execution.getVariable("toAccountCurrency"));
        String feesAmount = String.valueOf(caseService.getVariable(instanceId,"feesAmount"));

        Map<String, Object> transactionsParameters = new HashMap<>();
        List<Map<String, Object>> transactionList = new ArrayList<>();
        transactionsParameters.put("AcctId", accountNumber);
        transactionsParameters.put("Amount", feesAmount);
        transactionsParameters.put("Currency", transactionCcy);
        transactionsParameters.put("ValueDt", "");
        transactionsParameters.put("TrnParticulars", accountId + " - Тодорхойлолтын хураамж");
        transactionsParameters.put("PartTrnRmks", accountId + " - Тодорхойлолтын хураамж");
        transactionsParameters.put("accCCY", accountCurrency);
        transactionsParameters.put("Rate", "");
        transactionsParameters.put("RateCode", "");
        transactionsParameters.put("UserPartTrnCode", "");
        transactionList.add(transactionsParameters);

        MakeAccountFeeTransactionInput input = new MakeAccountFeeTransactionInput(instanceId, feesAmount, transactionCcy , "NR", transactionList);

        MakeAccountFeeTransaction makeAccountFeeTransaction = new MakeAccountFeeTransaction(branchBankingService);
        Map<String, String> outputParam = makeAccountFeeTransaction.execute(input);

        String bankTransactionNo = outputParam.get(TRANSACTION_ID);
        String transactionDate = outputParam.get(TRAN_DATE);

        execution.setVariable(TRANSACTION_NUMBER, bankTransactionNo);
        caseService.setVariable(instanceId, TRANSACTION_NUMBER, bankTransactionNo);

        execution.setVariable(PAID_DATE, transactionDate);
        caseService.setVariable(instanceId, PAID_DATE, transactionDate);


        LOG.info("########### Add Cash Credit is successfully made with transaction id = [{}] \n  WITH PROCESS INSTANCE ID = [{}]", bankTransactionNo, instanceId);
    }
}
