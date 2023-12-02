package consumption.case_listener;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
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
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class SendCalculationCompleteSmsListener implements CaseExecutionListener
{
  public static final String SMS_XAC_SALARY_CALCULATION_COMPLETE = "sms.xac.SALARY_CALCULATION_COMPLETE";
  private final MessagingService messagingService;
  private static final Logger LOGGER = LoggerFactory.getLogger(SendCalculationCompleteSmsListener.class);

  public SendCalculationCompleteSmsListener(MessagingService messagingService)
  {
    this.messagingService = messagingService;
  }

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    final String state = String.valueOf(caseExecution.getVariable(STATE));
    final String confirmedState = ProcessRequestState.fromEnumToString(ProcessRequestState.CONFIRMED);
    if (!state.equals(confirmedState))
    {
      return;
    }
    String phoneNumber = (String) caseExecution.getVariable(PHONE_NUMBER);
    String locale = (String) caseExecution.getVariable(LOCALE);
    String requestId = String.valueOf(caseExecution.getVariable(PROCESS_REQUEST_ID));
    Message message = MessageUtil.getMessageByLocale(SMS_XAC_SALARY_CALCULATION_COMPLETE, locale.toLowerCase());
    LOGGER.info(ONLINE_SALARY_LOG_HASH + " SENDING MESSAGE SERVICE AFTER FIRST LOAN CALCULATION COMPLETED, requestId [{}]", requestId);
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, String.valueOf(caseExecution.getVariable(PROCESS_TYPE_ID)));
    input.put(PHONE_NUMBER, phoneNumber);
    input.put(MESSAGE, message.getText());
    this.messagingService.sendSms(input);
  }
}
