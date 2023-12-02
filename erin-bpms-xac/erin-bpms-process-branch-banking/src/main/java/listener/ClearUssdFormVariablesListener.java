package listener;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class ClearUssdFormVariablesListener implements ExecutionListener
{

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    execution.setVariable("customerId", null);
    caseService.setVariable(instanceId, "customerId", null);

    execution.setVariable("phoneNumber", null);
    caseService.setVariable(instanceId, "phoneNumber", null);

    execution.setVariable("ussdPhoneNumber", null);
    caseService.setVariable(instanceId, "ussdPhoneNumber", null);

    execution.setVariable("customerStatus", null);
    caseService.setVariable(instanceId, "customerStatus", null);

    execution.setVariable("registeredDt", null);
    caseService.setVariable(instanceId, "registeredDt", null);

    execution.setVariable("registeredBranch", null);
    caseService.setVariable(instanceId, "registeredBranch", null);

    execution.setVariable("coreNumber", null);
    caseService.setVariable(instanceId, "coreNumber", null);

    execution.setVariable("lastLoggedDt", null);
    caseService.setVariable(instanceId, "lastLoggedDt", null);

    execution.setVariable("registeredEmployee", null);
    caseService.setVariable(instanceId, "registeredEmployee", null);

    execution.setVariable("failedLoginAttempt", null);
    caseService.setVariable(instanceId, "failedLoginAttempt", null);

    execution.setVariable("customerFullName", null);
    caseService.setVariable(instanceId, "customerFullName", null);

    execution.setVariable("customerRegister", null);
    caseService.setVariable(instanceId, "customerRegister", null);

    execution.setVariable("checkRegistration", null);
    caseService.setVariable(instanceId, "checkRegistration", null);

    execution.setVariable("mainAccount", null);
    caseService.setVariable(instanceId, "mainAccount", null);

    execution.setVariable("customerIdTwo", null);
    caseService.setVariable(instanceId, "customerIdTwo", null);

  }
}
