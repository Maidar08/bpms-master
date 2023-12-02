package mn.erin.aim.provider;

import java.io.Serializable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;

import mn.erin.domain.aim.provider.ExtSessionInfo;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;

/**
 * @author mLkhagvasuren
 */
public class ShiroExtSessionInfoCache extends SessionListenerAdapter implements ExtSessionInfoCache, Serializable
{
  private static final long serialVersionUID = 2150029554642740572L;

  private static final String ATTR_EXT_SESSION_INFO = "extSessionInfo";

  @Override
  public void setSessionInfo(ExtSessionInfo extSessionInfo)
  {
    session().setAttribute(ATTR_EXT_SESSION_INFO, extSessionInfo);
  }

  @Override
  public ExtSessionInfo getSessionInfo()
  {
    return (ExtSessionInfo) session().getAttribute(ATTR_EXT_SESSION_INFO);
  }

  @Override
  public void onStop(Session session)
  {
    session().removeAttribute(ATTR_EXT_SESSION_INFO);
  }

  @Override
  public void onExpiration(Session session)
  {
    session().removeAttribute(ATTR_EXT_SESSION_INFO);
  }

  private static Session session()
  {
    return SecurityUtils.getSubject().getSession(false);
  }
}
