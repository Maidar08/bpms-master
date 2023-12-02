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

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.util.process.DigitalLoanUtils;

import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.util.CamundaUtils.toTransaction;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.removeCommaAndGetBigDecimal;

public class AddTransactionTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AddTransactionTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;
  private final Environment environment;

  public AddTransactionTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository, Environment environment)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.processRequestRepository = processRequestRepository;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      String accountId = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));

      String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
      String settleAmountString = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
      BigDecimal requestedLoanAmount= (BigDecimal) execution.getVariable("requestedLoanAmount");
      BigDecimal settleAmt = processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE) && requestedLoanAmount != null ?
          requestedLoanAmount : removeCommaAndGetBigDecimal(settleAmountString);
      String loanAccountBranch = String.valueOf(execution.getVariable("branch"));
      String defaultAccount = String.valueOf(execution.getVariable("defaultAccount"));
      String choBranchId = Objects.requireNonNull(environment.getProperty(CHO_BRANCH_NUMBER), "Could not get CHO branch number from config file!");

      Map<String, Object> requestBody = toTransaction(execution, accountId, defaultAccount, settleAmt, settleAmt, null);
      requestBody.put(PROCESS_REQUEST_ID, requestId);
      requestBody.put(PHONE_NUMBER, execution.getVariable(PHONE_NUMBER));

      final Map<String, Object> response = directOnlineCoreBankingService.addTransaction(requestBody);
      final String status = String.valueOf(response.get("status"));
      if (status.equals("SUCCESS"))
      {
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "SUCCESSFULLY TRANSACT TO DEFAULT ACCOUNT. CIF NUMBER = [{}], REQUEST ID = [{}]", cif, requestId);
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "FAILED TO TRANSACT TO DEFAULT ACCOUNT. CIF NUMBER = [{}] REQUEST ID = [{}]", cif, requestId);

        if (loanAccountBranch.equals(choBranchId))
        {
          updateRequestState(execution, ProcessRequestState.DISBURSE_FAILED);
        }
        throw new BpmnError("Transact to default account", errorMessage);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      throw new BpmnError("Transfer to default account", e.getMessage());
    }
  }

  private void updateRequestState(DelegateExecution execution, ProcessRequestState state) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    boolean isStateUpdated = DigitalLoanUtils.updateRequestState(processRequestRepository, processRequestId, state);
    if (isStateUpdated)
    {
      LOGGER.info("{} Updated process request state to {} with request id [{}].", ONLINE_SALARY_LOG_HASH,fromEnumToString(state), processRequestId);
    }
  }
}
