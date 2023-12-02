package consumption.process_listener;

import static consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static consumption.constant.CamundaVariableConstants.CALCULATE_AMOUNT;
import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_LEASING_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestParameters;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

public class SetCalculationStateListener implements ExecutionListener
{

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetCalculationStateListener.class);

  public SetCalculationStateListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void notify(DelegateExecution execution) throws UseCaseException
  {
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String grantLoanAmount = String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT));

    if (execution.hasVariable(CALCULATE_AMOUNT) && (boolean) execution.getVariable(CALCULATE_AMOUNT))
    {
      updateRequestInfo(execution, processInstanceId, requestId);
    }
    else
    {
      Map<String, Serializable> parameters = new HashMap<>();

      parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
      parameters.put(DEBT_INCOME_INSURANCE_STRING, String.valueOf(execution.getVariable(DEBT_INCOME_INSURANCE_STRING)));
      parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);
      parameters.put(GRANT_LOAN_AMOUNT_STRING, grantLoanAmount);
      Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
      processParams.put(ONLINE_LEASING, parameters);
      updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), processInstanceId, processParams);
      updateRequestParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, parameters);
      LOGGER.info(ONLINE_LEASING_LOG + "Updated fixed accepted loan amount to {}, with process request id = [{}]",
          execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT), requestId);
    }
  }

  private void updateRequestInfo(DelegateExecution execution, String instanceId, String requestId) throws UseCaseException
  {
    ProcessRepository processRepository = bpmsRepositoryRegistry.getProcessRepository();
    ProcessRequestRepository processRequestRepository = bpmsRepositoryRegistry.getProcessRequestRepository();

    String grantLoanAmount = String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT));
    String interestRate = String.valueOf(execution.getVariable(INTEREST_RATE));

    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);
    parameters.put(GRANT_LOAN_AMOUNT_STRING, grantLoanAmount);
    parameters.put(INTEREST_RATE, interestRate);
    parameters.put(LOAN_CLASS_NAME, String.valueOf(execution.getVariable(LOAN_CLASS_NAME)));
    parameters.put(FIRST_PAYMENT_DATE, String.valueOf(execution.getVariable(FIRST_PAYMENT_DATE)));
    parameters.put(LOAN_PRODUCT_DESCRIPTION, String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION)));
    parameters.put(FULL_NAME, String.valueOf(execution.getVariable(FULL_NAME)));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ONLINE_LEASING, parameters);

    updateProcessParameters(aimServiceRegistry, processRepository, instanceId, processParams);
    updateRequestParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, parameters);
    updateRequestState(processRequestRepository, requestId, ProcessRequestState.fromStringToEnum(String.valueOf(execution.getVariable(STATE))));
    LOGGER.info(ONLINE_LEASING_LOG + " Updated process state to {}, with process request id = [{}]", execution.getVariable(STATE), requestId);
  }
}
