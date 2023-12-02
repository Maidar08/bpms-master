package mn.erin.bpm.domain.ohs.xac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.internet.MimeMessage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import mn.erin.common.mail.EmailService;
import mn.erin.spring.common.mail.EmailServiceImpl;

/**
 * @author Zorig
 */
public class XacEmailService implements EmailService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

  protected static final String HOST = "spring.mail.host";
  protected static final String PORT = "spring.mail.port";
  protected static final String USERNAME = "spring.mail.username";
  protected static final String PASSWORD = "spring.mail.password";
  protected static final String PROTOCOL = "spring.mail.protocol";
  protected static final String SENDER_EMAIL = "spring.mail.sender.current.user.email";
  protected static final String SENT_FROM = "spring.mail.sender.from.value";
  protected static final String STARTTLS = "spring.mail.smtp.starttls.enable";
  protected static final String SSL = "spring.mail.smtp.ssl.enable";
  protected static final String AUTH = "spring.mail.smtp.auth";

  private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
  private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
  private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
  private static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
  private static final String MAIL_DEBUG = "mail.debug";
  private static final String MAIL_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
  public static final String DIGITAL_LOAN_MAIL_SENDER = "spring.digital.loan.mail.sender.value";
  public static final String DIGITAL_LOAN_MAIL_SENDER_USER_NAME = "sprint.digital.loan.mail.sender.username";
  public static final String DIGITAL_LOAN_MAIL_SENDER_PASSWORD = "spring.digital.loan.mail.sender.password";

  private JavaMailSenderImpl sender;
  private Environment environment;

  public XacEmailService(Environment environment)
  {
    this.environment = environment;

    this.sender = new JavaMailSenderImpl();
    this.sender.setHost(environment.getProperty(HOST));
    this.sender.setPort(Integer.valueOf(environment.getProperty(PORT)));
    this.sender.setUsername(environment.getProperty(USERNAME));
    this.sender.setPassword(environment.getProperty(PASSWORD));
    this.sender.setProtocol(environment.getProperty(PROTOCOL));
  }

  @Override
  public boolean sendEmail(String recipient, Map<String, Object> emailTemplateData)
  {
    String subject = (String) emailTemplateData.get("subject");

    String templateName = (String) emailTemplateData.get("templateName");

    Template template = null;
    try
    {
      template = getTemplate(templateName, this.getClass(), "/templates");
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }

    return sendEmailWithTemplate(Collections.singletonList(recipient), subject, template, emailTemplateData);
  }


  @Override
  public boolean sendEmail(List<String> recipients,  List<String> recipientsBcc, String subject, String templateName, Map<String, Object> templateData, Map<String, byte[]> attachmentData)
  {
    Template template;
    try
    {
      template = getTemplate(templateName, this.getClass(), "/templates");
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }

    return sendEmailWithTemplate(recipients, subject, template, templateData, attachmentData);
  }

  @Override
  public boolean sendEmail(String recipient, String subject, String templateName, Map<String, Object> templateData, Map<String, byte[]> attachmentData)
  {
    Template template;
    try
    {
      template = getTemplate(templateName, this.getClass(), "/templates");
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }

    return sendEmailWithTemplate(Collections.singletonList(recipient), subject, template, templateData, attachmentData);
  }

  @Override
  public boolean sendEmail(List<String> list, List<String> list1, String s, String s1, Map<String, Object> map)
  {
    return false;
  }


  private boolean sendEmailWithTemplate(List<String> recipients, String subject, Template template, Map<String, Object> emailTemplateData)
  {
    LOGGER.info("XAC Email Service - Send Email Properties : Host = {}, Port = {}, Protocol = {}, Smtp authentication = {}, Username = {}, SSL Enable = {}",
        environment.getProperty(HOST),
        environment.getProperty(PORT),
        environment.getProperty(PROTOCOL),
        environment.getProperty(AUTH),
        environment.getProperty(USERNAME),
        environment.getProperty(SSL));

    MimeMessage message = sender.createMimeMessage();
    try
    {
      String sentFrom = setSentFrom((String) emailTemplateData.get("sentFrom"));

      String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailTemplateData);

      MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          StandardCharsets.UTF_8.name());

      if (recipients.size() == 1)
      {
        helper.setTo(recipients.get(0));
      }
      else
      {
        helper.setTo(recipients.toArray(new String[0]));
      }

      helper.setText(text, true);
      helper.setSubject(subject);
      helper.setFrom(sentFrom);

      Properties mailProperties = sender.getJavaMailProperties();
      setMailProperties(mailProperties);

      LOGGER.info("###### SENDING EMAIL ....");
      sender.send(message);

      return true;
    }
    catch (Exception e)
    {
      LOGGER.error("############# EMAIL SERVICE EXCEPTION : " + e.getMessage(), e);
      return false;
    }
  }

  private boolean sendEmailWithTemplate(List<String> recipients, String subject, Template template, Map<String, Object> emailTemplateData,
      Map<String, byte[]> attachmentData)
  {
    LOGGER.info("XAC Email Service - Send Email Properties : Host = {}, Port = {}, Protocol = {}, Smtp authentication = {}, Username = {}, SSL Enable = {}",
        environment.getProperty(HOST),
        environment.getProperty(PORT),
        environment.getProperty(PROTOCOL),
        environment.getProperty(AUTH),
        environment.getProperty(USERNAME),
        environment.getProperty(SSL));

    MimeMessage message = sender.createMimeMessage();
    try
    {
      String sentFrom = setSentFrom(emailTemplateData);

      String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailTemplateData);

      MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          StandardCharsets.UTF_8.name());

      if (recipients.size() == 1)
      {
        helper.setTo(recipients.get(0));
      }
      else
      {
        helper.setTo(recipients.toArray(new String[0]));
      }

      helper.setText(text, true);
      helper.setSubject(subject);
      helper.setFrom(sentFrom);
      setConfigs(sender, emailTemplateData);

      if (attachmentData != null)
      {
        LOGGER.info("###### ATTACHING FILES TO EMAIL ....");
        for (Map.Entry<String, byte[]> attachment : attachmentData.entrySet())
        {
          helper.addAttachment(attachment.getKey(), new ByteArrayResource(attachment.getValue()), "application/pdf");
        }
      }

      Properties mailProperties = sender.getJavaMailProperties();
      setMailProperties(mailProperties);

      LOGGER.info("###### SENDING EMAIL ....");
      sender.send(message);

      return true;
    }
    catch (Exception e)
    {
      LOGGER.error("############# EMAIL SERVICE EXCEPTION : " + e.getMessage(), e);
      return false;
    }
  }

  private boolean sendEmailStatic(List<String> recipients, String subject, Map<String, Object> emailData)
  {
    LOGGER.info("XAC Email Service - Send Email Properties : Host = {}, Port = {}, Protocol = {}, Smtp authentication = {}, Username = {}, SSL Enable = {}",
        environment.getProperty(HOST),
        environment.getProperty(PORT),
        environment.getProperty(PROTOCOL),
        environment.getProperty(AUTH),
        environment.getProperty(USERNAME),
        environment.getProperty(SSL));

    MimeMessage message = sender.createMimeMessage();
    try
    {
      String state = (String) emailData.get("state");
      String senderName = (String) emailData.get("sender");
      String reason = (String) emailData.get("reason");
      String sentFrom = setSentFrom((String) emailData.get("sentFrom"));
      String explanation = (String) emailData.get("explanation");

      //create text message using email data map
      String text = "Энэ өдрийн мэнд хүргэе!" + "<br><br>";
      text = text + subject + "<br><br>";

      text = text + "Танд " + senderName + "-с " + state + " төлөвтэй өргөдөл илгээлээ.<br><br>";

      if (!StringUtils.isBlank(reason))
      {
        text = text + "Шалтгаан : " + reason + "<br><br>";
      }

      if (!StringUtils.isBlank(explanation))
      {
        text = text + "Тайлбар : " + explanation + "<br><br>";
      }

      text = text + "Баярлалаа.";

      MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          StandardCharsets.UTF_8.name());

      if (recipients.size() == 1)
      {
        helper.setTo(recipients.get(0));
      }
      else
      {
        helper.setTo(recipients.toArray(new String[0]));
      }

      helper.setText(text, true);
      helper.setSubject(subject);
      helper.setFrom(sentFrom);

      Properties mailProperties = sender.getJavaMailProperties();
      setMailProperties(mailProperties);

      LOGGER.info("###### SENDING EMAIL ....");
      sender.send(message);

      return true;
    }
    catch (Exception e)
    {
      LOGGER.error("############# EMAIL SERVICE EXCEPTION : " + e.getMessage(), e);
      return false;
    }
  }

  private void setMailProperties(Properties mailProperties)
  {
    putProperty(mailProperties, MAIL_TRANSPORT_PROTOCOL, environment.getProperty(PROTOCOL));
    putProperty(mailProperties, MAIL_SMTP_AUTH, environment.getProperty(AUTH));
    putProperty(mailProperties, MAIL_SMTP_STARTTLS_ENABLE, environment.getProperty(STARTTLS));
    putProperty(mailProperties, MAIL_SMTP_SSL_ENABLE, environment.getProperty(SSL));

    mailProperties.put(MAIL_DEBUG, "true");
  }

  private void putProperty(Properties mailProperties, String key, Object value)
  {
    if (null != key && null != value)
    {
      LOGGER.info("############ PUTS MAIL PROPERTY WITH KEY = [{}], VALUE = [{}]", key, value);
      mailProperties.put(key, value);
    }
    else
    {
      LOGGER.warn("########### MAIL PROPERTY ANY VALUE NULL, KEY = [{}], VALUE = [{}]", key, value);
    }
  }

  private String setSentFrom(String sentFrom)
  {
    boolean isCurrentUserEmail = Boolean.valueOf(environment.getProperty(SENDER_EMAIL));

    if (isCurrentUserEmail)
    {
      LOGGER.info("######### SENT FROM EMAIL IS = [{}]", sentFrom);
      return sentFrom;
    }
    else
    {
      LOGGER.info("######## SENT FROM EMAIL IS = [{}]", environment.getProperty(SENT_FROM));
      return environment.getProperty(SENT_FROM);
    }
  }

  private String setSentFrom(Map<String, Object> emailTemplateData)
  {
    boolean isCurrentUserEmail = Boolean.parseBoolean(environment.getProperty(SENDER_EMAIL));
    String sentFrom = (String) emailTemplateData.get("sentFrom");

    if (isCurrentUserEmail)
    {
      LOGGER.info("######### SENT FROM EMAIL IS = [{}]", sentFrom);
      return sentFrom;
    }
    else
    {
      if (emailTemplateData.containsKey("isDigitalLoan") && Boolean.TRUE.equals(emailTemplateData.get("isDigitalLoan")))
      {
        String senderEmailDigital = environment.getProperty(DIGITAL_LOAN_MAIL_SENDER);
        LOGGER.info("######## SENT FROM EMAIL IS = [{}]", senderEmailDigital);
        return senderEmailDigital;
      }

      String sentFromProperty = environment.getProperty(SENT_FROM);
      LOGGER.info("######### SENT FROM EMAIL IS = [{}]", sentFromProperty);
      return sentFromProperty;
    }
  }

  private Template getTemplate(String templateName, Class context, String pathName) throws IOException
  {
    Configuration configuration = new Configuration();

    configuration.setClassForTemplateLoading(context, pathName);

    return configuration.getTemplate(templateName, StandardCharsets.UTF_8.name());
  }

  private void setConfigs(JavaMailSenderImpl sender, Map<String, Object> emailTemplateData)
  {
    sender.setProtocol(environment.getProperty(PROTOCOL));
    sender.setHost(environment.getProperty(HOST));
    sender.setPort(Integer.valueOf(environment.getProperty(PORT)));

    boolean isDigitalLoan = (boolean) emailTemplateData.get("isDigitalLoan");

    if (isDigitalLoan)
    {
      sender.setUsername(environment.getProperty(DIGITAL_LOAN_MAIL_SENDER_USER_NAME));

      if (!StringUtils.isBlank(environment.getProperty(DIGITAL_LOAN_MAIL_SENDER_PASSWORD)))
      {
        sender.setPassword(environment.getProperty(DIGITAL_LOAN_MAIL_SENDER_PASSWORD));
      }
    }
    else
    {
      sender.setUsername(environment.getProperty(USERNAME));

      if (!StringUtils.isBlank(environment.getProperty(PASSWORD)))
      {
        sender.setPassword(environment.getProperty(PASSWORD));
      }
    }
  }
}
