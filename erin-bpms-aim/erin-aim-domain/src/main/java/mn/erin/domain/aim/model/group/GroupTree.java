package mn.erin.domain.aim.model.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mn.erin.domain.aim.model.permission.Permission;

/**
 * @author Zorig
 */
public class GroupTree
{
  private final String id;
  private final String parent;
  private final String tenantId;
  private final String number;
  private String name;
  private int nthSibling;
  private List<GroupTree> children;
  private Collection<Permission> permissions;

  public GroupTree(String id, String parent, String tenantId, String number)
  {
    this.id = id;
    this.parent = parent;
    this.tenantId = tenantId;
    this.number = number;
    this.children = new ArrayList<>();
    this.permissions = new ArrayList<>();
  }

  public String getId()
  {
    return id;
  }

  public String getParent()
  {
    return parent;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getNumber()
  {
    return number;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }

  public void setNthSibling(int nthSibling)
  {
    this.nthSibling = nthSibling;
  }

  public boolean isRoot()
  {
    return parent == null;
  }

  public boolean isLeaf()
  {
    return children.isEmpty();
  }

  public void addChild(GroupTree group)
  {
    children.add(group);
  }

  //not a good way of implementing but just set it as this for now
  public void removeChild(int index)
  {
    children.remove(index);
  }

  public List<GroupTree> getChildren()
  {
    return children;
  }

  public void setChildren(List<GroupTree> children)
  {
    this.children = children;
  }

  public Collection<Permission> getPermissions()
  {
    return Collections.unmodifiableCollection(permissions);
  }

  public void setPermissions(Collection<Permission> permissions)
  {
    this.permissions = permissions;
  }
}
