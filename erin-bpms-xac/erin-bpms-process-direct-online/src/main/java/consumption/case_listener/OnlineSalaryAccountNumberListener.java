package consumption.case_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;

public class OnlineSalaryAccountNumberListener implements CaseExecutionListener
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;

  public OnlineSalaryAccountNumberListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateCaseExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String currentAccountNumber = (String) execution.getVariable(CURRENT_ACCOUNT_NUMBER);
    String loanAccountNumber = (String) execution.getVariable(LOAN_ACCOUNT_NUMBER);
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(CURRENT_ACCOUNT_NUMBER, currentAccountNumber);
    parameters.put(LOAN_ACCOUNT_NUMBER, loanAccountNumber);

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ParameterEntityType.ONLINE_SALARY, parameters);
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    updateProcessParameters.execute(input);
  }
}
