package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public class DownloadPublisherDocumentsByTypeInput
{
  private String instanceId;
  private String documentType;
  private Map<String, Object> parameter;

  public DownloadPublisherDocumentsByTypeInput(String instanceId, String documentType)
  {
    this.instanceId = instanceId;
    this.documentType = documentType;
  }

  public DownloadPublisherDocumentsByTypeInput(String instanceId, String documentType, Map<String, Object> parameter)
  {
    this.instanceId = instanceId;
    this.documentType = documentType;
    this.parameter = parameter;
  }

  public String getInstanceId()
  {
    return instanceId;
  }

  public void setInstanceId(String instanceId)
  {
    this.instanceId = instanceId;
  }

  public String getDocumentType()
  {
    return documentType;
  }

  public void setDocumentType(String documentType)
  {
    this.documentType = documentType;
  }

  public Map<String, Object> getParameter()
  {
    return parameter;
  }

  public void setParameter(Map<String, Object> parameter)
  {
    this.parameter = parameter;
  }
}
