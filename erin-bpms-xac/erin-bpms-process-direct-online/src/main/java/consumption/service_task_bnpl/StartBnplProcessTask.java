package consumption.service_task_bnpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersOutput;

import static consumption.constant.CamundaVariableConstants.AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.DOWNLOAD_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;

public class StartBnplProcessTask implements JavaDelegate
{
  private final DefaultParameterRepository defaultParameterRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;

  public StartBnplProcessTask(DefaultParameterRepository defaultParameterRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRepository processRepository)
  {
    this.defaultParameterRepository = defaultParameterRepository;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRepository = processRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    setLoanRequestAmountVariable(execution);
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, 0);
    execution.setVariable(DOWNLOAD_MONGOL_BANK, true);
    String processInstanceId = execution.getProcessInstanceId();
    execution.setVariable(PROCESS_INSTANCE_ID, processInstanceId);

    execution.getProcessEngine().getRuntimeService().getVariable(processInstanceId, RETRY_ATTEMPT);

    final Object retryNumber = execution.getProcessEngine().getRuntimeService().getVariable(processInstanceId, RETRY_ATTEMPT);
    execution.setVariable(RETRY_ATTEMPT, retryNumber);

    // this code was copied from get branch task
    //this is done to always add 76 points in scoring dmn
    execution.removeVariable("pro_btw_coll_ln");
    execution.setVariable("pro_btw_coll_ln", Double.valueOf(2));

    String email = String.valueOf(execution.getVariable(EMAIL));
    String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(EMAIL, email);
    parameters.put(PHONE_NUMBER, phoneNumber);
    parameters.put(BRANCH_NUMBER, String.valueOf(execution.getVariable(BRANCH_NUMBER)));
    parameters.put(DAN_REGISTER, String.valueOf(execution.getVariable(DAN_REGISTER)));

    // this is implemented for query
    String grantLoanAmount = execution.hasVariable(GRANT_LOAN_AMOUNT) ? String.valueOf(execution.getVariable(GRANT_LOAN_AMOUNT)) : "0";
    String score = execution.hasVariable(SCORING_SCORE) ? String.valueOf(execution.getVariable(SCORING_SCORE)) : "0";
    String scoreLevel = execution.hasVariable(SCORING_LEVEL_RISK) ? String.valueOf(execution.getVariable(SCORING_LEVEL_RISK)) : "0";
    parameters.put(FULL_NAME, String.valueOf(execution.getVariable(FULL_NAME)));
    parameters.put(SCORING_SCORE, score);
    parameters.put(SCORING_LEVEL_RISK, scoreLevel);
    parameters.put(GRANT_LOAN_AMOUNT, grantLoanAmount);

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(BNPL, parameters);
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    final UpdateProcessParametersOutput output = updateProcessParameters.execute(input);
  }

  private void setLoanRequestAmountVariable(DelegateExecution execution) throws UseCaseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput("BnplLoan", "LoanTerms");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);
    if (null != defaultParameters && !defaultParameters.isEmpty())
    {
      Map<String, Object> loanTerms = (Map<String, Object>) defaultParameters.get("LoanTerms");
      Object maxAmount = loanTerms.get("MaxAmount");
      Object term = loanTerms.get("Term");
      execution.setVariable(AMOUNT, maxAmount);
      execution.setVariable(TERM, term);
    }
  }
}



