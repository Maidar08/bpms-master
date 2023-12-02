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

import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.TRANSACTION_FAILED;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.changeChannelAndBranch;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

public class TransactionFailedErrorHandler implements ExecutionListener
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFailedErrorHandler.class);

  public TransactionFailedErrorHandler(AimServiceRegistry aimServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    execution.setVariable(STATE, ProcessRequestState.fromEnumToString(TRANSACTION_FAILED));

    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String branchNumber = String.valueOf(execution.getVariable(BRANCH_NUMBER));

    String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processType, branchNumber);
    execution.setVariable(BRANCH_NUMBER, defaultBranch);
    changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment, requestId, processInstanceId, processType, defaultBranch, "Internet bank");
    updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, TRANSACTION_FAILED);
    LOGGER.info("############# FAILED TO TRANSACT. CIF NUMBER = [{}] REQUEST ID = [{}]. {}",
        execution.getVariable(CIF_NUMBER), requestId,
        (StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE))) ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
  }
}

