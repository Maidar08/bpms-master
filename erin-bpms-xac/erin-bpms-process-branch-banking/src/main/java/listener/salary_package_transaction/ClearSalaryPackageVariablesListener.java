package listener.salary_package_transaction;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FILE_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.HAS_FEE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVALID_ACCOUNTS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_COUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TOTAL_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearSalaryPackageVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(ACCOUNT_ID_ENTER, null);
    caseService.setVariable(instanceId, ACCOUNT_ID_ENTER, null);

    execution.setVariable(ACCOUNT_NAME, null);
    caseService.setVariable(instanceId, ACCOUNT_NAME, null);

    execution.setVariable(ACCOUNT_BALANCE, null);
    caseService.setVariable(instanceId, ACCOUNT_BALANCE, null);

    execution.setVariable(ACCOUNT_CURRENCY, null);
    caseService.setVariable(instanceId, ACCOUNT_CURRENCY, null);

    execution.setVariable(TRANSACTION_COUNT, null);
    caseService.setVariable(instanceId, TRANSACTION_COUNT, null);

    execution.setVariable(TRANSACTION_TOTAL_AMOUNT, null);
    caseService.setVariable(instanceId, TRANSACTION_TOTAL_AMOUNT, null);

    execution.setVariable(HAS_FEE, null);
    caseService.setVariable(instanceId, HAS_FEE, null);

    execution.setVariable(INVALID_ACCOUNTS, null);
    caseService.setVariable(instanceId, INVALID_ACCOUNTS, null);

    execution.setVariable(TRANSACTION_DT, null);
    caseService.setVariable(instanceId, TRANSACTION_DT, null);

    execution.setVariable(TRANSACTION_DESCRIPTION, null);
    caseService.setVariable(instanceId, TRANSACTION_DESCRIPTION, null);

    execution.setVariable(TRANSACTION_AMOUNT, null);
    caseService.setVariable(instanceId, TRANSACTION_AMOUNT, null);

    execution.setVariable(FILE_NAME, null);
    caseService.setVariable(instanceId, FILE_NAME, null);
  }
}
