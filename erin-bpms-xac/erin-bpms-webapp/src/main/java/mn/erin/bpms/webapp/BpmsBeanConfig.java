/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.webapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import mn.erin.aim.rest.config.AimRestBeanConfig;
import mn.erin.bpm.domain.ohs.camunda.config.CamundaOhsBeanConfig;
import mn.erin.bpm.rest.config.BpmRestBeanConfig;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

/**
 * @author Tamir
 */
@Configuration
@EnableWebMvc
@Import({ AimRestBeanConfig.class, BpmRestBeanConfig.class, CamundaOhsBeanConfig.class })
@ComponentScan("mn.erin.bpms.webapp.exception")
public class BpmsBeanConfig
{
  @Bean
  public BpmsServiceRegistry bpmsServiceRegistry()
  {
    return new BpmsServiceRegistryImpl();
  }

  @Bean
  public BpmsRepositoryRegistry bpmsRepositoryRegistry()
  {
    return new BpmsRepositoryRegistryImpl();
  }

  @Bean
  public AimServiceRegistry aimServiceRegistry()
  {
    return new AimServiceRegistryImpl();
  }

//  @Bean
//  public BpmsDatabaseIndexImpl bpmsDatabaseIndex(DataSource dataSource, ProcessEngineProvider processEngineProvider)
//  {
//    return new BpmsDatabaseIndexImpl(dataSource, processEngineProvider);
//  }
}
