package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.util.Map;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.util.CamundaUtils.calculateDisbursementCharge;
import static consumption.util.CamundaUtils.toTransaction;
import static consumption.util.NumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FEES;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.fromEnumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class TransactRestAmountTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactRestAmountTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;

  public TransactRestAmountTask(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      ProcessRequestRepository processRequestRepository)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.processRequestRepository = processRequestRepository;
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
      BigDecimal requestedLoanAmount= (BigDecimal) execution.getVariable("requestedLoanAmount");
      String settleAmountString = processTypeId.equals(ONLINE_SALARY_PROCESS_TYPE) && requestedLoanAmount != null ?
          getThousandSeparatedString(getValidString(requestedLoanAmount)) :
          String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING));
      String fees = String.valueOf(execution.getVariable(FEES));
      BigDecimal chrSettleAmt = calculateDisbursementCharge(settleAmountString, fees);
      String deductionAccount = String.valueOf(execution.getVariable("deductionAccount"));

      Map<String, Object> requestBody = toTransaction(execution, deductionAccount, accountId, chrSettleAmt, chrSettleAmt, null);
      requestBody.put(PROCESS_REQUEST_ID, requestId);
      requestBody.put(PHONE_NUMBER, execution.getVariable(PHONE_NUMBER));

      final Map<String, Object> response = directOnlineCoreBankingService.addTransaction(requestBody);
      final String status = String.valueOf(response.get("status"));

      if (status.equals("SUCCESS"))
      {
        LOGGER.info(ONLINE_SALARY_LOG_HASH + "SUCCESSFULLY TRANSFER WITH CHARGE AMOUNT. CIF NUMBER = [{}]  REQUEST ID = [{}] CHARGE AMOUNT = [{}]",
            cif, requestId, chrSettleAmt);
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);

        LOGGER.info(ONLINE_SALARY_LOG_HASH + "FAILED TO TRANSFER WITH CHARGE AMOUNT!! CIF NUMBER = [{}] REQUEST ID = [{}]",
            cif, requestId);
        updateRequestState(execution, ProcessRequestState.DISBURSE_FAILED);
        throw new BpmnError("Transfer Rest Amount", errorMessage);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();

      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }

      throw new BpmnError("Transfer Rest Amount", e.getMessage());
    }
  }

  private void updateRequestState(DelegateExecution execution, ProcessRequestState state) throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
    UpdateRequestStateInput input = new UpdateRequestStateInput(processRequestId, state);
    UpdateRequestStateOutput output = updateRequestState.execute(input);
    boolean isStateUpdated = output.isUpdated();

    if (isStateUpdated)
    {
      LOGGER.info(ONLINE_SALARY_LOG_HASH + "Updated process request state to {} with request id [{}].", fromEnumToString(state), processRequestId);
    }
  }
}

