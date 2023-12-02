/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.webapp.standalone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mn.erin.aim.repository.memory.MemoryBasedAimRepositoryBeanConfig;
import mn.erin.aim.rest.config.AimRestBeanConfig;
import mn.erin.aim.rest.shiro.config.AimRealmShiroBeanConfig;
import mn.erin.alfresco.connector.service.download.AlfrescoRemoteDownloadService;
import mn.erin.alfresco.connector.service.download.DownloadService;
import mn.erin.bpm.rest.config.BpmRestBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@EnableWebMvc
@Import({
  AimRestBeanConfig.class,
  AimRealmShiroBeanConfig.class,
  MemoryBasedAimRepositoryBeanConfig.class,
  BpmRestBeanConfig.class })
@ComponentScan(basePackages = { "mn.erin.bpm.webapp.standalone.repository", "mn.erin.bpm.webapp.standalone.service" })
public class BpmStandaloneBeanConfig implements WebMvcConfigurer
{
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public DownloadService downloadService()
  {
    return new AlfrescoRemoteDownloadService();
  }
}
