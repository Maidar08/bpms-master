package listener.billing.tax;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ASSET_DETAIL;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BLNC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_CUT_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_FULL;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PERIOD;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_VALUE_TAX;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAYER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_TYPE_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Lkhagvadorj.A
 **/

public class ClearTaxVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(SEARCH_TYPE, null);
    caseService.setVariable(instanceId, SEARCH_TYPE, null);

    execution.setVariable(SEARCH_VALUE_TAX, null);
    caseService.setVariable(instanceId, SEARCH_VALUE_TAX, null);

    execution.setVariable(TAX_NUMBER, null);
    caseService.setVariable(instanceId, TAX_NUMBER, null);

    execution.setVariable(TAX_PAYER_NAME, null);
    caseService.setVariable(instanceId, TAX_PAYER_NAME, null);

    execution.setVariable(INVOICE_NUMBER, null);
    caseService.setVariable(instanceId, INVOICE_NUMBER, null);

    execution.setVariable(INVOICE_TYPE, null);
    caseService.setVariable(instanceId, INVOICE_TYPE, null);

    execution.setVariable(TAX_PAYER_NAME, null);
    caseService.setVariable(instanceId, TAX_PAYER_NAME, null);

    execution.setVariable(TAX_ACCOUNT_NAME, null);
    caseService.setVariable(instanceId, TAX_ACCOUNT_NAME, null);

    execution.setVariable(ASSET_DETAIL, null);
    caseService.setVariable(instanceId, ASSET_DETAIL, null);

    execution.setVariable(TAX_TYPE_CODE, null);
    caseService.setVariable(instanceId, TAX_TYPE_CODE, null);

    execution.setVariable(PERIOD, null);
    caseService.setVariable(instanceId, PERIOD, null);

    execution.setVariable(TRANSACTION_TYPE, null);
    caseService.setVariable(instanceId, TRANSACTION_TYPE, null);

    execution.setVariable(ACCOUNT_NUMBER, null);
    caseService.setVariable(instanceId, ACCOUNT_NUMBER, null);

    execution.setVariable(ACCOUNT_NAME, null);
    caseService.setVariable(instanceId, ACCOUNT_NAME, null);

    execution.setVariable(ACCOUNT_BALANCE, null);
    caseService.setVariable(instanceId, ACCOUNT_BALANCE, null);

    execution.setVariable(ACCOUNT_CURRENCY, null);
    caseService.setVariable(instanceId, ACCOUNT_CURRENCY, null);

    execution.setVariable(PAY_CUT_AMOUNT, null);
    caseService.setVariable(instanceId, PAY_CUT_AMOUNT, null);

    execution.setVariable(INVOICE_AMOUNT, null);
    caseService.setVariable(instanceId, INVOICE_AMOUNT, null);

    execution.setVariable(TAX_CURRENCY, null);
    caseService.setVariable(instanceId, TAX_CURRENCY, null);

    execution.setVariable(PAY_FULL, null);
    caseService.setVariable(instanceId, PAY_FULL, null);

    execution.setVariable(TRANSACTION_DESC, null);
    caseService.setVariable(instanceId, TRANSACTION_DESC, null);

    execution.setVariable(REGISTER_NUMBER, null);
    caseService.setVariable(instanceId, REGISTER_NUMBER, null);

    execution.setVariable(PHONE_NUMBER, null);
    caseService.setVariable(instanceId, PHONE_NUMBER, null);

    execution.setVariable(BLNC, null);
    caseService.setVariable(instanceId, BLNC, null);

  }
}
