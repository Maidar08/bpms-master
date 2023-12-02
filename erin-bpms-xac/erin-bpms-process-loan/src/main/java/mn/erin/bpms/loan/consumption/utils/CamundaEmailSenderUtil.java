package mn.erin.bpms.loan.consumption.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.common.mail.EmailService;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.usecase.SendEmailAsAssignedRequest;
import mn.erin.domain.bpm.usecase.SendEmailAsAssignedRequestInput;

/**
 * @author Tamir
 */
public class CamundaEmailSenderUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CamundaEmailSenderUtil.class);

  public static final String RECIPIENT_EMAIL = "recipientEmail";
  public static final String SENDER_EMAIL = "senderEmail";

  public static final String RECEIVED_USER = "receivedUser";
  public static final String RECEIVERS = "receivers";

  private CamundaEmailSenderUtil()
  {

  }

  public static Map<String, String> getEmailMap(Collection<User> users, String recipientUserId, String senderUserId) throws ProcessTaskException
  {
    Map<String, String> emails = new HashMap<>();

    emails.put(RECIPIENT_EMAIL, null);
    emails.put(SENDER_EMAIL, null);

    for (User user : users)
    {
      String userId = user.getUserId().getId();

      if (userId.equals(recipientUserId))
      {
        LOGGER.info("################# SUCCESSFUL MATCHED RECIPIENT USER ID WITH LDAP USER.");
        ContactInfo contactInfo = user.getContactInfo();

        if (null == contactInfo)
        {
          LOGGER.warn("########## RECIPIENT USER =[{}] contact info is null!", userId);
          throw new ProcessTaskException("########## Recipient user contact info is null with USER ID =" + userId);
        }

        String email = contactInfo.getEmail();
        LOGGER.info("####### SUCCESSFUL RECEIVED RECIPIENT EMAIL ADDRESS = [{}]", email);

        emails.put(RECIPIENT_EMAIL, email);
      }
      else if (userId.equals(senderUserId))
      {
        LOGGER.info("################# SUCCESSFUL MATCHED SENDER USER ID WITH LDAP USER.");
        ContactInfo contactInfo = user.getContactInfo();

        if (null == contactInfo)
        {
          LOGGER.warn("########## SENDER USER =[{}] contact info is null!", userId);
          throw new ProcessTaskException("########## Sender user contact info is null with USER ID =" + userId);
        }

        String email = contactInfo.getEmail();
        LOGGER.info("####### SUCCESSFUL RECEIVED SENDER USER EMAIL ADDRESS = [{}]", email);

        emails.put(SENDER_EMAIL, email);
      }
    }
    return emails;
  }

  public static boolean sendEmail(EmailService emailService, String subject, String recipient, String sender, String state, String reason,
      String sentFromEmailAddress, String explanation, String templateName) throws UseCaseException
  {
    SendEmailAsAssignedRequest sendEmailAsAssignedRequest = new SendEmailAsAssignedRequest(emailService);
    SendEmailAsAssignedRequestInput input = new SendEmailAsAssignedRequestInput(subject, recipient, sender, state, reason, sentFromEmailAddress, explanation,
        templateName);
    return sendEmailAsAssignedRequest.execute(input);
  }

  public static String getNameFromUserId(UserRepository userRepository, String userId) throws ProcessTaskException
  {
    User user = userRepository.findById(UserId.valueOf(userId));

    if (user == null)
    {
      throw new ProcessTaskException("User does not exist!");
    }

    if (user.getUserInfo() == null)
    {
      throw new ProcessTaskException("User does not have a name!");
    }

    return user.getUserInfo().getUserName();
  }

  public static String getProductName(ProcessTypeRepository processTypeRepository, String processTypeId)
  {
    ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(processTypeId));
    return processType.getName();
  }

  public static String getStateOfProcess(ProcessRequestRepository processRequestRepository, String processRequestId)
  {
    ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

    Map<String, String> stateMap = new HashMap<>();

    stateMap.put("NEW", "ШИНЭ");
    stateMap.put("STARTED", "СУДЛАГДАЖ БАЙНА");
    stateMap.put("ORG_REJECTED", "БАНК ТАТГАЛЗСАН");
    stateMap.put("CUST_REJECTED", "ХАРИЛЦАГЧ ТАТГАЛЗСАН");
    stateMap.put("CONFIRMED", "БАТЛАГДСАН");
    stateMap.put("RETURNED", "БУЦААСАН");
    stateMap.put("REJECTED", "ЗАХИРАЛ-БАНК ТАТГАЛЗСАН");
    stateMap.put("COMPLETED", "ДУУССАН");

    String stateMnValue = stateMap.get(processRequest.getState().toString());
    if(StringUtils.isBlank(stateMnValue)){
      return processRequest.getState().toString();
    }
    return stateMnValue;
  }
}
