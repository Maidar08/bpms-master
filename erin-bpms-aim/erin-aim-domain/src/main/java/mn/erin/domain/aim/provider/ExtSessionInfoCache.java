package mn.erin.domain.aim.provider;

/**
 * @author Lkhagvadorj.A
 **/

public interface ExtSessionInfoCache
{
  void setSessionInfo(ExtSessionInfo extSessionInfo);

  ExtSessionInfo getSessionInfo();
}
