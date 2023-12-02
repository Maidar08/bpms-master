package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static consumption.constant.CamundaVariableConstants.FAILED_STATUS;
import static consumption.constant.CamundaVariableConstants.LOAN_DISBURSEMENT_TASK;
import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.constant.CamundaVariableConstants.SUCCESS_STATUS;
import static consumption.util.CamundaUtils.getDisburseTransactionParam;
import static consumption.util.CamundaUtils.toLoanDisbursement;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLECTED_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFMISC10;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.changeChannelAndBranch;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

/**
 * @author Sukhbat
 */
public class DirectOnlineLoanDisbursementTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineLoanDisbursementTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;
  private final Environment environment;

  public DirectOnlineLoanDisbursementTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AimServiceRegistry aimServiceRegistry, Environment environment)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.aimServiceRegistry = aimServiceRegistry;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String branchId = String.valueOf(execution.getVariable(BRANCH_NUMBER));
    try
    {
      String creditAccount = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
      String fixedAcceptedAmountString = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
      BigDecimal disburseAmount = removeCommaAndGetBigDecimal(fixedAcceptedAmountString);
      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      String disburseCurrency = String.valueOf(execution.getVariable("disbursementSttlCcy"));
      String accCurrency = String.valueOf(execution.getVariable("accCurrency"));
      String logHash = getLogPrefix(processTypeId);
      String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
      if (branchId.equals(CHO_BRANCH))
      {
        branchId = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER), "Could not get CHO branch number from config file!");
      }

      Map<String, Object> partTransactions = getDisburseTransactionParam(creditAccount, accCurrency, disburseAmount);
      Map<String, Object> requestBody = toLoanDisbursement(loanAccountNumber, branchId, disburseAmount, disburseCurrency, partTransactions);
      //Зээлийн дансны валют
      requestBody.put("loanAccountCcy", disburseCurrency);
      //Зээлийн дүн
      requestBody.put("loanAmount", disburseAmount);
      requestBody.put("grossNetDisbt", environment.getProperty("grossNetDisbt"));
      requestBody.put(PROCESS_TYPE_ID, processTypeId);
      requestBody.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      final Map<String, Object> response = directOnlineCoreBankingService.createLoanDisbursementCharge(requestBody);
      final String status = String.valueOf(response.get("status"));
      if (status.equals("SUCCESS"))
      {
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          LOGGER.info("{} LOAN DISBURSEMENT SUCCESSFULLY CREATED. CIF NUMBER = [{}]  REQUEST ID = [{}] ACCOUNT NUMBER = [{}] SETTLE AMOUNT = [{}] TRACKNUMBER = [{}]",
              logHash, cif, requestId, loanAccountNumber, disburseAmount, trackNumber);
        }
        else
        {
          LOGGER.info("{} LOAN DISBURSEMENT SUCCESSFULLY CREATED. CIF NUMBER = [{}]  REQUEST ID = [{}] ACCOUNT NUMBER = [{}] SETTLE AMOUNT = [{}] ",
              logHash, cif, requestId, loanAccountNumber, disburseAmount);
        }
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          execution.setVariable(CONFMISC10, response.get(CONFMISC10));
          execution.setVariable(COLLECTED_AMOUNT, response.get(COLLECTED_AMOUNT));
          updateTaskStatus(execution, LOAN_DISBURSEMENT_TASK, SUCCESS_STATUS);
        }
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          LOGGER.info("{} FAILED TO CREATE LOAN DISBURSEMENT!! CIF NUMBER = [{}] REQUESTID = [{}] ACCOUNT NUMBER = [{}], TRACKNUMBER = [{}]", logHash, cif, requestId,
              loanAccountNumber, trackNumber);
        }
        else
        {
          LOGGER.info("{} FAILED TO CREATE LOAN DISBURSEMENT!! CIF NUMBER = [{}] REQUESTID = [{}] ACCOUNT NUMBER = [{}]", logHash, cif, requestId,
              loanAccountNumber);
        }
        if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        {
          updateFailedRequest(execution, requestId, processInstanceId, branchId, processTypeId);
        }
        throw new BpmnError("Loan Disbursement", errorMessage);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        updateTaskStatus(execution, LOAN_DISBURSEMENT_TASK, FAILED_STATUS);
        updateFailedRequest(execution, requestId, processInstanceId, branchId, processTypeId);
      }
      throw new BpmnError("Loan Disbursement", e.getMessage());
    }
  }

  private void updateFailedRequest(DelegateExecution execution, String requestId, String processInstanceId, String branchId, String processTypeId)
      throws UseCaseException
  {
    String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processTypeId, branchId);
    execution.setVariable(BRANCH_NUMBER, defaultBranch);

    updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, ProcessRequestState.DISBURSE_FAILED);
    changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment, requestId, processInstanceId, processTypeId, defaultBranch, "Internet bank");
  }
}
