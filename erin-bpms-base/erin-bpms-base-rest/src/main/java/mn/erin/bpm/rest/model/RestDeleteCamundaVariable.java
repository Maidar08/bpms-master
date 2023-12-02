package mn.erin.bpm.rest.model;

import java.util.List;

public class RestDeleteCamundaVariable
{
  private List<String> ids;

  public RestDeleteCamundaVariable(){}

  public RestDeleteCamundaVariable(List<String> ids)
  {
    this.ids = ids;
  }

  public void setIds(List<String> ids)
  {
    this.ids = ids;
  }

  public List<String> getIds()
  {
    return ids;
  }
}
