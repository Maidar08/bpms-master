/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.webapp.jdbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mn.erin.aim.rest.config.AimRestBeanConfig;
import mn.erin.aim.rest.shiro.config.AimRealmShiroBeanConfig;
import mn.erin.bpm.rest.config.BpmRestBeanConfig;

/**
 * @author EBazarragchaa
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:datasource.properties")
@Import({ BpmJdbcBeanConfig.class, AimRestBeanConfig.class, AimRealmShiroBeanConfig.class, BpmRestBeanConfig.class })
public class BpmJdbcAppBeanConfig implements WebMvcConfigurer
{
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
