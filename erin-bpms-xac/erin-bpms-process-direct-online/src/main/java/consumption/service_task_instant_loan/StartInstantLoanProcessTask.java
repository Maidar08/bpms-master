package consumption.service_task_instant_loan;

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
import static consumption.constant.CamundaVariableConstants.GRANT_MINIMUM_AMOUNT;
import static consumption.util.CamundaUtils.getFirstPaymentDate;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHARGE;
import static mn.erin.domain.bpm.BpmModuleConstants.DOWNLOAD_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FEES;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.KEY_FIELD_1;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class StartInstantLoanProcessTask implements JavaDelegate
{
  private final DefaultParameterRepository defaultParameterRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRepository processRepository;

  public StartInstantLoanProcessTask(DefaultParameterRepository defaultParameterRepository, AuthenticationService authenticationService,
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
    String processInstanceId = execution.getProcessInstanceId();
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, 0);
    execution.setVariable(PROCESS_INSTANCE_ID, processInstanceId);
    execution.setVariable(DOWNLOAD_MONGOL_BANK, true);
    execution.setVariable(FIRST_PAYMENT_DATE, getFirstPaymentDate());

    String email = String.valueOf(execution.getVariable(EMAIL));
    String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
    String grantMinimumAmount = String.valueOf(execution.getVariable(GRANT_MINIMUM_AMOUNT));


    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(EMAIL, email);
    parameters.put(PHONE_NUMBER, phoneNumber);
    parameters.put(GRANT_MINIMUM_AMOUNT, grantMinimumAmount);
    parameters.put(BRANCH_NUMBER, String.valueOf(execution.getVariable(BRANCH_NUMBER)));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(INSTANT_LOAN, parameters);
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    final UpdateProcessParametersOutput output = updateProcessParameters.execute(input);
  }

  private void setLoanRequestAmountVariable(DelegateExecution execution) throws UseCaseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput("instantLoan", "LoanTerms");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);
    if (null != defaultParameters && !defaultParameters.isEmpty())
    {
      Map<String, Object> loanTerms = (Map<String, Object>) defaultParameters.get("LoanTerms");
      Object maxAmount = loanTerms.get("MaxAmount");
      Object term = loanTerms.get("CalculationTerm");
      double charge = getNumericCharge(String.valueOf(loanTerms.get("Charge")));
      double interestRate = Double.parseDouble(getValidString(loanTerms.get("Interest")));
      execution.setVariable(AMOUNT, maxAmount);
      execution.setVariable(TERM, term);
      execution.setVariable(CHARGE, charge);
      execution.setVariable(FEES, charge);
      execution.setVariable("Interest", interestRate);
      execution.setVariable(INTEREST_RATE, interestRate);
    }
  }

  private double getNumericCharge(String charge)
  {
    charge = charge.replace("%", "");
    return Double.parseDouble(charge);
  }
}
