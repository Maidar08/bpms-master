/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ ShiroBaseBeanConfig.class })
public class IniRealmShiroBeanConfig
{
  @Bean
  public Realm realm()
  {
    return new IniRealm("classpath:shiro.ini");
  }
}
