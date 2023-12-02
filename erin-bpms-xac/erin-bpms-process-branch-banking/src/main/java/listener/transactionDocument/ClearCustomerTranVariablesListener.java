package listener.transactionDocument;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REGISTER_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_FORM_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearCustomerTranVariablesListener implements ExecutionListener
{
  @Override
  public void notify(DelegateExecution execution) throws Exception
  {

    CaseService caseService = execution.getProcessEngine().getCaseService();

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable(TRANSACTION_FORM_TYPE, null);
    caseService.setVariable(instanceId, TRANSACTION_FORM_TYPE, null);

    execution.setVariable(CUSTOMER_NAME, null);
    caseService.setVariable(instanceId, CUSTOMER_NAME, null);

    execution.setVariable(REGISTER_ID, null);
    caseService.setVariable(instanceId, REGISTER_ID, null);

    execution.setVariable(TRANSACTION_DATE, null);
    caseService.setVariable(instanceId, TRANSACTION_DATE, null);
  }
}
