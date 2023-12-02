package mn.erin.bpms.organization.request.webapp.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestCreateForm implements Serializable
{
  private String processType;
  private String requestId;
  private String caseInstanceId;
  private String taskId;
  private String taskDefinitionKey;
  private Map<String, Object> parameters;

  public RestCreateForm()
  {
  }

  public RestCreateForm(String processType, String requestId, String caseInstanceId, String taskId, String taskDefinitionKey, Map<String, Object> parameters)
  {
    this.processType = processType;
    this.requestId = requestId;
    this.caseInstanceId = caseInstanceId;
    this.taskId = taskId;
    this.taskDefinitionKey = taskDefinitionKey;
    this.parameters = parameters;
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

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getTaskId()
  {
    return taskId;
  }

  public void setTaskId(String taskId)
  {
    this.taskId = taskId;
  }

  public String getTaskDefinitionKey()
  {
    return taskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey)
  {
    this.taskDefinitionKey = taskDefinitionKey;
  }

  public Map<String, Object> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters)
  {
    this.parameters = parameters;
  }
}
