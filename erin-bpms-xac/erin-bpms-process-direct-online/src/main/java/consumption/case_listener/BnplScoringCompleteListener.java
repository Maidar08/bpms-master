package consumption.case_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;

import static consumption.constant.CamundaVariableConstants.SCORING_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.BNPL;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.ONLINE_LEASING;

/**
 * @author Odgavaa
 */
public class BnplScoringCompleteListener implements ExecutionListener
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public BnplScoringCompleteListener(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(BnplScoringCompleteListener.class);

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {

    final Object cif = execution.getVariable(CIF_NUMBER);
    final Object scoringLevel = execution.getVariable(SCORING_LEVEL_RISK);
    final Object score = execution.getVariable(SCORING_SCORE);
    final Object economicLevel = execution.getVariable("economic_level");
    final Object incomeType = execution.getVariable("income_type");
    final Object xacspan = execution.getVariable("xacspan");
    final Object workspan = execution.getVariable("workspan");
    final Object address = execution.getVariable("address");
    final Object gender = execution.getVariable("gender");
    final Object familyIncome = execution.getVariable("family_income");
    final Object joblessMembers = execution.getVariable("jobless_members");

    String instanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String pApprove = String.valueOf(execution.getVariable(SCORING_STATE));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    Map<String, Serializable> parameters = new HashMap<>();

    parameters.put(SCORING_STATE, pApprove);
    parameters.put(SCORING_LEVEL_RISK, String.valueOf(execution.getVariable(SCORING_LEVEL_RISK)));
    parameters.put(SCORING_SCORE, String.valueOf(execution.getVariable(SCORING_SCORE)));

    Map<ParameterEntityType, Map<String, Serializable>> processParams = new HashMap<>();
    if (processTypeId.equals(BNPL_PROCESS_TYPE_ID))
    {
      processParams.put(BNPL, parameters);
    }
    else
    {
      processParams.put(ONLINE_LEASING, parameters);
    }

    LOGGER.info("############# SCORING HAS CALCULATED FOR CIF = [{}] REQUEST ID = [{}]. "
            + "\n SCORING LEVEL:{} SCORE:{}"
            + " \n ECONOMIC LEVEL:{} INCOME TYPE:{} XACSPAN:{} WORKSPAN:{} ADDRESS:{} GENDER:{} FAMILY INCOME:{} JOBLESS MEMBERS:{}",
        cif, requestId, scoringLevel, score, economicLevel, incomeType, xacspan, workspan, address, gender, familyIncome, joblessMembers);

    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRepository());
    UpdateProcessParametersInput parametersInput = new UpdateProcessParametersInput(instanceId, processParams);
    updateProcessParameters.execute(parametersInput);

    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getProcessRequestRepository());
    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    updateRequestParameters.execute(input);
  }
}