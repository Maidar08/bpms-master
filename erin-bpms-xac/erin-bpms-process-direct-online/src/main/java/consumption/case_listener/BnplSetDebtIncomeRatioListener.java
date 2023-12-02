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
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.DEBT_INCOME_ISSUANCE_PERCENT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplSetDebtIncomeRatioListener implements ExecutionListener
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplSetDebtIncomeRatioListener.class);

  public BnplSetDebtIncomeRatioListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
    parameters.put(DEBT_INCOME_ISSUANCE_PERCENT, String.valueOf(execution.getVariable(DEBT_INCOME_ISSUANCE_PERCENT)));

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);


    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ParameterEntityType.BNPL, parameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    LOGGER.info(BNPL_LOG + "Updated Disburse debt income ratio to process parameter with value = [{}]",
        getValidString(execution.getVariable(DEBT_INCOME_ISSUANCE_PERCENT)));
  }
}
