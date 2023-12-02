package mn.erin.domain.bpm.usecase.process;

import java.util.List;

public class DeleteVariableInput
{
  private String processInstanceId;
  private String contextType;
  private List<String> ids;

  public DeleteVariableInput(String processInstanceId, String contextType, List<String> ids)
  {
    this.processInstanceId = processInstanceId;
    this.contextType = contextType;
    this.ids = ids;
  }

  public String getProcessInstanceId()
  {
    return processInstanceId;
  }

  public String getContextType()
  {
    return contextType;
  }

  public List<String> getIds()
  {
    return ids;
  }

  public void setProcessInstanceId(String processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public void setContextType(String contextType)
  {
    this.contextType = contextType;
  }

  public void setIds(List<String> ids)
  {
    this.ids = ids;
  }
}
