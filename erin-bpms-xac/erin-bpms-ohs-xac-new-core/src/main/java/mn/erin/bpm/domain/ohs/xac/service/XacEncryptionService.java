package mn.erin.bpm.domain.ohs.xac.service;

import mn.xac.encrypt.password.EncryptPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.EncryptionService;

/**
 * @author Lkhagvadorj.A
 **/

public class XacEncryptionService implements EncryptionService
{
  private final Environment environment;
  private static final Logger LOGGER = LoggerFactory.getLogger(XacEncryptionService.class);
  private final static String XAC_SECRET_KEY = "xacSecretKey";

  public XacEncryptionService(Environment environment)
  {
    this.environment = environment;
  }

  @Override
  public String encrypt(String input)
  {
    if (null == input)
    {
      return null;
    }
    String xacSecretKey = getXacSecretKey();
    String encodedXacSecretKey = EncryptPassword.encodeKey(xacSecretKey);
    try
    {
      return EncryptPassword.encrypt(input, encodedXacSecretKey);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Error occurred during encrypt user password!");
    }
  }

  @Override
  public String decrypt(String input)
  {
    if (null == input)
    {
      LOGGER.info("##### input is null");
      return null;
    }
    String xacSecretKey = getXacSecretKey();
    LOGGER.info("##### xac secret key=[{}].", xacSecretKey);
    String decodedXacSecretKey = EncryptPassword.encodeKey(xacSecretKey);
    LOGGER.info("##### decoded xac secret key=[{}].", xacSecretKey);
    try
    {
      return EncryptPassword.decrypt(input, decodedXacSecretKey);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Error occurred during decrypt user password!");
    }
  }

  private String getXacSecretKey()
  {
    return environment.getProperty(XAC_SECRET_KEY);
  }
}
