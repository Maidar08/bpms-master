package listener.account_reference;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE_REF;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CREATED_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_END_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_FULL_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearAccountReferenceVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {

    CaseService caseService = execution.getProcessEngine().getCaseService();

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(ACCOUNT_ID_ENTER, null);
    caseService.setVariable(instanceId, ACCOUNT_ID_ENTER, null);

    execution.setVariable("language", null);
    caseService.setVariable(instanceId, "language", null);

    execution.setVariable(CUSTOMER_FULL_NAME, null);
    caseService.setVariable(instanceId, CUSTOMER_FULL_NAME, null);

    execution.setVariable(ACCOUNT_BALANCE, null);
    caseService.setVariable(instanceId, ACCOUNT_BALANCE, null);

    execution.setVariable(ACCOUNT_CCY,null);
    caseService.setVariable(instanceId,ACCOUNT_CCY,null);

    execution.setVariable(ACCOUNT_CREATED_DATE, null);
    caseService.setVariable(instanceId, ACCOUNT_CREATED_DATE, null);

    execution.setVariable(ACCOUNT_END_DATE, null);
    caseService.setVariable(instanceId, ACCOUNT_END_DATE, null);

    execution.setVariable("showAccountCreatedDt", null);
    caseService.setVariable(instanceId, "showAccountCreatedDt", null);

    execution.setVariable("printAverageAmount", null);
    caseService.setVariable(instanceId,"printAverageAmount", null );

    execution.setVariable("printBalanceAmount", null);
    caseService.setVariable(instanceId, "printBalanceAmount", null);

    execution.setVariable("descriptionRecipient", null);
    caseService.setVariable(instanceId, "descriptionRecipient", null);

    execution.setVariable("additionalInfo", null);
    caseService.setVariable(instanceId, "additionalInfo", null);

    execution.setVariable(TRANSACTION_TYPE, null);
    caseService.setVariable(instanceId, TRANSACTION_TYPE, null);

    execution.setVariable(ACCOUNT_NUMBER, null);
    caseService.setVariable(instanceId, ACCOUNT_NUMBER, null);

    execution.setVariable(ACCOUNT_NAME, null);
    caseService.setVariable(instanceId, ACCOUNT_NAME, null);

    execution.setVariable(ACCOUNT_BALANCE_REF, null);
    caseService.setVariable(instanceId, ACCOUNT_BALANCE_REF, null);

    execution.setVariable(ACCOUNT_CURRENCY, null);
    caseService.setVariable(instanceId, ACCOUNT_CURRENCY, null);

    execution.setVariable("feesAmount", null);
    caseService.setVariable(instanceId, "feesAmount", null);

    execution.setVariable("exemptFromFees", null);
    caseService.setVariable(instanceId,"exemptFromFees", null );

    execution.setVariable("reduceFees", null);
    caseService.setVariable(instanceId, "reduceFees", null);
  }
}
