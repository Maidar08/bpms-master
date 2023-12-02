/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.webapp;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Tamir
 */
public class BpmsApplication
{
  protected void createApplication(ServletContext servletContext)
  {
    AnnotationConfigWebApplicationContext ctx = getContext();
    // use main bean config
    ctx.register(getBeanConfigClass());
    ctx.setServletContext(servletContext);

    servletContext.addListener(new ContextLoaderListener(ctx));

    FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilterFactoryBean", DelegatingFilterProxy.class);
    shiroFilter.setInitParameter("targetFilterLifecycle", "true");
    shiroFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

    ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
    servlet.setLoadOnStartup(1);
    servlet.addMapping("/");
  }

  protected Class<?> getBeanConfigClass()
  {
    return BpmsBeanConfig.class;
  }

  private AnnotationConfigWebApplicationContext getContext()
  {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.setConfigLocation(getClass().getPackage().getName());
    return context;
  }
}
