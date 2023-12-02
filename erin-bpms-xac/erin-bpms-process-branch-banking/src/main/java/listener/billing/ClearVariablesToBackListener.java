package listener.billing;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import mn.erin.domain.bpm.BpmBranchBankingConstants;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DEDUCTION_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_CUT_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;


public class ClearVariablesToBackListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {

    String actionName = (String) execution.getVariable(ACTION);
    if (actionName != null && actionName.equals("back"))
    {

      CaseService caseService = execution.getProcessEngine().getCaseService();
      String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

      execution.setVariable(ACCOUNT_NUMBER, null);
      caseService.setVariable(instanceId, ACCOUNT_NUMBER, null);

      execution.setVariable(ACCOUNT_NAME, null);
      caseService.setVariable(instanceId, ACCOUNT_NAME, null);

      execution.setVariable(ACCOUNT_CURRENCY, null);
      caseService.setVariable(instanceId,ACCOUNT_CURRENCY , null);

      execution.setVariable(ACCOUNT_BALANCE,null);
      caseService.setVariable(instanceId,ACCOUNT_BALANCE, null);

      execution.setVariable(DEDUCTION_AMOUNT, null);
      caseService.setVariable(instanceId, DEDUCTION_AMOUNT, null);

      execution.setVariable(BpmBranchBankingConstants.CUSTOMER_NAME, null);
      caseService.setVariable(instanceId, BpmBranchBankingConstants.CUSTOMER_NAME, null);

      execution.setVariable(TRANSACTION_DESCRIPTION, null);
      caseService.setVariable(instanceId, TRANSACTION_DESCRIPTION, null);

      execution.setVariable(PHONE_NUMBER, null);
      caseService.setVariable(instanceId, PHONE_NUMBER,null);

      execution.setVariable(REGISTER_NUMBER, null);
      caseService.setVariable(instanceId, REGISTER_NUMBER, null);

      execution.setVariable(TRANSACTION_DESC, null);
      caseService.setVariable(instanceId, TRANSACTION_DESC, null);

      execution.setVariable(PAY_CUT_AMOUNT, null);
      caseService.setVariable(instanceId, PAY_CUT_AMOUNT, null);

      execution.setVariable(PAY_AMOUNT,null);
      caseService.setVariable(instanceId, PAY_AMOUNT, null);
    }
  }
}
