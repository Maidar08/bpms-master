package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.message.Message;
import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.bpm.service.MessagingService;

import static consumption.constant.CamundaVariableConstants.LOCALE;
import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MESSAGE;
import static mn.erin.common.utils.NumberUtils.bigDecimalToString;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CURRENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.EXTEND;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class DirectOnlineSendDisbursementDetailSms implements JavaDelegate
{
  private final MessagingService messagingService;
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineSendDisbursementDetailSms.class);
  private static final String SMS_XAC_DISBURSEMENT_DETAIL = "sms.xac.DISBURSMENT_DETAIL_SMS";
  private static final String SMS_XAC_BNPL_DISBURSEMENT_DETAIL = "sms.xac.BNPL_LOAN_DISBURSED_SMS";
  private static final String SMS_XAC_INSTANT_LOAN_DISBURSEMENT_DETAIL = "sms.xac.INSTANT_LOAN_DISBURSED_SMS";
  private static final String SMS_XAC_ONLINE_LEASING_DISBURSEMENT = "sms.xac.ONLINE_LEASING_DISBURSED_SMS";
  private static final String SMS_XAC_INSTANT_LOAN_EXTEND_DETAIL = "sms.xac.INSTANT_LOAN_EXTEND_SMS";

  public DirectOnlineSendDisbursementDetailSms(MessagingService messagingService)
  {
    this.messagingService = messagingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    boolean isInstantLoan = false;
    try
    {
      String processType = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      String phoneNumber = String.valueOf(execution.getVariable(PHONE_NUMBER));
      String locale = String.valueOf(execution.getVariable(LOCALE));
      String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
      String account = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));
      LocalDate localDate = LocalDate.now();
      String currentSystemDate = localDate.toString();

      Message message;
      String log = getLogPrefix(processType);

      if (processType.equals(BNPL_PROCESS_TYPE_ID))
      {
        message = MessageUtil.getMessageByLocale(SMS_XAC_BNPL_DISBURSEMENT_DETAIL, locale.toLowerCase());
      }
      else if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
      {
        isInstantLoan = true;
        String actionType = String.valueOf(execution.getVariable(ACTION_TYPE));
        account = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
        if (actionType.equals(EXTEND))
        {
          message = MessageUtil.getMessageByLocale(SMS_XAC_INSTANT_LOAN_EXTEND_DETAIL, locale.toLowerCase());
        }
        else
        {
          message = MessageUtil.getMessageByLocale(SMS_XAC_INSTANT_LOAN_DISBURSEMENT_DETAIL, locale.toLowerCase());
        }
        String amount = String.valueOf(execution.getVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT));
        message.setText(message.getText().replace("[{accountNumber}]", account));
        message.setText(message.getText().replace("[{disbursedDate}]", currentSystemDate));
        message.setText(message.getText().replace("[{loanAmount}]", amount));
      }
      else if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        account = String.valueOf(execution.getVariable(CURRENT_ACCOUNT_NUMBER));
        message = MessageUtil.getMessageByLocale(SMS_XAC_ONLINE_LEASING_DISBURSEMENT, locale.toLowerCase());
        String amount = String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT));
        message.setText(message.getText().replace("[{accountNumber}]", account));
        message.setText(message.getText().replace("[{disbursedDate}]", currentSystemDate));
        message.setText(message.getText().replace("[{loanAmount}]", amount));
      }
      else
      {
        message = MessageUtil.getMessageByLocale(SMS_XAC_DISBURSEMENT_DETAIL, locale.toLowerCase());
        BigDecimal requestedLoanAmount = (BigDecimal) execution.getVariable("requestedLoanAmount");
        String amount = getValidString(execution.getVariable("requestedLoanAmount")).equals(EMPTY_VALUE) ?
            String.valueOf(execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)) : bigDecimalToString(requestedLoanAmount);
        String replaceAccount = message.getText().replace("[{accountNumber}]", account);
        String replaceDate = replaceAccount.replace("[{disbursedDate}]", currentSystemDate);
        String finalReplace = replaceDate.replace("[{loanAmount}]", amount);
        message.setText(finalReplace);
      }
      LOGGER.info("{} SENDING MESSAGE SERVICE FOR DISBURSEMENT DETAIL, requestId [{}]. {}", log, requestId,
          (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      Map<String, String> input = new HashMap<>();
      input.put(PROCESS_TYPE_ID, processType);
      input.put(PHONE_NUMBER, phoneNumber);
      input.put(MESSAGE, message.getText());
      final boolean smsState = this.messagingService.sendSms(input);
      if (smsState)
      {
        if (isInstantLoan)
          updateTaskStatus(execution, "Sent sms of disbursement detail", "Success");
        LOGGER.info("{} SUCCESSFULLY SENT SMS OF DISBURSEMENT DETAIL. {}", log,
            (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
      }
      else
      {
        if (isInstantLoan)
          updateTaskStatus(execution, "Sent sms of disbursement detail", "Failed");
        LOGGER.info("{} SMS OF DISBURSEMENT DETAIL IS FAILED. {}", log, (isInstantLoan ? " ActionType :" + execution.getVariable(ACTION_TYPE) + "." : ""));
        throw new BpmnError("Send SMS", "Unknown error");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Sent sms of disbursement detail", "Failed");
      String message = e.getMessage() == null ? "java.lang.NullPointerException" : e.getMessage();
      execution.setVariable(ERROR_CAUSE, message);
      throw new BpmnError("Send SMS", message);
    }
  }
}
