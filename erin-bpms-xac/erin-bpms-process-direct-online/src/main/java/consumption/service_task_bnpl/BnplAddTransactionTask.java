package consumption.service_task_bnpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.MessagingService;
import mn.erin.domain.bpm.usecase.bnpl.SetBnplInvoiceState;
import mn.erin.domain.bpm.usecase.bnpl.SetInvoiceStateInput;

import static consumption.constant.CamundaVariableConstants.FAILED_STATUS;
import static consumption.constant.CamundaVariableConstants.LOCALE;
import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.constant.CamundaVariableConstants.SUCCESS_STATUS;
import static consumption.util.CamundaUtils.toTransaction;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MESSAGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_AMOUNT;
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_NUM;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class BnplAddTransactionTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplAddTransactionTask.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final MessagingService messagingService;
  private final BpmsServiceRegistry bpmsServiceRegistry;

  public BnplAddTransactionTask(DirectOnlineCoreBankingService directOnlineCoreBankingService, MessagingService messagingService,
      BpmsServiceRegistry bpmsServiceRegistry)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
    this.messagingService = messagingService;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String logHash = BNPL_LOG;
    boolean isInstantLoan = false;
    if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      isInstantLoan = true;
      logHash = INSTANT_LOAN_LOG;
    }
    String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
    String locale = String.valueOf(execution.getVariable(LOCALE));
    String cif = String.valueOf(execution.getVariable(CIF_NUMBER));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    try
    {
      String invoiceNum = String.valueOf(execution.getVariable(INVOICE_NUM));
      String accountId = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
      String defaultAccount = String.valueOf(execution.getVariable("defaultAccount"));
      BigDecimal invoiceAmount = (BigDecimal) execution.getVariable(INVOICE_AMOUNT);
      Long advanceAmountPercent = (Long) execution.getVariable("advanceAmountPercent");
      BigDecimal advanceAmount = BigDecimal.ZERO;
      if (isInstantLoan)
      {
        String clearBalanceString = getValidString(execution.getVariable("clearBalance"));
        String chargeString = getValidString(execution.getVariable("charge"));
        if (StringUtils.isNotBlank(clearBalanceString) && StringUtils.isNotBlank(chargeString))
        {
          BigDecimal clearBalance = new BigDecimal(clearBalanceString);
          BigDecimal charge = new BigDecimal(chargeString);
          advanceAmount = clearBalance.divide(BigDecimal.valueOf(100)).multiply(charge);
        }
      }
      else
      {
        advanceAmount = invoiceAmount.multiply(BigDecimal.valueOf(advanceAmountPercent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.UP);
      }

      Map<String, Object> requestBody = toTransaction(execution, defaultAccount, accountId, advanceAmount, advanceAmount,
          isInstantLoan ? null : "advanceTransaction");
      requestBody.put(PROCESS_REQUEST_ID, requestId);
      requestBody.put(PHONE_NUMBER, execution.getVariable(PHONE_NUMBER));

      final Map<String, Object> response = directOnlineCoreBankingService.addTransaction(requestBody);
      final String status = String.valueOf(response.get("status"));
      if (status.equals("SUCCESS"))
      {
        if (isInstantLoan)
        {
          updateTaskStatus(execution, "Add Charging Transaction Task", SUCCESS_STATUS);
        }
        else
        {
          updateInvoiceState(invoiceNum);
        }
        LOGGER.info("{} SUCCESSFULLY TRANSACT TO DEFAULT ACCOUNT. CIF NUMBER = [{}], REQUEST ID = [{}], ADVANCE AMOUNT = [{}]. {}",
            logHash, cif, requestId, advanceAmount, isInstantLoan ? "ActionType: " + execution.getVariable(ACTION_TYPE) + "." : "");
      }
      else
      {
        String errorMessage = String.valueOf(response.get("error"));
        execution.setVariable(ERROR_CAUSE, errorMessage);
        if (isInstantLoan)
        {
          updateTaskStatus(execution, "Add Charging Transaction Task", FAILED_STATUS);
        }
        else
        {Map<String, String> input = new HashMap<>();
          input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
          input.put(PHONE_NUMBER, phoneNumber);
          input.put(LOCALE, locale);
          sendSMS(input);
        }
        LOGGER.info("{} FAILED TO TRANSACT TO DEFAULT ACCOUNT. CIF NUMBER = [{}] REQUEST ID = [{}]. {}", logHash, cif, requestId,
            (isInstantLoan ? "ActionType: " + execution.getVariable(ACTION_TYPE) + "." : ""));
        throw new BpmnError("Transaction", errorMessage);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      if (isInstantLoan)
      {
        updateTaskStatus(execution, "Add Charging Transaction Task", FAILED_STATUS);
      }
      else
      {
        Map<String, String> input = new HashMap<>();
        input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
        input.put(PHONE_NUMBER, phoneNumber);
        input.put(LOCALE, locale);
        sendSMS(input);
      }
      LOGGER.info("{} FAILED TO TRANSACT TO DEFAULT ACCOUNT. CIF NUMBER = [{}] REQUEST ID = [{}]. {}", logHash, cif, requestId,
          (isInstantLoan ? "ActionType: " + execution.getVariable(ACTION_TYPE) + "." : ""));
      throw new BpmnError("Transaction", getValidString(e.getMessage()).equals(EMPTY_VALUE) ? getValidString(e) : e.getMessage());
    }
  }

  private void updateInvoiceState(String invoiceNum) throws UseCaseException
  {
    SetInvoiceStateInput setInvoiceStateInput = new SetInvoiceStateInput(invoiceNum, "CONFIRMED_NOT_DISBURSED");
    SetBnplInvoiceState setBnplInvoiceState = new SetBnplInvoiceState(bpmsServiceRegistry.getBnplCoreBankingService());
    setBnplInvoiceState.execute(setInvoiceStateInput);
  }

  private void sendSMS(Map<String, String> input) throws BpmServiceException
  {
    String message = MessageUtil.getMessageByLocale("bnpl.xac.transaction.error", input.get(LOCALE)).getText();
    input.put(MESSAGE, message);
    final boolean smsState = this.messagingService.sendSms(input);
    if (smsState)
    {
      LOGGER.info(BNPL_LOG + "SUCCESSFULLY SENT SMS OF DISBURSEMENT DETAIL");
    }
    else
    {
      LOGGER.info(BNPL_LOG + "SMS OF DISBURSEMENT DETAIL IS FAILED");
    }
  }
}
