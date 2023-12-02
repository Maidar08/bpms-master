package consumption.case_listener;

import java.io.Serializable;
import java.math.BigDecimal;
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
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.AMOUNT_REJECTED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.CONFIRMED;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromStringToEnum;


public class BnplSetCalculationStateListener implements ExecutionListener
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplSetCalculationStateListener.class);

  public BnplSetCalculationStateListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, ProcessRepository processRepository)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRequestRepository = processRequestRepository;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    Map<String, Serializable> parameters = new HashMap<>();
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));

    parameters.put(BNPL_LOAN_AMOUNT, String.valueOf(execution.getVariable(BNPL_LOAN_AMOUNT)));

    setProcessState(execution, processRequestRepository, requestId);

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ParameterEntityType.valueOf(BNPL), parameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    LOGGER.info(BNPL_LOG + "Updated bnpl calculated loan amount to {}, with process request id = [{}]", execution.getVariable(BNPL_LOAN_AMOUNT), requestId);
  }

  private void setProcessState(DelegateExecution execution, ProcessRequestRepository processRequestRepository, String processRequestId) throws UseCaseException
  {
    long grantMinimumAmount = (long) execution.getVariable("grantMinimumAmount");
    BigDecimal bnplLoanAmount = (BigDecimal) execution.getVariable(BNPL_LOAN_AMOUNT);
    String processRequestState;
    if (bnplLoanAmount.compareTo(BigDecimal.valueOf(grantMinimumAmount)) == -1)
    {
      processRequestState = fromEnumToString(AMOUNT_REJECTED);
      execution.setVariable(STATE, processRequestState);
    }
    else
    {
      processRequestState = fromEnumToString(CONFIRMED);
      execution.setVariable(STATE, processRequestState);
    }

    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, fromStringToEnum(processRequestState));
    updateRequestState.execute(input);
    LOGGER.info(BNPL_LOG + "Process request state updated as  {}, Loan amount = {}, with process request id = [{}]", processRequestState, bnplLoanAmount,
        processRequestId);
  }
}
