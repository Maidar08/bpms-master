package consumption.case_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import consumption.util.CamundaUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;

public class RefundTransactionListener implements ExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(RefundTransactionListener.class);
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final ProcessRepository processRepository;
  private final ProcessRequestRepository processRequestRepository;
  private final Environment environment;

  public RefundTransactionListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, ProcessRepository processRepository,
      ProcessRequestRepository processRequestRepository, Environment environment)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.processRepository = processRepository;
    this.processRequestRepository = processRequestRepository;
    this.environment = environment;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));

    if (execution.hasVariable("errorProcess"))
    {
      final Object errorTransaction = execution.getVariable("errorProcess");
      final String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      String currentAccountNumber = (String) execution.getVariable(CURRENT_ACCOUNT_NUMBER);
      String defaultAccount = String.valueOf(execution.getVariable("defaultAccount"));

      Map<String, Serializable> parameters = new HashMap<>();
      if (processTypeId.equals(BNPL_PROCESS_TYPE_ID))
      {
        parameters.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
        parameters.put("defaultAccount", defaultAccount);
      }
      else if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processTypeId,
            String.valueOf(execution.getVariable(BRANCH_NUMBER)));
        execution.setVariable(BRANCH_NUMBER, defaultBranch);
        parameters.put(BRANCH_NUMBER, defaultBranch);
      }
      parameters.put(CHANNEL, "Internet bank");

      UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
      UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
      updateRequestParameters.execute(input);

      Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
      processParams.put(ParameterEntityType.getTypeByProcessType(processTypeId), parameters);
      UpdateProcessParametersInput processParametersInput = new UpdateProcessParametersInput(processInstanceId, processParams);
      UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
      updateProcessParameters.execute(processParametersInput);
      LOGGER.error(BNPL_LOG + "ERROR OCCURRED ON REFUND TRANSACTION TO DEFAULT ACCOUNT: {}", errorTransaction);
    }
  }
}
