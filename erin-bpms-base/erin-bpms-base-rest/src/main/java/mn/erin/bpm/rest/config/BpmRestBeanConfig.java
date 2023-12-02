/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import mn.erin.domain.bpm.usecase.cases.get_cases.CloseCasesByDateInput;

/**
 * @author EBazarragchaa
 */
@Configuration
@ComponentScan({ "mn.erin.bpm.rest.controller" })
public class BpmRestBeanConfig
{
  @Bean
  @Scope("singleton")
  public CloseCasesByDateInput getCloseCasesByDateInput()
  {
    return new CloseCasesByDateInput();
  }
}
