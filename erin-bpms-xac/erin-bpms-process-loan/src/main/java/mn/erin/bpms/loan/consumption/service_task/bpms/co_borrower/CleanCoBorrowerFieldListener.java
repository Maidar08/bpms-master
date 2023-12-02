package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.setIndexedCoBorrowerVariable;
import static mn.erin.domain.bpm.BpmModuleConstants.ADDRESS_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.INCOME_TYPE_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.OCCUPANCY_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.STRING_AS_EMPTY;
import static mn.erin.domain.bpm.BpmModuleConstants.TYPE_CO_BORROWER;

/**
 * @author Tamir
 */
public class CleanCoBorrowerFieldListener implements TaskListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanCoBorrowerFieldListener.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository repositoryRegistry;

  public CleanCoBorrowerFieldListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository repositoryRegistry)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.repositoryRegistry = repositoryRegistry;
  }

  @Override
  public void notify(DelegateTask delegateTask)
  {
    LOGGER.info("########### Cleaning co-borrower main form fields ...");

    String caseInstanceId = delegateTask.getCaseInstanceId();
    DelegateExecution execution = delegateTask.getExecution();

    setIndexedCoBorrowerVariable(execution, REGISTER_NUMBER_CO_BORROWER, caseInstanceId);
    setIndexedCoBorrowerVariable(execution, CIF_NUMBER_CO_BORROWER, caseInstanceId);

    setIndexedCoBorrowerVariable(execution, FULL_NAME_CO_BORROWER, caseInstanceId);
    setIndexedCoBorrowerVariable(execution, ADDRESS_CO_BORROWER, caseInstanceId);

    setIndexedCoBorrowerVariable(execution, OCCUPANCY_CO_BORROWER, caseInstanceId);
    setIndexedCoBorrowerVariable(execution, PHONE_NUMBER_CO_BORROWER, caseInstanceId);

    setIndexedCoBorrowerVariable(execution, EMAIL_CO_BORROWER, caseInstanceId);
    setIndexedCoBorrowerVariable(execution, INCOME_TYPE_CO_BORROWER, caseInstanceId);

    Map<String, Serializable> parameter = new HashMap<>();

    if (execution.hasVariable(TYPE_CO_BORROWER))
    {
      setIndexedCoBorrowerVariable(execution, TYPE_CO_BORROWER, caseInstanceId);
      parameter.put(TYPE_CO_BORROWER, STRING_AS_EMPTY);
    }

    parameter.put(REGISTER_NUMBER_CO_BORROWER, STRING_AS_EMPTY);
    parameter.put(INCOME_TYPE_CO_BORROWER, STRING_AS_EMPTY);

    Map<ParameterEntityType, Map<String, Serializable>> updateParameters = new HashMap<>();
    updateParameters.put(ParameterEntityType.FORM, parameter);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, updateParameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, repositoryRegistry);

    try
    {
      updateProcessParameters.execute(input);
    }
    catch (UseCaseException e)
    {
      e.printStackTrace();
    }

    CaseService caseService = execution.getProcessEngine().getCaseService();
    execution.setVariable(OCCUPANCY_CO_BORROWER, "");
    execution.setVariable(PHONE_NUMBER_CO_BORROWER, "");
    execution.setVariable(EMAIL_CO_BORROWER, "");
    caseService.setVariable(caseInstanceId, OCCUPANCY_CO_BORROWER, "");
    caseService.setVariable(caseInstanceId, PHONE_NUMBER_CO_BORROWER, "");
    caseService.setVariable(caseInstanceId, EMAIL_CO_BORROWER, "");

    LOGGER.info("########### Successful cleaned up main form fields.");
  }
}