/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ ShiroBaseBeanConfig.class })
public class MemoryBasedRealmShiroBeanConfig
{
  @Bean
  public Realm realm()
  {
    return new MemoryBasedRealm();
  }

  class MemoryBasedRealm implements Realm
  {
    private static final String MEMORY_BASED_REALM = "MemoryBasedRealm";

    private Map<String, String> credentials = new HashMap<>();

    private static final String USER_NAME_1 = "tamir";
    private static final String USER_NAME_2 = "hrManager";
    private static final String USER_NAME_3 = "director";

    private static final String USER_NAME_4 = "bayartsetseg";
    private static final String USER_NAME_5 = "oyungerel";
    private static final String USER_NAME_6 = "altansoyombo";
    private static final String USER_NAME_7 = "bolortsetseg";

    private static final String DEFAULT_PASSWORD = "demo";

    {
      credentials.put(USER_NAME_1, DEFAULT_PASSWORD);
      credentials.put(USER_NAME_2, DEFAULT_PASSWORD);

      credentials.put(USER_NAME_3, DEFAULT_PASSWORD);
      credentials.put(USER_NAME_4, DEFAULT_PASSWORD);

      credentials.put(USER_NAME_5, DEFAULT_PASSWORD);
      credentials.put(USER_NAME_6, DEFAULT_PASSWORD);
      credentials.put(USER_NAME_7, DEFAULT_PASSWORD);
    }

    @Override
    public String getName()
    {
      return MEMORY_BASED_REALM;
    }

    @Override
    public boolean supports(AuthenticationToken token)
    {
      return true;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token)
    {
      UsernamePasswordToken uToken = (UsernamePasswordToken) token;

      String password = String.copyValueOf(uToken.getPassword());

      if (uToken.getUsername() == null
          || uToken.getUsername().isEmpty()
          || !credentials.containsKey(uToken.getUsername())
          || !credentials.containsValue(password))
      {
        throw new UnknownAccountException("Username not found!");
      }

      return new SimpleAuthenticationInfo(
          uToken.getUsername(),
          credentials.get(uToken.getUsername()),
          getName());
    }
  }
}
