package consumption.process_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.getTypeByProcessType;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;

public class SaveCreatedLoanAccountListener implements ExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SaveCreatedLoanAccountListener.class);
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public SaveCreatedLoanAccountListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String processType = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    String instanceId = processType.equals(ONLINE_SALARY_PROCESS_TYPE) ?
        getValidString(execution.getVariable(CASE_INSTANCE_ID)) :
        getValidString(execution.getVariable(PROCESS_INSTANCE_ID));
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(LOAN_ACCOUNT_NUMBER, getValidString(execution.getVariable(LOAN_ACCOUNT_NUMBER)));
    Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
    processParameters.put(getTypeByProcessType(processType), parameters);
    updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), instanceId, processParameters);
    LOGGER.info("######### Loan account number {} saved in Process Parameter table.", execution.getVariable(LOAN_ACCOUNT_NUMBER));
  }
}
