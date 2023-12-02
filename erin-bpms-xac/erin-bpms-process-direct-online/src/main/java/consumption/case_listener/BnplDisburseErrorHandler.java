package consumption.case_listener;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.util.process.BpmUtils;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.getTypeByProcessType;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;

public class BnplDisburseErrorHandler implements ExecutionListener
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplDisburseErrorHandler.class);

  public BnplDisburseErrorHandler(AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    String processState = "DISBURSE_FAILED";
    execution.setVariable(STATE, processState);

    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String branchNumber = String.valueOf(execution.getVariable(BRANCH_NUMBER));

    String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processType, branchNumber);
    execution.setVariable(BRANCH_NUMBER, defaultBranch);

    boolean isStateUpdated = DigitalLoanUtils.updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, ProcessRequestState.fromStringToEnum(processState));
    if (isStateUpdated)
    {
      LOGGER.info("############# {} : Updated process state to {}, with process request id = [{}]. {}",
          getTypeByProcessType(processType), processState, requestId,
          (StringUtils.isBlank(BpmUtils.getStringValue(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
    }

    DigitalLoanUtils.changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment, requestId, processInstanceId, processType, defaultBranch, "Internet bank");
  }
}
