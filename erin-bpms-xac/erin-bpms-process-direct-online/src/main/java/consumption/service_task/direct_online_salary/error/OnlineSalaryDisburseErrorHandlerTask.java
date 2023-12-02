package consumption.service_task.direct_online_salary.error;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DISBURSE_FAILED;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.changeChannelAndBranch;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryDisburseErrorHandlerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryDisburseErrorHandlerTask.class);
  private final ProcessRequestRepository processRequestRepository;
  private final AimServiceRegistry aimServiceRegistry;
  private final DefaultParameterRepository defaultParameterRepository;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;

  public OnlineSalaryDisburseErrorHandlerTask(ProcessRequestRepository processRequestRepository, AimServiceRegistry aimServiceRegistry,
      DefaultParameterRepository defaultParameterRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.processRequestRepository = processRequestRepository;
    this.aimServiceRegistry = aimServiceRegistry;
    this.defaultParameterRepository = defaultParameterRepository;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (execution.hasVariable("errorProcess"))
    {
      boolean isInstantLoan = false;
      if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        isInstantLoan = true;
      }
      final Object errorProcess = execution.getVariable("errorProcess");
      if (!isInstantLoan)
      {
        execution.setVariable(STATE, "DISBURSE_FAILED");
      }

      final String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      final String branchNumber = String.valueOf(execution.getVariable(BRANCH_NUMBER));
      String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));

      final String defaultBranch = getDefaultBranchExceptCho(defaultParameterRepository, environment, processType, branchNumber);
      execution.setVariable(BRANCH_NUMBER, defaultBranch);

      changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment,requestId, processInstanceId, processType, defaultBranch, "Internet bank");

      if (!isInstantLoan){
        updateRequestState(processRequestRepository, requestId, DISBURSE_FAILED);
      }
      LOGGER.error("{} ERROR OCCURRED AFTER ACCOUNT CREATION AT: {}. {}", (isInstantLoan ? INSTANT_LOAN_LOG : ONLINE_SALARY_LOG_HASH),errorProcess,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }
}
