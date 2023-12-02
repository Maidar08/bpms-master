package mn.erin.domain.bpm.usecase.organization;

import java.util.Map;

public class SaveCreateOrganizationFormDataInput
{
  private String processType;
  private String requestId;
  private Map<String, Object> properties;

  public SaveCreateOrganizationFormDataInput(String processType, String requestId, Map<String, Object> properties)
  {
    this.processType = processType;
    this.requestId = requestId;
    this.properties = properties;
  }

  public String getProcessType()
  {
    return processType;
  }

  public void setProcessType(String processType)
  {
    this.processType = processType;
  }

  public String getRequestId()
  {
    return requestId;
  }

  public void setRequestId(String requestId)
  {
    this.requestId = requestId;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, Object> properties)
  {
    this.properties = properties;
  }
}
