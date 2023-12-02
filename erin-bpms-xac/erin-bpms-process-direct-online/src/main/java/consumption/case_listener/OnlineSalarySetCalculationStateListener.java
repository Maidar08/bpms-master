package consumption.case_listener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
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
import static consumption.constant.CamundaVariableConstants.SCORING_STATE;
import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.common.utils.NumberUtils.bigDecimalToString;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_INSURANCE_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_SALARY;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalarySetCalculationStateListener implements CaseExecutionListener
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalarySetCalculationStateListener.class);

  public OnlineSalarySetCalculationStateListener(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
      ProcessRepository processRepository)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRequestRepository = processRequestRepository;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateCaseExecution execution) throws UseCaseException
  {
    if (execution.hasVariable(CALCULATE_AMOUNT) && (boolean) execution.getVariable(CALCULATE_AMOUNT))
    {
      updateRequestState(execution);
    }
    else
    {
      Map<String, Serializable> parameters = new HashMap<>();
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      BigDecimal requestedLoanAmount= (BigDecimal) execution.getVariable("requestedLoanAmount");
      parameters.put(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
      parameters.put(DEBT_INCOME_INSURANCE_STRING, String.valueOf(execution.getVariable(DEBT_INCOME_INSURANCE_STRING)));
      if (requestedLoanAmount != null) {
        parameters.put("requestedLoanAmount", bigDecimalToString(requestedLoanAmount));
      }
      
      UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
      UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
      updateRequestParameters.execute(input);

      String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
      processParams.put(ONLINE_SALARY, parameters);
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
      updateProcessParameters.execute(parametersInput);
      LOGGER.info("#############  ONLINE SALARY - Updated fixed accepted loan amount to {}, with process request id = [{}]", execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT), requestId);
    }

  }

  private void updateRequestState(DelegateCaseExecution execution) throws UseCaseException
  {
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String scoringLevelRisk = String.valueOf(execution.getVariable(SCORING_LEVEL_RISK));
    String pApprove = String.valueOf(execution.getVariable(SCORING_STATE));
    String grantLoanAmount = String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT));
    String interestRate = String.valueOf(execution.getVariable(INTEREST_RATE));

    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);
    parameters.put(GRANT_LOAN_AMOUNT_STRING, grantLoanAmount);
    parameters.put(SCORING_LEVEL_RISK, scoringLevelRisk);
    parameters.put(SCORING_STATE, pApprove);
    parameters.put(INTEREST_RATE, interestRate);
    parameters.put(SCORING_SCORE, String.valueOf(execution.getVariable(SCORING_SCORE)));
    parameters.put(LOAN_CLASS_NAME, String.valueOf(execution.getVariable(LOAN_CLASS_NAME)));
    parameters.put(FIRST_PAYMENT_DATE, String.valueOf(execution.getVariable(FIRST_PAYMENT_DATE)));
    parameters.put(LOAN_PRODUCT_DESCRIPTION, String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION)));
    parameters.put(FULL_NAME, String.valueOf(execution.getVariable(FULL_NAME)));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ONLINE_SALARY, parameters);

    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);

    UpdateRequestStateInput updateRequestStateInput = new UpdateRequestStateInput(requestId, ProcessRequestState.fromStringToEnum(
        String.valueOf(execution.getVariable(STATE))));
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    updateRequestState.execute(updateRequestStateInput);

    LOGGER.info("#############  ONLINE SALARY - Updated process state to {}, with process request id = [{}]", execution.getVariable(STATE), requestId);
  }
}
