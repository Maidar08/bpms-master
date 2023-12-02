/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.webapp.standalone;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mn.erin.aim.repository.memory.MemoryBasedAimRepositoryBeanConfig;
import mn.erin.aim.rest.config.AimRestBeanConfig;
import mn.erin.aim.rest.shiro.config.AimRealmShiroBeanConfig;
import mn.erin.rest.swagger.ui.SwaggerConfiguration;

@Configuration
@EnableWebMvc
@PropertySource("classpath:app.properties")
@Import({
    AimRestBeanConfig.class,
    AimRealmShiroBeanConfig.class,
    SwaggerConfiguration.class,
    MemoryBasedAimRepositoryBeanConfig.class
})
public class AimStandaloneBeanConfig implements WebMvcConfigurer
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
