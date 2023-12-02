package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */
@Table("AIM_GROUP")
public class JdbcGroup
{
  @Id
  String id;
  String tenantId;
  String name;
  String parentId;
  int nthSibling;

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }

  public void setNthSibling(int nthSibling)
  {
    this.nthSibling = nthSibling;
  }
}
