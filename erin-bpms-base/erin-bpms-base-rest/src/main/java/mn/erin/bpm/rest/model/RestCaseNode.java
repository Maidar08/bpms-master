package mn.erin.bpm.rest.model;

import java.util.List;

/**
 * @author Lkhagvadorj.A
 **/

public class RestCaseNode
{
  private String processId;
  private String label;
  private List<RestCaseNode> children;

  public RestCaseNode(String processId, String label, List<RestCaseNode> children)
  {
    this.processId = processId;
    this.label = label;
    this.children = children;
  }

  public String getProcessId()
  {
    return processId;
  }

  public void setProcessId(String processId)
  {
    this.processId = processId;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public List<RestCaseNode> getChildren()
  {
    return children;
  }

  public void setChildren(List<RestCaseNode> children)
  {
    this.children = children;
  }
}
