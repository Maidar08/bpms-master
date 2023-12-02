package mn.erin.aim.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Zorig
 */

@Table("AIM_GROUP_PARENT_CHILD")
public class JdbcGroupParentChild
{
  @Id
  String parentId;
  String childId;

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getChildId()
  {
    return childId;
  }

  public void setChildId(String childId)
  {
    this.childId = childId;
  }

  @Override
  public String toString()
  {
    return "JdbcGroupParentChild{" +
        "parentId='" + parentId + '\'' +
        ", childId='" + childId + '\'' +
        '}';
  }
}
