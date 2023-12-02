package consumption.service_task_online_leasing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersOutput;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static consumption.constant.CamundaVariableConstants.AMOUNT;
import static consumption.constant.CamundaVariableConstants.GRANT_MINIMUM_AMOUNT;
import static consumption.util.CamundaUtils.getFirstPaymentDate;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CHARGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DOWNLOAD_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.FEES;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.KEY_FIELD_1;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.RETRY_ATTEMPT;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateProcessParameters;

public class StartOnlineLeasingProcessTask implements JavaDelegate
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public StartOnlineLeasingProcessTask(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processInstanceId = execution.getProcessInstanceId();
    setLoanRequestAmountVariable(execution, processInstanceId);

    execution.setVariable(PROCESS_INSTANCE_ID, processInstanceId);
    execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT, 0);
    execution.setVariable(PROCESS_INSTANCE_ID, processInstanceId);
    execution.setVariable(DOWNLOAD_MONGOL_BANK, true);
    execution.setVariable(FIRST_PAYMENT_DATE, getFirstPaymentDate());

    execution.getProcessEngine().getRuntimeService().getVariable(processInstanceId, RETRY_ATTEMPT);

    final Object retryNumber = execution.getProcessEngine().getRuntimeService().getVariable(processInstanceId, RETRY_ATTEMPT);
    execution.setVariable(RETRY_ATTEMPT, retryNumber);

    // this code was copied from get branch task
    //this is done to always add 76 points in scoring dmn
    execution.removeVariable("pro_btw_coll_ln");
    execution.setVariable("pro_btw_coll_ln", Double.valueOf(2));

    String cifNumber = getValidString(execution.getVariable(CIF_NUMBER));
    String keyField1 = getValidString(execution.getVariable(KEY_FIELD_1));
    String trackNumber = getValidString(execution.getVariable(TRACK_NUMBER));
    String email = String.valueOf(execution.getVariable(EMAIL));
    String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
    String grantMinimumAmount = String.valueOf(execution.getVariable(GRANT_MINIMUM_AMOUNT));
    Map<String, Serializable> parameters = new HashMap<>();

    parameters.put(CIF_NUMBER, cifNumber);
    parameters.put(EMAIL, email);
    parameters.put(PHONE_NUMBER, phoneNumber);
    parameters.put(GRANT_MINIMUM_AMOUNT, grantMinimumAmount);
    parameters.put(BRANCH_NUMBER, String.valueOf(execution.getVariable(BRANCH_NUMBER)));
    parameters.put(KEY_FIELD_1, keyField1);
    parameters.put(TRACK_NUMBER, trackNumber);


    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    processParams.put(ONLINE_LEASING, parameters);
    UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, processParams);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
    final UpdateProcessParametersOutput output = updateProcessParameters.execute(input);
  }

  private void setLoanRequestAmountVariable(DelegateExecution execution, String processInstanceId) throws UseCaseException
  {
    String productCategory = getValidString(execution.getVariable(PRODUCT_CATEGORY));
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(bpmsRepositoryRegistry.getDefaultParameterRepository());
    GetGeneralInfoInput input = new GetGeneralInfoInput(productCategory, "LoanTerms");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);
    if (null != defaultParameters && !defaultParameters.isEmpty())
    {
      Map<String, Object> loanTerms = (Map<String, Object>) defaultParameters.get("LoanTerms");
      Object maxAmount = loanTerms.get("MaxAmount");
      Object term = loanTerms.get("Term");
      double interestRate = Double.parseDouble(getValidString(loanTerms.get("Interest")));
      long  minimumAmount = Long.parseLong(getValidString(loanTerms.get("grantMinimumAmount")));

      execution.setVariable(AMOUNT, maxAmount);
      execution.setVariable(TERM, term);
      execution.setVariable(INTEREST_RATE,interestRate);
      execution.setVariable("grantMinimumAmount", minimumAmount);

      Map<String, Serializable> parameters = new HashMap<>();
      Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
      parameters.put(INTEREST_RATE, interestRate);
      parameters.put(CHARGE, 1 );
      parameters.put(TERM, Integer.parseInt(getValidString(term)));

      processParams.put(ONLINE_LEASING, parameters);
      updateProcessParameters(aimServiceRegistry, bpmsRepositoryRegistry.getProcessRepository(), processInstanceId, processParams);
    }
  }
}

