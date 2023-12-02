/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import mn.erin.bpm.domain.ohs.camunda.CamundaCaseService;
import mn.erin.bpm.domain.ohs.camunda.CamundaExecutionService;
import mn.erin.bpm.domain.ohs.camunda.CamundaProcessEngineProvider;
import mn.erin.bpm.domain.ohs.camunda.CamundaProcessTypeService;
import mn.erin.bpm.domain.ohs.camunda.CamundaRunTimeService;
import mn.erin.bpm.domain.ohs.camunda.CamundaTaskFormService;
import mn.erin.bpm.domain.ohs.camunda.CamundaTaskService;
import mn.erin.bpm.domain.ohs.camunda.ProcessEngineProvider;
import mn.erin.domain.bpm.provider.DefaultFingerPrintProvider;
import mn.erin.domain.bpm.provider.FingerPrintProvider;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.ProcessTypeService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;

/**
 * @author Tamir
 */
@Configuration
public class CamundaOhsBeanConfig
{

  @Bean
  public ProcessEngineProvider processEngineProvider()
  {
    return new CamundaProcessEngineProvider();
  }

  @Bean
  public TaskService taskService()
  {
    return new CamundaTaskService(processEngineProvider());
  }

  @Bean
  public CaseService caseService()
  {
    return new CamundaCaseService(processEngineProvider());
  }

  @Bean
  public TaskFormService taskFormService()
  {
    return new CamundaTaskFormService(processEngineProvider(), fingerPrintProvider());
  }

  @Bean
  public RuntimeService runtimeService()
  {
    return new CamundaRunTimeService(processEngineProvider());
  }

  @Bean
  @RequestScope
  public FingerPrintProvider fingerPrintProvider()
  {
    return new DefaultFingerPrintProvider();
  }

  @Bean
  public ExecutionService executionService()
  {
    return new CamundaExecutionService(processEngineProvider());
  }

  @Bean
  public ProcessTypeService processTypeService(@Autowired ProcessTypeRepository processTypeRepository,
      @Autowired ProcessRequestRepository processRequestRepository)
  {
    return new CamundaProcessTypeService(processEngineProvider(), processTypeRepository, processRequestRepository);
  }
}
