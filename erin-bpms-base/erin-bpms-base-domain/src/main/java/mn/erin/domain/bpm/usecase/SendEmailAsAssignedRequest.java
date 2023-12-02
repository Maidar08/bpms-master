package mn.erin.domain.bpm.usecase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import mn.erin.common.mail.EmailService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class SendEmailAsAssignedRequest extends AbstractUseCase<SendEmailAsAssignedRequestInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailAsAssignedRequest.class);

  private final EmailService emailService;

  public SendEmailAsAssignedRequest(EmailService emailService)
  {
    this.emailService = Objects.requireNonNull(emailService, "Xac Email Service Is Required!");
  }

  @Override
  public Boolean execute(SendEmailAsAssignedRequestInput input) throws UseCaseException
  {
    validateInput(input);

    //get and set required data for email
    Map<String, Object> emailData = new HashMap<>();
    emailData.put("subject", input.getSubject());
    emailData.put("state", input.getState());
    emailData.put("reason", input.getReason());
    emailData.put("sender", input.getNameOfSender());
    emailData.put("sentFrom", input.getSentFrom());
    emailData.put("explanation", input.getExplanation());
    emailData.put("templateName", input.getTemplateName());

    sendEmailAsync(emailService, input.getEmailRecipient(), emailData);

    return true;
  }

  public static void sendEmailAsync(EmailService emailService, String recipient, Map<String, Object> templateData)
  {
    LOGGER.info("################ Sending Email Async");
    CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> {
      return emailService.sendEmail(recipient, templateData);
    });
    result.handle((isEmailSent, exception) -> {
      if (isEmailSent && exception == null)
      {
        LOGGER.info("Sent an email to: [{}]", recipient);
      }
      else
      {
        LOGGER.error("Failed to send an email to: [{}]", recipient, exception);
      }

      return null;
    });
  }

  private void validateInput(SendEmailAsAssignedRequestInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input is null!");
    }
    else if (StringUtils.isBlank(input.getEmailRecipient()))
    {
      throw new UseCaseException("Email recipient is blank!");
    }
    else if (StringUtils.isBlank(input.getNameOfSender()))
    {
      throw new UseCaseException("Name of Sender Is Blank!");
    }
    else if (StringUtils.isBlank(input.getState()))
    {
      throw new UseCaseException("State of request is blank!");
    }
    else if (StringUtils.isBlank(input.getSentFrom()))
    {
      throw new UseCaseException("Sent From Email Address is blank!");
    }
    else if (StringUtils.isBlank(input.getTemplateName()))
    {
      throw new UseCaseException("Email Template Name is blank!");
    }
  }
}
