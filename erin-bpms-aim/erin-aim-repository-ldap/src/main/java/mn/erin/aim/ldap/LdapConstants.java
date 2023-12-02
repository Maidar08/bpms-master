package mn.erin.aim.ldap;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LdapConstants
{
  public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

  public static final String FILTER_ORGANIZATIONAL_PERSON = "(objectClass=organizationalPerson)";
  public static final String FILTER_ACCOUNT_NAME = "(sAMAccountName=__userID__)";

  public static final String ATTRIBUTE_MAIL = "mail";
  public static final String ATTRIBUTE_TELEPHONE = "telephoneNumber";
  public static final String ATTRIBUTE_GIVEN_NAME = "givenName";
  public static final String ATTRIBUTE_SAMACCOUNT_NAME = "sAMAccountName";
  public static final String ATTRIBUTE_SN = "sn";
}
