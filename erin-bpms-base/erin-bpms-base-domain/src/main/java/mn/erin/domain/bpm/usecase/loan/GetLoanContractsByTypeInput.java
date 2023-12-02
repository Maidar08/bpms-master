package mn.erin.domain.bpm.usecase.loan;

import java.util.Map;

/**
 * @author Bilguunbor
 */

public class GetLoanContractsByTypeInput
{
  private String instanceId;
  private String documentType;
  private Map<String, Object> parameter;

  public GetLoanContractsByTypeInput(String instanceId, String documentType, Map<String, Object> parameter)
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
