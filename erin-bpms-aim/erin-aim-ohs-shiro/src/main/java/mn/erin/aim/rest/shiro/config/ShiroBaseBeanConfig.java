/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.hazelcast.cache.HazelcastCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import mn.erin.aim.provider.ShiroExtSessionInfoCache;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ShiroBeanConfiguration.class,
    ShiroAnnotationProcessorConfiguration.class,
    ShiroWebConfiguration.class,
    ShiroWebFilterConfiguration.class })
public abstract class ShiroBaseBeanConfig
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroBaseBeanConfig.class);

  @Bean
  public Map<String, Filter> filters()
  {
    // Responsible for adding ERIN specific "secure" request filter.
    // new "secure" definition for securing "secure.resource" paths
    Map<String, Filter> filters = new LinkedHashMap<>();
    filters.put("secure", new ErinShiroFilter());
    return filters;
  }

  private CacheManager cacheManager()
  {
//     TODO : Production configuration, please don't forget to uncomment following line when product release
    return new HazelcastCacheManager();

    // Development environment configuration
//    HazelcastCacheManager hazelcastCacheManager = new HazelcastCacheManager();
//    Config config = new Config();
//
//    NetworkConfig networkConfig = new NetworkConfig();
//    MulticastConfig multicastConfig = new MulticastConfig().setEnabled(false);
//
//    networkConfig.setJoin(new JoinConfig().setMulticastConfig(multicastConfig));
//    config.setNetworkConfig(networkConfig);
//
//    hazelcastCacheManager.setConfig(config);
//
//    return hazelcastCacheManager;
  }

  private SessionDAO sessionDAO()
  {
    return new EnterpriseCacheSessionDAO();
  }

  @Bean
  @Inject
  SecurityManager securityManager(Realm realm, Environment environment)
  {
    long sessionTimeout = getSessionTimeout(environment);

    LOGGER.info("########################### Session time out = [{}]", sessionTimeout);

    DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
    manager.setRealm(realm);
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setSessionIdUrlRewritingEnabled(false);

    if (sessionTimeout > 0)
    {
      sessionManager.setGlobalSessionTimeout(sessionTimeout);
    }

    Cookie sessionCookie = new SimpleCookie();
    sessionCookie.setName("erin");
    sessionCookie.setPath("/");
    sessionManager.setSessionIdCookie(sessionCookie);
    manager.setSessionManager(sessionManager);

    LOGGER.info("############################## Initializing cache manager ################################");

    sessionManager.setCacheManager(cacheManager());
    sessionManager.setSessionDAO(sessionDAO());

    return manager;
  }

  @Bean
  @Inject
  ShiroFilterChainDefinition shiroFilterChainDefinition(Environment environment)
  {
    // Always remember to define your filter chains based on a FIRST MATCH WINS policy!
    DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();

    definition.addPathDefinition("/aim/login", "anon");
    definition.addPathDefinition("/bpm/documents/user-manual", "anon");
    definition.addPathDefinition("swagger-ui.html", "anon");

    String[] resources = environment.getProperty("secure.resources").split(",");
    for (String resource : resources)
    {
      definition.addPathDefinition("/" + resource, "secure");
    }

    definition.addPathDefinition("/**", "anon");
    return definition;
  }

  @Bean
  AuthenticationService authenticationService()
  {
    return new ShiroAuthenticationService();
  }

  @Bean
  AuthorizationService authorizationService()
  {
    return new ShiroAuthorizationService();
  }

  @Bean
  PermissionService permissionService()
  {
    return new ShiroPermissionService();
  }

  @Bean
  TenantIdProvider tenantIdProvider()
  {
    return new ShiroTenantIdProvider();
  }

  @Bean
  public ExtSessionInfoCache sessionInfoCache()
  {
    return new ShiroExtSessionInfoCache();
  }

  private long getSessionTimeout(Environment environment)
  {
    String timeoutInMinutes = environment.getProperty("shiro.session.timeout");
    if (!StringUtils.isBlank(timeoutInMinutes))
    {
      try
      {
        long minutes = Long.parseLong(timeoutInMinutes);
        final long MILLIS_PER_SECOND = 1000;
        final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
        return minutes * MILLIS_PER_MINUTE;
      }
      catch (NumberFormatException e)
      {
        LOGGER.error(e.getMessage(), e);
        return 0;
      }
    }

    return 0;
  }
}
