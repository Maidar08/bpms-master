package mn.erin.domain.bpm.usecase.task;

public class GetActiveTaskByProcessIdAndDefinitionKeyInput
{
  private final String processId;
  private final String definitionKey;

  public GetActiveTaskByProcessIdAndDefinitionKeyInput(String processId, String definitionKey)
  {
    this.processId = processId;
    this.definitionKey = definitionKey;
  }

  public String getProcessId()
  {
    return processId;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }
}
