package mn.erin.domain.bpm.usecase.cases.start_case;

/**
 * @author Tamir
 */
public class StartCaseInput
{
  private String definitionKey;

  public StartCaseInput(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

}
