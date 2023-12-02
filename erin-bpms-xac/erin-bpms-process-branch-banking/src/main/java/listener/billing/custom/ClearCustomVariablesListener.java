package listener.billing.custom;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_TAX_PAYER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DEDUCTION_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REGISTER_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_VALUE_CUSTOM;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;

public class ClearCustomVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {

    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(SEARCH_TYPE, null);
    caseService.setVariable(instanceId, SEARCH_TYPE, null);

    execution.setVariable(SEARCH_VALUE_CUSTOM, null);
    caseService.setVariable(instanceId, SEARCH_VALUE_CUSTOM, null);

    execution.setVariable(REGISTER_ID, null);
    caseService.setVariable(instanceId, REGISTER_ID, null);

    execution.setVariable(INVOICE_NUMBER, null);
    caseService.setVariable(instanceId, INVOICE_NUMBER, null);

    execution.setVariable(PAYMENT_AMOUNT, null);
    caseService.setVariable(instanceId, PAYMENT_AMOUNT, null);

    execution.setVariable(CURRENCY_TYPE, null);
    caseService.setVariable(instanceId, CURRENCY_TYPE, null);

    execution.setVariable(TRANSACTION_TYPE, null);
    caseService.setVariable(instanceId, TRANSACTION_TYPE, null);

    execution.setVariable(ACCOUNT_NUMBER, null);
    caseService.setVariable(instanceId, ACCOUNT_NUMBER, null);

    execution.setVariable(ACCOUNT_NAME, null);
    caseService.setVariable(instanceId, ACCOUNT_NAME, null);

    execution.setVariable(ACCOUNT_BALANCE, null);
    caseService.setVariable(instanceId,ACCOUNT_BALANCE, null);

    execution.setVariable(ACCOUNT_CURRENCY, null);
    caseService.setVariable(instanceId, ACCOUNT_CURRENCY, null);

    execution.setVariable(DEDUCTION_AMOUNT, null);
    caseService.setVariable(instanceId, DEDUCTION_AMOUNT, null);

    execution.setVariable(PAY_AMOUNT, null);
    caseService.setVariable(instanceId, PAY_AMOUNT, null);

    execution.setVariable(TRANSACTION_CURRENCY, null);
    caseService.setVariable(instanceId, TRANSACTION_CURRENCY, null);

    execution.setVariable(PHONE_NUMBER, null);
    caseService.setVariable(instanceId, PHONE_NUMBER, null);

    execution.setVariable(TRANSACTION_DESCRIPTION, null);
    caseService.setVariable(instanceId, TRANSACTION_DESCRIPTION, null);

    execution.setVariable(CUSTOMER_NAME, null);
    caseService.setVariable(instanceId, CUSTOMER_NAME, null);

    execution.setVariable(CUSTOM_TAX_PAYER_NAME, null);
    caseService.setVariable(instanceId, CUSTOM_TAX_PAYER_NAME, null);

  }
}
