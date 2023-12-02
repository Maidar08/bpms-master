package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class GetProcessInput
{
  private final String processInstanceId;

  public GetProcessInput(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }
}
