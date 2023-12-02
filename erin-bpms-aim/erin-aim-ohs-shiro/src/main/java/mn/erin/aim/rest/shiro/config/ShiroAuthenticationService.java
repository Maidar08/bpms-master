/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.service.AuthenticationService;

/**
 * @author EBazarragchaa
 */
public class ShiroAuthenticationService implements AuthenticationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroAuthenticationService.class);

  @Override
  public String authenticate(String tenantId, String userId, String password,  boolean killPreviousSession)
  {
    Validate.notBlank(tenantId, "tenantId is missing!");
    Validate.notBlank(userId, "userId is missing!");
    Validate.notBlank(password, "password is missing!");

    List<Session> previousSessions = getPreviousSessions(tenantId, userId);

    Subject currentUser = SecurityUtils.getSubject();

    if (!currentUser.isAuthenticated())
    {
      LOGGER.info("Logging user [tenant={}, id={}]", tenantId, userId);
      currentUser.login(new AimAuthenticationToken(tenantId, userId, password));

      Session currentSession = getCurrentSession();

      if (null != currentSession)
      {
        LOGGER.info("New session = [{}] created for user [tenant={}, id={}]", currentSession.getId(), tenantId, userId);
      }
    }
    else
    {
      LOGGER.info("User [tenant={}, id={}] already has an active session].", tenantId, userId);
    }

    Session currentSession = getCurrentSession();
    if (null != currentSession)
    {
      currentSession.setAttribute(AimConstants.SESSION_ATTR_TENANT_ID, tenantId);

      if (null != previousSessions)
      {
        for (Session previousSession : previousSessions)
        {
          if (killPreviousSession && null != previousSession && !previousSession.getId().equals(currentSession.getId()))
          {
            // in case of successful login remove old session
            LOGGER.warn("Removing previous session id={} for user [tenant={}, id={}]", previousSession.getId(), tenantId, userId);
            getDefaultWebSessionManager().stop(new DefaultSessionKey(previousSession.getId()));
          }
        }
      }

      return (String) currentSession.getId();
    }
    else
    {
      return null;
    }
  }

  @Override
  public String authenticate(String tenantId, String userId, String password)
  {
    return authenticate(tenantId, userId, password, true);
  }

  @Override
  public String logout()
  {
    Subject currentUser = SecurityUtils.getSubject();
    String userId = getCurrentUserId();
    if (currentUser.isAuthenticated())
    {
      currentUser.logout();
    }

    return userId;
  }

  @Override
  public String getCurrentUserId()
  {
    Subject currentUser = SecurityUtils.getSubject();
    return (String) currentUser.getPrincipal();
  }

  @Override
  public boolean isCurrentUserAuthenticated()
  {
    return SecurityUtils.getSubject().isAuthenticated();
  }

  @Override
  public String getToken()
  {
    return (String) SecurityUtils.getSubject().getSession().getId();
  }

  private List<Session> getPreviousSessions(String tenantId, String userId)
  {
    List<Session> previousSessions = new ArrayList<>();

    try
    {
      for (Session session : getDefaultWebSessionManager().getSessionDAO().getActiveSessions())
      {
        SimplePrincipalCollection principal = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        String sessionTenantId = (String) session.getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);
        LOGGER.info("Previous session [{}] found: [user={}, tenant={}]", session.getId(), principal, sessionTenantId);
        if (principal != null && userId.equals(principal.getPrimaryPrincipal())
          && !StringUtils.isBlank(sessionTenantId) && sessionTenantId.equals(tenantId))
        {
          previousSessions.add(session);
        }
      }
      LOGGER.info("Previous session size = [{}] for [user={}, tenant={}]", previousSessions.size(), userId, tenantId);
      return previousSessions;
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    return null;
  }

  private Session getCurrentSession()
  {
    try
    {
      return SecurityUtils.getSubject().getSession();
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.warn("Currently no session available", e);
      return null;
    }
  }

  private DefaultWebSessionManager getDefaultWebSessionManager()
  {
    DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
    return (DefaultWebSessionManager) defaultWebSecurityManager.getSessionManager();
  }
}
