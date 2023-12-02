package mn.erin.aim.rest.shiro.config;

import javax.inject.Inject;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ ShiroBaseBeanConfig.class })
public class AdRealmShiroBeanConfig
{

  @Bean
  @Inject
  public Realm realm(Environment environment)
  {
    ActiveDirectoryRealm realm = new ActiveDirectoryRealm();
    realm.setSystemUsername(environment.getProperty("activeDirectoryRealm.systemUsername"));
    realm.setPrincipalSuffix(environment.getProperty("activeDirectoryRealm.principalSuffix"));
    realm.setSystemPassword(environment.getProperty("activeDirectoryRealm.systemPassword"));
    realm.setSearchBase(environment.getProperty("activeDirectoryRealm.searchBase"));
    realm.setUrl(environment.getProperty("activeDirectoryRealm.url"));
    return realm;
  }
}
