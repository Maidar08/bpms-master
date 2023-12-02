/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.webapp.jdbc.branch.banking;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

import mn.erin.bpms.webapp.BpmsApplication;

/**
 * @author Tamir
 */
public class BpmsJdbcApplication extends BpmsApplication implements WebApplicationInitializer
{
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException
  {
    createApplication(servletContext);
  }

  @Override
  protected Class<?> getBeanConfigClass()
  {
    return BpmsJdbcBeanConfig.class;
  }
}
