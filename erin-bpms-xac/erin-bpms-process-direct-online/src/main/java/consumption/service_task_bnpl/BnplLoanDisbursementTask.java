package consumption.service_task_bnpl;

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
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static consumption.util.CamundaUtils.getDisburseTransactionParam;
import static consumption.util.CamundaUtils.toLoanDisbursement;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getDefaultBranchExceptCho;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.changeChannelAndBranch;

public class BnplLoanDisbursementTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplLoanDisbursementTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;
  private final Environment environment;
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public BnplLoanDisbursementTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, ProcessRequestRepository processRequestRepository,
      Environment environment, AimServiceRegistry aimServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.processRequestRepository = processRequestRepository;
    this.environment = environment;
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean isInstantLoan = false;
    String logHash = BNPL_LOG;
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String branchId = String.valueOf(execution.getVariable(BRANCH_NUMBER));
    try
    {
      String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
      String creditAccount = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
      BigDecimal disburseAmount;
      if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        isInstantLoan = true;
        logHash = INSTANT_LOAN_LOG;
        disburseAmount = new BigDecimal(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT).toString());
//        if ((boolean) execution.getVariable(HAS_ACTIVE_LOAN_ACCOUNT))
//        {
//          disburseAmount = disburseAmount.add(new BigDecimal(execution.getVariable(XAC_CLOSING_LOAN_AMOUNT).toString()));
//        }
//        String actionType = getValidString(execution.getVariable(ACTION_TYPE));
//        if (!StringUtils.isBlank(actionType) && (actionType.equals(EXTEND)))
//        {
//          String clearBalanceString = getValidString(execution.getVariable("clearBalance"));
//          if (StringUtils.isNotBlank(clearBalanceString))
//          {
//            disburseAmount = new BigDecimal(clearBalanceString);
//          }
//        }
      }
      else
      {
        disburseAmount = (BigDecimal) execution.getVariable(INVOICE_AMOUNT_75);
      }
      String loanAccountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      String disburseCurrency = String.valueOf(execution.getVariable("disbursementSttlCcy"));
      String accCurrency = String.valueOf(execution.getVariable("accCurrency"));
      if (branchId.equals(CHO_BRANCH))
      {
        branchId = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER), "Could not get CHO branch number from config file!");
      }

      Map<String, Object> partTransactions = getDisburseTransactionParam(creditAccount, accCurrency, disburseAmount);
      Map<String, Object> requestBody = toLoanDisbursement(loanAccountNumber, branchId, disburseAmount, disburseCurrency, partTransactions);
      //Зээлийн дансны валют
      requestBody.put("loanAccountCcy", disburseCurrency);
      //Зээлийн дүн
      final Map<String, Object> response = directOnlineCoreBankingService.createLoanDisbursement(requestBody);
      final String status = String.valueOf(response.get("status"));
      if (status.equals("SUCCESS"))
      {
        updateRequestState(execution, requestId, ProcessRequestState.DISBURSED, isInstantLoan, logHash);
        LOGGER.info("{} LOAN DISBURSEMENT SUCCESSFULLY CREATED. CIF NUMBER = [{}]  REQUEST ID = [{}] ACCOUNT NUMBER = [{}] SETTLE AMOUNT = [{}]. {}",
            logHash, cif, requestId, loanAccountNumber, disburseAmount, (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        if (isInstantLoan)
          updateTaskStatus(execution, "Loan Disbursement task", "Success");
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        updateRequestState(execution, requestId, ProcessRequestState.DISBURSE_FAILED, isInstantLoan, logHash);
        String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processType, branchId);
        execution.setVariable(BRANCH_NUMBER, defaultBranch);
        changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment,requestId, processInstanceId, processType, branchId,"Internet bank");
        LOGGER.info("{} FAILED TO CREATE LOAN DISBURSEMENT!! CIF NUMBER = [{}] REQUESTID = [{}] ACCOUNT NUMBER = [{}]. {}",
            logHash, cif, requestId, loanAccountNumber, (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        if (isInstantLoan)
          updateTaskStatus(execution, "Loan Disbursement task", "Failed");
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
      String defaultBranch = getDefaultBranchExceptCho(bpmsRepositoryRegistry.getDefaultParameterRepository(), environment, processType, branchId);
      execution.setVariable(BRANCH_NUMBER, defaultBranch);
      updateRequestState(execution, requestId, ProcessRequestState.DISBURSE_FAILED, isInstantLoan, logHash);
      changeChannelAndBranch(bpmsRepositoryRegistry, aimServiceRegistry, environment,requestId, processInstanceId, processType, defaultBranch, "Internet bank");
      throw new BpmnError("Loan Disbursement", e.getMessage());
    }
  }

  private void updateRequestState(DelegateExecution execution, String processRequestId, ProcessRequestState state, boolean isInstantLoan, String logHash) throws UseCaseException
  {
    boolean isStateUpdated = DigitalLoanUtils.updateRequestState(processRequestRepository, processRequestId, state);
    if (isStateUpdated)
    {
      LOGGER.info("{} Updated process request state to {} with request id [{}]. {}", logHash, fromEnumToString(state), processRequestId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
    }
  }
}
