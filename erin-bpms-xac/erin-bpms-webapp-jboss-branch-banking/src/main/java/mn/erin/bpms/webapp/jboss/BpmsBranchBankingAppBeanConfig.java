/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.webapp.jboss;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import mn.erin.aim.ldap.LdapConfig;
import mn.erin.aim.ldap.LdapUserRepository;
import mn.erin.aim.rest.shiro.config.AdRealmShiroBeanConfig;
import mn.erin.alfresco.connector.config.AlfrescoConnectorBeanConfig;
import mn.erin.bpms.branch.banking.config.CamundaBranchBankingBeanConfig;
import mn.erin.bpms.branch.banking.webapp.config.BranchBankingRestBeanConfig;
import mn.erin.bpms.webapp.BpmsBeanConfig;

/**
 * @author Tamir
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:bpms.properties")
@Import({ RepositoryBeanConfig.class, AdRealmShiroBeanConfig.class, BpmsBeanConfig.class,
    BranchBankingRestBeanConfig.class,
    CamundaBranchBankingBeanConfig.class,
    AlfrescoConnectorBeanConfig.class })
public class BpmsBranchBankingAppBeanConfig
{
  @Bean
  @Inject
  public LdapConfig ldapConfig(Environment environment)
  {
    LdapConfig ldapConfig = new LdapConfig();

    ldapConfig.setBaseDn(environment.getProperty("activeDirectoryRealm.searchBase"));
    ldapConfig.setUsername(environment.getProperty("activeDirectoryRealm.systemUsername"));
    ldapConfig.setPassword(environment.getProperty("activeDirectoryRealm.systemPassword"));
    ldapConfig.setUrl(environment.getProperty("activeDirectoryRealm.url"));
    ldapConfig.setTenantId(environment.getProperty("activeDirectoryRealm.tenantId"));

    return ldapConfig;
  }

  @Bean
  @Inject
  public LdapUserRepository ldapUserRepository(LdapConfig ldapConfig)
  {
    LdapUserRepository ldapUserRepository = new LdapUserRepository();

    ldapUserRepository.setLdapConfig(ldapConfig);

    return ldapUserRepository;
  }
}
