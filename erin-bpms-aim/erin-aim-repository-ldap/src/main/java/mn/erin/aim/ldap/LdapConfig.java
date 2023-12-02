package mn.erin.aim.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LdapConfig
{
  private static final String LDAP_URL = "ldap.url";
  private static final String BASE_DN = "ldap.base-dn";
  private static final String USER_NAME = "ldap.username";
  private static final String PASS = "ldap.password";
  private static final String TENANT = "ldap.tenantId";

  private String url;
  private String baseDn;
  private String username;
  private String password;
  private String tenantId;

  public LdapConfig()
  {

  }

  public static LdapConfig load(InputStream is) throws IOException
  {
    Properties properties = new Properties();
    properties.load(is);
    LdapConfig config = new LdapConfig();
    config.url = properties.getProperty(LDAP_URL);
    config.baseDn = properties.getProperty(BASE_DN);
    config.username = properties.getProperty(USER_NAME);
    config.password = properties.getProperty(PASS);
    config.tenantId = properties.getProperty(TENANT);

    return config;
  }

  public String getUrl()
  {
    return this.url;
  }

  public String getBaseDn()
  {
    return this.baseDn;
  }

  public String getUsername()
  {
    return this.username;
  }

  public String getPassword()
  {
    return this.password;
  }

  public String getTenantId()
  {
    return this.tenantId;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public void setBaseDn(String baseDn)
  {
    this.baseDn = baseDn;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }
}
