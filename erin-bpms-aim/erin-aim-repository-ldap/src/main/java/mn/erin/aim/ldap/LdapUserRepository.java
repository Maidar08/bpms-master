package mn.erin.aim.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LdapUserRepository implements UserRepository, InitializingBean
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserRepository.class);

  private static final String[] REQUIRED_ATTRIBUTES = new String[] {
      LdapConstants.ATTRIBUTE_MAIL,
      LdapConstants.ATTRIBUTE_TELEPHONE,
      LdapConstants.ATTRIBUTE_SAMACCOUNT_NAME,
      LdapConstants.ATTRIBUTE_GIVEN_NAME,
      LdapConstants.ATTRIBUTE_SN
  };

  private Properties ldapProperties;
  private String baseDn;
  private TenantId tenantId;

  @Inject
  public void setLdapConfig(LdapConfig ldapConfig)
  {
    this.ldapProperties = new Properties();
    this.ldapProperties.put(Context.INITIAL_CONTEXT_FACTORY, LdapConstants.INITIAL_CONTEXT_FACTORY);
    this.ldapProperties.put(Context.PROVIDER_URL, ldapConfig.getUrl());
    this.ldapProperties.put(Context.SECURITY_PRINCIPAL, ldapConfig.getUsername());
    this.ldapProperties.put(Context.SECURITY_CREDENTIALS, ldapConfig.getPassword());

    this.baseDn = ldapConfig.getBaseDn();
    this.tenantId = TenantId.valueOf(ldapConfig.getTenantId());
  }

  @Override
  public void afterPropertiesSet()
  {
    // not implemented
  }

  @Override
  public List<User> getAllUsers(TenantId tenantId)
  {
    return getAllUsersIntern(tenantId);
  }

  @Override
  public User findById(EntityId userId)
  {
    if (userId == null)
    {
      LOGGER.error("###########  UserId cannot be null");
      throw new IllegalArgumentException("UserId cannot be null");
    }
    if (!(userId instanceof UserId))
    {
      LOGGER.error(userId.getClass() + " is not of type UserId");
      throw new IllegalArgumentException(userId.getClass() + " is not of type UserId");
    }

    String[] baseDirectoryNames = split(baseDn);
    try
    {
      for (String baseDirectory : baseDirectoryNames)
      {
        LOGGER.info("############ LDAP looking for user [{}] in rootDir [{}]", userId.getId(), baseDirectory);
        DirContext context = new InitialDirContext(this.ldapProperties);
        String query = LdapConstants.FILTER_ACCOUNT_NAME.replace("__userID__", userId.getId());
        NamingEnumeration<SearchResult> results = context.search(baseDirectory, query, getControls());
        while (results.hasMore())
        {
          Attributes attributes = results.next().getAttributes();
          User user = getUser(attributes);
          if (null != user)
          {
            LOGGER.info("############ LDAP found user = [{}]", userId.getId());
            return user;
          }
          LOGGER.info("############ LDAP could not found user with id = {} ", userId.getId());
        }
        results.close();
      }
    }
    catch (NamingException e)
    {
      LOGGER.error("############ LDAP failed to locate user", e);
    }
    return null;
  }

  @Override
  public Collection<User> findAll()
  {
    return getAllUsers(this.tenantId);
  }

  @Override
  public User createUser(TenantId tenantId, UserInfo userInfo, ContactInfo contactInfo) throws AimRepositoryException
  {
    return null;
  }

  @Override
  public User getUserByTenantId(TenantId tenantId, UserId userId) throws AimRepositoryException
  {
    List<User> users = getAllUsers(this.tenantId);
    for (User user : users)
    {
      if (userId.sameValueAs(user.getUserId()))
      {
        return user;
      }
    }
    return null;
  }

  @Override
  public User updateUserInfo(UserId userId, UserInfo newUserInfo) throws AimRepositoryException
  {
    return null;
  }

  @Override
  public User updateContactInfo(UserId userId, ContactInfo contactInfo) throws AimRepositoryException
  {
    return null;
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId)
  {
    return null;
  }

  @Override
  public boolean deleteUser(UserId userId)
  {
    return false;
  }

  private User getUser(Attributes attributes)
  {
    try
    {
      String accountName = attribute(LdapConstants.ATTRIBUTE_SAMACCOUNT_NAME, attributes);
      if (null == accountName)
      {
        return null;
      }

      String givenName = attribute(LdapConstants.ATTRIBUTE_GIVEN_NAME, attributes);
      String surname = attribute(LdapConstants.ATTRIBUTE_SN, attributes);
      UserInfo userInfo = new UserInfo(givenName, surname);

      String mail = attribute(LdapConstants.ATTRIBUTE_MAIL, attributes);
      String telephoneNumber = attribute(LdapConstants.ATTRIBUTE_TELEPHONE, attributes);
      ContactInfo contactInfo = new ContactInfo(mail, telephoneNumber);

      return new User(UserId.valueOf(accountName), this.tenantId, userInfo, contactInfo);
    }
    catch (NamingException e)
    {
      LOGGER.warn("Attributes for [{}] is not a valid user object", attributes.getIDs(), e);
    }
    return null;
  }

  private List<User> getAllUsersIntern(TenantId tenantId)
  {
    if (!this.tenantId.equals(tenantId))
    {
      return Collections.emptyList();
    }

    try
    {
      LOGGER.info("############# LDAP BASE_DN = [{}]", baseDn);
      LOGGER.info("########## LDAP REQUIRED_ATTRIBUTES = {}", Arrays.asList(REQUIRED_ATTRIBUTES));
      LOGGER.info("############# LDAP FILTER_OBJECT_CLASS = [{}]", LdapConstants.FILTER_ORGANIZATIONAL_PERSON);

      String[] baseDirectoryNames = split(baseDn);
      LOGGER.info("############ LDAP found [{}] base directories", baseDirectoryNames.length);
      List<User> allUsers = new ArrayList<>();
      for (String rootDir : baseDirectoryNames)
      {
        allUsers.addAll(getUsersFor(rootDir));
      }
      LOGGER.info("############ LDAP users size = [{}]", allUsers.size());
      return allUsers;
    }
    catch (NamingException e)
    {
      LOGGER.error("############ LDAP failed to fetch user list", e);
    }
    return Collections.emptyList();
  }

  private List<User> getUsersFor(String rootDir) throws NamingException
  {
    DirContext context = new InitialDirContext(this.ldapProperties);
    NamingEnumeration<SearchResult> results = context.search(rootDir, LdapConstants.FILTER_ORGANIZATIONAL_PERSON, getControls());
    List<User> users = new ArrayList<>();
    while (results.hasMore())
    {
      Attributes attributes = results.next().getAttributes();
      User user = getUser(attributes);
      if (null != user)
      {
        users.add(user);
      }
    }
    results.close();
    LOGGER.info("############ LDAP found [{}] users for rootDir [{}]", users.size(), rootDir);
    return users;
  }

  private static SearchControls getControls()
  {
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setReturningAttributes(LdapUserRepository.REQUIRED_ATTRIBUTES);
    controls.setTimeLimit(90_000); // timeout 1m 30sec
    return controls;
  }

  private static String attribute(String name, Attributes attributes) throws NamingException
  {
    Attribute attribute = attributes.get(name);
    if (attribute == null)
    {
      return null;
    }
    return attribute.get(0).toString();
  }

  private static String[] split(String src)
  {
    List<String> tokens = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(src, ";");
    while (tokenizer.hasMoreTokens())
    {
      tokens.add(tokenizer.nextToken());
    }
    return tokens.toArray(new String[0]);
  }
}
