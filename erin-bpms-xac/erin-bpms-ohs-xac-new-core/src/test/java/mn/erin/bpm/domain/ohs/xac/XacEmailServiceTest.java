package mn.erin.bpm.domain.ohs.xac;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;

/**
 * @author Zorig
 */
@Ignore
public class XacEmailServiceTest
{
  private Environment environment;
  private XacEmailService xacEmailService;

  protected static final String HOST = "spring.mail.host";
  protected static final String PORT = "spring.mail.port";
  protected static final String USERNAME = "spring.mail.username";
  protected static final String PASSWORD = "spring.mail.password";
  protected static final String PROTOCOL = "spring.mail.protocol";
  protected static final String SENT_FROM = "spring.mail.sender.from.value";
  protected static final String STARTTLS = "spring.mail.smtp.starttls.enable";
  protected static final String AUTH = "spring.mail.smtp.auth";

  @Before
  public void setUp()
  {
    environment = Mockito.mock(Environment.class);
    Mockito.when(environment.getProperty(HOST)).thenReturn("mail.erin.systems");
    Mockito.when(environment.getProperty(PORT)).thenReturn("587");
    Mockito.when(environment.getProperty(USERNAME)).thenReturn("tester@erin.systems");
    Mockito.when(environment.getProperty(PASSWORD)).thenReturn("MkrC$9s@5s76Epj9");
    Mockito.when(environment.getProperty(PROTOCOL)).thenReturn("smtp");
    Mockito.when(environment.getProperty(STARTTLS)).thenReturn("true");
    Mockito.when(environment.getProperty(AUTH)).thenReturn("true");
    Mockito.when(environment.getProperty(SENT_FROM)).thenReturn("jarvis@erin.systems");
    xacEmailService = new XacEmailService(environment);

    /*Mockito.when(environment.getProperty(HOST)).thenReturn("smtp.gmail.com");
    Mockito.when(environment.getProperty(PORT)).thenReturn("587");
    Mockito.when(environment.getProperty(USERNAME)).thenReturn("zorigmagnaituvshin@gmail.com");
    Mockito.when(environment.getProperty(PASSWORD)).thenReturn("");
    Mockito.when(environment.getProperty(PROTOCOL)).thenReturn("smtp");
    Mockito.when(environment.getProperty(STARTTLS)).thenReturn("true");
    Mockito.when(environment.getProperty(AUTH)).thenReturn("true");*/
  }

  @Test
  public void test_send_email()
  {
    boolean b = xacEmailService.sendEmail("bolortsetseg.otgonbaatar@erin.systems", getTemplateData());
    Assert.assertEquals(true, b);
  }

  Map<String, Object> getTemplateData(){
    Map<String, Object> templateData = new HashMap<>();
    templateData.put(LOAN_ACCOUNT_NUMBER, "loanAccountNumber");
    templateData.put("loanAmount", 100000);
    templateData.put("subject", "test");
    templateData.put("templateName", "send-instantLoan-email-mn.ftl");
    templateData.put("isDigitalLoan", true);
    return templateData;
  }
}
