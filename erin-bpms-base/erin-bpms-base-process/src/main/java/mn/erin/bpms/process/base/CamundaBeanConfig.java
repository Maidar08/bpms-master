package mn.erin.bpms.process.base;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author EBazarragchaa
 */
@Configuration
public class CamundaBeanConfig implements WebMvcConfigurer
{
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
  }

  @Bean
  public ProcessEngineService processEngineService()
  {
    return BpmPlatform.getProcessEngineService();
  }

  @Bean(destroyMethod = "")
  public ProcessEngine processEngine()
  {
    return BpmPlatform.getDefaultProcessEngine();
  }

}
