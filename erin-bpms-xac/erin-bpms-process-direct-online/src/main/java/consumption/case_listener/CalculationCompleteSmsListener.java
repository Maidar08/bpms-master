package consumption.case_listener;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.message.Message;
import mn.erin.domain.base.usecase.MessageUtil;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.service.MessagingService;

import static consumption.constant.CamundaVariableConstants.LOCALE;
import static consumption.constant.CamundaVariableConstants.PHONE_NUMBER;
import static consumption.constant.CamundaVariableConstants.STATE;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.getLogPrefix;

public class CalculationCompleteSmsListener implements ExecutionListener
{
  public static final String SMS_XAC_INSTANT_LAON_CALCULATION_COMPLETE = "sms.xac.INSTANT_LOAN_CALCULATION_COMPLETE";
  public static final String SMS_XAC_ONLINE_LEASING_CALCULATION_COMPLETE = "sms.xac.ONLINE_LEASING_CALCULATION_COMPLETE";
  private final MessagingService messagingService;
  private static final Logger LOGGER = LoggerFactory.getLogger(CalculationCompleteSmsListener.class);

  public CalculationCompleteSmsListener(MessagingService messagingService)
  {
    this.messagingService = messagingService;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    final String state = String.valueOf(execution.getVariable(STATE));
    final String confirmedState = ProcessRequestState.fromEnumToString(ProcessRequestState.CONFIRMED);
    if (!state.equals(confirmedState))
    {
      return;
    }
    String phoneNumber = execution.getVariable(PHONE_NUMBER).toString();
    String locale = String.valueOf(execution.getVariable(LOCALE));
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    Message message;
    if (processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      message = MessageUtil.getMessageByLocale(SMS_XAC_INSTANT_LAON_CALCULATION_COMPLETE, locale.toLowerCase());
    }
    else
    {
      message = MessageUtil.getMessageByLocale(SMS_XAC_ONLINE_LEASING_CALCULATION_COMPLETE, locale.toLowerCase());
    }
    LOGGER.info("{} SENDING MESSAGE SERVICE AFTER FIRST LOAN CALCULATION COMPLETED, requestId [{}]",
        getLogPrefix(processTypeId), requestId);
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, processTypeId);
    input.put(PHONE_NUMBER, phoneNumber);
    input.put(MESSAGE, message.getText());
    this.messagingService.sendSms(input);
  }
}
