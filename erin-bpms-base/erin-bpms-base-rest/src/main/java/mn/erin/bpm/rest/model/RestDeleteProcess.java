package mn.erin.bpm.rest.model;

public class RestDeleteProcess
{

  private String processRequestId;
  private String processInstanceId;
  private String processState;

  public RestDeleteProcess()
  {

  }

  public RestDeleteProcess(String processRequestId, String processInstanceId, String processRequestState)
  {
    this.processRequestId = processRequestId;
    this.processInstanceId = processInstanceId;
    this.processState = processRequestState;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public String getProcessRequestState()
  {
    return processState;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public void setProcessState(String processState)
  {
    this.processState = processState;
  }


}
