package mn.erin.domain.bpm.usecase.branch_banking.ussd;

import java.util.Map;

public class UpdateUserUSSDInput
{
  private Map<String, Object> userInfo;
  private String languageId;
  private final String instanceId;

  public UpdateUserUSSDInput(Map<String, Object> userInfo, String languageId, String instanceId)
  {
    this.userInfo = userInfo;
    this.languageId = languageId;
    this.instanceId = instanceId;
  }

  public Map<String, Object> getUserInfo()
  {
    return userInfo;
  }

  public void setUserInfo(Map<String, Object> userInfo)
  {
    this.userInfo = userInfo;
  }

  public String getLanguageId()
  {
    return languageId;
  }

  public void setLanguageId(String languageId)
  {
    this.languageId = languageId;
  }

  public String getInstanceId()
  {
    return instanceId;
  }
}
