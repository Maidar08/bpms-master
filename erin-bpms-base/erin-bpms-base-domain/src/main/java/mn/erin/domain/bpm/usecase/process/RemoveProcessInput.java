package mn.erin.domain.bpm.usecase.process;
/**
 @author Lkahvadorj.A
 **/
public class RemoveProcessInput
{
  private final String processRequestId;
  private final String processInstanceId;
  private final String processRequestState;

  public RemoveProcessInput(String processRequestId, String processInstanceId, String processRequestState)
  {
    this.processRequestId = processRequestId;
    this.processInstanceId = processInstanceId;
    this.processRequestState = processRequestState;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public String getProcessRequestState()
  {
    return processRequestState;
  }
}
