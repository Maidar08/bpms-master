package mn.erin.bpms.loan.consumption.service_task;

import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.common.mail.EmailService;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.RECEIVERS;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.RECIPIENT_EMAIL;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.SENDER_EMAIL;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.getEmailMap;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.getNameFromUserId;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.getProductName;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.getStateOfProcess;
import static mn.erin.bpms.loan.consumption.utils.CamundaEmailSenderUtil.sendEmail;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class SendEmailTaskForSendLoanDecision implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailTaskForSendLoanDecision.class);

  private final EmailService emailService;
  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessTypeRepository processTypeRepository;

  public SendEmailTaskForSendLoanDecision(EmailService emailService, AuthenticationService authenticationService, UserRepository userRepository,
      ProcessRequestRepository processRequestRepository,
      ProcessTypeRepository processTypeRepository)
  {
    this.emailService = emailService;
    this.authenticationService = authenticationService;
    this.userRepository = userRepository;
    this.processRequestRepository = processRequestRepository;
    this.processTypeRepository = processTypeRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      LOGGER.info("############# SEND LOAN DECISION TASK EMAIL SENDING .............");

      String registrationNumber = (String) execution.getVariable("registerNumber");
      String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

      String recipientUserName = (String) execution.getVariable(RECEIVERS);

      LOGGER.info("#########  Send Email Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId);

      Collection<User> users = userRepository.getAllUsers(TenantId.valueOf("xac"));

      if (!users.isEmpty())
      {
        LOGGER.info("######### LDAP USERS SIZE = [{}]", users.size());
      }
      else
      {
        LOGGER.warn("######## LDAP USERS EMPTY!");
      }

      String senderUserId = authenticationService.getCurrentUserId();
      Map<String, String> emails = getEmailMap(users, recipientUserName, senderUserId);

      String recipientEmail = emails.get(RECIPIENT_EMAIL);
      String senderEmail = emails.get(SENDER_EMAIL);

      String senderName = getNameFromUserId(userRepository, authenticationService.getCurrentUserId());
      String state = getStateOfProcess(processRequestRepository, requestId);
      String reason = "";
      String explanation;
      String customerName = (String) execution.getVariable("fullName");

      String processTypeId = (String) execution.getVariable("processTypeId");
      String productName = getProductName(processTypeRepository, processTypeId);

      String subject = productName + " - " + requestId + " - " + customerName;

      Object explanationObj = execution.getVariable("loanCommentExplanation");

      if (explanationObj != null)
      {
        explanation = (String) explanationObj;
      }
      else
      {
        explanation = "";
      }

      String templateName = "send-for-decision.ftl";

      LOGGER.info("#########  Sending Email To: " + recipientEmail);
      boolean isSent = sendEmail(emailService, subject, recipientEmail, senderName, state, reason, senderEmail, explanation, templateName);

      LOGGER.info("##### IS SENT FIRST RECIPIENT : [{}]", isSent);

      LOGGER.info("#########  Finished Sending Email Task...");
    }
    catch (UseCaseException | ProcessTaskException e)
    {
      LOGGER.error("Email was not sent: " + e.getMessage());
    }

    LOGGER.info("############# SUCCESSFUL SENT EMAIL FOR SEND LOAN DECISION TASK.............");
  }
}
