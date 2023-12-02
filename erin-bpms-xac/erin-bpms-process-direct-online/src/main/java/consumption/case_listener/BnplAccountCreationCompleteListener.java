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
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplAccountCreationCompleteListener implements ExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplAccountCreationCompleteListener.class);
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;

  public BnplAccountCreationCompleteListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String currentAccountNumber = (String) execution.getVariable(CURRENT_ACCOUNT_NUMBER);
    String loanAccountNumber = (String) execution.getVariable(LOAN_ACCOUNT_NUMBER);
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
    parameters.put(LOAN_ACCOUNT_NUMBER, loanAccountNumber);

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    ParameterEntityType parameterEntityType = getEntityType(getValidString(execution.getVariable(PROCESS_TYPE_ID)));
    processParams.put(parameterEntityType, parameters);
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    updateProcessParameters.execute(input);
    LOGGER.info("######### Loan account number {} saved in Process Parameter table.", loanAccountNumber);
  }
  private ParameterEntityType getEntityType(String processTypeId){
    switch (processTypeId){
    case BNPL_PROCESS_TYPE_ID:
      return ParameterEntityType.BNPL;
    case INSTANT_LOAN_PROCESS_TYPE_ID:
      return  ParameterEntityType.INSTANT_LOAN;
    default:
      return ParameterEntityType.ONLINE_LEASING;
    }
  }
}
