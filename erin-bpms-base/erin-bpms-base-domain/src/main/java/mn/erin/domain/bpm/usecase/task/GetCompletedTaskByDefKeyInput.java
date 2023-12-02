package mn.erin.domain.bpm.usecase.task;

/**
 * @author Tamir
 */
public class GetCompletedTaskByDefKeyInput
{
  private String caseInstanceId;
  private String taskDefinitionKey;

  public GetCompletedTaskByDefKeyInput(String caseInstanceId, String taskDefinitionKey)
  {
    this.caseInstanceId = caseInstanceId;
    this.taskDefinitionKey = taskDefinitionKey;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getTaskDefinitionKey()
  {
    return taskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey)
  {
    this.taskDefinitionKey = taskDefinitionKey;
  }
}
