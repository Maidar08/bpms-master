package mn.erin.domain.bpm.usecase.process;

/**
 * @author Zorig
 */
public class DeleteProcessInput
{
  private final String processInstanceId;

  public DeleteProcessInput(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }
}
