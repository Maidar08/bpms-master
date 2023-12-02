package mn.erin.domain.aim.usecase.group;

import java.util.List;

/**
 * @author Zorig
 */
public class GetGroupOutput
{
  private final String id;
  private final String parentId;
  private final String tenantId;
  private final String name;
  private final String description;
  private final int nthSibling;
  private final List<String> children;

  public GetGroupOutput(String id, String parentId, String tenantId, String name, String description, int nthSibling,
      List<String> children)
  {
    this.id = id;
    this.parentId = parentId;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.nthSibling = nthSibling;
    this.children = children;
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }

  public List<String> getChildren()
  {
    return children;
  }
}
