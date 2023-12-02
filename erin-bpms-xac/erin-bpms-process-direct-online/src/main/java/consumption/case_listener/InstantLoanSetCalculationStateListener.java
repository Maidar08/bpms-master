package consumption.case_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static consumption.constant.CamundaVariableConstants.CALCULATE_AMOUNT;
import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
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
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;

public class InstantLoanSetCalculationStateListener implements ExecutionListener
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(InstantLoanSetCalculationStateListener.class);

  public InstantLoanSetCalculationStateListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, ProcessRepository processRepository)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRequestRepository = processRequestRepository;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateExecution execution) throws UseCaseException
  {
    if (execution.hasVariable(CALCULATE_AMOUNT) && (boolean) execution.getVariable(CALCULATE_AMOUNT))
    {
      updateRequestState(execution);
    }
    else
    {
      Map<String, Serializable> parameters = new HashMap<>();
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
      parameters.put(DEBT_INCOME_INSURANCE_STRING, String.valueOf(execution.getVariable(DEBT_INCOME_INSURANCE_STRING)));

      UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
      UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
      updateRequestParameters.execute(input);

      String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
      processParams.put(INSTANT_LOAN, parameters);
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
      updateProcessParameters.execute(parametersInput);
      LOGGER.info(INSTANT_LOAN_LOG + "Updated fixed accepted loan amount to {}, with process request id = [{}]",
          execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT), requestId);
    }
  }

  private void updateRequestState(DelegateExecution execution) throws UseCaseException
  {
    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String grantLoanAmount = String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT));
    String interestRate = String.valueOf(execution.getVariable(INTEREST_RATE));

    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);
    parameters.put(GRANT_LOAN_AMOUNT_STRING, grantLoanAmount);
    parameters.put("Interest", (Double) execution.getVariable("Interest"));
    parameters.put(INTEREST_RATE, interestRate);
    parameters.put(LOAN_CLASS_NAME, String.valueOf(execution.getVariable(LOAN_CLASS_NAME)));
    parameters.put(FIRST_PAYMENT_DATE, String.valueOf(execution.getVariable(FIRST_PAYMENT_DATE)));
    parameters.put(LOAN_PRODUCT_DESCRIPTION, String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION)));
    parameters.put(FULL_NAME, String.valueOf(execution.getVariable(FULL_NAME)));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(INSTANT_LOAN, parameters);

    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);

    UpdateRequestStateInput updateRequestStateInput = new UpdateRequestStateInput(requestId,
        ProcessRequestState.fromStringToEnum(String.valueOf(execution.getVariable(STATE))));
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    updateRequestState.execute(updateRequestStateInput);

    LOGGER.info(INSTANT_LOAN_LOG + " Updated process state to {}, with process request id = [{}]", execution.getVariable(STATE), requestId);
  }
}
