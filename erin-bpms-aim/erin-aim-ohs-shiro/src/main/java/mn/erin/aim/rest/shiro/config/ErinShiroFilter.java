/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

/**
 * Responsible for throwing 401 status exception in case of unauthorized request calls (session timeout, session kickout etc.)
 *
 * @author EBazarragchaa
 */
public class ErinShiroFilter extends FormAuthenticationFilter
{
  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
  {
    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    return false;
  }
}
