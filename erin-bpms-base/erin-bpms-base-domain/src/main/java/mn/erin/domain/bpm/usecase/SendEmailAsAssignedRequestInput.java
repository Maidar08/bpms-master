package mn.erin.domain.bpm.usecase;

/**
 * @author Zorig
 */
public class SendEmailAsAssignedRequestInput
{
  private final String subject;
  private final String emailRecipient;
  private final String nameOfSender;
  private final String state;
  private final String reason;
  private final String sentFrom;
  private final String explanation;
  private final String templateName;

  public SendEmailAsAssignedRequestInput(String subject, String emailRecipient, String nameOfSender, String state, String reason, String sentFrom,
      String explanation, String templateName)
  {
    this.subject = subject;
    this.emailRecipient = emailRecipient;
    this.nameOfSender = nameOfSender;
    this.state = state;
    this.reason = reason;
    this.sentFrom = sentFrom;
    this.explanation = explanation;
    this.templateName = templateName;
  }

  public String getTemplateName()
  {
    return templateName;
  }

  public String getSubject()
  {
    return subject;
  }

  public String getEmailRecipient()
  {
    return emailRecipient;
  }

  public String getNameOfSender()
  {
    return nameOfSender;
  }

  public String getState()
  {
    return state;
  }

  public String getReason()
  {
    return reason;
  }

  public String getSentFrom()
  {
    return sentFrom;
  }

  public String getExplanation()
  {
    return explanation;
  }
}
